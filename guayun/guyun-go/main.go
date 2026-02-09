package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/WuXBiao/guayun-go/bazi"
	"github.com/WuXBiao/guayun-go/fortune"
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

	var req struct {
		Year   int    `json:"year"`
		Month  int    `json:"month"`
		Day    int    `json:"day"`
		Hour   int    `json:"hour"`
		Minute int    `json:"minute"`
		Gender string `json:"gender"`
	}

	body, err := io.ReadAll(r.Body)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"error": "Failed to read request body"})
		return
	}
	defer r.Body.Close()

	if err := json.Unmarshal(body, &req); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"error": "Invalid JSON format"})
		return
	}

	// Use UTC for calculation consistency, or ensure server's time.Local is correctly configured.
	location, _ := time.LoadLocation("Asia/Shanghai")
	birthTime := time.Date(req.Year, time.Month(req.Month), req.Day, req.Hour, req.Minute, 0, 0, location)

	baziInfo := bazi.NewBazi(birthTime)
	fortuneResult := fortune.PredictFortune(baziInfo, req.Gender)

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]interface{}{
		"code":    0,
		"message": "success",
		"data":    fortuneResult,
	})
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
