package bazi

import (
	"strings"
	"time"
)

var ( // 农历信息
	tianGan       = [10]string{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"}
	diZhi         = [12]string{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"}
	chineseZodiac = [12]string{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"}
	chineseMonth  = [12]string{"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"}
	chineseDay    = [30]string{"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"}
	solarTerm     = [24]string{"小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"}
	lunarInfo     = [151]int{0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0}
	solarTermInfo = [151]string{"15:14", "12:10", "21:04", "19:18", "06:10", "00:22", "05:27", "21:08", "16:11", "11:15", "07:53", "01:48", "12:21", "06:21", "22:01", "18:31", "13:21", "07:01", "22:31", "13:51", "08:02", "05:32", "00:22", "17:51", "15:14", "12:10", "21:04", "19:18", "06:10", "00:22", "05:27", "21:08", "16:11", "11:15", "07:53", "01:48", "12:21", "06:21", "22:01", "18:31", "13:21", "07:01", "22:31", "13:51", "08:02", "05:32", "00:22", "17:51", "15:14", "12:10", "21:04", "19:18", "06:10", "00:22", "05:27", "21:08", "16:11", "11:15", "07:53", "01:48", "12:21", "06:21", "22:01", "18:31", "13:21", "07:01", "22:31", "13:51", "08:02", "05:32", "00:22", "17:51", "15:14", "12:10", "21:04", "19:18", "06:10", "00:22", "05:27", "21:08", "16:11", "11:15", "07:53", "01:48", "12:21", "06:21", "22:01", "18:31", "13:21", "07:01", "22:31", "13:51", "08:02", "05:32", "00:22", "17:51", "15:14", "12:10", "21:04", "19:18", "06:10", "00:22", "05:27", "21:08", "16:11", "11:15", "07:53", "01:48", "12:21", "06:21", "22:01", "18:31", "13:21", "07:01", "22:31", "13:51", "08:02", "05:32", "00:22", "17:51", "15:14", "12:10", "21:04", "19:18", "06:10", "00:22", "05:27", "21:08", "16:11", "11:15", "07:53", "01:48", "12:21", "06:21", "22:01", "18:31", "13:21", "07:01", "22:31", "13:51", "08:02", "05:32", "00:22", "17:51", "15:14"}
)

type Lunar struct {
	Year            int
	Month           int
	Day             int
	Hour            int
	Minute          int
	Second          int
	IsLeap          bool
	YearGan         string
	YearZhi         string
	MonthGan        string
	MonthZhi        string
	DayGan          string
	DayZhi          string
	HourGan         string
	HourZhi         string
	ChineseZodiac   string
	SolarTerm       string
	ChineseMonth    string
	ChineseDay      string
	Constellation   string
}

// SolarToLunarStr 阳历转农历，传入如 "2020-03-16 14:20:30" 格式的字符串
func SolarToLunarStr(solarDate string) Lunar {
	loc, _ := time.LoadLocation("Asia/Shanghai")
	t, _ := time.ParseInLocation("2006-01-02 15:04:05", solarDate, loc)
	return SolarToLunar(t)
}

// SolarToLunar 阳历转农历
func SolarToLunar(solarDate time.Time) Lunar {
	if solarDate.Year() < 1900 || solarDate.Year() > 2100 {
		return Lunar{}
	}

	var lunar Lunar
	lunar.Year = solarDate.Year()
	lunar.Month = int(solarDate.Month())
	lunar.Day = solarDate.Day()
	lunar.Hour = solarDate.Hour()
	lunar.Minute = solarDate.Minute()
	lunar.Second = solarDate.Second()

	// 获取农历年份
	getLunarYear(&lunar)
	// 获取农历月份
	getLunarMonth(&lunar)
	// 获取农历日
	getLunarDay(&lunar)
	// 获取星座
	getConstellation(&lunar)
	// 获取节气
	getSolarTerm(&lunar)

	return lunar
}

// 获取农历年份
func getLunarYear(lunar *Lunar) {
	year := lunar.Year
	month := lunar.Month
	day := lunar.Day

	if year == 1900 && month == 1 && day < 31 {
		lunar.YearGan = "己"
		lunar.YearZhi = "亥"
		lunar.ChineseZodiac = "猪"
		return
	}

	// 1900年1月31日是农历春节
	loc, _ := time.LoadLocation("Asia/Shanghai")
	baseDate, _ := time.ParseInLocation("2006-01-02", "1900-01-31", loc)
	targetDate, _ := time.ParseInLocation("2006-01-02", time.Date(year, time.Month(month), day, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)
	days := int(targetDate.Sub(baseDate).Hours() / 24)

	// 农历年份
	lunarYear := 1900
	var daysInYear int
	for lunarYear < 2101 && days > 0 {
		daysInYear = getLunarYearDays(lunarYear)
		days -= daysInYear
		lunarYear++
	}

	if days < 0 {
		days += daysInYear
		lunarYear--
	}

	lunar.Year = lunarYear
	lunar.YearGan = tianGan[(lunar.Year-4)%10]
	lunar.YearZhi = diZhi[(lunar.Year-4)%12]
	lunar.ChineseZodiac = chineseZodiac[(lunar.Year-4)%12]

	// 获取时辰
	lunar.HourZhi = diZhi[(lunar.Hour+1)%12]

	// 计算天干
	dayGan := getGan(lunar.Year, lunar.Month, lunar.Day)
	lunar.HourGan = tianGan[(dayGan*2+lunar.Hour/2)%10]
}

// 获取农历月份
func getLunarMonth(lunar *Lunar) {
	year := lunar.Year
	month := lunar.Month
	day := lunar.Day

	loc, _ := time.LoadLocation("Asia/Shanghai")
	baseDate, _ := time.ParseInLocation("2006-01-02", time.Date(year, 1, 1, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)
	targetDate, _ := time.ParseInLocation("2006-01-02", time.Date(year, time.Month(month), day, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)
	days := int(targetDate.Sub(baseDate).Hours() / 24)

	leapMonth := getLeapMonth(year)
	lunar.IsLeap = false

	var i int
	for i = 1; i < 13 && days > 0; i++ {
		monthDays := getMonthDays(year, i)
		if lunar.IsLeap == true {
			i--
			lunar.IsLeap = false
		}

		if leapMonth > 0 && i == (leapMonth+1) && lunar.IsLeap == false {
			lunar.IsLeap = true
			i--
			monthDays = getLeapMonthDays(year)
		} else {
			lunar.IsLeap = false
		}

		days -= monthDays
	}

	if days == 0 && leapMonth > 0 && i == leapMonth+1 {
		if lunar.IsLeap {
			lunar.IsLeap = false
		} else {
			lunar.IsLeap = true
			i--
		}
	}

	if days < 0 {
		days += getMonthDays(year, i-1)
		i--
	}

	lunar.Month = i
	lunar.ChineseMonth = chineseMonth[i-1]

	// 月柱
	monthIndex := (year-1900)*12 + month + 12
	if day >= getSolarTermDay(year, (month-1)*2) {
		monthIndex++
	}
	lunar.MonthGan = tianGan[monthIndex%10]
	lunar.MonthZhi = diZhi[monthIndex%12]
}

// 获取农历日
func getLunarDay(lunar *Lunar) {
	year := lunar.Year
	month := lunar.Month
	day := lunar.Day

	loc, _ := time.LoadLocation("Asia/Shanghai")
	baseDate, _ := time.ParseInLocation("2006-01-02", "1900-01-01", loc)
	targetDate, _ := time.ParseInLocation("2006-01-02", time.Date(year, time.Month(month), day, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)
	offset := int(targetDate.Sub(baseDate).Hours() / 24)

	dayCyclical := offset + 40
	lunar.DayGan = tianGan[dayCyclical%10]
	lunar.DayZhi = diZhi[dayCyclical%12]

	loc, _ = time.LoadLocation("Asia/Shanghai")
	baseDate, _ = time.ParseInLocation("2006-01-02", time.Date(lunar.Year, time.Month(lunar.Month), 1, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)
	targetDate, _ = time.ParseInLocation("2006-01-02", time.Date(year, time.Month(month), day, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)
	days := int(targetDate.Sub(baseDate).Hours() / 24)
	lunar.Day = days + 1
	lunar.ChineseDay = chineseDay[days]
}

// 获取星座
func getConstellation(lunar *Lunar) {
	month := lunar.Month
	day := lunar.Day

	constellations := [12]string{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"}
	constellationSplit := [12]int{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22}

	index := month - 1
	if day < constellationSplit[index] {
		index = (index + 11) % 12
	}
	lunar.Constellation = constellations[index]
}

// 获取节气
func getSolarTerm(lunar *Lunar) {
	year := lunar.Year
	month := lunar.Month
	day := lunar.Day

	loc, _ := time.LoadLocation("Asia/Shanghai")
	baseDate, _ := time.ParseInLocation("2006-01-02", time.Date(year, time.Month(month), day, 0, 0, 0, 0, loc).Format("2006-01-02"), loc)

	var i int
	for i = 23; i >= 0; i-- {
		st := getSolarTermTime(year, i)
		if baseDate.Sub(st).Hours() >= 0 {
			lunar.SolarTerm = solarTerm[i]
			break
		}
	}
}

// 获取天干
func getGan(year, month, day int) int {
	gan := []int{8, 9, 0, 1, 2, 3, 4, 5, 6, 7}
	return gan[getGanZhi(year, month, day)%10]
}

// 获取干支
func getGanZhi(year, month, day int) int {
	if month < 3 {
		year--
		month += 12
	}
	c := year / 100
	y := year % 100
	return (y + y/4 + c/4 - 2*c + 2*month + 3*(month+1)/5 + day - 3) % 60
}

// 获取农历年份的总天数
func getLunarYearDays(year int) int {
	sum := 348
	for i := 0x8000; i > 0x8; i >>= 1 {
		if (lunarInfo[year-1900] & i) != 0 {
			sum++
		}
	}
	return sum + getLeapMonthDays(year)
}

// 获取农历闰月月份，0代表没有闰月
func getLeapMonth(year int) int {
	return lunarInfo[year-1900] & 0xf
}

// 获取农历闰月的天数
func getLeapMonthDays(year int) int {
	if getLeapMonth(year) != 0 {
		if (lunarInfo[year-1900] & 0x10000) != 0 {
			return 30
		} else {
			return 29
		}
	}
	return 0
}

// 获取农历月份的天数
func getMonthDays(year, month int) int {
	if (lunarInfo[year-1900] & (0x10000 >> uint(month))) != 0 {
		return 30
	}
	return 29
}

// 获取节气日
func getSolarTermDay(year, index int) int {
	st := getSolarTermTime(year, index)
	return st.Day()
}

// 获取节气时间
func getSolarTermTime(year, index int) time.Time {
	loc, _ := time.LoadLocation("Asia/Shanghai")
	st, _ := time.ParseInLocation("2006-01-02 15:04:05", "1900-01-06 02:05:00", loc)

	num := float64(31556925974.7*float64(year-1900)) + float64(strings.Split(solarTermInfo[year-1900], ":")[0])
	st = st.Add(time.Duration(num) * time.Millisecond)

	return st
}
