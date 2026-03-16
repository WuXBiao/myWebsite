package database

import (
	"log"
	"sentinel-ai/server-go/models"

	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var DB *gorm.DB

func InitDB() {
	var err error
	// For simplicity, using SQLite. In production, use environment variable for DSN.
	DB, err = gorm.Open(sqlite.Open("sentinel.db"), &gorm.Config{})
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}

	// Auto Migrate the schema
	err = DB.AutoMigrate(&models.User{}, &models.Device{}, &models.Event{})
	if err != nil {
		log.Fatal("Failed to migrate database:", err)
	}

	log.Println("Database connected and migrated successfully.")
}
