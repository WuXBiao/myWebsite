# Excel 导出自定义行功能说明

## 功能概述

在 Excel 导出的标题行和表头行之间添加了一个自定义内容行，用于显示期间、制表人、备注等额外信息。

## 修改内容

### 1. 新增方法

#### `insertCustomRow(Sheet sheet, Workbook workbook, String period)`
- **功能**：在标题行下方插入自定义内容行
- **位置**：第 2 行（索引为 1）
- **合并范围**：A2 到 L2（整行合并）
- **样式**：左对齐、垂直居中、灰色字体

#### `buildCustomText(String period)`
- **功能**：构建自定义文本内容
- **参数**：`period` - 统计期间
- **返回**：自定义文本字符串

### 2. 行结构变化

**修改前：**
```
第 1 行 (索引 0): 大标题 "大类资产细项结构表（考核）"
第 2 行 (索引 1): 表头 "指标" + 列名
第 3 行 (索引 2): 数据行...
```

**修改后：**
```
第 1 行 (索引 0): 大标题 "大类资产细项结构表（考核）"
第 2 行 (索引 1): 自定义内容行 ← 新增
第 3 行 (索引 2): 表头 "指标" + 列名
第 4 行 (索引 3): 数据行...
```

## 使用方法

### 基本使用

```java
@Autowired
private ExportService exportService;

// 调用导出方法
String period = "2024 年 11 月";
byte[] excelData = exportService.exportToExcel(dataList, period);
```

### 自定义内容定制

修改 `buildCustomText` 方法来定义你的显示内容：

```java
private String buildCustomText(String period) {
    StringBuilder sb = new StringBuilder();
    
    // 1. 显示期间
    if (period != null && !period.trim().isEmpty()) {
        sb.append("统计期间：").append(period);
    }
    
    // 2. 添加制表人信息
    sb.append("  |  制表人：张三");
    
    // 3. 添加部门信息
    sb.append("  |  部门：财务部");
    
    // 4. 添加备注
    sb.append("  |  备注：本数据仅供参考");
    
    return sb.toString();
}
```

### 显示效果示例

```
┌────────────────────────────────────────────────┐
│      大类资产细项结构表（考核）                │ ← 标题行
├────────────────────────────────────────────────┤
│ 统计期间：2024 年 11 月  |  制表人：张三         │ ← 自定义行（新增）
├──────┬──────┬──────┬──────┬──────┬─────────────┤
│ 指标 │ 7 月末│ ...  │ ...  │ ...  │ ...         │ ← 表头行
├──────┼──────┼──────┼──────┼──────┼─────────────┤
│ 数据 │ ...  │ ...  │ ...  │ ...  │ ...         │ ← 数据行
```

## 样式自定义

### 修改行高

```java
customRow.setHeightInPoints(25f); // 调整行高（像素）
```

### 修改对齐方式

```java
// 居中对齐
customStyle.setAlignment(HorizontalAlignment.CENTER);

// 右对齐
customStyle.setAlignment(HorizontalAlignment.RIGHT);
```

### 修改字体样式

```java
Font font = workbook.createFont();
font.setFontHeightInPoints((short) 12); // 字体大小
font.setBold(true); // 加粗
font.setColor(IndexedColors.BLUE.getIndex()); // 蓝色
font.setFontName("微软雅黑"); // 字体
```

### 修改背景色

```java
customStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
customStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
```

## 高级用法

### 1. 多行自定义内容

```java
private void insertCustomRow(Sheet sheet, Workbook workbook, String period) {
    // 第一行：期间信息
    Row row1 = sheet.createRow(1);
    Cell cell1 = row1.createCell(0);
    cell1.setCellValue("统计期间：" + period);
    
    // 第二行：制表人和备注
    Row row2 = sheet.createRow(2);
    Cell cell2 = row2.createCell(0);
    cell2.setCellValue("制表人：张三  |  备注：仅供参考");
    
    // 调整后续表头行的位置
    // ...
}
```

### 2. 条件显示内容

```java
private String buildCustomText(String period) {
    StringBuilder sb = new StringBuilder();
    
    if ("quarterly".equals(period)) {
        sb.append("季度报告 | ");
    } else if ("annual".equals(period)) {
        sb.append("年度报告 | ");
    }
    
    sb.append("生成时间：").append(new Date());
    return sb.toString();
}
```

### 3. 动态获取当前用户

```java
private String buildCustomText(String period) {
    StringBuilder sb = new StringBuilder();
    
    // 假设可以从安全上下文获取当前用户
    String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    
    sb.append("统计期间：").append(period);
    sb.append("  |  生成时间：").append(LocalDateTime.now());
    sb.append("  |  操作员：").append(currentUser);
    
    return sb.toString();
}
```

## 注意事项

1. **行号调整**：
   - 由于插入了新行，原有的表头行和数据行索引都需要 +1
   - 代码中已相应调整：`sheet.getRow(2)` 代替原来的 `sheet.getRow(1)`

2. **合并区域**：
   - 自定义行使用了整行合并（0-11 列）
   - 如需部分合并，调整 `CellRangeAddress` 参数

3. **样式一致性**：
   - 建议保持与整体表格风格一致
   - 字体大小、颜色应与标题和表头协调

4. **性能考虑**：
   - 对于大量数据导出，自定义行影响很小
   - 可以安全使用

## 常见问题

### Q: 如何不显示自定义行？
A: 在调用时传入空字符串或 null：
```java
exportService.exportToExcel(dataList, ""); // 自定义行为空
```

### Q: 如何在自定义行中添加多个独立字段？
A: 可以使用分隔符连接：
```java
sb.append("期间：").append(period);
sb.append("  |  制表人：").append(userName);
sb.append("  |  页码：1/10");
```

### Q: 如何修改自定义行的位置？
A: 调整行索引：
```java
// 在第 3 行插入（索引 2）
Row customRow = sheet.createRow(2);
```

## 扩展建议

1. **添加水印**：可以在自定义行中添加防伪水印
2. **二维码/条形码**：集成相关库生成追踪码
3. **动态模板**：根据不同类型的报表使用不同的模板
4. **国际化支持**：根据用户语言环境显示不同文字
