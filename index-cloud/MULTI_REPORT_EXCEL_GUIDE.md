# 多报表拼接工具类使用指南

## 功能概述

`MultiReportExcelUtil` 是一个通用的 Excel 工具类，支持将多个**字段不同**的报表拼接到同一个 Excel Sheet 中。

## 核心特性

- ✅ **支持多个报表拼接**
- ✅ **每个报表字段可以不同**
- ✅ **自动样式美化**
- ✅ **灵活的列宽设置**
- ✅ **Map 数据快速转换**
- ✅ **一次生成，无需多次操作**

## 快速开始

### 基础用法

```java
import com.index.utils.MultiReportExcelUtil;
import com.index.utils.MultiReportExcelUtil.ReportConfig;

// 1. 准备报表 1 的数据
List<String> headers1 = Arrays.asList("用户ID", "用户名", "邮箱");
List<List<Object>> data1 = Arrays.asList(
    Arrays.asList(1, "张三", "zhangsan@example.com"),
    Arrays.asList(2, "李四", "lisi@example.com")
);

// 2. 准备报表 2 的数据（字段可以不同）
List<String> headers2 = Arrays.asList("订单号", "商品", "金额");
List<List<Object>> data2 = Arrays.asList(
    Arrays.asList("ORD001", "iPhone 15", 7999.00),
    Arrays.asList("ORD002", "MacBook Pro", 14999.00)
);

// 3. 创建报表配置
List<ReportConfig> reports = new ArrayList<>();

reports.add(new ReportConfig("用户信息表", headers1, data1));
reports.add(new ReportConfig("订单信息表", headers2, data2));

// 4. 生成 Excel
byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);

// 5. 输出或保存
response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
response.setHeader("Content-Disposition", "attachment; filename=汇总报表.xlsx");
response.getOutputStream().write(excelBytes);
```

## 完整示例

### 场景：导出业务汇总报表

假设有三个不同的报表需要导出：
1. 用户统计表
2. 订单统计表  
3. 产品库存表

```java
@Service
public class ReportExportService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 导出业务汇总报表
     */
    public byte[] exportBusinessSummary() throws IOException {
        // 1. 查询数据
        List<User> users = userMapper.selectAll();
        List<Order> orders = orderMapper.selectRecentOrders();
        List<Product> products = productMapper.selectInventory();
        
        // 2. 转换为报表格式
        
        // 报表 1：用户统计
        List<String> userHeaders = Arrays.asList("用户ID", "用户名", "手机号", "注册时间");
        List<List<Object>> userData = new ArrayList<>();
        for (User user : users) {
            userData.add(Arrays.asList(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getCreateTime()
            ));
        }
        
        // 报表 2：订单统计
        List<String> orderHeaders = Arrays.asList("订单号", "客户名称", "商品名称", "数量", "金额");
        List<List<Object>> orderData = new ArrayList<>();
        for (Order order : orders) {
            orderData.add(Arrays.asList(
                order.getOrderNo(),
                order.getCustomerName(),
                order.getProductName(),
                order.getQuantity(),
                order.getAmount()
            ));
        }
        
        // 报表 3：产品库存
        List<String> productHeaders = Arrays.asList("产品编码", "产品名称", "仓库", "库存量", "预警状态");
        List<List<Object>> productData = new ArrayList<>();
        for (Product product : products) {
            productData.add(Arrays.asList(
                product.getProductCode(),
                product.getProductName(),
                product.getWarehouse(),
                product.getStockQty(),
                product.getWarningStatus()
            ));
        }
        
        // 3. 创建报表配置
        List<ReportConfig> reports = new ArrayList<>();
        
        reports.add(new ReportConfig(
            "用户统计表",
            userHeaders,
            userData,
            new int[]{15, 20, 20, 25} // 列宽
        ));
        
        reports.add(new ReportConfig(
            "订单统计表",
            orderHeaders,
            orderData,
            new int[]{20, 20, 25, 15, 20}
        ));
        
        reports.add(new ReportConfig(
            "产品库存表",
            productHeaders,
            productData,
            new int[]{20, 30, 20, 15, 15}
        ));
        
        // 4. 生成 Excel
        return MultiReportExcelUtil.mergeReportsToExcel(reports);
    }
}
```

### Controller 调用

```java
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @Autowired
    private ReportExportService reportExportService;
    
    @GetMapping("/export")
    public void exportSummary(HttpServletResponse response) throws IOException {
        byte[] excelBytes = reportExportService.exportBusinessSummary();
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=业务汇总报表.xlsx");
        response.setContentLength(excelBytes.length);
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }
}
```

## 便捷方法：从 Map 创建报表

如果数据已经是 Map 格式，可以使用便捷方法：

