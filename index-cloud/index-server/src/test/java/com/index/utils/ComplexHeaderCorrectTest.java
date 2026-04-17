package com.index.utils;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 复杂表头测试 - 正确示范版
 * 
 * 核心原则：
 * 1. 需要合并的单元格必须写相同的值（不要用空字符串）
 * 2. 跨几行就写几遍相同的值
 * 3. 跨几列就写几遍相同的值
 */
public class ComplexHeaderCorrectTest {
    
    @Test
    public void testSimpleSingleRow() throws IOException {
        // 示例 1：简单单行表头
        
        List<List<String>> headers = Arrays.asList(
            Arrays.asList("用户 ID", "用户名", "邮箱", "手机号", "注册时间")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList(1, "张三", "zhangsan@example.com", "13800138000", "2024-01-01"),
            Arrays.asList(2, "李四", "lisi@example.com", "13900139000", "2024-01-02")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "用户信息表",
            "包含所有注册用户的基本信息",
            headers,
            data,
            new int[]{15, 20, 30, 20, 20}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/simple_header_with_desc.xlsx")) {
            fos.write(excelBytes);
            System.out.println("带描述的简单表头 Excel 已生成");
        }
    }
    
    @Test
    public void testTwoLevelMergedHeader() throws IOException {
        // 示例 2：双层表头（跨列合并）
        
        /*
         * 效果：
         * ┌───────────────┬───────────────┐
         * │   第一季度     │   第二季度     │
         * ├───────┬───────┼───────┬───────┤
         * │ 销售额 │ 销售量 │ 销售额 │ 销售量 │
         */
        
        List<List<String>> headers = Arrays.asList(
            // 第 1 行："第一季度"跨 2 列，"第二季度"跨 2 列
            Arrays.asList("第一季度", "第一季度", "第二季度", "第二季度"),
            // 第 2 行：具体的指标
            Arrays.asList("销售额", "销售量", "销售额", "销售量")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("100 万", "500 台", "150 万", "700 台"),
            Arrays.asList("120 万", "600 台", "180 万", "850 台")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "销售统计表",
            headers,
            data,
            new int[]{15, 15, 15, 15}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/two_level_merged.xlsx")) {
            fos.write(excelBytes);
            System.out.println("双层合并表头 Excel 已生成");
        }
    }
    
    @Test
    public void testThreeLevelMergedHeader() throws IOException {
        // 示例 3：三层表头（跨行 + 跨列合并）
        
        /*
         * 效果：
         * ┌──────┬────────────────────────┐
         * │ 学号 │        成绩信息         │
         * │      ├────────┬───────┬───────┤
         * │      │  语文   │  数学  │ 英语  │
         * │      ├────┬───┼───┬───┼───┬───┤
         * │      │平时│期末│平时│期末│平时│期末│
         */
        
        List<List<String>> headers = Arrays.asList(
            // 第 1 行："学号"跨 3 行，"成绩信息"跨 6 列
            Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息", "成绩信息", "成绩信息"),
            // 第 2 行："学号"继续（实现跨行），"语文"、"数学"、"英语"各跨 2 列
            Arrays.asList("学号", "语文", "语文", "数学", "数学", "英语", "英语"),
            // 第 3 行："学号"继续（实现跨 3 行），具体指标
            Arrays.asList("学号", "平时", "期末", "平时", "期末", "平时", "期末")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("001", "80", "85", "90", "95", "85", "90"),
            Arrays.asList("002", "82", "87", "92", "96", "87", "91")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "学生成绩表",
            headers,
            data,
            new int[]{15, 12, 12, 12, 12, 12, 12}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/three_level_merged.xlsx")) {
            fos.write(excelBytes);
            System.out.println("三层合并表头 Excel 已生成");
        }
    }
    
    @Test
    public void testMixedHeaders() throws IOException {
        // 示例 4：多个报表混合（简单 + 复杂）
        
        // 报表 1：简单表头
        List<List<String>> headers1 = Arrays.asList(
            Arrays.asList("产品编码", "产品名称", "单价", "库存量")
        );
        List<List<Object>> data1 = Arrays.asList(
            Arrays.asList("P001", "iPhone 15", 7999.00, 100),
            Arrays.asList("P002", "MacBook Pro", 14999.00, 50)
        );
        
        // 报表 2：双层合并表头
        List<List<String>> headers2 = Arrays.asList(
            // 第 1 行："仓库信息"跨 2 列，"库存统计"跨 2 列
            Arrays.asList("仓库信息", "仓库信息", "库存统计", "库存统计"),
            // 第 2 行：具体字段
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
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/mixed_headers_correct.xlsx")) {
            fos.write(excelBytes);
            System.out.println("混合表头 Excel 已生成");
        }
    }
    
