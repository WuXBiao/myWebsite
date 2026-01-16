package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strings"

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
