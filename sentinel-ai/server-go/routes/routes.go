package routes

import (
	"sentinel-ai/server-go/controllers"
	"sentinel-ai/server-go/middleware"

	"github.com/gin-gonic/gin"
)

func SetupRoutes(r *gin.Engine) {
	// Public routes
	api := r.Group("/api")
	{
		api.POST("/register", controllers.Register)
		api.POST("/login", controllers.Login)
		api.POST("/devices/register", controllers.RegisterDevice) // Device self-registration
	}

	// Protected routes
	protected := api.Group("/")
	protected.Use(middleware.AuthMiddleware())
	{
		protected.GET("/devices", controllers.GetDevices)
		protected.GET("/devices/scan", controllers.ScanDevices)
		protected.GET("/devices/scan-usb", controllers.ScanUSBDevices)
		protected.GET("/devices/:device_id", controllers.GetDeviceByID)
		protected.POST("/devices", controllers.CreateDevice)
		protected.DELETE("/devices/:id", controllers.DeleteDevice)
	}

	// Stream routes (WebSocket)
	// These might need separate auth handling as WS headers are tricky
	r.GET("/ws/stream/:id", controllers.HandleDeviceStream) // Device connects here
	r.GET("/ws/watch/:id", controllers.HandleViewerStream)  // Viewer connects here
}
