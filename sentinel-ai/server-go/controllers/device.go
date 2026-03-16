package controllers

import (
	"net/http"
	"sentinel-ai/server-go/database"
	"sentinel-ai/server-go/models"
	"time"

	"github.com/gin-gonic/gin"
)

type CreateDeviceInput struct {
	Name     string `json:"name" binding:"required"`
	DeviceID string `json:"device_id" binding:"required"`
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

	device := models.Device{
		Name:     input.Name,
		DeviceID: input.DeviceID,
		Status:   "offline",
		LastSeen: time.Now(),
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
