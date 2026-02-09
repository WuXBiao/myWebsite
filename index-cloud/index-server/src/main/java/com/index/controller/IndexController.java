package com.index.controller;

import com.index.common.Result;
import com.index.entity.IndexConfig;
import com.index.entity.IndexData;
import com.index.service.ExportService;
import com.index.service.IndexService;
import com.index.service.MyBatisIndexService;
import com.index.vo.IndexTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 指标管理控制器
 */
@RestController
@RequestMapping("/api/index")
@CrossOrigin(origins = "*")
public class IndexController {

    @Autowired
    private MyBatisIndexService indexService;

    @Autowired
    private ExportService exportService;

    // ==================== 配置相关 ====================

    /**
     * 获取所有配置
     */
    @GetMapping("/config/list")
    public Result<List<IndexConfig>> getAllConfigs() {
        List<IndexConfig> list = indexService.getAllConfigs();
        return Result.success(list);
    }

    /**
     * 获取配置详情
     */
    @GetMapping("/config/{id}")
    public Result<IndexConfig> getConfigById(@PathVariable Long id) {
        IndexConfig config = indexService.getConfigById(id);
        if (config == null) {
            return Result.error(404, "配置不存在");
        }
        return Result.success(config);
    }

    /**
     * 保存配置
     */
    @PostMapping("/config")
    public Result<IndexConfig> saveConfig(@RequestBody IndexConfig config) {
        IndexConfig saved = indexService.saveConfig(config);
        return Result.success(saved);
    }

    /**
     * 批量保存配置
     */
    @PostMapping("/config/batch")
    public Result<List<IndexConfig>> saveAllConfigs(@RequestBody List<IndexConfig> configs) {
        List<IndexConfig> saved = indexService.saveAllConfigs(configs);
        return Result.success(saved);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/config/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        indexService.deleteConfig(id);
        return Result.success();
    }

    // ==================== 数据相关 ====================

    /**
     * 获取指标树形结构（带数据）
     */
    @GetMapping("/tree")
    public Result<List<IndexTreeVO>> getIndexTree(@RequestParam String period) {
        List<IndexTreeVO> tree = indexService.getIndexTree(period);
        return Result.success(tree);
    }

    /**
     * 按分类获取指标树形结构
     */
    @GetMapping("/tree/category")
    public Result<List<IndexTreeVO>> getIndexTreeByCategory(
            @RequestParam String period,
            @RequestParam String category) {
        List<IndexTreeVO> tree = indexService.getIndexTreeByCategory(period, category);
        return Result.success(tree);
    }

    /**
     * 获取指定期间的数据列表
     */
    @GetMapping("/data/list")
    public Result<List<IndexData>> getDataList(@RequestParam String period) {
        List<IndexData> list = indexService.getDataWithConfig(period);
        return Result.success(list);
    }

    /**
     * 分页查询数据
     */
    @GetMapping("/data/page")
    public Result<Page<IndexData>> getDataPage(
            @RequestParam String period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IndexData> result = indexService.getDataPage(period, pageable);
        return Result.success(result);
    }

    /**
     * 保存数据
     */
    @PostMapping("/data")
    public Result<IndexData> saveData(@RequestBody IndexData data) {
        IndexData saved = indexService.saveData(data);
        return Result.success(saved);
    }

    /**
     * 批量保存数据
     */
    @PostMapping("/data/batch")
    public Result<List<IndexData>> saveAllData(@RequestBody List<IndexData> dataList) {
        List<IndexData> saved = indexService.saveAllData(dataList);
        return Result.success(saved);
    }

    /**
     * 删除指定期间的数据
     */
    @DeleteMapping("/data/period/{period}")
    public Result<Void> deleteDataByPeriod(@PathVariable String period) {
        indexService.deleteDataByPeriod(period);
        return Result.success();
    }

    // ==================== 通用 ====================

    /**
     * 获取所有期间
     */
    @GetMapping("/periods")
    public Result<List<String>> getAllPeriods() {
        List<String> periods = indexService.getAllPeriods();
        return Result.success(periods);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        List<String> categories = indexService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 导出 Excel
     */
    @GetMapping("/export")
    public void exportExcel(
            @RequestParam String period,
            @RequestParam(required = false) String category,
            HttpServletResponse response) {
        OutputStream out = null;
        try {
            List<IndexData> data = indexService.getDataWithConfig(period);
            
            // 按分类过滤
            if (category != null && !category.isEmpty()) {
                data.removeIf(d -> d.getConfig() == null || !category.equals(d.getConfig().getCategory()));
            }

            byte[] excelData = exportService.exportToExcel(data, period);

            String filename = "大类资产细项结构表_" + period + ".xlsx";
            String encodedFilename = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
            response.setContentLength(excelData.length);

            out = response.getOutputStream();
            out.write(excelData);
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