```java
// 查询数据（返回 Map 列表）
List<Map<String, Object>> userData = userMapper.selectUsersAsMap();
List<Map<String, Object>> orderData = orderMapper.selectOrdersAsMap();

// 使用便捷方法创建报表
List<ReportConfig> reports = new ArrayList<>();

reports.add(MultiReportExcelUtil.createReportFromMap(
    "用户列表",
    Arrays.asList("ID", "姓名", "邮箱", "手机"),
    userData,
    new String[]{"id", "name", "email", "phone"} // Map 的 key，按顺序对应列
));

reports.add(MultiReportExcelUtil.createReportFromMap(
    "订单列表",
    Arrays.asList("订单号", "商品", "数量", "金额"),
    orderData,
    new String[]{"order_no", "product", "quantity", "amount"}
));

// 生成 Excel
byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
```

## 高级用法

### 1. 自定义列宽

```java
// 列宽单位：字符数
int[] columnWidths = {15, 20, 30, 20, 20};

new ReportConfig("报表名称", headers, data, columnWidths);
```

### 2. 空数据处理

```java
// 即使数据为空也可以正常生成（只有表头）
List<List<Object>> emptyData = new ArrayList<>();
new ReportConfig("空报表", headers, emptyData);
```

### 3. 动态报表数量

```java
List<ReportConfig> reports = new ArrayList<>();

// 根据条件动态添加报表
if (showUserReport) {
    reports.add(createUserReport());
}

if (showOrderReport) {
    reports.add(createOrderReport());
}

if (showProductReport) {
    reports.add(createProductReport());
}

byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
```

## 数据结构说明

### ReportConfig 类

```java
public class ReportConfig {
    private String reportName;          // 报表标题
    private List<String> headers;       // 表头列表
    private List<List<Object>> dataList; // 数据列表
    private int[] columnWidths;         // 列宽数组（可选）
}
```

### 数据格式要求

```java
// 表头：字符串列表
List<String> headers = Arrays.asList("列 1", "列 2", "列 3");

// 数据：二维列表
List<List<Object>> dataList = Arrays.asList(
    Arrays.asList("值 1", "值 2", "值 3"),  // 第一行
    Arrays.asList("值 4", "值 5", "值 6"),  // 第二行
    ...
);
```

## 样式说明

### 默认样式

- **标题行**：
  - 字体：宋体，14 号，加粗
  - 对齐：居中
  - 合并：整行合并（A-L 列）
  
- **表头行**：
  - 字体：宋体，11 号，加粗
  - 背景：浅灰色
  - 对齐：居中
  - 边框：细边框
  
- **数据行**：
  - 字体：宋体，10 号
  - 对齐：左对齐
  - 边框：细边框

### 自动调整

- 报表之间自动添加空行
- 最后一个报表后不添加空行
- 列宽可手动设置或自动调整

## 注意事项

### 1. 数据类型支持

支持的数据类型：
- `String` - 字符串
- `Number` - 数字（Integer、Double、BigDecimal 等）
- 其他类型会自动调用 `toString()`

### 2. 列数限制

- 建议每个报表的列数不超过 20 列
- 如果列数差异较大，会以最大列数为准

### 3. 性能考虑

- 适合中等规模数据（每页 < 1000 条）
- 大数据量建议分页导出
- 多个报表总行数建议 < 10000 行

### 4. 内存占用

```java
// 推荐：分批处理大量数据
if (totalRows > 10000) {
    // 分多个 Sheet 或提示用户分批导出
}
```

## 常见问题

### Q1: 如何修改报表之间的间距？

A: 修改工具类中的空行逻辑：
```java
// 在两个报表之间添加多个空行
if (i < reports.size() - 1) {
    sheet.createRow(currentRowNum++); // 一个空行
    sheet.createRow(currentRowNum++); // 两个空行
}
```

### Q2: 如何自定义样式？

A: 修改工具类中的样式方法：
```java
private static CellStyle createTitleStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    // 自定义样式...
    return style;
}
```

### Q3: 如何添加页脚或汇总行？

A: 在每个报表的数据后添加汇总行：
```java
dataList.add(Arrays.asList("合计", "", totalAmount));
```

### Q4: 支持多个 Sheet 吗？

A: 当前版本只支持单个 Sheet。如需多个 Sheet，可以：
```java
Sheet sheet1 = workbook.createSheet("报表 1");
Sheet sheet2 = workbook.createSheet("报表 2");
// 分别处理每个 Sheet
```

## 测试

运行测试类查看效果：

```bash
cd /Users/xubiaowu/projects/myWebsite/index-cloud/index-server
mvn test -Dtest=MultiReportExcelUtilTest
```

测试文件会生成到 `/tmp/` 目录：
- `multi_report_test.xlsx` - 多报表示例
- `map_report_test.xlsx` - Map 数据示例
- `empty_report_test.xlsx` - 空报表示例
- `single_report_test.xlsx` - 单报表示例

## 扩展建议

1. **添加水印**：在标题行添加水印
2. **条件格式**：根据数值设置颜色
3. **图表集成**：在报表后添加图表
4. **冻结窗格**：冻结表头行
5. **打印设置**：设置打印区域和页眉页脚
