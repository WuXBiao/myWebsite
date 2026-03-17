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

var httpClient = &http.Client{}

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

// getImageFromRequest extracts image data from request
func getImageFromRequest(c *gin.Context) ([]byte, error) {
	file, err := c.FormFile("image")
	if err != nil {
		return nil, fmt.Errorf("no image provided")
	}

	src, err := file.Open()
	if err != nil {
		return nil, fmt.Errorf("failed to open image")
	}
	defer src.Close()

	imageData, err := io.ReadAll(src)
	if err != nil {
		return nil, fmt.Errorf("failed to read image")
	}

	return imageData, nil
}

// sendToAIService sends image data to AI service and returns the response
func sendToAIService(imageData []byte, endpoint string) (map[string]interface{}, error) {
	body := new(bytes.Buffer)
	writer := multipart.NewWriter(body)

	part, err := writer.CreateFormFile("image", "frame.jpg")
	if err != nil {
		return nil, fmt.Errorf("failed to create form file")
	}

	if _, err := part.Write(imageData); err != nil {
		return nil, fmt.Errorf("failed to write image data")
	}

	if err := writer.Close(); err != nil {
		return nil, fmt.Errorf("failed to close writer")
	}

	req, err := http.NewRequest("POST", AIServiceURL+endpoint, body)
	if err != nil {
		return nil, fmt.Errorf("failed to create request")
	}

	req.Header.Set("Content-Type", writer.FormDataContentType())

	resp, err := httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("failed to connect to AI service")
	}
	defer resp.Body.Close()

	var result map[string]interface{}
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, fmt.Errorf("failed to parse AI response")
	}

	return result, nil
}

// AnalyzeFrame analyzes a video frame for objects and gestures
func AnalyzeFrame(c *gin.Context) {
	imageData, err := getImageFromRequest(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	result, err := sendToAIService(imageData, "/analyze")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

// DetectObjects detects objects in a frame
func DetectObjects(c *gin.Context) {
	imageData, err := getImageFromRequest(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	result, err := sendToAIService(imageData, "/detect")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

// RecognizeGesture recognizes hand gestures in a frame
func RecognizeGesture(c *gin.Context) {
	imageData, err := getImageFromRequest(c)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	result, err := sendToAIService(imageData, "/recognize-gesture")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}
