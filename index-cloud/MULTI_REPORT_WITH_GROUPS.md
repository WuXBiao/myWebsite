# 多报表拼接工具 - 业务分组功能

## 功能概述

在原有基础上增加了**业务分组**功能，支持在表头下方、数据行上方添加合并单元格的业务名称行。

## 效果展示

### 无分组的报表
```
┌─────────────────────────────────────┐
│        用户信息表                    │ ← 报表标题
├──────┬──────┬──────┬──────┬─────────┤
│用户ID│用户名│ 邮箱 │手机号│注册时间 │ ← 表头
├──────┼──────┼──────┼──────┼─────────┤
│  1   │ 张三 │ ...  │ ...  │ ...     │ ← 数据行
│  2   │ 李四 │ ...  │ ...  │ ...     │
└──────┴──────┴──────┴──────┴─────────┘
```

### 有分组的报表（新增功能）
```
┌─────────────────────────────────────┐
│        用户信息表                    │ ← 报表标题
├──────┬──────┬──────┬──────┬─────────┤
│用户ID│用户名│ 邮箱 │手机号│注册时间 │ ← 表头
├──────┼──────┼──────┼──────┼─────────┤
│北京分公司                          │ ← 业务分组（跨 2 行）
├──────┼──────┼──────┼──────┼─────────┤
│  1   │ 张三 │ ...  │ ...  │ ...     │
│  2   │ 李四 │ ...  │ ...  │ ...     │
├──────┼──────┼──────┼──────┼─────────┤
│上海分公司                          │ ← 业务分组（跨 2 行）
├──────┼──────┼──────┼──────┼─────────┤
│  3   │ 王五 │ ...  │ ...  │ ...     │
│  4   │ 赵六 │ ...  │ ...  │ ...     │
└──────┴──────┴──────┴──────┴─────────┘
```

## GroupInfo 类说明

### 构造函数

```java
// 基础构造函数
public GroupInfo(String groupName, int startRow, int endRow)

// 完整构造函数（支持跨列）
public GroupInfo(String groupName, int startRow, int endRow, int colspan)
```

### 参数说明

- **groupName**: 分组名称（如："北京分公司"、"重点客户"）
- **startRow**: 起始行索引（从 0 开始，相对于数据行的位置）
- **endRow**: 结束行索引（包含）
- **colspan**: 跨列数（默认 1，可选）

## 使用示例

### 示例 1：简单的行分组

将用户按地区分组：

```java
// 准备数据
List<String> headers = Arrays.asList("用户ID", "用户名", "邮箱", "手机号");
List<List<Object>> data = Arrays.asList(
    Arrays.asList(1, "张三", "zhangsan@example.com", "13800138000"),
    Arrays.asList(2, "李四", "lisi@example.com", "13900139000"),
    Arrays.asList(3, "王五", "wangwu@example.com", "13600136000"),
    Arrays.asList(4, "赵六", "zhaoliu@example.com", "13700137000")
);

// 创建分组信息
List<GroupInfo> groups = new ArrayList<>();

// 前两个用户属于"北京分公司"（第 0-1 行）
groups.add(new GroupInfo("北京分公司", 0, 1));

// 后两个用户属于"上海分公司"（第 2-3 行）
groups.add(new GroupInfo("上海分公司", 2, 3));

// 创建报表配置
ReportConfig config = new ReportConfig(
    "用户信息表",
    headers,
    data,
    new int[]{15, 20, 30, 20},
    groups // 添加分组信息
);

// 生成 Excel
List<ReportConfig> reports = Arrays.asList(config);
byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
```

### 示例 2：跨列分组

订单报表中，重点客户占用多列：

