package com.index.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 指标配置实体类（静态配置信息）
 */
@Data
@Entity
@Table(name = "t_index_config")
public class IndexConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 父级指标ID，为空表示顶级
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 层级（0=分类标题行，1=一级指标，2=二级指标...）
     */
    @Column(name = "level")
    private Integer level;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 分类
     */
    @Column(name = "category", length = 100)
    private String category;

    /**
     * 子分类
     */
    @Column(name = "sub_category", length = 100)
    private String subCategory;

    /**
     * 指标编号
     */
    @Column(name = "index_code", length = 50)
    private String indexCode;

    /**
     * 指标名称
     */
    @Column(name = "index_name", length = 200)
    private String indexName;

    /**
     * 行类型：DATA=数据行，SECTION=横向分类标题行
     */
    @Column(name = "row_type", length = 20)
    private String rowType;

    /**
     * 横向分类标题
     */
    @Column(name = "section_title", length = 200)
    private String sectionTitle;

    /**
     * 横向分类说明
     */
    @Column(name = "section_note", length = 500)
    private String sectionNote;

    /**
     * 备注/取数路径
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
