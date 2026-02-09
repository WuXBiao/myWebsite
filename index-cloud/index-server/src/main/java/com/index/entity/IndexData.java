package com.index.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 指标数据实体类（动态数据）
 */
@Data
@Entity
@Table(name = "t_index_data")
public class IndexData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的配置ID
     */
    @Column(name = "config_id")
    private Long configId;

    /**
     * 数据期间（年月，如202507）
     */
    @Column(name = "period", length = 6)
    private String period;

    /**
     * 余额（万元）
     */
    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;

    /**
     * 较上月增量（万元）
     */
    @Column(name = "mom_increment", precision = 18, scale = 2)
    private BigDecimal momIncrement;

    /**
     * 较年初增量（万元）
     */
    @Column(name = "ytd_increment", precision = 18, scale = 2)
    private BigDecimal ytdIncrement;

    /**
     * 较年初增速（%）
     */
    @Column(name = "ytd_growth_rate", precision = 10, scale = 4)
    private BigDecimal ytdGrowthRate;

    /**
     * 余额占各项贷款比重（%）
     */
    @Column(name = "deposit_ratio", precision = 10, scale = 4)
    private BigDecimal depositRatio;

    /**
     * 占比较年初（%）
     */
    @Column(name = "ratio_vs_ytd", precision = 10, scale = 4)
    private BigDecimal ratioVsYtd;

    /**
     * 年日均（万元）
     */
    @Column(name = "yearly_avg", precision = 18, scale = 2)
    private BigDecimal yearlyAvg;

    /**
     * 利息收入（万元）
     */
    @Column(name = "interest_income", precision = 18, scale = 2)
    private BigDecimal interestIncome;

    /**
     * 利息支出（万元）
     */
    @Column(name = "interest_expense", precision = 18, scale = 2)
    private BigDecimal interestExpense;

    /**
     * 收益率/付息率（%）
     */
    @Column(name = "interest_rate", precision = 10, scale = 4)
    private BigDecimal interestRate;

    /**
     * 收益率较上月（%）
     */
    @Column(name = "rate_vs_mom", precision = 10, scale = 4)
    private BigDecimal rateVsMom;

    /**
     * 收益率较年初（%）
     */
    @Column(name = "rate_vs_yoy", precision = 10, scale = 4)
    private BigDecimal rateVsYoy;

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

    /**
     * 关联的配置对象（非数据库字段）
     */
    @Transient
    private IndexConfig config;

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
