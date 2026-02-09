package com.index.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 指标树形结构 VO
 */
@Data
public class IndexTreeVO {

    private Long id;
    private Long parentId;
    private Integer level;
    private Integer sortOrder;
    private String category;
    private String subCategory;
    private String indexCode;
    private String indexName;
    private String rowType;
    private String sectionTitle;
    private String sectionNote;
    private BigDecimal balance;
    private BigDecimal momIncrement;
    private BigDecimal ytdIncrement;
    private BigDecimal ytdGrowthRate;
    private BigDecimal depositRatio;
    private BigDecimal ratioVsYtd;
    private BigDecimal yearlyAvg;
    private BigDecimal interestIncome;
    private BigDecimal interestExpense;
    private BigDecimal interestRate;
    private BigDecimal rateVsMom;
    private BigDecimal rateVsYoy;
    private String remark;
    private String period;

    /**
     * 子指标列表
     */
    private List<IndexTreeVO> children = new ArrayList<>();
}
