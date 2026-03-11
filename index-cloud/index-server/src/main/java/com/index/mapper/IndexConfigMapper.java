package com.index.mapper;

import com.index.entity.IndexConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndexConfigMapper {

    /**
     * 查询所有指标配置
     */
    List<IndexConfig> findAll();

    /**
     * 根据ID查询指标配置
     */
    IndexConfig findById(@Param("id") Long id);

    /**
     * 根据父ID查询指标配置
     */
    List<IndexConfig> findByParentId(@Param("parentId") Long parentId);

    /**
     * 根据合并项查询指标配置
     */
    List<IndexConfig> findByMergeItem(@Param("mergeItem") String mergeItem);

    /**
     * 查询所有合并项
     */
    List<String> findAllMergeItems();

    /**
     * 根据指标编码查询
     */
    IndexConfig findByIndexCode(@Param("indexCode") String indexCode);

    /**
     * 保存指标配置
     */
    void save(IndexConfig config);

    /**
     * 批量保存指标配置
     */
    void saveBatch(List<IndexConfig> configs);

    /**
     * 删除指标配置
     */
    void deleteById(@Param("id") Long id);
}