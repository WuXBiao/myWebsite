package com.index.service.impl;

import com.index.entity.IndexConfig;
import com.index.entity.IndexData;
import com.index.service.IndexService;
import com.index.service.MyBatisIndexService;
import com.index.vo.IndexTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 指标服务实现类（使用MyBatis）
 */
@Service
@Transactional
public class IndexServiceImpl implements IndexService {

    @Autowired
    private MyBatisIndexService myBatisIndexService;

    // ==================== 配置相关 ====================

    @Override
    public List<IndexConfig> getAllConfigs() {
        return myBatisIndexService.getAllConfigs();
    }

    @Override
    public IndexConfig getConfigById(Long id) {
        return myBatisIndexService.getConfigById(id);
    }

    @Override
    public IndexConfig saveConfig(IndexConfig config) {
        myBatisIndexService.saveConfig(config);
        return config;
    }

    @Override
    public List<IndexConfig> saveAllConfigs(List<IndexConfig> configs) {
        myBatisIndexService.saveAllConfigs(configs);
        return configs;
    }

    @Override
    public void deleteConfig(Long id) {
        myBatisIndexService.deleteConfig(id);
    }

    @Override
    public List<String> getAllCategories() {
        return myBatisIndexService.getAllCategories();
    }

    // ==================== 数据相关 ====================

    @Override
    public List<IndexData> getDataWithConfig(String period) {
        return myBatisIndexService.getDataWithConfig(period);
    }

    @Override
    public Page<IndexData> getDataPage(String period, Pageable pageable) {
        return myBatisIndexService.getDataPage(period, pageable);
    }

    @Override
    public IndexData saveData(IndexData data) {
        myBatisIndexService.saveData(data);
        return data;
    }

    @Override
    public List<IndexData> saveAllData(List<IndexData> dataList) {
        myBatisIndexService.saveAllData(dataList);
        return dataList;
    }

    @Override
    public void deleteDataByPeriod(String period) {
        myBatisIndexService.deleteDataByPeriod(period);
    }

    @Override
    public List<String> getAllPeriods() {
        return myBatisIndexService.getAllPeriods();
    }

    // ==================== 树形结构 ====================

    @Override
    public List<IndexTreeVO> getIndexTree(String period) {
        return myBatisIndexService.getIndexTree(period);
    }

    @Override
    public List<IndexTreeVO> getIndexTreeByCategory(String period, String category) {
        return myBatisIndexService.getIndexTreeByCategory(period, category);
    }
}
