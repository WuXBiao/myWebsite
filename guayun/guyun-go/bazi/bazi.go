package bazi

import (
	"fmt"
	"time"
	"unicode/utf8"
)

// Bazi struct to hold the four pillars
type Bazi struct {
	YearPillar  string `json:"year_pillar"`
	MonthPillar string `json:"month_pillar"`
	DayPillar   string `json:"day_pillar"`
	HourPillar  string `json:"hour_pillar"`
	DayMaster   rune   `json:"day_master"`
}

// TenGods 十神
type TenGods struct {
	Year  string `json:"year"`
	Month string `json:"month"`
	Day   string `json:"day"`
	Hour  string `json:"hour"`
}

// FiveElementAnalysis 存储五行分析结果
type FiveElementAnalysis struct {
	Wood  string `json:"wood"`
	Fire  string `json:"fire"`
	Earth string `json:"earth"`
	Metal string `json:"metal"`
	Water string `json:"water"`
}

// DayMasterAnalysis holds the analysis of the Day Master's strength and favorable elements.
type DayMasterAnalysis struct {
	Strength          string   `json:"strength"`
	FavorableElements []string `json:"favorable_elements"`
	UnfavorableElements []string `json:"unfavorable_elements"`
}

var earthlyFiveElements = map[rune]string{
	'子': "水", '丑': "土", '寅': "木", '卯': "木", '辰': "土", '巳': "火",
	'午': "火", '未': "土", '申': "金", '酉': "金", '戌': "土", '亥': "水",
}

var hiddenStems = map[rune][]rune{
	'子': {'癸'}, '丑': {'癸', '辛', '己'}, '寅': {'甲', '丙', '戊'}, '卯': {'乙'},
	'辰': {'乙', '戊', '癸'}, '巳': {'庚', '丙', '戊'}, '午': {'丁', '己'}, '未': {'乙', '己', '丁'},
	'申': {'戊', '庚', '壬'}, '酉': {'辛'}, '戌': {'辛', '丁', '戊'}, '亥': {'甲', '壬'},
}

var stemInfo = map[rune]struct {
	Element string
	YinYang string
}{
	'甲': {"木", "阳"}, '乙': {"木", "阴"}, '丙': {"火", "阳"}, '丁': {"火", "阴"},
	'戊': {"土", "阳"}, '己': {"土", "阴"}, '庚': {"金", "阳"}, '辛': {"金", "阴"},
	'壬': {"水", "阳"}, '癸': {"水", "阴"},
}

var fiveElementRelation = map[string]map[string]string{
	"木": {"木": "比和", "火": "我生", "土": "我克", "金": "克我", "水": "生我"},
	"火": {"火": "比和", "土": "我生", "金": "我克", "水": "克我", "木": "生我"},
	"土": {"土": "比和", "金": "我生", "水": "我克", "木": "克我", "火": "生我"},
	"金": {"金": "比和", "水": "我生", "木": "我克", "火": "克我", "土": "生我"},
	"水": {"水": "比和", "木": "我生", "火": "我克", "土": "克我", "金": "生我"},
}

var tenGodsName = map[string]map[string]string{
	"比和": {"同": "比肩", "异": "劫财"}, "我生": {"同": "食神", "异": "伤官"},
	"我克": {"同": "偏财", "异": "正财"}, "克我": {"同": "七杀", "异": "正官"},
	"生我": {"同": "偏印", "异": "正印"},
}

func NewBazi(t time.Time) *Bazi {
	lunarData := SolarToLunar(t)
	dayMaster, _ := utf8.DecodeRuneInString(lunarData.DayGan)

	return &Bazi{
		YearPillar:  fmt.Sprintf("%s%s", lunarData.YearGan, lunarData.YearZhi),
		MonthPillar: fmt.Sprintf("%s%s", lunarData.MonthGan, lunarData.MonthZhi),
		DayPillar:   fmt.Sprintf("%s%s", lunarData.DayGan, lunarData.DayZhi),
		HourPillar:  fmt.Sprintf("%s%s", lunarData.HourGan, lunarData.HourZhi),
		DayMaster:   dayMaster,
	}
}

