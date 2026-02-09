package com.index.mapper;

import com.index.entity.IndexData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndexDataMapper {

    /**
     * 查询所有指标数据
     */
    List<IndexData> findAll();

    /**
     * 根据ID查询指标数据
     */
    IndexData findById(@Param("id") Long id);

    /**
     * 根据期间查询指标数据
     */
    List<IndexData> findByPeriod(@Param("period") String period);

    /**
     * 根据配置ID和期间查询指标数据
     */
    IndexData findByConfigIdAndPeriod(@Param("configId") Long configId, @Param("period") String period);

    /**
     * 根据配置ID列表和期间查询指标数据
     */
    List<IndexData> findByConfigIdInAndPeriod(@Param("configIds") List<Long> configIds, @Param("period") String period);

    /**
     * 查询所有期间
     */
    List<String> findAllPeriods();

    /**
     * 根据期间删除指标数据
     */
    void deleteByPeriod(@Param("period") String period);

    /**
     * 保存指标数据
     */
    void save(IndexData data);

    /**
     * 批量保存指标数据
     */
    void saveBatch(List<IndexData> dataList);
}