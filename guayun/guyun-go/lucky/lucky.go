package lucky

import (
	"encoding/json"
	"fmt"
)

// GetLuckyNumbers 获取幸运数字
func GetLuckyNumbers(baziInfo map[string]string) string {
	// 简单的算法：根据年月日的和
	sum := 0
	for _, key := range []string{"year", "month", "day"} {
		if val, ok := baziInfo[key]; ok && len(val) >= 2 {
			// 取地支的数值
			branches := []string{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"}
			for i, b := range branches {
				if string(val[1]) == b {
					sum += i + 1
					break
				}
			}
		}
	}

	// 生成两个幸运数字
	num1 := (sum % 9) + 1
	num2 := ((sum + 3) % 9) + 1

	return fmt.Sprintf("[%d, %d]", num1, num2)
}

// GetLuckyColors 获取幸运颜色
func GetLuckyColors(fiveElements string) string {
	var elements map[string]string
	json.Unmarshal([]byte(fiveElements), &elements)

	colorMap := map[string][]string{
		"木": {"绿", "青"},
		"火": {"红", "紫"},
		"土": {"黄", "棕"},
		"金": {"白", "银"},
		"水": {"黑", "蓝"},
	}

	// 找出最旺的五行
	maxStrength := ""
	maxElement := ""
	strengthOrder := map[string]int{"旺": 5, "强": 4, "中": 3, "弱": 2, "缺": 1}

	for elem, strength := range elements {
		if strengthOrder[strength] > strengthOrder[maxStrength] {
			maxStrength = strength
			maxElement = elem
		}
	}

	colors := colorMap[maxElement]
	if len(colors) == 0 {
		colors = []string{"红", "绿"}
	}

	return fmt.Sprintf(`["%s", "%s"]`, colors[0], colors[1])
}

// GetLuckyDirection 获取幸运方向
func GetLuckyDirection(baziInfo map[string]string) string {
	// 根据八字的地支推断幸运方向
	directionMap := map[string]string{
		"子": "北", "丑": "东北", "寅": "东", "卯": "东",
		"辰": "东南", "巳": "南", "午": "南", "未": "西南",
		"申": "西", "酉": "西", "戌": "西北", "亥": "北",
	}

	// 取日支作为参考
	if dayVal, ok := baziInfo["day"]; ok && len(dayVal) >= 2 {
		branch := string(dayVal[1])
		if dir, ok := directionMap[branch]; ok {
			return dir
		}
	}

	return "东南"
}
