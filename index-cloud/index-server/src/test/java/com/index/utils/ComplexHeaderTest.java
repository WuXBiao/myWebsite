package com.index.utils;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 复杂表头测试示例
 */
public class ComplexHeaderTest {
    
    @Test
    public void testSimpleHeader() throws IOException {
        // 示例 1：简单单行表头
        
        // 表头只有一行
        List<List<String>> headers = Arrays.asList(
            Arrays.asList("用户ID", "用户名", "邮箱", "手机号", "注册时间")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList(1, "张三", "zhangsan@example.com", "13800138000", "2024-01-01"),
            Arrays.asList(2, "李四", "lisi@example.com", "13900139000", "2024-01-02"),
            Arrays.asList(3, "王五", "wangwu@example.com", "13600136000", "2024-01-03")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "用户信息表",
            headers,
            data,
            new int[]{15, 20, 30, 20, 20}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/simple_header_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("简单表头 Excel 已生成");
        }
    }
    
    @Test
    public void testTwoLevelHeader() throws IOException {
        // 示例 2：双层表头（一级分类 + 二级明细）
        
        /*
         * 效果：
         * ┌─────────────────────────────────────────────────────┐
         * │              销售统计表                              │
         * ├──────────────┬──────────────┬───────────────────────┤
         * │   第一季度    │   第二季度    │      第三季度          │
         * ├──────┬───────┼──────┬───────┼───────┬───────────────┤
         * │ 销售额│销售量 │ 销售额│销售量  │ 销售额  │ 销售量       │
         * ├──────┼───────┼──────┼───────┼───────┼───────────────┤
         * │ 100万 │ 500台 │ 150万 │ 700台  │ 200万  │ 1000 台      │
         * └──────┴───────┴──────┴───────┴───────┴───────────────┘
         */
        
        List<List<String>> complexHeaders = Arrays.asList(
            // 第一行表头：季度分类
            Arrays.asList("第一季度", "第一季度", "第二季度", "第二季度", "第三季度", "第三季度"),
            // 第二行表头：具体指标
            Arrays.asList("销售额", "销售量", "销售额", "销售量", "销售额", "销售量")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("100 万", "500 台", "150 万", "700 台", "200 万", "1000 台"),
            Arrays.asList("120 万", "600 台", "180 万", "850 台", "220 万", "1100 台"),
            Arrays.asList("140 万", "700 台", "200 万", "900 台", "240 万", "1200 台")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "销售统计表",
            complexHeaders,
            data,
            new int[]{15, 15, 15, 15, 15, 15}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/two_level_header_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("双层表头 Excel 已生成");
        }
    }
    
    @Test
    public void testMergedComplexHeader() throws IOException {
        // 示例 3：复杂合并表头（跨列 + 跨行）
        
        /*
         * 效果：
         * ┌─────────────────────────────────────────────────────────────┐
         * │                    学生成绩表                                │
         * ├──────────┬───────────────────────────────────────────────────┤
         * │ 学号     │                  成绩信息                          │
         * │          ├──────────────┬──────────────┬─────────────────────┤
         * │          │   语文       │    数学      │      英语            │
         * │          ├──────┬───────┼──────┬───────┼───────┬─────────────┤
         * │          │ 平时 │ 期末  │ 平时 │ 期末  │ 平时  │ 期末         │
         * ├──────────┼──────┼───────┼──────┼───────┼───────┼─────────────┤
         * │ 001      │ 80   │ 85    │ 90   │ 95    │ 85    │ 90          │
         * └──────────┴──────┴───────┴──────┴───────┴───────┴─────────────┘
         */
        
        List<List<String>> complexHeaders = Arrays.asList(
            // 第 1 行：大分类
            Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息", "成绩信息", "成绩信息"),
            // 第 2 行：科目分类（使用相同值实现自动合并）
            Arrays.asList("学号", "语文", "语文", "数学", "数学", "英语", "英语"),
            // 第 3 行：具体指标（使用相同值实现自动合并）
            Arrays.asList("学号", "平时", "期末", "平时", "期末", "平时", "期末")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("001", "80", "85", "90", "95", "85", "90"),
            Arrays.asList("002", "82", "87", "92", "96", "87", "91"),
            Arrays.asList("003", "78", "83", "88", "93", "83", "88")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "学生成绩表",
            complexHeaders,
            data,
            new int[]{15, 12, 12, 12, 12, 12, 12}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/merged_complex_header_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("复杂合并表头 Excel 已生成");
        }
    }
    
