package com.index.service.impl;

import com.index.entity.IndexConfig;
import com.index.entity.IndexData;
import com.index.mapper.IndexConfigMapper;
import com.index.mapper.IndexDataMapper;
import com.index.service.MyBatisIndexService;
import com.index.vo.IndexTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * 使用MyBatis的指标服务实现类
 */
@Service
@Transactional
public class MyBatisIndexServiceImpl implements MyBatisIndexService {

    @Autowired
    private IndexConfigMapper configMapper;

    @Autowired
    private IndexDataMapper dataMapper;

    // ==================== 配置相关 ====================

    @Override
    public List<IndexConfig> getAllConfigs() {
        return configMapper.findAll();
    }

    @Override
    public IndexConfig getConfigById(Long id) {
        return configMapper.findById(id);
    }

    @Override
    public IndexConfig saveConfig(IndexConfig config) {
        if (config.getId() != null) {
            // 更新操作
            configMapper.save(config);
        } else {
            // 新增操作
            configMapper.save(config);
        }
        return config;
    }

    @Override
    public List<IndexConfig> saveAllConfigs(List<IndexConfig> configs) {
        configMapper.saveBatch(configs);
        return configs;
    }

    @Override
    public void deleteConfig(Long id) {
        configMapper.deleteById(id);
    }

    @Override
    public List<String> getAllCategories() {
        return configMapper.findAllCategories();
    }

    // ==================== 数据相关 ====================

    @Override
    public List<IndexData> getDataWithConfig(String period) {
        // 使用MyBatis的一对一关联查询，直接返回带配置信息的数据
        return dataMapper.findByPeriod(period);
    }

    @Override
    public Page<IndexData> getDataPage(String period, Pageable pageable) {
        // 这里暂时使用简单实现，实际MyBatis分页需要额外配置
        List<IndexData> allData = dataMapper.findByPeriod(period);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allData.size());
        List<IndexData> pageContent = new ArrayList<>();
        if (start < allData.size()) {
            pageContent = allData.subList(start, end);
        }
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allData.size());
    }

    @Override
    public IndexData saveData(IndexData data) {
        if (data.getId() != null) {
            // 更新操作
            dataMapper.save(data);
        } else {
            // 新增操作
            dataMapper.save(data);
        }
        return data;
    }

    @Override
    public List<IndexData> saveAllData(List<IndexData> dataList) {
        dataMapper.saveBatch(dataList);
        return dataList;
    }

    @Override
    public void deleteDataByPeriod(String period) {
        dataMapper.deleteByPeriod(period);
    }

    @Override
    public List<String> getAllPeriods() {
        return dataMapper.findAllPeriods();
    }

    // ==================== 树形结构相关 ====================

    @Override
    public List<IndexTreeVO> getIndexTree(String period) {
        List<IndexConfig> configs = configMapper.findAll();
        List<IndexData> dataList = dataMapper.findByPeriod(period);
        
        // 构建数据Map (configId -> data)
        Map<Long, IndexData> dataMap = dataList.stream()
                .collect(Collectors.toMap(IndexData::getConfigId, d -> d, (a, b) -> a));
        
        // 构建树
        return buildTree(configs, dataMap);
    }

    @Override
    public List<IndexTreeVO> getIndexTreeByCategory(String period, String category) {
        List<IndexConfig> configs = configMapper.findByCategory(category);
        List<Long> configIds = configs.stream().map(IndexConfig::getId).collect(Collectors.toList());
        List<IndexData> dataList = dataMapper.findByConfigIdInAndPeriod(configIds, period);
        
        Map<Long, IndexData> dataMap = dataList.stream()
                .collect(Collectors.toMap(IndexData::getConfigId, d -> d, (a, b) -> a));
        
        return buildTree(configs, dataMap);
    }

    /**
     * 构建树形结构
     */
    private List<IndexTreeVO> buildTree(List<IndexConfig> configs, Map<Long, IndexData> dataMap) {
        // 按 parentId 分组
        Map<Long, List<IndexConfig>> childMap = configs.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(IndexConfig::getParentId));
        
        // 获取顶级节点
        List<IndexConfig> roots = configs.stream()
                .filter(c -> c.getParentId() == null)
                .sorted(Comparator.comparingInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0))
                .collect(Collectors.toList());
        
        // 递归构建
        return roots.stream()
                .map(config -> buildTreeNode(config, childMap, dataMap))
                .collect(Collectors.toList());
    }

    /**
     * 递归构建树节点
     */
    private IndexTreeVO buildTreeNode(IndexConfig config, Map<Long, List<IndexConfig>> childMap, 
                                      Map<Long, IndexData> dataMap) {
        IndexTreeVO vo = new IndexTreeVO();
        vo.setId(config.getId());
        vo.setParentId(config.getParentId());
        vo.setLevel(config.getLevel());
        vo.setCategory(config.getCategory());
        vo.setSubCategory(config.getSubCategory());
        vo.setIndexCode(config.getIndexCode());
        vo.setIndexName(config.getIndexName());
        vo.setRowType(config.getRowType());
        vo.setSectionTitle(config.getSectionTitle());
        vo.setSectionNote(config.getSectionNote());
        vo.setRemark(config.getRemark());
        
        // 填充数据
        IndexData data = dataMap.get(config.getId());
        if (data != null) {
            vo.setBalance(data.getBalance());
            vo.setMomIncrement(data.getMomIncrement());
            vo.setYtdIncrement(data.getYtdIncrement());
            vo.setYtdGrowthRate(data.getYtdGrowthRate());
            vo.setDepositRatio(data.getDepositRatio());
            vo.setRatioVsYtd(data.getRatioVsYtd());
            vo.setYearlyAvg(data.getYearlyAvg());
            vo.setInterestIncome(data.getInterestIncome());
            vo.setInterestExpense(data.getInterestExpense());
            vo.setInterestRate(data.getInterestRate());
            vo.setRateVsMom(data.getRateVsMom());
            vo.setRateVsYoy(data.getRateVsYoy());
        }
        
        // 递归处理子节点
        List<IndexConfig> children = childMap.get(config.getId());
        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparingInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0));
            List<IndexTreeVO> childVOs = children.stream()
                    .map(child -> buildTreeNode(child, childMap, dataMap))
                    .collect(Collectors.toList());
            vo.setChildren(childVOs);
        }
        
        return vo;
    }
}
