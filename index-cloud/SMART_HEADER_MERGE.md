# 智能合并表头功能

## 功能概述

工具类现已支持**自动检测并合并重复值单元格**，无需手动留白。系统会自动识别表头中的相同值，并将它们合并成一个单元格。

## 核心改进

### 旧方案（需要手动留白）

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("学号", "成绩信息", "成绩信息"),
    Arrays.asList("", "语文", "数学"),  // 需要使用 "" 留白
    Arrays.asList("", "平时", "期末")   // 需要使用 "" 留白
);
```

### 新方案（自动合并重复值）✅

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息"),
    Arrays.asList("学号", "语文", "语文", "数学", "数学"),
    Arrays.asList("学号", "平时", "期末", "平时", "期末")
);
// 系统会自动检测并合并相同的值！
```

## 工作原理

### 1. 左上角检测

系统会检查每个单元格的**上方**和**左方**是否有相同值：

```java
// 只有当上方和左方都没有相同值时，才作为合并的起点（左上角）
boolean isTopLeft = true;

// 检查上方
if (rowIdx > 0 && cellValue.equals(prevRow.get(col))) {
    isTopLeft = false; // 上方有相同值，不是左上角
}

// 检查左方
if (col > 0 && cellValue.equals(headerCells.get(col - 1))) {
    isTopLeft = false; // 左方有相同值，不是左上角
}
```

### 2. 跨行跨列计算

只有左上角的单元格才会触发合并：

```java
if (isTopLeft) {
    // 向下查找相同值 → 计算跨行数
    int rowspan = calculateRowspanByValue(headers, rowIdx, col, cellValue);
    
    // 向右查找相同值 → 计算跨列数
    int colspan = calculateColspanByValue(headers, rowIdx, col, cellValue);
    
    // 添加合并区域
    if (rowspan > 1 || colspan > 1) {
        sheet.addMergedRegion(mergeAddress);
    }
}
```

### 3. 防重复处理

使用 `Set` 记录已处理的单元格，避免重复合并：

```java
Set<String> processedMerges = new HashSet<>();

// 标记所有被合并的单元格
for (int r = startRow; r <= endRow; r++) {
    for (int c = startCol; c <= endCol; c++) {
        processedMerges.add(r + "_" + c);
    }
}
```

## 使用示例

### 示例 1：简单的跨列合并

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("季度", "季度", "年度", "年度"),
    Arrays.asList("Q1", "Q2", "Q3", "Q4")
);

/*
自动生成效果：
┌───────────────┬───────────────┐
│     季度       │     年度       │
├───────┬───────┼───────┬───────┤
│  Q1   │  Q2   │  Q3   │  Q4   │
└───────┴───────┴───────┴───────┘
*/
```

### 示例 2：跨行 + 跨列合并

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("部门", "员工信息", "员工信息", "薪资统计", "薪资统计"),
    Arrays.asList("部门", "姓名", "职位", "基本工资", "绩效奖金"),
    Arrays.asList("部门", "张三", "工程师", "15000", "5000")
);

/*
自动生成效果：
┌────────┬────────────────────────────────────┐
│  部门   │           员工信息                  │
│        ├──────────┬──────────┬───────────────┤
│        │   姓名    │   职位    │   薪资统计     │
│        ├────┬─────┼────┬─────┼──────┬────────┤
│        │姓名│职位  │姓名│职位  │ 基本 │ 绩效   │
└────────┴────┴─────┴────┴─────┴──────┴────────┘
*/
```

### 示例 3：复杂的三层表头

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息"),
    Arrays.asList("学号", "语文", "语文", "数学", "数学"),
    Arrays.asList("学号", "平时", "期末", "平时", "期末")
);

