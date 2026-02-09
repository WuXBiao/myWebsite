package com.index.service;

import com.index.entity.IndexConfig;
import com.index.entity.IndexData;
import com.index.vo.IndexTreeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 使用MyBatis的指标服务接口
 */
public interface MyBatisIndexService {

    // ==================== 配置相关 ====================

    /**
     * 查询所有指标配置
     */
    List<IndexConfig> getAllConfigs();

    /**
     * 根据ID查询指标配置
     */
    IndexConfig getConfigById(Long id);

    /**
     * 保存指标配置
     */
    IndexConfig saveConfig(IndexConfig config);

    /**
     * 批量保存指标配置
     */
    List<IndexConfig> saveAllConfigs(List<IndexConfig> configs);

    /**
     * 删除指标配置
     */
    void deleteConfig(Long id);

    /**
     * 查询所有分类
     */
    List<String> getAllCategories();

    // ==================== 数据相关 ====================

    /**
     * 查询指定期间的数据（带关联的配置信息）
     */
    List<IndexData> getDataWithConfig(String period);

    /**
     * 分页查询指定期间的数据
     */
    Page<IndexData> getDataPage(String period, Pageable pageable);

    /**
     * 保存指标数据
     */
    IndexData saveData(IndexData data);

    /**
     * 批量保存指标数据
     */
    List<IndexData> saveAllData(List<IndexData> dataList);

    /**
     * 根据期间删除指标数据
     */
    void deleteDataByPeriod(String period);

    /**
     * 查询所有期间
     */
    List<String> getAllPeriods();

    // ==================== 树形结构相关 ====================

    /**
     * 获取指标树形结构（带数据）
     */
    List<IndexTreeVO> getIndexTree(String period);

    /**
     * 根据分类获取指标树形结构（带数据）
     */
    List<IndexTreeVO> getIndexTreeByCategory(String period, String category);
}