func (b *Bazi) GetTenGods() *TenGods {
	dayMasterInfo := stemInfo[b.DayMaster]

	getGod := func(pillar string) string {
		pillarRunes := []rune(pillar)
		if len(pillarRunes) < 1 {
			return ""
		}
		stem := pillarRunes[0]
		stemInfo := stemInfo[stem]

		relation := fiveElementRelation[dayMasterInfo.Element][stemInfo.Element]
		yinYangRelation := "同"
		if dayMasterInfo.YinYang != stemInfo.YinYang {
			yinYangRelation = "异"
		}

		return tenGodsName[relation][yinYangRelation]
	}

	return &TenGods{
		Year:  getGod(b.YearPillar),
		Month: getGod(b.MonthPillar),
		Day:   getGod(b.DayPillar),
		Hour:  getGod(b.HourPillar),
	}
}

func (b *Bazi) AnalyzeDayMasterStrength() *DayMasterAnalysis {
	dayMasterElement := stemInfo[b.DayMaster].Element
	monthBranch, _ := utf8.DecodeLastRuneInString(b.MonthPillar)

	// 1. 判断是否得令
	isTimely := false
	monthElement := earthlyFiveElements[monthBranch]
	if monthElement == dayMasterElement || fiveElementRelation[monthElement][dayMasterElement] == "生我" {
		isTimely = true
	}

	// 2. 计算五行分数
	scores := b.getFiveElementScores()

	// 3. 判断身强身弱
	supportScore := scores[dayMasterElement]
	supportScore += scores[getProducingElement(dayMasterElement)]

	strength := "身弱"
	if isTimely && supportScore > 150 {
		strength = "身强"
	} else if !isTimely && supportScore > 180 {
		strength = "身强"
	} else if supportScore < 100 {
		strength = "身弱"
	}

	// 4. 确定喜用神和忌神
	favorable := []string{}
	unfavorable := []string{}

	if strength == "身强" {
		unfavorable = append(unfavorable, dayMasterElement, getProducingElement(dayMasterElement))
		favorable = append(favorable, getControllingElement(dayMasterElement), getControlledElement(dayMasterElement), getDrainingElement(dayMasterElement))
	} else { // 身弱
		favorable = append(favorable, dayMasterElement, getProducingElement(dayMasterElement))
		unfavorable = append(unfavorable, getControllingElement(dayMasterElement), getControlledElement(dayMasterElement), getDrainingElement(dayMasterElement))
	}

	return &DayMasterAnalysis{
		Strength:          strength,
		FavorableElements: favorable,
		UnfavorableElements: unfavorable,
	}
}

func (b *Bazi) getFiveElementScores() map[string]float64 {
	scores := map[string]float64{
		"木": 0, "火": 0, "土": 0, "金": 0, "水": 0,
	}

	// 天干分数
	stemScores := map[rune]float64{'甲': 12, '乙': 12, '丙': 12, '丁': 12, '戊': 12, '己': 12, '庚': 12, '辛': 12, '壬': 12, '癸': 12}
	// 地支分数 (本气)
	branchScores := map[rune]float64{'子': 12, '丑': 9, '寅': 12, '卯': 12, '辰': 9, '巳': 12, '午': 12, '未': 9, '申': 12, '酉': 12, '戌': 9, '亥': 12}

	pillars := []string{b.YearPillar, b.MonthPillar, b.DayPillar, b.HourPillar}
	for i, pillar := range pillars {
		pillarRunes := []rune(pillar)
		if len(pillarRunes) < 2 {
			continue
		}
		stem := pillarRunes[0]
		branch := pillarRunes[1]

		// 加天干分数
		if score, ok := stemScores[stem]; ok {
			scores[stemInfo[stem].Element] += score
		}

		// 加地支本气分数
		if score, ok := branchScores[branch]; ok {
			// 月令权重加倍
			if i == 1 {
				score *= 2
			}
			scores[earthlyFiveElements[branch]] += score
		}

		// 加地支藏干分数 (非本气)
		for _, hidden := range hiddenStems[branch] {
			if earthlyFiveElements[branch] != stemInfo[hidden].Element {
				score := 6.0 // 余气、杂气简化处理
				if i == 1 { // 月令权重
					score *= 1.5
				}
				scores[stemInfo[hidden].Element] += score
			}
		}
	}

	return scores
}

