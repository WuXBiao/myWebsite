package com.index.utils;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 多报表拼接工具类测试
 */
public class MultiReportExcelUtilTest {
    
    @Test
    public void testMergeReports() throws IOException {
        // 准备测试数据
            
        // 报表 1：用户信息（单行表头）
        List<List<String>> headers1 = Arrays.asList(
            Arrays.asList("用户 ID", "用户名", "邮箱", "手机号", "注册时间")
        );
        List<List<Object>> data1 = Arrays.asList(
            Arrays.asList(1, "张三", "zhangsan@example.com", "13800138000", "2024-01-01"),
            Arrays.asList(2, "李四", "lisi@example.com", "13900139000", "2024-01-02"),
            Arrays.asList(3, "王五", "wangwu@example.com", "13600136000", "2024-01-03")
        );
            
        // 报表 2：订单信息（单行表头）
        List<List<String>> headers2 = Arrays.asList(
            Arrays.asList("订单号", "商品名称", "数量", "单价", "总价")
        );
        List<List<Object>> data2 = Arrays.asList(
            Arrays.asList("ORD001", "iPhone 15", 1, 7999.00, 7999.00),
            Arrays.asList("ORD002", "MacBook Pro", 2, 14999.00, 29998.00),
            Arrays.asList("ORD003", "AirPods Pro", 5, 1999.00, 9995.00)
        );
            
        // 报表 3：库存信息（单行表头）
        List<List<String>> headers3 = Arrays.asList(
            Arrays.asList("商品编码", "仓库", "库存数量", "预警线", "状态")
        );
        List<List<Object>> data3 = Arrays.asList(
            Arrays.asList("P001", "北京仓", 100, 50, "充足"),
            Arrays.asList("P002", "上海仓", 30, 50, "预警"),
            Arrays.asList("P003", "广州仓", 200, 80, "充足")
        );
            
        // 创建报表配置
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
            
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "用户信息表",
            headers1,
            data1,
            new int[]{15, 20, 30, 20, 20} // 列宽
        ));
            
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "订单信息表",
            headers2,
            data2,
            new int[]{20, 25, 15, 15, 15}
        ));
            
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "库存信息表",
            headers3,
            data3,
            new int[]{20, 20, 15, 15, 15}
        ));
            
        // 生成 Excel
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
            
        // 保存到文件（用于查看效果）
        try (FileOutputStream fos = new FileOutputStream("/tmp/multi_report_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("Excel 文件已生成：/tmp/multi_report_test.xlsx");
        }
            
        System.out.println("Excel 文件大小：" + excelBytes.length + " bytes");
    }
    
    @Test
    public void testEmptyReport() throws IOException {
        // 测试空数据报表（单行表头）
        List<List<String>> headers = Arrays.asList(
            Arrays.asList("列 1", "列 2", "列 3")
        );
        List<List<Object>> data = new ArrayList<>(); // 空数据
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig("空报表", headers, data));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/empty_report_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("空报表 Excel 已生成");
        }
    }
    
    @Test
    public void testSingleReport() throws IOException {
        // 测试单个报表（单行表头）
        List<List<String>> headers = Arrays.asList(
            Arrays.asList("序号", "指标名称", "数值", "占比")
        );
        List<List<Object>> data = Arrays.asList(
            Arrays.asList(1, "总资产", 1000000.00, "100%"),
            Arrays.asList(2, "贷款余额", 600000.00, "60%"),
            Arrays.asList(3, "存款余额", 400000.00, "40%")
        );
        
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "资产负债表",
            headers,
            data,
            new int[]{10, 30, 20, 15}
        ));
        
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/single_report_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("单报表 Excel 已生成");
        }
    }
    
    @Test
    public void testWithGroups() throws IOException {
        // 测试带业务分组的报表
        
        // 报表 1：用户信息（带分组，单行表头）
        List<List<String>> headers1 = Arrays.asList(
            Arrays.asList("用户 ID", "用户名", "邮箱", "手机号")
        );
        List<List<Object>> data1 = Arrays.asList(
            Arrays.asList(1, "张三", "zhangsan@example.com", "13800138000"),
            Arrays.asList(2, "李四", "lisi@example.com", "13900139000"),
            Arrays.asList(3, "王五", "wangwu@example.com", "13600136000"),
            Arrays.asList(4, "赵六", "zhaoliu@example.com", "13700137000")
        );
        
        // 创建分组信息
        List<MultiReportExcelUtil.GroupInfo> groups1 = new ArrayList<>();
        // 前两个用户属于"北京分公司"
        groups1.add(new MultiReportExcelUtil.GroupInfo("北京分公司", 0, 1, 1));
        // 后两个用户属于"上海分公司"
        groups1.add(new MultiReportExcelUtil.GroupInfo("上海分公司", 2, 3, 1));
        
        // 报表 2：订单信息（带跨列分组，单行表头）
        List<List<String>> headers2 = Arrays.asList(
            Arrays.asList("订单号", "商品名称", "数量", "单价", "总价")
        );
        List<List<Object>> data2 = Arrays.asList(
            Arrays.asList("ORD001", "iPhone 15", 1, 7999.00, 7999.00),
            Arrays.asList("ORD002", "MacBook Pro", 2, 14999.00, 29998.00),
            Arrays.asList("ORD003", "AirPods Pro", 5, 1999.00, 9995.00)
        );
        
        // 创建跨列分组
        List<MultiReportExcelUtil.GroupInfo> groups2 = new ArrayList<>();
        // 第一个订单占 2 行
        groups2.add(new MultiReportExcelUtil.GroupInfo("重点客户", 0, 1, 2));
        // 普通订单
        groups2.add(new MultiReportExcelUtil.GroupInfo("普通订单", 2, 2, 1));
        
        // 创建报表配置
        List<MultiReportExcelUtil.ReportConfig> reports = new ArrayList<>();
        
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "用户信息表（带分组）",
            headers1,
            data1,
            new int[]{15, 20, 30, 20},
            groups1
        ));
        
        reports.add(new MultiReportExcelUtil.ReportConfig(
            "订单信息表（带跨列分组）",
            headers2,
            data2,
            new int[]{20, 25, 15, 15, 15},
            groups2
        ));
        
        // 生成 Excel
        byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
        
        // 保存到文件
        try (FileOutputStream fos = new FileOutputStream("/tmp/group_report_test.xlsx")) {
            fos.write(excelBytes);
            System.out.println("带分组的 Excel 已生成：/tmp/group_report_test.xlsx");
        }
    }
}