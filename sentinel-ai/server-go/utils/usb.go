package utils

import (
	"fmt"
	"os/exec"
	"runtime"
	"strings"
)

type USBDevice struct {
	DeviceID     string `json:"device_id"`
	Name         string `json:"name"`
	Manufacturer string `json:"manufacturer"`
	Model        string `json:"model"`
	SerialNumber string `json:"serial_number"`
	VendorID     string `json:"vendor_id"`
	ProductID    string `json:"product_id"`
}

func ScanUSBDevices() ([]USBDevice, error) {
	switch runtime.GOOS {
	case "darwin":
		devices, err := scanUSBDevicesMac()
		if err == nil && len(devices) > 0 {
			return devices, nil
		}
		return scanUSBDevicesIOKit()
	case "linux":
		return scanUSBDevicesLinux()
	case "windows":
		return scanUSBDevicesWindows()
	default:
		return []USBDevice{}, fmt.Errorf("unsupported OS: %s", runtime.GOOS)
	}
}

func scanUSBDevicesMac() ([]USBDevice, error) {
	devices := []USBDevice{}

	cmd := exec.Command("system_profiler", "SPUSBDataType", "-json")
	output, err := cmd.Output()
	if err != nil {
		return devices, err
	}

	outputStr := string(output)

	lines := strings.Split(outputStr, "\n")
	var currentDevice *USBDevice

	for _, line := range lines {
		line = strings.TrimSpace(line)

		if strings.Contains(line, "\"_name\"") {
			if currentDevice != nil && currentDevice.Name != "" {
				devices = append(devices, *currentDevice)
			}
			currentDevice = &USBDevice{}

			parts := strings.Split(line, ":")
			if len(parts) > 1 {
				name := strings.Trim(strings.TrimSpace(parts[1]), "\"")
				currentDevice.Name = name
				currentDevice.DeviceID = generateDeviceID(name)
			}
		}

		if currentDevice != nil {
			if strings.Contains(line, "\"Manufacturer\"") {
				parts := strings.Split(line, ":")
				if len(parts) > 1 {
					currentDevice.Manufacturer = strings.Trim(strings.TrimSpace(parts[1]), "\",")
				}
			}
			if strings.Contains(line, "\"Product\"") {
				parts := strings.Split(line, ":")
				if len(parts) > 1 {
					currentDevice.Model = strings.Trim(strings.TrimSpace(parts[1]), "\",")
				}
			}
			if strings.Contains(line, "\"Serial Number\"") {
				parts := strings.Split(line, ":")
				if len(parts) > 1 {
					currentDevice.SerialNumber = strings.Trim(strings.TrimSpace(parts[1]), "\",")
				}
			}
		}
	}

	if currentDevice != nil && currentDevice.Name != "" {
		devices = append(devices, *currentDevice)
	}

	return devices, nil
}