// 五行关系辅助函数
func getProducingElement(element string) string {
	relations := map[string]string{"木": "水", "火": "木", "土": "火", "金": "土", "水": "金"}
	return relations[element]
}

func getDrainingElement(element string) string {
	relations := map[string]string{"木": "火", "火": "土", "土": "金", "金": "水", "水": "木"}
	return relations[element]
}

func getControllingElement(element string) string {
	relations := map[string]string{"木": "金", "火": "水", "土": "木", "金": "火", "水": "土"}
	return relations[element]
}

func getControlledElement(element string) string {
	relations := map[string]string{"木": "土", "火": "金", "土": "水", "金": "木", "水": "火"}
	return relations[element]
}

// GetDayBranchElement returns the five element of the day branch.
func GetDayBranchElement(baziInfo *Bazi) string {
	dayBranch, _ := utf8.DecodeLastRuneInString(baziInfo.DayPillar)
	return earthlyFiveElements[dayBranch]
}

// AnalyzeFiveElements 分析五行属性 (旧函数，保留以兼容)
func AnalyzeFiveElements(bazi *Bazi) *FiveElementAnalysis {
	heavenlyFiveElements := map[rune]string{
		'甲': "木", '乙': "木",
		'丙': "火", '丁': "火",
		'戊': "土", '己': "土",
		'庚': "金", '辛': "金",
		'壬': "水", '癸': "水",
	}

	elementCount := map[string]int{
		"木": 0, "火": 0, "土": 0, "金": 0, "水": 0,
	}

	pillars := []string{bazi.YearPillar, bazi.MonthPillar, bazi.DayPillar, bazi.HourPillar}
	for _, pillar := range pillars {
		pillarRunes := []rune(pillar)
		if len(pillarRunes) == 2 {
			stem := pillarRunes[0]
			branch := pillarRunes[1]
			if element, ok := heavenlyFiveElements[stem]; ok {
				elementCount[element]++
			}
			if element, ok := earthlyFiveElements[branch]; ok {
				elementCount[element]++
			}
		}
	}

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

	return &FiveElementAnalysis{
		Wood:  getStrength(elementCount["木"]),
		Fire:  getStrength(elementCount["火"]),
		Earth: getStrength(elementCount["土"]),
		Metal: getStrength(elementCount["金"]),
		Water: getStrength(elementCount["水"]),
	}
}

func (b *Bazi) GetTenGods() *TenGods {
	dayMasterInfo := stemInfo[b.DayMaster]

	getGod := func(pillar string) string {
		pillarRunes := []rune(pillar)
		if len(pillarRunes) < 1 {
			return ""
		}
		stem := pillarRunes[0]
		stemInfo := stemInfo[stem]

		relation := fiveElementRelation[dayMasterInfo.Element][stemInfo.Element]
		yinYangRelation := "同"
		if dayMasterInfo.YinYang != stemInfo.YinYang {
			yinYangRelation = "异"
		}

		return tenGodsName[relation][yinYangRelation]
	}

	return &TenGods{
		Year:  getGod(b.YearPillar),
		Month: getGod(b.MonthPillar),
		Day:   getGod(b.DayPillar),
		Hour:  getGod(b.HourPillar),
	}
}

func (b *Bazi) AnalyzeDayMasterStrength() *DayMasterAnalysis {
	dayMasterElement := stemInfo[b.DayMaster].Element
	monthBranch, _ := utf8.DecodeLastRuneInString(b.MonthPillar)

	isTimely := false
	monthElement := earthlyFiveElements[monthBranch]
	if monthElement == dayMasterElement || fiveElementRelation[monthElement][dayMasterElement] == "生我" {
		isTimely = true
	}

	scores := b.getFiveElementScores()

	supportScore := scores[dayMasterElement]
	supportScore += scores[getProducingElement(dayMasterElement)]

	strength := "身弱"
	if isTimely && supportScore > 150 {
		strength = "身强"
	} else if !isTimely && supportScore > 180 {
		strength = "身强"
	} else if supportScore < 100 {
		strength = "身弱"
	}

	favorable := []string{}
	unfavorable := []string{}

	if strength == "身强" {
		unfavorable = append(unfavorable, dayMasterElement, getProducingElement(dayMasterElement))
		favorable = append(favorable, getControllingElement(dayMasterElement), getControlledElement(dayMasterElement), getDrainingElement(dayMasterElement))
	} else {
		favorable = append(favorable, dayMasterElement, getProducingElement(dayMasterElement))
		unfavorable = append(unfavorable, getControllingElement(dayMasterElement), getControlledElement(dayMasterElement), getDrainingElement(dayMasterElement))
	}

	return &DayMasterAnalysis{
		Strength:          strength,
		FavorableElements: favorable,
		UnfavorableElements: unfavorable,
	}
}