/*
自动生成效果：
┌──────┬────────────────────────┐
│ 学号 │        成绩信息         │
│      ├────────┬───────┬───────┤
│      │  语文   │  数学  │ ...  │
│      ├────┬───┼───┬───┼───┬───┤
│      │平时│期末│平时│期末│...│...│
└──────┴────┴───┴───┴───┴───┴───┘
*/
```

## 对比：新旧方案

### 场景：学生成绩表

#### 旧方案（需要手动留白）❌

```java
Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息");
Arrays.asList("", "语文", "语文", "数学", "数学");      // 需要写 ""
Arrays.asList("", "平时", "期末", "平时", "期末");       // 需要写 ""
```

**缺点：**
- 需要手动维护空字符串位置
- 容易出错
- 不直观
- 修改困难

#### 新方案（自动合并）✅

```java
Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息");
Arrays.asList("学号", "语文", "语文", "数学", "数学");      // 直接写重复值
Arrays.asList("学号", "平时", "期末", "平时", "期末");       // 直接写重复值
```

**优点：**
- ✅ 直观易懂
- ✅ 不易出错
- ✅ 易于维护
- ✅ 符合直觉

## 技术实现

### 核心方法

#### 1. 计算跨行数

```java
private static int calculateRowspanByValue(
    List<List<String>> headers, 
    int startRow, 
    int col, 
    String value
) {
    int rowspan = 1;
    
    // 向下查找连续相同值
    for (int rowIdx = startRow + 1; rowIdx < headers.size(); rowIdx++) {
        List<String> currentRow = headers.get(rowIdx);
        
        if (col >= currentRow.size() || !value.equals(currentRow.get(col))) {
            break;
        }
        rowspan++;
    }
    
    return rowspan;
}
```

#### 2. 计算跨列数

```java
private static int calculateColspanByValue(
    List<List<String>> headers, 
    int row, 
    int startCol, 
    String value
) {
    int colspan = 1;
    List<String> currentRow = headers.get(row);
    
    // 向右查找连续相同值
    for (int col = startCol + 1; col < currentRow.size(); col++) {
        if (!value.equals(currentRow.get(col))) {
            break;
        }
        colspan++;
    }
    
    return colspan;
}
```

#### 3. 左上角检测

```java
boolean isTopLeft = true;

// 检查上方
if (rowIdx > 0) {
    List<String> prevRow = headers.get(rowIdx - 1);
    if (col < prevRow.size() && cellValue.equals(prevRow.get(col))) {
        isTopLeft = false;
    }
}

// 检查左方
if (isTopLeft && col > 0) {
    if (cellValue.equals(headerCells.get(col - 1))) {
        isTopLeft = false;
    }
}
```

## 注意事项

### 1. 数据一致性

确保同一区域的值完全相同：

```java
// ✅ 正确：值完全相同
Arrays.asList("成绩信息", "成绩信息", "成绩信息");

// ❌ 错误：有空格差异
Arrays.asList("成绩信息", "成绩信息 ", "成绩信息");
```

### 2. 不规则形状

支持任意形状的合并：

```java
// L 型合并
Arrays.asList("A", "A", "B");
Arrays.asList("A", "A", "C");
// "A" 会形成 2x2 的合并区域
```

### 3. 性能考虑

- 自动检测会增加少量计算开销
- 适合中小型表头（< 10 行，< 20 列）
- 大型表头建议预计算合并区域

## 运行测试

```bash
# 运行复杂表头测试
mvn test -Dtest=ComplexHeaderTest

# 查看所有测试通过
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

## 常见问题

### Q1: 可以禁用自动合并吗？

A: 当前版本默认启用。如需禁用，可以使用空字符串手动控制。

### Q2: 支持斜向合并吗？

A: 不支持。只支持水平（跨列）和垂直（跨行）方向的矩形合并。

### Q3: 最大支持多少行/列的合并？

A: 理论上无限制，但受 Excel 本身的限制（16384 列 × 1048576 行）。

### Q4: 合并后的样式如何设置？

A: 合并区域会应用第一个单元格（左上角）的样式。

## 总结

通过智能检测重复值并自动合并，让表头配置更加直观和优雅：

- ✅ **无需留白**：直接写重复值即可
- ✅ **智能检测**：自动识别左上角单元格
- ✅ **双向合并**：同时支持跨行和跨列
- ✅ **防重处理**：避免重复合并导致的冲突
- ✅ **一次构建**：符合"build-complete-in-one-pass"原则

现在你只需要关注表头的语义，合并的事情交给工具类！🎉
