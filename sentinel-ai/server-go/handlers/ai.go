package handlers

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"

	"github.com/gin-gonic/gin"
)

const AIServiceURL = "http://localhost:5001"

type DetectionResult struct {
	Class      string    `json:"class"`
	Confidence float64   `json:"confidence"`
	BBox       []float64 `json:"bbox"`
}

type Landmark struct {
	X float64 `json:"x"`
	Y float64 `json:"y"`
	Z float64 `json:"z"`
}

type Gesture struct {
	Hand       string     `json:"hand"`
	Confidence float64    `json:"confidence"`
	Landmarks  []Landmark `json:"landmarks"`
}

type AnalysisResponse struct {
	Message  string            `json:"message"`
	Objects  []DetectionResult `json:"objects"`
	Gestures []Gesture         `json:"gestures"`
}

// AnalyzeFrame analyzes a video frame for objects and gestures
func AnalyzeFrame(c *gin.Context) {
	fmt.Println("[AI] AnalyzeFrame called")
	// Get image file from request
	file, err := c.FormFile("image")
	if err != nil {
		fmt.Println("[AI] Error getting image file:", err)
		c.JSON(http.StatusBadRequest, gin.H{"error": "No image provided"})
		return
	}
	fmt.Println("[AI] Image file received, size:", file.Size)

	// Open the file
	src, err := file.Open()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to open image"})
		return
	}
	defer src.Close()

	// Read file content
	imageData, err := io.ReadAll(src)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to read image"})
		return
	}

	// Create multipart request body
	body := new(bytes.Buffer)
	writer := multipart.NewWriter(body)

	// Add image field
	part, err := writer.CreateFormFile("image", "frame.jpg")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create form file"})
		return
	}

	if _, err := part.Write(imageData); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to write image data"})
		return
	}

	// Close the writer to finalize the multipart message
	if err := writer.Close(); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to close writer"})
		return
	}

	// Create request to AI service
	req, err := http.NewRequest("POST", AIServiceURL+"/analyze", body)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create request"})
		return
	}

	// Set content type with boundary
	req.Header.Set("Content-Type", writer.FormDataContentType())

	// Send request to AI service
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to connect to AI service"})
		return
	}
	defer resp.Body.Close()

	// Parse response
	var result map[string]interface{}
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse AI response"})
		return
	}

	c.JSON(http.StatusOK, result)
}

// DetectObjects detects objects in a frame
func DetectObjects(c *gin.Context) {
	file, err := c.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "No image provided"})
		return
	}

	src, err := file.Open()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to open image"})
		return
	}
	defer src.Close()

	imageData, err := io.ReadAll(src)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to read image"})
		return
	}

	req, err := createMultipartRequest(imageData)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create request"})
		return
	}

	req.URL.Path = "/detect"

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to connect to AI service"})
		return
	}
	defer resp.Body.Close()

	var result map[string]interface{}
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse response"})
		return
	}

	c.JSON(http.StatusOK, result)
}

// RecognizeGesture recognizes hand gestures in a frame
func RecognizeGesture(c *gin.Context) {
	file, err := c.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "No image provided"})
		return
	}

	src, err := file.Open()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to open image"})
		return
	}
	defer src.Close()

	imageData, err := io.ReadAll(src)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to read image"})
		return
	}

	req, err := createMultipartRequest(imageData)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create request"})
		return
	}

	req.URL.Path = "/recognize-gesture"

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to connect to AI service"})
		return
	}
	defer resp.Body.Close()

	var result map[string]interface{}
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse response"})
		return
	}

	c.JSON(http.StatusOK, result)
}

// createMultipartRequest creates a multipart form request with image data
func createMultipartRequest(imageData []byte) (*http.Request, error) {
	body := new(bytes.Buffer)

	// Write multipart boundary
	boundary := "----WebKitFormBoundary7MA4YWxkTrZu0gW"
	body.WriteString(fmt.Sprintf("--%s\r\n", boundary))
	body.WriteString("Content-Disposition: form-data; name=\"image\"; filename=\"frame.jpg\"\r\n")
	body.WriteString("Content-Type: image/jpeg\r\n\r\n")
	body.Write(imageData)
	body.WriteString(fmt.Sprintf("\r\n--%s--\r\n", boundary))

	req, err := http.NewRequest("POST", AIServiceURL+"/analyze", body)
	if err != nil {
		return nil, err
	}

	req.Header.Set("Content-Type", fmt.Sprintf("multipart/form-data; boundary=%s", boundary))
	return req, nil
}
