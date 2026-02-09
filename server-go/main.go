package main

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
)

const (
	uploadDir   = "./uploads"
	maxFileSize = 100 * 1024 * 1024 // 100MB
)

// contains 检查字符串是否包含子串
func contains(s, substr string) bool {
	return strings.Contains(s, substr)
}

func init() {
	// 创建上传文件夹
	if err := os.MkdirAll(uploadDir, os.ModePerm); err != nil {
		fmt.Printf("Failed to create uploads directory: %v\n", err)
	}
}

func main() {
	router := gin.Default()

	// 添加 CORS 中间件
	router.Use(corsMiddleware())

	router.POST("/api/ai/chat", aiChat)

	// 上传文件接口
	router.POST("/api/upload", uploadFile)

	// 下载文件接口
	router.GET("/api/download/:filename", downloadFile)

	// 获取文件列表接口
	router.GET("/api/files", listFiles)

	// 健康检查接口
	router.GET("/api/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status": "ok",
		})
	})

	// 生产环境：使用 HTTP
	log.Println("Starting server on :8080...")
	if err := router.Run(":8080"); err != nil {
		fmt.Printf("Failed to start server: %v\n", err)
	}

}

type aiChatRequest struct {
	Message string `json:"message"`
	Model   string `json:"model"`
}

type openAIChatCompletionRequest struct {
	Model    string                 `json:"model"`
	Messages []map[string]string    `json:"messages"`
	Stream   bool                   `json:"stream,omitempty"`
	Extra    map[string]interface{} `json:"-"`
}

func (r openAIChatCompletionRequest) MarshalJSON() ([]byte, error) {
	type alias openAIChatCompletionRequest
	b, err := json.Marshal(alias(r))
	if err != nil {
		return nil, err
	}
	if len(r.Extra) == 0 {
		return b, nil
	}

	var m map[string]interface{}
	if err := json.Unmarshal(b, &m); err != nil {
		return nil, err
	}
	for k, v := range r.Extra {
		m[k] = v
	}
	return json.Marshal(m)
}

type openAIChatCompletionResponse struct {
	Choices []struct {
		Message struct {
			Content string `json:"content"`
		} `json:"message"`
	} `json:"choices"`
	Error *struct {
		Message string `json:"message"`
	} `json:"error"`
}

func aiChat(c *gin.Context) {
	var req aiChatRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid request body"})
		return
	}
	if strings.TrimSpace(req.Message) == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "message is required"})
		return
	}

	apiKey := strings.TrimSpace(os.Getenv("AI_API_KEY"))
	if apiKey == "" {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "AI_API_KEY is not set"})
		return
	}

	baseURL := strings.TrimSpace(os.Getenv("AI_BASE_URL"))
	if baseURL == "" {
		baseURL = "https://api.openai.com/v1"
	}
	baseURL = strings.TrimRight(baseURL, "/")

	model := strings.TrimSpace(req.Model)
	if model == "" {
		model = strings.TrimSpace(os.Getenv("AI_MODEL"))
	}
	if model == "" {
		model = "gpt-3.5-turbo"
	}

	timeoutSeconds := 60
	if raw := strings.TrimSpace(os.Getenv("AI_TIMEOUT_SECONDS")); raw != "" {
		if v, err := strconv.Atoi(raw); err == nil && v > 0 {
			timeoutSeconds = v
		}
	}

	payload := openAIChatCompletionRequest{
		Model: model,
		Messages: []map[string]string{
			{"role": "user", "content": req.Message},
		},
	}

	body, err := json.Marshal(payload)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to encode request"})
		return
	}

	ctx, cancel := context.WithTimeout(c.Request.Context(), time.Duration(timeoutSeconds)*time.Second)
	defer cancel()

	client := &http.Client{}
	upstreamReq, err := http.NewRequestWithContext(ctx, http.MethodPost, baseURL+"/chat/completions", bytes.NewReader(body))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create upstream request"})
		return
	}
	upstreamReq.Header.Set("Content-Type", "application/json")
	upstreamReq.Header.Set("Authorization", "Bearer "+apiKey)

	resp, err := client.Do(upstreamReq)
	if err != nil {
		c.JSON(http.StatusBadGateway, gin.H{
			"error":        "upstream request failed",
			"detail":       err.Error(),
			"timeout_sec":  timeoutSeconds,
			"upstream_url": baseURL,
		})
		return
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		c.JSON(http.StatusBadGateway, gin.H{"error": "failed to read upstream response"})
		return
	}

	var parsed openAIChatCompletionResponse
	if err := json.Unmarshal(respBody, &parsed); err != nil {
		c.JSON(http.StatusBadGateway, gin.H{"error": "invalid upstream response"})
		return
	}
	if parsed.Error != nil && strings.TrimSpace(parsed.Error.Message) != "" {
		c.JSON(http.StatusBadGateway, gin.H{
			"error":           parsed.Error.Message,
			"upstream_status": resp.StatusCode,
			"upstream_body":   string(respBody),
		})
		return
	}
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		c.JSON(http.StatusBadGateway, gin.H{
			"error":           "upstream returned non-2xx",
			"upstream_status": resp.StatusCode,
			"upstream_body":   string(respBody),
		})
		return
	}
	if len(parsed.Choices) == 0 {
		c.JSON(http.StatusBadGateway, gin.H{
			"error":           "empty upstream response",
			"upstream_status": resp.StatusCode,
			"upstream_body":   string(respBody),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"reply": parsed.Choices[0].Message.Content,
	})
}

// corsMiddleware 处理 CORS 跨域请求
func corsMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Credentials", "true")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, origin, Cache-Control, X-Requested-With")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, DELETE")

		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}

		c.Next()
	}
}

// uploadFile 处理文件上传
func uploadFile(c *gin.Context) {
	// 设置最大上传文件大小
	c.Request.ParseMultipartForm(maxFileSize)

	file, handler, err := c.Request.FormFile("file")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "No file provided",
		})
		return
	}
	defer file.Close()

	// 创建目标文件
	filepath := filepath.Join(uploadDir, handler.Filename)
	dst, err := os.Create(filepath)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": "Failed to save file",
		})
		return
	}
	defer dst.Close()

	// 复制文件内容
	if _, err := io.Copy(dst, file); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": "Failed to save file",
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message":  "File uploaded successfully",
		"filename": handler.Filename,
	})
}

// downloadFile 处理文件下载
func downloadFile(c *gin.Context) {
	filename := c.Param("filename")

	// 防止目录遍历攻击
	if contains(filename, "..") || contains(filename, "/") || contains(filename, "\\") {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid filename",
		})
		return
	}

	filepath := filepath.Join(uploadDir, filename)

	// 检查文件是否存在
	if _, err := os.Stat(filepath); err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"error": "File not found",
		})
		return
	}

	c.FileAttachment(filepath, filename)
}

// listFiles 获取所有上传的文件列表
func listFiles(c *gin.Context) {
	files, err := os.ReadDir(uploadDir)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": "Failed to read files",
		})
		return
	}

	var fileList []gin.H
	for _, file := range files {
		if !file.IsDir() {
			info, _ := file.Info()
			fileList = append(fileList, gin.H{
				"name": file.Name(),
				"size": info.Size(),
			})
		}
	}

	c.JSON(http.StatusOK, gin.H{
		"files": fileList,
	})
}
