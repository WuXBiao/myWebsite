package controllers

import (
	"net/http"
	"sentinel-ai/server-go/database"
	"sentinel-ai/server-go/models"
	"sentinel-ai/server-go/utils"
	"time"

	"github.com/gin-gonic/gin"
)

type CreateDeviceInput struct {
	Name     string `json:"name" binding:"required"`
	DeviceID string `json:"device_id" binding:"required"`
}

type RegisterDeviceInput struct {
	DeviceID string `json:"device_id" binding:"required"`
	Name     string `json:"name"`
	IP       string `json:"ip"`
}

type ScannedDevice struct {
	DeviceID string `json:"device_id"`
	Name     string `json:"name"`
	IP       string `json:"ip"`
	Status   string `json:"status"`
	IsAdded  bool   `json:"is_added"`
}

func GetDevices(c *gin.Context) {
	var devices []models.Device
	database.DB.Find(&devices)
	c.JSON(http.StatusOK, devices)
}

func CreateDevice(c *gin.Context) {
	var input CreateDeviceInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Check if device already exists (excluding soft-deleted records)
	var existingDevice models.Device
	if result := database.DB.Where("device_id = ?", input.DeviceID).First(&existingDevice); result.Error == nil {
		c.JSON(http.StatusConflict, gin.H{"error": "Device already exists", "device": existingDevice})
		return
	}

	// Check if device exists but is soft-deleted, if so permanently delete it
	var deletedDevice models.Device
	if result := database.DB.Unscoped().Where("device_id = ? AND deleted_at IS NOT NULL", input.DeviceID).First(&deletedDevice); result.Error == nil {
		database.DB.Unscoped().Delete(&deletedDevice)
	}

	device := models.Device{
		Name:         input.Name,
		HardwareCode: input.DeviceID,
		Status:       "offline",
		LastSeen:     time.Now(),
	}

	if result := database.DB.Create(&device); result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create device"})
		return
	}

	c.JSON(http.StatusOK, device)
}

func DeleteDevice(c *gin.Context) {
	id := c.Param("id")
	if result := database.DB.Delete(&models.Device{}, id); result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete device"})
		return
	}
	c.JSON(http.StatusOK, gin.H{"message": "Device deleted"})
}

func RegisterDevice(c *gin.Context) {
	var input RegisterDeviceInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var existingDevice models.Device
	result := database.DB.Where("device_id = ?", input.DeviceID).First(&existingDevice)

	if result.Error == nil {
		existingDevice.Status = "online"
		existingDevice.LastSeen = time.Now()
		if input.IP != "" {
			existingDevice.IP = input.IP
		}
		database.DB.Save(&existingDevice)
		c.JSON(http.StatusOK, gin.H{"message": "Device updated", "device": existingDevice})
		return
	}

	deviceName := input.Name
	if deviceName == "" {
		deviceName = input.DeviceID
	}

	device := models.Device{
		Name:         deviceName,
		HardwareCode: input.DeviceID,
		Status:       "online",
		IP:           input.IP,
		LastSeen:     time.Now(),
	}

	if result := database.DB.Create(&device); result.Error != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to register device"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Device registered", "device": device})
}

func ScanDevices(c *gin.Context) {
	var devices []models.Device
	database.DB.Find(&devices)

	scannedDevices := make([]ScannedDevice, 0)
	for _, device := range devices {
		scannedDevices = append(scannedDevices, ScannedDevice{
			DeviceID: device.HardwareCode,
			Name:     device.Name,
			IP:       device.IP,
			Status:   device.Status,
			IsAdded:  true,
		})
	}

	c.JSON(http.StatusOK, gin.H{
		"devices": scannedDevices,
		"count":   len(scannedDevices),
	})
}

func GetDeviceByID(c *gin.Context) {
	deviceID := c.Param("device_id")
	var device models.Device

	if result := database.DB.Where("device_id = ?", deviceID).First(&device); result.Error != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Device not found"})
		return
	}

	c.JSON(http.StatusOK, device)
}

func ScanUSBDevices(c *gin.Context) {
	usbDevices, err := utils.ScanUSBDevices()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to scan USB devices", "details": err.Error()})
		return
	}

	var registeredDeviceIDs map[string]bool = make(map[string]bool)
	var devices []models.Device
	database.DB.Find(&devices)
	for _, device := range devices {
		registeredDeviceIDs[device.HardwareCode] = true
	}

	type USBDeviceResponse struct {
		DeviceID     string `json:"device_id"`
		Name         string `json:"name"`
		Manufacturer string `json:"manufacturer"`
		Model        string `json:"model"`
		SerialNumber string `json:"serial_number"`
		VendorID     string `json:"vendor_id"`
		ProductID    string `json:"product_id"`
		IsAdded      bool   `json:"is_added"`
	}

	response := make([]USBDeviceResponse, 0)
	for _, usbDevice := range usbDevices {
		isAdded := registeredDeviceIDs[usbDevice.DeviceID]
		response = append(response, USBDeviceResponse{
			DeviceID:     usbDevice.DeviceID,
			Name:         usbDevice.Name,
			Manufacturer: usbDevice.Manufacturer,
			Model:        usbDevice.Model,
			SerialNumber: usbDevice.SerialNumber,
			VendorID:     usbDevice.VendorID,
			ProductID:    usbDevice.ProductID,
			IsAdded:      isAdded,
		})
	}

	c.JSON(http.StatusOK, gin.H{
		"devices": response,
		"count":   len(response),
	})
}

func ScanCameraDevices(c *gin.Context) {
	cameraDevices, err := utils.ScanCameraDevices()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to scan camera devices", "details": err.Error()})
		return
	}

	var registeredDeviceIDs map[string]bool = make(map[string]bool)
	var devices []models.Device
	database.DB.Find(&devices)
	for _, device := range devices {
		registeredDeviceIDs[device.HardwareCode] = true
	}

	type CameraDeviceResponse struct {
		DeviceID     string `json:"device_id"`
		Name         string `json:"name"`
		Path         string `json:"path"`
		Manufacturer string `json:"manufacturer"`
		IsAdded      bool   `json:"is_added"`
	}

	response := make([]CameraDeviceResponse, 0)
	for _, camera := range cameraDevices {
		isAdded := registeredDeviceIDs[camera.DeviceID]
		response = append(response, CameraDeviceResponse{
			DeviceID:     camera.DeviceID,
			Name:         camera.Name,
			Path:         camera.Path,
			Manufacturer: camera.Manufacturer,
			IsAdded:      isAdded,
		})
	}

	c.JSON(http.StatusOK, gin.H{
		"devices": response,
		"count":   len(response),
	})
}
