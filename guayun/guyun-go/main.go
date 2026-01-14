package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/zeromicro/go-zero/core/conf"
	"github.com/zeromicro/go-zero/rest"
)

type Config struct {
	rest.RestConf
}

func main() {
	var c Config
	conf.MustLoad("config.yaml", &c)

	server := rest.MustNewServer(c.RestConf)
	defer server.Stop()

	// 注册路由
	registerRoutes(server)

	fmt.Printf("Starting server at %s:%d...\n", c.Host, c.Port)
	server.Start()
}

func registerRoutes(server *rest.Server) {
	// 健康检查
	server.AddRoute(rest.Route{
		Method:  http.MethodGet,
		Path:    "/api/health",
		Handler: healthHandler,
	})

	// 八字推测
	server.AddRoute(rest.Route{
		Method:  http.MethodPost,
		Path:    "/api/fortune/predict",
		Handler: predictFortuneHandler,
	})

	// 获取用户历史记录
	server.AddRoute(rest.Route{
		Method:  http.MethodGet,
		Path:    "/api/fortune/history/:userId",
		Handler: getHistoryHandler,
	})

	// 获取运势详情
	server.AddRoute(rest.Route{
		Method:  http.MethodGet,
		Path:    "/api/fortune/detail/:recordId",
		Handler: getDetailHandler,
	})
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	fmt.Fprintf(w, `{"status":"ok","timestamp":"%s"}`, time.Now().Format(time.RFC3339))
}

func predictFortuneHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")

	if r.Method == http.MethodOptions {
		w.WriteHeader(http.StatusOK)
		return
	}

	// 解析请求
	var req struct {
		Year   int    `json:"year"`
		Month  int    `json:"month"`
		Day    int    `json:"day"`
		Hour   int    `json:"hour"`
		Minute int    `json:"minute"`
		Gender string `json:"gender"`
	}

	// 解析 JSON 请求体
	body, err := io.ReadAll(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, `{"error":"Failed to read request body"}`)
		return
	}
	defer r.Body.Close()

	if err := json.Unmarshal(body, &req); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		fmt.Fprintf(w, `{"error":"Invalid JSON format"}`)
		return
	}

	// 计算八字
	baziInfo := calculateBazi(req.Year, req.Month, req.Day, req.Hour, req.Minute)

	// 推测运势
	fortune := predictFortune(baziInfo, req.Gender)

	w.WriteHeader(http.StatusOK)
	fmt.Fprintf(w, `{
		"code": 0,
		"message": "success",
		"data": {
			"bazi": %s,
			"fortune": %s
		}
	}`, baziInfo, fortune)
}

func getHistoryHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")

	// 这里应该从数据库获取历史记录
	w.WriteHeader(http.StatusOK)
	fmt.Fprintf(w, `{"code":0,"message":"success","data":[]}`)
}

func getDetailHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")

	// 这里应该从数据库获取详细信息
	w.WriteHeader(http.StatusOK)
	fmt.Fprintf(w, `{"code":0,"message":"success","data":{}}`)
}

// 计算八字信息
func calculateBazi(year, month, day, hour, minute int) string {
	// 简化的八字计算逻辑
	// 实际应用中需要更复杂的农历转换和天干地支计算

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

// 推测运势
func predictFortune(baziInfo, gender string) string {
	// 简化的运势推测逻辑
	return fmt.Sprintf(`{
		"shortTerm": {
			"period": "近三个月",
			"overall": "平稳上升",
			"career": "工作顺利，有晋升机会",
			"love": "感情稳定，有惊喜",
			"health": "身体健康，需注意休息",
			"wealth": "财运不错，适合投资",
			"score": 75
		},
		"longTerm": {
			"period": "今年全年",
			"overall": "运势良好",
			"career": "事业发展稳定，有突破机会",
			"love": "感情运势上升，有新机遇",
			"health": "整体健康，需防秋冬季疾病",
			"wealth": "财运稳定增长",
			"score": 72
		},
		"fiveElements": {
			"wood": "旺",
			"fire": "中",
			"earth": "中",
			"metal": "弱",
			"water": "旺"
		},
		"luckyColor": ["红", "绿"],
		"luckyNumber": [5, 8],
		"luckyDirection": "东南"
	}`)
}