    @Test
    public void testWithGroupsAndMergedHeader() throws IOException {
        // 示例 5：复杂表头 + 业务分组
        
        /*
         * 表头结构：
         * ┌────────┬────────────────────────────────────┐
         * │  部门   │           员工信息                  │
         * │        ├──────────┬──────────┬───────────────┤
         * │        │   姓名    │   职位    │   薪资统计     │
         */
        
        List<List<String>> complexHeaders = Arrays.asList(
            // 第 1 行："部门"跨 2 行，"员工信息"跨 3 列
            Arrays.asList("部门", "员工信息", "员工信息", "员工信息", "薪资统计", "薪资统计"),
            // 第 2 行："部门"继续（实现跨 2 行），具体字段
            Arrays.asList("部门", "姓名", "职位", "入职日期", "基本工资", "绩效奖金")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("技术部", "张三", "工程师", "2020-01-01", "15000", "5000"),
            Arrays.asList("技术部", "李四", "高级工程师", "2019-06-15", "20000", "8000"),
            Arrays.asList("销售部", "王五", "销售经理", "2018-03-20", "18000", "10000"),
            Arrays.asList("销售部", "赵六", "销售代表", "2021-07-10", "12000", "6000")
        );
        
        // 添加业务分组（在数据行中添加分组标题）
        List<MultiReportExcelUtil.GroupInfo> groups = new ArrayList<>();
        groups.add(new MultiReportExcelUtil.GroupInfo("技术部", 0, 1));  // 前 2 行
        groups.add(new MultiReportExcelUtil.GroupInfo("销售部", 2, 3));  // 后 2 行
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "员工薪资统计表",
            complexHeaders,
            data,
            new int[]{15, 15, 20, 20, 15, 15},
            groups
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/groups_with_merged_header.xlsx")) {
            fos.write(excelBytes);
            System.out.println("复杂表头 + 业务分组 Excel 已生成");
        }
    }
    
    @Test
    public void testIrregularHeader() throws IOException {
        // 示例 6：不规则表头（每行列数不同）
        
        /*
         * 效果：
         * ┌──────────────────────┬─────────────────────────────┐
         * │      基本信息         │         联系信息              │
         * ├──────┬─────┬─────┬────┼────────┬─────────┬─────────┤
         * │ 姓名 │ 年龄 │ 性别 │城市│ 电话    │ 邮箱     │ 地址     │
         */
        
        List<List<String>> complexHeaders = Arrays.asList(
            // 第 1 行："基本信息"跨 4 列，"联系信息"跨 3 列
            Arrays.asList("基本信息", "基本信息", "基本信息", "基本信息", 
                         "联系信息", "联系信息", "联系信息"),
            // 第 2 行：具体字段
            Arrays.asList("姓名", "年龄", "性别", "城市", 
                         "电话", "邮箱", "地址")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList("张三", "25", "男", "北京", 
                         "13800138000", "zhangsan@example.com", "北京市朝阳区"),
            Arrays.asList("李四", "28", "女", "上海", 
                         "13900139000", "lisi@example.com", "上海市浦东新区")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "客户信息表",
            complexHeaders,
            data
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/irregular_header_correct.xlsx")) {
            fos.write(excelBytes);
            System.out.println("不规则表头 Excel 已生成");
        }
    }
    
    @Test
    public void testFullRowMergedHeader() throws IOException {
        // 示例 7：整行合并的表头
        
        List<List<String>> headers = Arrays.asList(
            // 第 1 行："综合统计表"跨所有列（假设 6 列）
            Arrays.asList("综合统计表", "综合统计表", "综合统计表", 
                         "综合统计表", "综合统计表", "综合统计表"),
            // 第 2 行：具体字段
            Arrays.asList("序号", "指标名称", "数值", "占比", "同期值", "增长率")
        );
        
        List<List<Object>> data = Arrays.asList(
            Arrays.asList(1, "总资产", 1000000.00, "100%", 900000.00, "11.1%"),
            Arrays.asList(2, "贷款余额", 600000.00, "60%", 550000.00, "9.1%")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "资产负债表",
            headers,
            data,
            new int[]{10, 30, 20, 15, 20, 15}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/full_row_merged.xlsx")) {
            fos.write(excelBytes);
            System.out.println("整行合并表头 Excel 已生成");
        }
    }
}