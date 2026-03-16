package models

import (
	"time"

	"gorm.io/gorm"
)

type User struct {
	ID        uint           `json:"id"`
	Username  string         `json:"username"`
	Password  string         `json:"-"`
	Email     string         `json:"email"`
	CreatedAt time.Time      `json:"created_at"`
	UpdatedAt time.Time      `json:"updated_at"`
	DeletedAt gorm.DeletedAt `json:"-"`
}

type Device struct {
	ID           uint           `json:"id"`
	Name         string         `json:"name"`
	HardwareCode string         `gorm:"column:device_id" json:"device_id"` // Hardware ID - column mapping only
	Status       string         `json:"status"`
	IP           string         `json:"ip"`
	LastSeen     time.Time      `json:"last_seen"`
	CreatedAt    time.Time      `json:"created_at"`
	UpdatedAt    time.Time      `json:"updated_at"`
	DeletedAt    gorm.DeletedAt `json:"-"`
}

type Event struct {
	ID         uint      `json:"id"`
	DeviceID   uint      `json:"device_id"`
	Device     Device    `gorm:"foreignKey:DeviceID;references:ID" json:"device"`
	EventType  string    `json:"event_type"`
	Confidence float64   `json:"confidence"`
	ImageURL   string    `json:"image_url"`
	Data       string    `json:"data"`
	CreatedAt  time.Time `json:"created_at"`
}
