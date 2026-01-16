package bazi

import "fmt"

// CalculateBazi 计算八字信息
func CalculateBazi(year, month, day, hour, minute int) string {
	heavenlyStems := []string{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"}
	earthlyBranches := []string{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"}

	// 安全的模运算函数，确保结果为正数
	safeModulo := func(n, m int) int {
		result := n % m
		if result < 0 {
			result += m
		}
		return result
	}

	// 简化计算（实际需要更精确的算法）
	yearStem := heavenlyStems[safeModulo(year-1900, 10)]
	yearBranch := earthlyBranches[safeModulo(year-1900, 12)]

	monthStem := heavenlyStems[safeModulo(month-1, 10)]
	monthBranch := earthlyBranches[safeModulo(month-1, 12)]

	dayStem := heavenlyStems[safeModulo(day-1, 10)]
	dayBranch := earthlyBranches[safeModulo(day-1, 12)]

	hourStem := heavenlyStems[safeModulo(hour, 10)]
	hourBranch := earthlyBranches[safeModulo(hour, 12)]

	return fmt.Sprintf(`{
		"year": "%s%s",
		"month": "%s%s",
		"day": "%s%s",
		"hour": "%s%s"
	}`, yearStem, yearBranch, monthStem, monthBranch, dayStem, dayBranch, hourStem, hourBranch)
}

// AnalyzeFiveElements 分析五行属性
func AnalyzeFiveElements(baziInfo map[string]string) string {
	// 天干五行对应关系
	heavenlyFiveElements := map[string]string{
		"甲": "木", "乙": "木",
		"丙": "火", "丁": "火",
		"戊": "土", "己": "土",
		"庚": "金", "辛": "金",
		"壬": "水", "癸": "水",
	}

	// 地支五行对应关系
	earthlyFiveElements := map[string]string{
		"子": "水", "丑": "土",
		"寅": "木", "卯": "木",
		"辰": "土", "巳": "火",
		"午": "火", "未": "土",
		"申": "金", "酉": "金",
		"戌": "土", "亥": "水",
	}

	// 统计五行出现次数
	elementCount := make(map[string]int)

	for _, key := range []string{"year", "month", "day", "hour"} {
		if val, ok := baziInfo[key]; ok && len(val) >= 2 {
			// 天干
			if elem, ok := heavenlyFiveElements[string(val[0])]; ok {
				elementCount[elem]++
			}
			// 地支
			if elem, ok := earthlyFiveElements[string(val[1])]; ok {
				elementCount[elem]++
			}
		}
	}

	// 判断五行强弱
	getStrength := func(count int) string {
		switch {
		case count >= 4:
			return "旺"
		case count == 3:
			return "强"
		case count == 2:
			return "中"
		case count == 1:
			return "弱"
		default:
			return "缺"
		}
	}

	return fmt.Sprintf(`{
		"wood": "%s",
		"fire": "%s",
		"earth": "%s",
		"metal": "%s",
		"water": "%s"
	}`,
		getStrength(elementCount["木"]),
		getStrength(elementCount["火"]),
		getStrength(elementCount["土"]),
		getStrength(elementCount["金"]),
		getStrength(elementCount["水"]))
}
