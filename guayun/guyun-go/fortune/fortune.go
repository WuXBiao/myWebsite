package fortune

import (
	"strings"

	"github.com/WuXBiao/guayun-go/bazi"
	"github.com/WuXBiao/guayun-go/lucky"
)

// FortuneResult holds the complete fortune prediction
type FortuneResult struct {
	BaziInfo         *bazi.Bazi                 `json:"bazi_info"`
	TenGods          *bazi.TenGods              `json:"ten_gods"`
	DayMasterAnalysis *bazi.DayMasterAnalysis    `json:"day_master_analysis"`
	Career           string                     `json:"career"`
	Wealth           string                     `json:"wealth"`
	Love             string                     `json:"love"`
	Health           string                     `json:"health"`
	LuckyInfo        *LuckyInfo                 `json:"lucky_info"`
}

// LuckyInfo holds lucky elements
type LuckyInfo struct {
	Colors    []string `json:"colors"`
	Numbers   []int    `json:"numbers"`
	Direction string   `json:"direction"`
}

// PredictFortune 推测运势
func PredictFortune(baziInfo *bazi.Bazi, gender string) *FortuneResult {
	tenGods := baziInfo.GetTenGods()
	dayMasterAnalysis := baziInfo.AnalyzeDayMasterStrength()

	return &FortuneResult{
		BaziInfo:         baziInfo,
		TenGods:          tenGods,
		DayMasterAnalysis: dayMasterAnalysis,
		Career:           analyzeCareer(tenGods, dayMasterAnalysis),
		Wealth:           analyzeWealth(tenGods, dayMasterAnalysis),
		Love:             analyzeLove(baziInfo, dayMasterAnalysis, gender),
		Health:           analyzeHealth(dayMasterAnalysis),
		LuckyInfo: &LuckyInfo{
			Colors:    lucky.GetLuckyColors(bazi.AnalyzeFiveElements(baziInfo)), // Kept for simplicity
			Numbers:   lucky.GetLuckyNumbers(baziInfo),
			Direction: lucky.GetLuckyDirection(baziInfo),
		},
	}
}

func analyzeCareer(tenGods *bazi.TenGods, analysis *bazi.DayMasterAnalysis) string {
	if analysis.Strength == "身强" {
		if contains(tenGods.Month, "正官") || contains(tenGods.Month, "七杀") {
			return "事业心强，有领导才能，适合在政府或大企业发展。"
		}
		return "精力充沛，适合创业或从事有挑战性的工作。"
	} else {
		if contains(tenGods.Month, "正印") || contains(tenGods.Month, "偏印") {
			return "文笔佳，有贵人相助，适合从事文教、科研类工作。"
		}
		return "性格温和，适合团队合作，从事稳定类型的工作。"
	}
}

func analyzeWealth(tenGods *bazi.TenGods, analysis *bazi.DayMasterAnalysis) string {
	if analysis.Strength == "身强" {
		if contains(tenGods.Month, "正财") || contains(tenGods.Month, "偏财") {
			return "财运旺盛，善于理财，有机会获得丰厚回报。"
		}
		return "求财需努力，亲力亲为，方能有所收获。"
	} else {
		if contains(tenGods.Month, "比肩") || contains(tenGods.Month, "劫财") {
			return "适合与人合作求财，但需注意防范财务风险。"
		}
		return "财运平稳，不宜进行高风险投资，适合稳健理财。"
	}
}

func analyzeLove(baziInfo *bazi.Bazi, analysis *bazi.DayMasterAnalysis, gender string) string {
	// 夫妻宫分析 (日支)
	dayBranchElement := bazi.GetDayBranchElement(baziInfo)
	isFavorable := false
	for _, elem := range analysis.FavorableElements {
		if elem == dayBranchElement {
			isFavorable = true
			break
		}
	}

	if isFavorable {
		return "夫妻宫为喜用神，与伴侣关系和谐，能互相扶持，感情生活美满。"
	} else {
		return "夫妻宫为忌神，感情中易有波折，需要双方更多的沟通和理解来维持和谐。"
	}
}

func analyzeHealth(analysis *bazi.DayMasterAnalysis) string {
	// 简单的五行平衡分析
	if len(analysis.FavorableElements) >= 3 {
		return "五行较为均衡，身体素质良好，注意日常作息即可。"
	}
	missingElements := getMissingElements(analysis)
	return fmt.Sprintf("五行有所偏颇，需注意 %s 相关的健康问题，建议通过饮食、运动进行调理。", strings.Join(missingElements, "、"))
}

func contains(s, substr string) bool {
	return strings.Contains(s, substr)
}

func getMissingElements(analysis *bazi.DayMasterAnalysis) []string {
	// A simplified way to suggest what might be 'missing' or weak
	// For a real analysis, we'd look at the scores map.
	allElements := []string{"木", "火", "土", "金", "水"}
	present := make(map[string]bool)
	for _, e := range analysis.FavorableElements {
		present[e] = true
	}
	for _, e := range analysis.UnfavorableElements {
		present[e] = true
	}

	missing := []string{}
	for _, e := range allElements {
		if !present[e] {
			missing = append(missing, e)
		}
	}
	return missing
}
