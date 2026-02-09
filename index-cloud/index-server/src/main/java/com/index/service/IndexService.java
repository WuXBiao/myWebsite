package com.index.service;

import com.index.entity.IndexConfig;
import com.index.entity.IndexData;
import com.index.vo.IndexTreeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 指标服务接口
 */
public interface IndexService {

    // ==================== 配置相关 ====================

    /**
     * 获取所有配置
     */
    List<IndexConfig> getAllConfigs();

    /**
     * 获取配置详情
     */
    IndexConfig getConfigById(Long id);

    /**
     * 保存配置
     */
    IndexConfig saveConfig(IndexConfig config);

    /**
     * 批量保存配置
     */
    List<IndexConfig> saveAllConfigs(List<IndexConfig> configs);

    /**
     * 删除配置
     */
    void deleteConfig(Long id);

    /**
     * 获取所有分类
     */
    List<String> getAllCategories();

    // ==================== 数据相关 ====================

    /**
     * 获取指定期间的所有数据（带配置信息）
     */
    List<IndexData> getDataWithConfig(String period);

    /**
     * 分页查询数据
     */
    Page<IndexData> getDataPage(String period, Pageable pageable);

    /**
     * 保存数据
     */
    IndexData saveData(IndexData data);

    /**
     * 批量保存数据
     */
    List<IndexData> saveAllData(List<IndexData> dataList);

    /**
     * 删除指定期间的数据
     */
    void deleteDataByPeriod(String period);

    /**
     * 获取所有期间
     */
    List<String> getAllPeriods();

    // ==================== 树形结构 ====================

    /**
     * 获取带数据的树形结构
     */
    List<IndexTreeVO> getIndexTree(String period);

    /**
     * 按分类获取树形结构
     */
    List<IndexTreeVO> getIndexTreeByCategory(String period, String category);
}
