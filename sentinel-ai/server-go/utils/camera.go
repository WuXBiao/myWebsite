package utils

import (
	"fmt"
	"os"
	"os/exec"
	"runtime"
	"strings"
)

type CameraDevice struct {
	Name         string `json:"name"`
	DeviceID     string `json:"device_id"`
	Path         string `json:"path"`
	Manufacturer string `json:"manufacturer"`
}

func ScanCameraDevices() ([]CameraDevice, error) {
	switch runtime.GOOS {
	case "darwin":
		return scanCameraDevicesMac()
	case "linux":
		return scanCameraDevicesLinux()
	case "windows":
		return scanCameraDevicesWindows()
	default:
		return []CameraDevice{}, fmt.Errorf("unsupported OS: %s", runtime.GOOS)
	}
}

func scanCameraDevicesMac() ([]CameraDevice, error) {
	devices := []CameraDevice{}

	cmd := exec.Command("system_profiler", "SPCameraDataType", "-json")
	output, err := cmd.Output()
	if err != nil {
		return devices, err
	}

	outputStr := string(output)
	lines := strings.Split(outputStr, "\n")

	var currentCamera *CameraDevice

	for _, line := range lines {
		line = strings.TrimSpace(line)

		if strings.Contains(line, "\"_name\"") {
			if currentCamera != nil && currentCamera.Name != "" {
				devices = append(devices, *currentCamera)
			}
			currentCamera = &CameraDevice{}

			parts := strings.Split(line, ":")
			if len(parts) > 1 {
				name := strings.Trim(strings.TrimSpace(parts[1]), "\"")
				currentCamera.Name = name
				currentCamera.DeviceID = generateCameraID(name)
			}
		}

		if currentCamera != nil {
			if strings.Contains(line, "\"Manufacturer\"") {
				parts := strings.Split(line, ":")
				if len(parts) > 1 {
					currentCamera.Manufacturer = strings.Trim(strings.TrimSpace(parts[1]), "\",")
				}
			}
		}
	}

	if currentCamera != nil && currentCamera.Name != "" {
		devices = append(devices, *currentCamera)
	}

	return devices, nil
}

func scanCameraDevicesLinux() ([]CameraDevice, error) {
	devices := []CameraDevice{}

	entries, err := os.ReadDir("/dev")
	if err != nil {
		return devices, err
	}

	for _, entry := range entries {
		if strings.HasPrefix(entry.Name(), "video") {
			devicePath := "/dev/" + entry.Name()
			cmd := exec.Command("v4l2-ctl", "-d", devicePath, "--info")
			output, err := cmd.Output()
			if err == nil {
				outputStr := string(output)
				lines := strings.Split(outputStr, "\n")
				var name string
				for _, line := range lines {
					if strings.Contains(line, "Card type") {
						parts := strings.Split(line, ":")
						if len(parts) > 1 {
							name = strings.TrimSpace(parts[1])
							break
						}
					}
				}
				if name == "" {
					name = entry.Name()
				}

				devices = append(devices, CameraDevice{
					Name:     name,
					DeviceID: generateCameraID(name),
					Path:     devicePath,
				})
			}
		}
	}

	return devices, nil
}

func scanCameraDevicesWindows() ([]CameraDevice, error) {
	devices := []CameraDevice{}

	cmd := exec.Command("powershell", "-Command", `
		Get-WmiObject Win32_PnPDevice -Filter "ClassGuid='{6994AD05-93D7-11D0-A3CC-00A0C9223196}'" | 
		Select-Object Name, DeviceID | 
		ConvertTo-Json
	`)
	output, err := cmd.Output()
	if err != nil {
		return devices, err
	}

	outputStr := string(output)
	lines := strings.Split(outputStr, "\n")

	var currentCamera *CameraDevice

	for _, line := range lines {
		line = strings.TrimSpace(line)

		if strings.Contains(line, "\"Name\"") {
			if currentCamera != nil && currentCamera.Name != "" {
				devices = append(devices, *currentCamera)
			}
			currentCamera = &CameraDevice{}

			parts := strings.Split(line, ":")
			if len(parts) > 1 {
				name := strings.Trim(strings.TrimSpace(parts[1]), "\",")
				currentCamera.Name = name
				currentCamera.DeviceID = generateCameraID(name)
			}
		}

		if currentCamera != nil {
			if strings.Contains(line, "\"DeviceID\"") {
				parts := strings.Split(line, ":")
				if len(parts) > 1 {
					currentCamera.Path = strings.Trim(strings.TrimSpace(parts[1]), "\",")
				}
			}
		}
	}

	if currentCamera != nil && currentCamera.Name != "" {
		devices = append(devices, *currentCamera)
	}

	return devices, nil
}

func generateCameraID(name string) string {
	name = removeANSIColors(name)
	name = strings.ToLower(name)
	name = strings.ReplaceAll(name, " ", "_")
	name = strings.ReplaceAll(name, "-", "_")

	if len(name) > 50 {
		name = name[:50]
	}

	return "camera_" + name
}
