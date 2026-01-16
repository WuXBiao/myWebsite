package fortune

import (
	"encoding/json"
	"fmt"

	"github.com/WuXBiao/guayun-go/bazi"
	"github.com/WuXBiao/guayun-go/lucky"
)

// PredictFortune 推测运势
func PredictFortune(baziInfo, gender string) string {
	// 解析八字信息
	var baziMap map[string]string
	if err := json.Unmarshal([]byte(baziInfo), &baziMap); err != nil {
		return `{"error":"Failed to parse bazi info"}`
	}

	// 分析五行
	fiveElements := bazi.AnalyzeFiveElements(baziMap)

	// 推测短期运势
	shortTerm := PredictShortTerm(baziMap, fiveElements, gender)

	// 推测长期运势
	longTerm := PredictLongTerm(baziMap, fiveElements, gender)

	// 获取幸运数字和颜色
	luckyNumbers := lucky.GetLuckyNumbers(baziMap)
	luckyColors := lucky.GetLuckyColors(fiveElements)
	luckyDirection := lucky.GetLuckyDirection(baziMap)

	return fmt.Sprintf(`{
		"shortTerm": %s,
		"longTerm": %s,
		"fiveElements": %s,
		"luckyColor": %s,
		"luckyNumber": %s,
		"luckyDirection": "%s"
	}`, shortTerm, longTerm, fiveElements, luckyColors, luckyNumbers, luckyDirection)
}

// PredictShortTerm 推测短期运势（近三个月）
func PredictShortTerm(baziInfo map[string]string, fiveElements string, gender string) string {
	// 解析五行信息
	var elements map[string]string
	json.Unmarshal([]byte(fiveElements), &elements)

	// 根据五行和性别推测运势
	overall := GetOverallFortune(elements, "short")
	career := GetCareerFortune(elements, gender, "short")
	love := GetLoveFortune(elements, gender, "short")
	health := GetHealthFortune(elements, "short")
	wealth := GetWealthFortune(elements, "short")
	score := CalculateScore(elements, "short")

	return fmt.Sprintf(`{
		"period": "近三个月",
		"overall": "%s",
		"career": "%s",
		"love": "%s",
		"health": "%s",
		"wealth": "%s",
		"score": %d
	}`, overall, career, love, health, wealth, score)
}

// PredictLongTerm 推测长期运势（全年）
func PredictLongTerm(baziInfo map[string]string, fiveElements string, gender string) string {
	// 解析五行信息
	var elements map[string]string
	json.Unmarshal([]byte(fiveElements), &elements)

	overall := GetOverallFortune(elements, "long")
	career := GetCareerFortune(elements, gender, "long")
	love := GetLoveFortune(elements, gender, "long")
	health := GetHealthFortune(elements, "long")
	wealth := GetWealthFortune(elements, "long")
	score := CalculateScore(elements, "long")

	return fmt.Sprintf(`{
		"period": "今年全年",
		"overall": "%s",
		"career": "%s",
		"love": "%s",
		"health": "%s",
		"wealth": "%s",
		"score": %d
	}`, overall, career, love, health, wealth, score)
}

// GetOverallFortune 获取总体运势
func GetOverallFortune(elements map[string]string, period string) string {
	fortuneMap := map[string]map[string]string{
		"short": {
			"旺": "运势上升", "强": "运势良好", "中": "平稳上升",
			"弱": "需要调整", "缺": "运势低迷",
		},
		"long": {
			"旺": "运势很好", "强": "运势良好", "中": "平稳发展",
			"弱": "需要努力", "缺": "需要改善",
		},
	}

	// 统计五行强弱
	strongCount := 0
	for _, strength := range elements {
		if strength == "旺" || strength == "强" {
			strongCount++
		}
	}

	strength := "中"
	if strongCount >= 3 {
		strength = "旺"
	} else if strongCount == 2 {
		strength = "强"
	} else if strongCount <= 1 {
		strength = "弱"
	}

	return fortuneMap[period][strength]
}

// GetCareerFortune 获取事业运势
func GetCareerFortune(elements map[string]string, gender string, period string) string {
	careerMap := map[string]map[string]map[string]string{
		"short": {
			"male": {
				"旺": "事业蒸蒸日上，有晋升机会",
				"强": "工作顺利，业绩突出",
				"中": "工作稳定，有进展",
				"弱": "工作压力大，需调整",
			},
			"female": {
				"旺": "事业发展快速，机遇众多",
				"强": "工作表现优异，受重视",
				"中": "工作稳定，逐步进展",
				"弱": "工作遇冷，需要突破",
			},
		},
		"long": {
			"male": {
				"旺": "事业发展稳定，有重大突破",
				"强": "事业运势良好，升职在即",
				"中": "事业稳步发展，机会不少",
				"弱": "事业需要调整，谨慎决策",
			},
			"female": {
				"旺": "事业腾飞，机遇连连",
				"强": "事业发展顺利，前景光明",
				"中": "事业稳定增长，循序渐进",
				"弱": "事业停滞，需要改变策略",
			},
		},
	}

	// 判断强弱
	strength := "中"
	if elements["火"] == "旺" || elements["木"] == "旺" {
		strength = "旺"
	} else if elements["火"] == "强" || elements["木"] == "强" {
		strength = "强"
	} else if elements["火"] == "弱" && elements["木"] == "弱" {
		strength = "弱"
	}

	genderKey := "male"
	if gender == "female" {
		genderKey = "female"
	}

	return careerMap[period][genderKey][strength]
}