func (b *Bazi) getFiveElementScores() map[string]float64 {
	scores := map[string]float64{
		"木": 0, "火": 0, "土": 0, "金": 0, "水": 0,
	}

	stemScores := map[rune]float64{'甲': 12, '乙': 12, '丙': 12, '丁': 12, '戊': 12, '己': 12, '庚': 12, '辛': 12, '壬': 12, '癸': 12}
	branchScores := map[rune]float64{'子': 12, '丑': 9, '寅': 12, '卯': 12, '辰': 9, '巳': 12, '午': 12, '未': 9, '申': 12, '酉': 12, '戌': 9, '亥': 12}

	pillars := []string{b.YearPillar, b.MonthPillar, b.DayPillar, b.HourPillar}
	for i, pillar := range pillars {
		pillarRunes := []rune(pillar)
		if len(pillarRunes) < 2 {
			continue
		}
		stem := pillarRunes[0]
		branch := pillarRunes[1]

		if score, ok := stemScores[stem]; ok {
			scores[stemInfo[stem].Element] += score
		}

		if score, ok := branchScores[branch]; ok {
			if i == 1 {
				score *= 2
			}
			scores[earthlyFiveElements[branch]] += score
		}

		for _, hidden := range hiddenStems[branch] {
			if earthlyFiveElements[branch] != stemInfo[hidden].Element {
				score := 6.0
				if i == 1 {
					score *= 1.5
				}
				scores[stemInfo[hidden].Element] += score
			}
		}
	}

	return scores
}

func getProducingElement(element string) string {
	relations := map[string]string{"木": "水", "火": "木", "土": "火", "金": "土", "水": "金"}
	return relations[element]
}

func getDrainingElement(element string) string {
	relations := map[string]string{"木": "火", "火": "土", "土": "金", "金": "水", "水": "木"}
	return relations[element]
}

func getControllingElement(element string) string {
	relations := map[string]string{"木": "金", "火": "水", "土": "木", "金": "火", "水": "土"}
	return relations[element]
}

func getControlledElement(element string) string {
	relations := map[string]string{"木": "土", "火": "金", "土": "水", "金": "木", "水": "火"}
	return relations[element]
}

func GetDayBranchElement(baziInfo *Bazi) string {
	dayBranch, _ := utf8.DecodeLastRuneInString(baziInfo.DayPillar)
	return earthlyFiveElements[dayBranch]
}

func AnalyzeFiveElements(bazi *Bazi) *FiveElementAnalysis {
	heavenlyFiveElements := map[rune]string{
		'甲': "木", '乙': "木",
		'丙': "火", '丁': "火",
		'戊': "土", '己': "土",
		'庚': "金", '辛': "金",
		'壬': "水", '癸': "水",
	}

	elementCount := map[string]int{
		"木": 0, "火": 0, "土": 0, "金": 0, "水": 0,
	}

	pillars := []string{bazi.YearPillar, bazi.MonthPillar, bazi.DayPillar, bazi.HourPillar}
	for _, pillar := range pillars {
		pillarRunes := []rune(pillar)
		if len(pillarRunes) == 2 {
			stem := pillarRunes[0]
			branch := pillarRunes[1]
			if element, ok := heavenlyFiveElements[stem]; ok {
				elementCount[element]++
			}
			if element, ok := earthlyFiveElements[branch]; ok {
				elementCount[element]++
			}
		}
	}

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

	return &FiveElementAnalysis{
		Wood:  getStrength(elementCount["木"]),
		Fire:  getStrength(elementCount["火"]),
		Earth: getStrength(elementCount["土"]),
		Metal: getStrength(elementCount["金"]),
		Water: getStrength(elementCount["水"]),
	}
}
