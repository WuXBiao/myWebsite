# Excel 导出自定义行 - 优雅实现方案

## 核心思路

使用 POI 的 `shiftRows` 方法一次性移动所有行，避免逐个移动和删除的复杂操作。

## 关键代码

```java
// 1. 生成 Workbook（EasyPoi 自动生成标题行和表头行）
Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entityList, exportList);
Sheet sheet = workbook.getSheetAt(0);

// 2. 设置标题行高度
Row titleRow = sheet.getRow(0);
titleRow.setHeightInPoints(28f);

// 3. 【关键】使用 shiftRows 一次性将所有行下移一行
//    参数：startRow, endRow, nRows
//    从第 1 行开始，到最后一行，向下移动 1 行
sheet.shiftRows(1, sheet.getLastRowNum(), 1);

// 4. 在空出的第 1 行创建自定义内容行
Row customRow = sheet.createRow(1);
customRow.setHeightInPoints(20f);

// 5. 设置自定义行内容和样式
setupCustomRow(customRow, sheet, workbook, period);
```

## 优势对比

### ❌ 旧方案（不优雅）
```java
// 1. 获取原表头行
Row originalHeaderRow = sheet.getRow(1);

// 2. 创建新行
Row newHeaderRow = sheet.createRow(2);

// 3. 逐单元格复制内容
for (int i = 0; i < originalHeaderRow.getLastCellNum(); i++) {
    Cell oldCell = originalHeaderRow.getCell(i);
    if (oldCell != null) {
        Cell newCell = newHeaderRow.createCell(i);
        newCell.setCellValue(oldCell.getStringCellValue());
        newCell.setCellStyle(oldCell.getCellStyle());
    }
}

// 4. 删除原行
sheet.removeRow(originalHeaderRow);

// 问题：
// - 代码冗长
// - 性能差（多次操作）
// - 容易出错
```

### ✅ 新方案（优雅）
```java
// 一行代码搞定
sheet.shiftRows(1, sheet.getLastRowNum(), 1);

// 优点：
// - 简洁明了
// - 一次性操作
// - POI 内部优化，性能好
// - 保留所有样式和格式
```

## 最终效果

```
执行前：
┌────────────────────────────────┐
│ 0: 大标题                      │
│ 1: 表头（指标 | 7 月末 | ...）   │  ← EasyPoi 生成
│ 2: 数据行 1                     │
│ 3: 数据行 2                     │
└────────────────────────────────┘

执行 shiftRows(1, lastRow, 1) 后：
┌────────────────────────────────┐
│ 0: 大标题                      │
│ 1: [空行]                      │  ← 空出位置
│ 2: 表头（指标 | 7 月末 | ...）   │  ← 自动下移
│ 3: 数据行 1                     │  ← 自动下移
│ 4: 数据行 2                     │  ← 自动下移
└────────────────────────────────┘

创建自定义行后：
┌────────────────────────────────┐
│ 0: 大标题                      │
│ 1: 统计期间：2024 年 11 月       │  ← 自定义内容
│ 2: 表头（指标 | 7 月末 | ...）   │
│ 3: 数据行 1                     │
│ 4: 数据行 2                     │
└────────────────────────────────┘
```

## shiftRows 方法详解

```java
/**
 * 将工作表中的行向下移动
 * 
 * @param startRow 起始行索引（包含）
 * @param endRow 结束行索引（包含）
 * @param nRows 移动的行数（正数向下，负数向上）
 */
void shiftRows(int startRow, int endRow, int nRows);
```

### 参数说明

- **startRow**: `1` - 从第 1 行开始（保留第 0 行标题）
- **endRow**: `sheet.getLastRowNum()` - 到最后一行
- **nRows**: `1` - 向下移动 1 行

### 特性

1. **保留样式**：所有单元格样式、公式、合并区域都会保留
2. **批量操作**：一次移动多行，性能优化
3. **自动调整**：自动处理行的插入和删除

## 自定义行样式

```java
private void setupCustomRow(Row customRow, Sheet sheet, Workbook workbook, String period) {
    // 1. 整行合并
    CellRangeAddress mergeRegion = new CellRangeAddress(1, 1, 0, 11);
    sheet.addMergedRegion(mergeRegion);
    
    // 2. 创建单元格
    Cell cell = customRow.createCell(0);
    
    // 3. 设置内容
    cell.setCellValue("统计期间：" + period);
    
    // 4. 设置样式
    CellStyle style = workbook.createCellStyle();
    style.setAlignment(HorizontalAlignment.LEFT);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    
    Font font = workbook.createFont();
    font.setFontHeightInPoints((short) 10);
    font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setFont(font);
    
    cell.setCellStyle(style);
}
```

## 扩展用法

### 1. 插入多行自定义内容

```java
// 下移 2 行
sheet.shiftRows(1, sheet.getLastRowNum(), 2);

// 创建两个自定义行
Row customRow1 = sheet.createRow(1);
Row customRow2 = sheet.createRow(2);

// 分别设置内容
setupCustomRow1(customRow1, workbook, "期间信息");
setupCustomRow2(customRow2, workbook, "制表人信息");
```

### 2. 条件显示自定义行

```java
if (showCustomInfo) {
    sheet.shiftRows(1, sheet.getLastRowNum(), 1);
    Row customRow = sheet.createRow(1);
    setupCustomRow(customRow, sheet, workbook, period);
}
```

### 3. 动态调整行高

```java
// 根据内容长度动态调整行高
String content = "统计期间：" + period + "  |  制表人：张三";
Row customRow = sheet.createRow(1);
customRow.setHeightInPoints(content.length() > 50 ? 30f : 20f);
```

## 注意事项

1. **执行顺序**：
   - 必须在 EasyPoi 生成表格之后
   - 必须在处理合并单元格之前
   
2. **行号计算**：
   - 移动后，原第 1 行变成第 2 行
   - 后续所有操作的行号都要 +1
   
3. **性能考虑**：
   - `shiftRows` 是批量操作，性能很好
   - 对于超大文件（>10000 行）也能快速完成

## 完整流程

```java
public byte[] exportToExcel(List<IndexData> dataList, String period) {
    // 1. EasyPoi 生成基础表格
    Workbook workbook = ExcelExportUtil.exportExcel(...);
    Sheet sheet = workbook.getSheetAt(0);
    
    // 2. 设置标题行
    Row titleRow = sheet.getRow(0);
    titleRow.setHeightInPoints(28f);
    
    // 3. 一次性下移所有行
    sheet.shiftRows(1, sheet.getLastRowNum(), 1);
    
    // 4. 创建自定义行
    Row customRow = sheet.createRow(1);
    setupCustomRow(customRow, sheet, workbook, period);
    
    // 5. 处理表头行（现在在第 2 行）
    Row headerRow = sheet.getRow(2);
    // ... 设置表头样式和合并
    
    // 6. 处理数据行
    processMergedCells(sheet, exportList);
    
    // 7. 输出
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();
    
    return out.toByteArray();
}
```

## 总结

使用 `shiftRows` 方法的核心优势：
- ✅ **代码简洁**：一行代替几十行
- ✅ **性能优秀**：POI 内部批量优化
- ✅ **易于维护**：逻辑清晰，不易出错
- ✅ **保留格式**：自动保留所有样式和公式
- ✅ **扩展性强**：轻松支持多行自定义内容

这是一个经过实践检验的优雅解决方案！
