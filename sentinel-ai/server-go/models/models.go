package models

import (
	"time"

	"gorm.io/gorm"
)

type User struct {
	ID        uint           `gorm:"primaryKey" json:"id"`
	Username  string         `gorm:"uniqueIndex;not null" json:"username"`
	Password  string         `gorm:"not null" json:"-"` // Don't return password in JSON
	Email     string         `gorm:"uniqueIndex" json:"email"`
	CreatedAt time.Time      `json:"created_at"`
	UpdatedAt time.Time      `json:"updated_at"`
	DeletedAt gorm.DeletedAt `gorm:"index" json:"-"`
}

type Device struct {
	ID        uint           `gorm:"primaryKey" json:"id"`
	Name      string         `gorm:"not null" json:"name"`
	DeviceID  string         `gorm:"uniqueIndex;not null" json:"device_id"` // Hardware ID
	Status    string         `gorm:"default:'offline'" json:"status"`       // online, offline, active
	IP        string         `json:"ip"`
	LastSeen  time.Time      `json:"last_seen"`
	CreatedAt time.Time      `json:"created_at"`
	UpdatedAt time.Time      `json:"updated_at"`
	DeletedAt gorm.DeletedAt `gorm:"index" json:"-"`
}

type Event struct {
	ID         uint      `gorm:"primaryKey" json:"id"`
	DeviceID   uint      `gorm:"not null;index" json:"device_id"`
	Device     Device    `gorm:"foreignKey:DeviceID" json:"device"`
	EventType  string    `gorm:"not null" json:"event_type"` // person_detected, motion, etc.
	Confidence float64   `json:"confidence"`
	ImageURL   string    `json:"image_url"`
	Data       string    `json:"data"` // JSON string for extra data
	CreatedAt  time.Time `json:"created_at"`
}