    @Test
    public void testMixedHeaders() throws IOException {
        // 示例 4：多个报表，每个都有不同的复杂表头
        
        // 报表 1：简单表头
        List<List<String>> headers1 = Arrays.asList(
            Arrays.asList("产品编码", "产品名称", "单价", "库存量")
        );
        List<List<Object>> data1 = Arrays.asList(
            Arrays.asList("P001", "iPhone 15", 7999.00, 100),
            Arrays.asList("P002", "MacBook Pro", 14999.00, 50)
        );
        
        // 报表 2：双层表头
        List<List<String>> headers2 = Arrays.asList(
            // 第一行
            Arrays.asList("仓库信息", "仓库信息", "库存统计", "库存统计"),
            // 第二行
            Arrays.asList("仓库名称", "仓库位置", "商品数量", "总价值")
        );
        List<List<Object>> data2 = Arrays.asList(
            Arrays.asList("北京仓", "北京市朝阳区", 500, 5000000.00),
            Arrays.asList("上海仓", "上海市浦东新区", 600, 6000000.00)
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "产品信息表",
            headers1,
            data1,
            new int[]{15, 25, 15, 15}
        ));
        
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "仓库统计表",
            headers2,
            data2,
            new int[]{20, 25, 15, 20}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/mixed_headers_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("混合表头 Excel 已生成");
        }
    }
    
    @Test
    public void testWithGroupsAndComplexHeader() throws IOException {
        // 示例 5：复杂表头 + 业务分组
        
        List<List<String>> complexHeaders = Arrays.asList(
            Arrays.asList("部门", "员工信息", "员工信息", "员工信息", "薪资统计", "薪资统计"),
            Arrays.asList("部门", "姓名", "职位", "入职日期", "基本工资", "绩效奖金")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("技术部", "张三", "工程师", "2020-01-01", "15000", "5000"),
            Arrays.asList("技术部", "李四", "高级工程师", "2019-06-15", "20000", "8000"),
            Arrays.asList("销售部", "王五", "销售经理", "2018-03-20", "18000", "10000"),
            Arrays.asList("销售部", "赵六", "销售代表", "2021-07-10", "12000", "6000")
        );
        
        // 添加业务分组
        List<MultiReportExcelUtil.GroupInfo> groups = new ArrayList<>();
        groups.add(new MultiReportExcelUtil.GroupInfo("技术部", 0, 1));
        groups.add(new MultiReportExcelUtil.GroupInfo("销售部", 2, 3));
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "员工薪资统计表",
            complexHeaders,
            data,
            new int[]{15, 15, 20, 20, 15, 15},
            groups
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/groups_with_complex_header_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("复杂表头 + 业务分组 Excel 已生成");
        }
    }
    
    @Test
    public void testIrregularHeader() throws IOException {
        // 示例 6：不规则表头（每行列数不同）
        
        List<List<String>> complexHeaders = Arrays.asList(
            // 第 1 行：只有两个大分类
            Arrays.asList("基本信息", "联系信息"),
            // 第 2 行：详细信息（列数比第一行多）
            Arrays.asList("姓名", "年龄", "性别", "城市", "电话", "邮箱", "地址")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("张三", "25", "男", "北京", "13800138000", "zhangsan@example.com", "北京市朝阳区"),
            Arrays.asList("李四", "28", "女", "上海", "13900139000", "lisi@example.com", "上海市浦东新区")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "客户信息表",
            complexHeaders,
            data
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/irregular_header_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("不规则表头 Excel 已生成");
        }
    }
}