```java
List<String> headers = Arrays.asList("订单号", "商品名称", "数量", "单价", "总价");
List<List<Object>> data = Arrays.asList(
    Arrays.asList("ORD001", "iPhone 15", 1, 7999.00, 7999.00),
    Arrays.asList("ORD002", "MacBook Pro", 2, 14999.00, 29998.00),
    Arrays.asList("ORD003", "AirPods Pro", 5, 1999.00, 9995.00)
);

List<GroupInfo> groups = new ArrayList<>();

// "重点客户"占 2 列，跨 2 行（第 0-1 行）
groups.add(new GroupInfo("重点客户", 0, 1, 2));

// "普通订单"占 1 列，第 2 行
groups.add(new GroupInfo("普通订单", 2, 2, 1));

ReportConfig config = new ReportConfig(
    "订单信息表",
    headers,
    data,
    new int[]{20, 25, 15, 15, 15},
    groups
);
```

### 示例 3：多个报表都有分组

```java
List<ReportConfig> reports = new ArrayList<>();

// 报表 1：用户表（按地区分组）
List<GroupInfo> userGroups = new ArrayList<>();
userGroups.add(new GroupInfo("北京分公司", 0, 2));
userGroups.add(new GroupInfo("上海分公司", 3, 5));
userGroups.add(new GroupInfo("广州分公司", 6, 8));

reports.add(new ReportConfig(
    "用户统计表",
    userHeaders,
    userData,
    columnWidths,
    userGroups
));

// 报表 2：订单表（按客户类型分组）
List<GroupInfo> orderGroups = new ArrayList<>();
orderGroups.add(new GroupInfo("VIP 客户", 0, 4));
orderGroups.add(new GroupInfo("普通客户", 5, 9));

reports.add(new ReportConfig(
    "订单统计表",
    orderHeaders,
    orderData,
    columnWidths,
    orderGroups
));

// 生成 Excel
byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(reports);
```

## 实际业务场景

### 场景 1：银行报表 - 按支行分组

```java
List<String> headers = Arrays.asList("账号", "户名", "余额", "开户日期");
List<List<Object>> data = ...; // 查询数据库

List<GroupInfo> groups = new ArrayList<>();

// 按支行分组
groups.add(new GroupInfo("朝阳支行", 0, 9));      // 10 个账户
groups.add(new GroupInfo("海淀支行", 10, 19));    // 10 个账户
groups.add(new GroupInfo("西城支行", 20, 29));    // 10 个账户

ReportConfig config = new ReportConfig(
    "各支行存款统计表",
    headers,
    data,
    null,
    groups
);
```

### 场景 2：销售报表 - 按大区分组

```java
List<String> headers = Arrays.asList("产品", "销售员", "销售额", "销售量");
List<List<Object>> data = ...;

List<GroupInfo> groups = new ArrayList<>();

// 按销售大区分组
groups.add(new GroupInfo("华北区", 0, 14));
groups.add(new GroupInfo("华东区", 15, 29));
groups.add(new GroupInfo("华南区", 30, 44));

ReportConfig config = new ReportConfig(
    "销售业绩统计表",
    headers,
    data,
    null,
    groups
);
```

### 场景 3：库存报表 - 按仓库分组

```java
List<String> headers = Arrays.asList("商品编码", "商品名称", "库存量", "预警状态");
List<List<Object>> data = ...;

List<GroupInfo> groups = new ArrayList<>();

// 按仓库分组
groups.add(new GroupInfo("北京仓", 0, 4));
groups.add(new GroupInfo("上海仓", 5, 9));
groups.add(new GroupInfo("广州仓", 10, 14));

ReportConfig config = new ReportConfig(
    "商品库存统计表",
    headers,
    data,
    null,
    groups
);
```

## 样式说明

### 分组单元格样式

- **字体**：宋体，10 号，加粗
- **对齐**：居中
- **背景色**：浅蓝色
- **边框**：细边框

### 与报表标题的区别

| 元素 | 位置 | 合并范围 | 背景色 | 字体大小 |
|------|------|----------|--------|----------|
| **报表标题** | 每个报表第一行 | 整行合并 | 白色 | 14 |
| **业务分组** | 表头下方 | 可配置跨列/跨行 | 浅蓝色 | 10 |
| **表头** | 报表标题下一行 | 不合并 | 浅灰色 | 11 |
| **数据行** | 表头下方 | 不合并 | 白色 | 10 |