// GetLoveFortune 获取感情运势
func GetLoveFortune(elements map[string]string, gender string, period string) string {
	loveMap := map[string]map[string]map[string]string{
		"short": {
			"male": {
				"旺": "感情运势旺盛，有新的邂逅",
				"强": "感情稳定，有惊喜",
				"中": "感情平稳，有小浪漫",
				"弱": "感情需要经营，多沟通",
			},
			"female": {
				"旺": "桃花运旺盛，魅力十足",
				"强": "感情甜蜜，有进展",
				"中": "感情稳定，温馨和谐",
				"弱": "感情需要关注，主动出击",
			},
		},
		"long": {
			"male": {
				"旺": "感情运势极佳，有结婚机会",
				"强": "感情发展顺利，有重大进展",
				"中": "感情稳定发展，逐步深化",
				"弱": "感情需要耐心，不可急躁",
			},
			"female": {
				"旺": "桃花运旺盛，有良缘",
				"强": "感情发展顺利，有望成就",
				"中": "感情稳定，有美好前景",
				"弱": "感情需要主动，把握机会",
			},
		},
	}

	// 判断强弱
	strength := "中"
	if elements["火"] == "旺" || elements["水"] == "旺" {
		strength = "旺"
	} else if elements["火"] == "强" || elements["水"] == "强" {
		strength = "强"
	} else if elements["火"] == "弱" && elements["水"] == "弱" {
		strength = "弱"
	}

	genderKey := "male"
	if gender == "female" {
		genderKey = "female"
	}

	return loveMap[period][genderKey][strength]
}

// GetHealthFortune 获取健康运势
func GetHealthFortune(elements map[string]string, period string) string {
	healthMap := map[string]map[string]string{
		"short": {
			"旺": "身体状态很好，精力充沛",
			"强": "身体健康，精神焕发",
			"中": "身体健康，需注意休息",
			"弱": "身体欠佳，需要调理",
		},
		"long": {
			"旺": "全年身体状态优异，精力充足",
			"强": "身体健康，抵抗力强",
			"中": "身体健康，需防秋冬季疾病",
			"弱": "身体需要调理，定期检查",
		},
	}

	// 判断强弱
	strength := "中"
	if elements["金"] == "旺" || elements["水"] == "旺" {
		strength = "旺"
	} else if elements["金"] == "强" || elements["水"] == "强" {
		strength = "强"
	} else if elements["金"] == "弱" && elements["水"] == "弱" {
		strength = "弱"
	}

	return healthMap[period][strength]
}

// GetWealthFortune 获取财运
func GetWealthFortune(elements map[string]string, period string) string {
	wealthMap := map[string]map[string]string{
		"short": {
			"旺": "财运旺盛，有意外之财",
			"强": "财运不错，适合投资",
			"中": "财运平稳，稳步增长",
			"弱": "财运平淡，需要谨慎",
		},
		"long": {
			"旺": "财运极佳，收入增加",
			"强": "财运稳定增长，有突破",
			"中": "财运稳定，循序渐进",
			"弱": "财运需要关注，谨慎理财",
		},
	}

	// 判断强弱
	strength := "中"
	if elements["金"] == "旺" || elements["土"] == "旺" {
		strength = "旺"
	} else if elements["金"] == "强" || elements["土"] == "强" {
		strength = "强"
	} else if elements["金"] == "弱" && elements["土"] == "弱" {
		strength = "弱"
	}

	return wealthMap[period][strength]
}

// CalculateScore 计算运势评分
func CalculateScore(elements map[string]string, period string) int {
	baseScore := 60
	bonus := 0

	// 根据五行强弱加分
	for _, strength := range elements {
		switch strength {
		case "旺":
			bonus += 8
		case "强":
			bonus += 5
		case "中":
			bonus += 2
		case "弱":
			bonus -= 3
		case "缺":
			bonus -= 5
		}
	}

	score := baseScore + bonus
	if score > 100 {
		score = 100
	}
	if score < 0 {
		score = 0
	}

	// 长期运势通常比短期低一点
	if period == "long" && score > 5 {
		score -= 3
	}

	return score
}
