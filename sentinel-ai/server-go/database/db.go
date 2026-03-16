package database

import (
	"fmt"
	"log"
	"os"

	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

var DB *gorm.DB

func InitDB() {
	var err error

	dbUser := os.Getenv("DB_USER")
	dbPassword := os.Getenv("DB_PASSWORD")
	dbHost := os.Getenv("DB_HOST")
	dbPort := os.Getenv("DB_PORT")
	dbName := os.Getenv("DB_NAME")

	// Check if environment variables are set, otherwise use defaults
	if dbUser == "" {
		dbUser = "root"
	}
	if dbPassword == "" {
		dbPassword = "root"
	}
	if dbHost == "" {
		dbHost = "127.0.0.1"
	}
	if dbPort == "" {
		dbPort = "3306"
	}
	if dbName == "" {
		dbName = "sentinel_ai"
	}

	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?charset=utf8mb4&parseTime=True&loc=Local",
		dbUser, dbPassword, dbHost, dbPort, dbName)

	DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Printf("Failed to connect to MySQL database '%s': %v", dbName, err)
		log.Println("Attempting to create database if not exists...")

		// Try to connect without DB name to create it
		rootDsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/?charset=utf8mb4&parseTime=True&loc=Local",
			dbUser, dbPassword, dbHost, dbPort)
		rootDB, err := gorm.Open(mysql.Open(rootDsn), &gorm.Config{})
		if err != nil {
			log.Fatal("Failed to connect to MySQL server:", err)
		}

		// Create database
		if err := rootDB.Exec(fmt.Sprintf("CREATE DATABASE IF NOT EXISTS %s", dbName)).Error; err != nil {
			log.Fatal("Failed to create database:", err)
		}

		// Reconnect to the specific database
		DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})
		if err != nil {
			log.Fatal("Failed to connect to database after creation:", err)
		}
	}

	// Execute SQL script to create tables
	sqlScript := `
	CREATE TABLE IF NOT EXISTS users (
	    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	    username VARCHAR(255) NOT NULL UNIQUE,
	    password VARCHAR(255) NOT NULL,
	    email VARCHAR(255) UNIQUE,
	    created_at DATETIME(3),
	    updated_at DATETIME(3),
	    deleted_at DATETIME(3),
	    INDEX idx_deleted_at (deleted_at)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
	`

	if err := DB.Exec(sqlScript).Error; err != nil {
		log.Fatal("Failed to create users table:", err)
	}

	sqlScript = `
	CREATE TABLE IF NOT EXISTS devices (
	    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	    name VARCHAR(255) NOT NULL,
	    device_id VARCHAR(255) NOT NULL UNIQUE,
	    status VARCHAR(50) DEFAULT 'offline',
	    ip VARCHAR(255),
	    last_seen DATETIME(3),
	    created_at DATETIME(3),
	    updated_at DATETIME(3),
	    deleted_at DATETIME(3),
	    INDEX idx_deleted_at (deleted_at)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
	`

	if err := DB.Exec(sqlScript).Error; err != nil {
		log.Fatal("Failed to create devices table:", err)
	}

	sqlScript = `
	CREATE TABLE IF NOT EXISTS events (
	    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	    device_id BIGINT UNSIGNED NOT NULL,
	    event_type VARCHAR(255) NOT NULL,
	    confidence DOUBLE,
	    image_url LONGTEXT,
	    data LONGTEXT,
	    created_at DATETIME(3),
	    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
	    INDEX idx_device_id (device_id),
	    INDEX idx_event_type (event_type)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
	`

	if err := DB.Exec(sqlScript).Error; err != nil {
		log.Fatal("Failed to create events table:", err)
	}

	log.Println("MySQL Database connected and tables created successfully.")
}
