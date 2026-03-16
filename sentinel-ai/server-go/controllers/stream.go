package controllers

import (
	"bytes"
	"encoding/json"
	"io"
	"log"
	"mime/multipart"
	"net/http"
	"sentinel-ai/server-go/database"
	"sentinel-ai/server-go/models"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true // Allow all origins for demo
	},
}

// StreamHub maintains the set of active clients and broadcasts messages to clients.
type StreamHub struct {
	// deviceID -> list of viewer connections
	viewers map[string][]*websocket.Conn
	// deviceID -> broadcaster connection (device)
	devices map[string]*websocket.Conn
	mu      sync.RWMutex
}

var Hub = StreamHub{
	viewers: make(map[string][]*websocket.Conn),
	devices: make(map[string]*websocket.Conn),
}

// HandleDeviceStream handles the WebSocket connection from the camera device
func HandleDeviceStream(c *gin.Context) {
	deviceID := c.Param("id")
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Println("Device WS Upgrade Error:", err)
		return
	}
	defer conn.Close()

	Hub.mu.Lock()
	if oldConn, ok := Hub.devices[deviceID]; ok {
		oldConn.Close()
	}
	Hub.devices[deviceID] = conn
	Hub.mu.Unlock()

	log.Printf("Device connected: %s", deviceID)

	lastAnalysisTime := time.Now()

	for {
		messageType, p, err := conn.ReadMessage()
		if err != nil {
			log.Printf("Device %s disconnected: %v", deviceID, err)
			break
		}

		// Analyze frame every 3 seconds to avoid overloading
		if time.Since(lastAnalysisTime) > 3*time.Second {
			lastAnalysisTime = time.Now()
			// Copy data for goroutine
			frameData := make([]byte, len(p))
			copy(frameData, p)
			go analyzeFrame(deviceID, frameData)
		}

		// Broadcast to all viewers
		Hub.mu.RLock()
		viewers := Hub.viewers[deviceID]
		Hub.mu.RUnlock()

		for _, viewer := range viewers {
			if err := viewer.WriteMessage(messageType, p); err != nil {
				log.Println("Error writing to viewer:", err)
			}
		}
	}

	Hub.mu.Lock()
	delete(Hub.devices, deviceID)
	Hub.mu.Unlock()
}

// HandleViewerStream handles the WebSocket connection from the web client
func HandleViewerStream(c *gin.Context) {
	deviceID := c.Param("id")
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Println("Viewer WS Upgrade Error:", err)
		return
	}
	defer conn.Close()

	Hub.mu.Lock()
	if Hub.viewers[deviceID] == nil {
		Hub.viewers[deviceID] = []*websocket.Conn{}
	}
	Hub.viewers[deviceID] = append(Hub.viewers[deviceID], conn)
	Hub.mu.Unlock()

	log.Printf("Viewer connected to device: %s", deviceID)

	// Keep connection alive until client disconnects
	for {
		if _, _, err := conn.ReadMessage(); err != nil {
			break
		}
	}

	Hub.mu.Lock()
	// Remove viewer from list
	viewers := Hub.viewers[deviceID]
	for i, v := range viewers {
		if v == conn {
			Hub.viewers[deviceID] = append(viewers[:i], viewers[i+1:]...)
			break
		}
	}
	Hub.mu.Unlock()
}

type AIResponse struct {
	Message string `json:"message"`
	Objects []struct {
		Class      string    `json:"class"`
		Confidence float64   `json:"confidence"`
		Bbox       []float64 `json:"bbox"`
	} `json:"objects"`
}

func analyzeFrame(deviceID string, frameData []byte) {
	// Create multipart form
	body := &bytes.Buffer{}
	writer := multipart.NewWriter(body)
	part, err := writer.CreateFormFile("image", "frame.jpg")
	if err != nil {
		log.Println("Error creating form file:", err)
		return
	}
	part.Write(frameData)
	writer.Close()

	req, err := http.NewRequest("POST", "http://localhost:5001/detect", body)
	if err != nil {
		log.Println("Error creating request:", err)
		return
	}
	req.Header.Set("Content-Type", writer.FormDataContentType())

	client := &http.Client{Timeout: 5 * time.Second}
	resp, err := client.Do(req)
	if err != nil {
		// log.Println("AI Service Error:", err) // Suppress error if AI service is down
		return
	}
	defer resp.Body.Close()

	respBody, _ := io.ReadAll(resp.Body)
	var aiResp AIResponse
	if err := json.Unmarshal(respBody, &aiResp); err != nil {
		log.Println("Error decoding AI response:", err)
		return
	}

	if len(aiResp.Objects) > 0 {
		log.Printf("Device %s detected: %d objects", deviceID, len(aiResp.Objects))
		
		// Find device ID (uint) from DB
		var device models.Device
		if result := database.DB.Where("device_id = ?", deviceID).First(&device); result.Error == nil {
			// Save event
			event := models.Event{
				DeviceID:   device.ID,
				EventType:  "detection",
				Confidence: aiResp.Objects[0].Confidence,
				Data:       string(respBody), // Save raw JSON for now
				CreatedAt:  time.Now(),
			}
			database.DB.Create(&event)
		}
	}
}
