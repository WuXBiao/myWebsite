package lucky

import (
	"fmt"
	"github.com/WuXBiao/guayun-go/bazi"
	"reflect"
)

// GetLuckyNumbers 获取幸运数字
func GetLuckyNumbers(baziInfo *bazi.Bazi) []int {
	sum := 0
	branches := []string{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"}
	pillars := []string{baziInfo.YearPillar, baziInfo.MonthPillar, baziInfo.DayPillar}

	for _, pillar := range pillars {
		if len(pillar) >= 6 { // a rune is 3 bytes
			branch := string([]rune(pillar)[1])
			for i, b := range branches {
				if branch == b {
					sum += i + 1
					break
				}
			}
		}
	}

	num1 := (sum % 9) + 1
	num2 := ((sum + 3) % 9) + 1
	return []int{num1, num2}
}

// GetLuckyColors 获取幸运颜色
func GetLuckyColors(analysis *bazi.FiveElementAnalysis) []string {
	colorMap := map[string][]string{
		"木": {"绿", "青"},
		"火": {"红", "紫"},
		"土": {"黄", "棕"},
		"金": {"白", "银"},
		"水": {"黑", "蓝"},
	}

	strengthOrder := map[string]int{"旺": 5, "强": 4, "中": 3, "弱": 2, "缺": 1}
	maxStrength := 0
	maxElement := ""

	v := reflect.ValueOf(*analysis)
	t := v.Type()
	for i := 0; i < v.NumField(); i++ {
		fieldName := t.Field(i).Name
		strength := v.Field(i).String()
		if strengthOrder[strength] > maxStrength {
			maxStrength = strengthOrder[strength]
			// Convert field name to Chinese element name
			switch fieldName {
			case "Wood":
				maxElement = "木"
			case "Fire":
				maxElement = "火"
			case "Earth":
				maxElement = "土"
			case "Metal":
				maxElement = "金"
			case "Water":
				maxElement = "水"
			}
		}
	}

	colors, ok := colorMap[maxElement]
	if !ok || len(colors) == 0 {
		return []string{"红", "绿"} // Default colors
	}

	return colors
}

// GetLuckyDirection 获取幸运方向
func GetLuckyDirection(baziInfo *bazi.Bazi) string {
	directionMap := map[string]string{
		"子": "北", "丑": "东北", "寅": "东", "卯": "东",
		"辰": "东南", "巳": "南", "午": "南", "未": "西南",
		"申": "西", "酉": "西", "戌": "西北", "亥": "北",
	}

	if len(baziInfo.DayPillar) >= 6 {
		branch := string([]rune(baziInfo.DayPillar)[1])
		if dir, ok := directionMap[branch]; ok {
			return dir
		}
	}

	return "东南"
}