func scanUSBDevicesIOKit() ([]USBDevice, error) {
	devices := []USBDevice{}

	cmd := exec.Command("ioreg", "-p", "IOUSB", "-l", "-b", "-n", "IOUSBHostDevice")
	output, err := cmd.Output()
	if err != nil {
		return devices, err
	}

	outputStr := string(output)
	lines := strings.Split(outputStr, "\n")

	var currentDevice *USBDevice
	var inDevice bool

	for _, line := range lines {
		trimmedLine := strings.TrimSpace(line)

		if strings.Contains(trimmedLine, "<class IOUSBHostDevice") {
			if currentDevice != nil && currentDevice.Name != "" {
				devices = append(devices, *currentDevice)
			}
			currentDevice = &USBDevice{}
			inDevice = true

			if strings.Contains(trimmedLine, "@") {
				parts := strings.Split(trimmedLine, "@")
				if len(parts) > 0 {
					nameWithClass := parts[0]
					nameWithClass = strings.TrimSpace(nameWithClass)
					nameWithClass = strings.TrimPrefix(nameWithClass, "+-o ")
					nameWithClass = strings.TrimPrefix(nameWithClass, "| +-o ")
					nameWithClass = removeANSIColors(nameWithClass)

					if nameWithClass != "" {
						currentDevice.Name = nameWithClass
						currentDevice.DeviceID = generateDeviceID(nameWithClass)
					}
				}
			}
		}

		if inDevice && currentDevice != nil {
			if strings.Contains(trimmedLine, "\"idVendor\"") {
				parts := strings.Split(trimmedLine, "=")
				if len(parts) > 1 {
					currentDevice.VendorID = strings.Trim(strings.TrimSpace(parts[1]), " ")
				}
			}
			if strings.Contains(trimmedLine, "\"idProduct\"") {
				parts := strings.Split(trimmedLine, "=")
				if len(parts) > 1 {
					currentDevice.ProductID = strings.Trim(strings.TrimSpace(parts[1]), " ")
				}
			}
			if strings.Contains(trimmedLine, "\"USB Serial Number\"") {
				parts := strings.Split(trimmedLine, "=")
				if len(parts) > 1 {
					currentDevice.SerialNumber = strings.Trim(strings.TrimSpace(parts[1]), "\"")
				}
			}
			if strings.Contains(trimmedLine, "\"iManufacturer\"") {
				parts := strings.Split(trimmedLine, "=")
				if len(parts) > 1 {
					currentDevice.Manufacturer = strings.Trim(strings.TrimSpace(parts[1]), " ")
				}
			}
		}

		if strings.Contains(trimmedLine, "}") && inDevice && currentDevice != nil && currentDevice.Name != "" {
			inDevice = false
		}
	}

	if currentDevice != nil && currentDevice.Name != "" {
		devices = append(devices, *currentDevice)
	}

	return devices, nil
}

func scanUSBDevicesLinux() ([]USBDevice, error) {
	devices := []USBDevice{}

	cmd := exec.Command("lsusb")
	output, err := cmd.Output()
	if err != nil {
		return devices, err
	}

	outputStr := string(output)
	lines := strings.Split(outputStr, "\n")

	for _, line := range lines {
		line = strings.TrimSpace(line)
		if line == "" {
			continue
		}

		parts := strings.Split(line, " ")
		if len(parts) < 7 {
			continue
		}

		vendorID := parts[1]
		productID := parts[2]

		var name string
		if len(parts) > 6 {
			name = strings.Join(parts[6:], " ")
		}

		device := USBDevice{
			DeviceID:  generateDeviceID(name),
			Name:      name,
			VendorID:  vendorID,
			ProductID: productID,
		}

		devices = append(devices, device)
	}

	return devices, nil
}

func scanUSBDevicesWindows() ([]USBDevice, error) {
	devices := []USBDevice{}

	cmd := exec.Command("powershell", "-Command",
		"Get-WmiObject Win32_USBDevice | Select-Object Name, DeviceID, Manufacturer")
	output, err := cmd.Output()
	if err != nil {
		return devices, err
	}

	outputStr := string(output)
	lines := strings.Split(outputStr, "\n")

	for _, line := range lines {
		line = strings.TrimSpace(line)
		if line == "" || strings.HasPrefix(line, "Name") {
			continue
		}

		parts := strings.Split(line, " ")
		if len(parts) < 1 {
			continue
		}

		name := strings.Join(parts, " ")
		device := USBDevice{
			DeviceID: generateDeviceID(name),
			Name:     name,
		}

		devices = append(devices, device)
	}

	return devices, nil
}

func generateDeviceID(name string) string {
	name = removeANSIColors(name)
	name = strings.ToLower(name)
	name = strings.ReplaceAll(name, " ", "_")
	name = strings.ReplaceAll(name, "-", "_")

	if len(name) > 50 {
		name = name[:50]
	}

	return "usb_" + name
}

func removeANSIColors(s string) string {
	result := ""
	i := 0
	for i < len(s) {
		if i+1 < len(s) && s[i:i+2] == "\x1b[" {
			j := i + 2
			for j < len(s) && s[j] != 'm' {
				j++
			}
			if j < len(s) {
				i = j + 1
				continue
			}
		}
		result += string(s[i])
		i++
	}
	return result
}