## 高级用法

### 1. 动态计算分组

```java
// 根据数据动态计算分组
Map<String, List<Integer>> regionMap = new HashMap<>();
for (int i = 0; i < userData.size(); i++) {
    String region = getRegionByUserId(userData.get(i).get(0));
    regionMap.computeIfAbsent(region, k -> new ArrayList<>()).add(i);
}

// 创建分组
List<GroupInfo> groups = new ArrayList<>();
for (Map.Entry<String, List<Integer>> entry : regionMap.entrySet()) {
    List<Integer> rows = entry.getValue();
    int startRow = rows.get(0);
    int endRow = rows.get(rows.size() - 1);
    groups.add(new GroupInfo(entry.getKey(), startRow, endRow));
}
```

### 2. 嵌套分组（多层级）

```java
// 第一层：大区
groups.add(new GroupInfo("华北区", 0, 19, 1));

// 第二层：省份（需要在数据行中处理）
// 可以在数据行的第一列添加缩进或标记
```

### 3. 条件分组

```java
// 根据金额分组
if (amount > 1000000) {
    groups.add(new GroupInfo("重点客户", startRow, endRow));
} else {
    groups.add(new GroupInfo("普通客户", startRow, endRow));
}
```

## 注意事项

### 1. 行索引计算

- `startRow` 和 `endRow` 是相对于数据行的索引
- 从 0 开始计数
- `endRow` 包含在内

```java
// 正确示例：前 3 行数据
new GroupInfo("分组 1", 0, 2); // 第 1、2、3 行

// 错误示例：容易混淆
new GroupInfo("分组 1", 1, 3); // 实际是第 2、3、4 行
```

### 2. 跨列限制

- `colspan` 不能超过报表的总列数
- 建议不超过 3 列，否则影响美观

```java
// 推荐：跨 1-2 列
new GroupInfo("分组", 0, 2, 2);

// 不推荐：跨太多列
new GroupInfo("分组", 0, 2, 10); // 会遮挡太多数据列
```

### 3. 分组重叠

避免分组之间重叠：

```java
// ❌ 错误：分组重叠
groups.add(new GroupInfo("分组 1", 0, 5));
groups.add(new GroupInfo("分组 2", 3, 8)); // 3-5 行重叠

// ✅ 正确：分组连续
groups.add(new GroupInfo("分组 1", 0, 2));
groups.add(new GroupInfo("分组 2", 3, 5));
groups.add(new GroupInfo("分组 3", 6, 8));
```

### 4. 空数据处理

如果某组没有数据，可以跳过：

```java
if (groupData.isEmpty()) {
    continue; // 跳过空分组
}
groups.add(new GroupInfo(groupName, startRow, endRow));
```

## 测试运行

```bash
# 运行带分组的测试
mvn test -Dtest=MultiReportExcelUtilTest#testWithGroups

# 查看生成的文件
open /tmp/group_report_test.xlsx
```

## 常见问题

### Q1: 如何在分组中显示更多信息？

A: 可以在分组名称中添加统计信息：
```java
String groupName = String.format("%s（%d人）", regionName, count);
groups.add(new GroupInfo(groupName, startRow, endRow));
```

### Q2: 分组可以跨报表吗？

A: 不可以。每个报表的分组是独立的，只在该报表内有效。

### Q3: 如何修改分组的样式？

A: 修改工具类中的 `createGroupStyle` 方法：
```java
private static CellStyle createGroupStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    // 自定义样式...
    return style;
}
```

### Q4: 分组名称太长怎么办？

A: 可以换行显示或缩写：
```java
// 换行显示
String groupName = "北京市朝阳区\\n第一支行";

// 或使用缩写
String groupName = "朝阳一支";
```

## 总结

业务分组功能让你的报表更加清晰有序，特别适合：
- ✅ 按地区/部门分组的数据
- ✅ 需要突出显示的分段统计
- ✅ 多层级的数据展示
- ✅ 银行、财务等专业报表

通过合理分组，可以显著提升报表的可读性和专业性！
