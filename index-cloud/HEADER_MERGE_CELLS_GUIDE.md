# 表头合并单元格功能说明

## 功能概述

工具类现已支持**表头中的空白单元格自动与上行合并**，通过空字符串 `""` 来实现跨行合并效果。

## 工作原理

### 1. 空字符串触发合并

```java
List<List<String>> headers = Arrays.asList(
    // 第 1 行
    Arrays.asList("学号", "成绩信息", "成绩信息"),
    // 第 2 行
    Arrays.asList("", "语文", "数学")  // "" 会自动与上行的"学号"合并
);
```

### 2. 自动计算跨行数

系统会自动向下查找连续的空单元格，计算需要跨多少行：

```java
// 示例：
Arrays.asList("部门", "员工信息", "员工信息")
Arrays.asList("", "姓名", "职位")      // "部门"下方是空，跨 2 行
Arrays.asList("", "基本工资", "绩效")   // 继续为空，但遇到非空停止

// 结果："部门"单元格会跨 2 行合并（A1:A2）
```

### 3. 避免与标题冲突

- 报表标题行（第 0 行）总是合并 A-L 列
- 表头的跨行合并从第 1 行之后开始
- 不会创建与标题行重叠的合并区域

## 使用示例

### 示例 1：简单的跨列合并

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("季度", "季度", "年度"),
    Arrays.asList("Q1", "Q2", "总计")
);

/*
效果：
┌───────────────┬───────────────┬─────────┐
│     季度       │     季度       │  年度    │
├───────┬───────┼───────┬───────┼─────────┤
│  Q1   │  Q2   │  Q1   │  Q2   │  总计    │
└───────┴───────┴───────┴───────┴─────────┘
*/
```

### 示例 2：跨行合并（空白单元格）

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("学号", "成绩信息", "成绩信息"),
    Arrays.asList("", "语文", "数学"),
    Arrays.asList("", "平时", "期末")
);

/*
效果：
┌──────┬────────────────────────┐
│ 学号 │        成绩信息         │
│      ├────────┬───────┬───────┤
│      │  语文   │  数学  │ ...  │
│      ├────┬───┼───┬───┼───┬───┤
│      │平时│期末│平时│期末│...│...│
└──────┴────┴───┴───┴───┴───┴───┘
*/
```

### 示例 3：复杂的跨行跨列合并

```java
List<List<String>> headers = Arrays.asList(
    Arrays.asList("部门", "员工信息", "员工信息", "薪资统计", "薪资统计"),
    Arrays.asList("", "姓名", "职位", "基本工资", "绩效奖金")
);

/*
效果：
┌────────┬────────────────────────────────────┐
│  部门   │           员工信息                  │
│        ├──────────┬──────────┬───────────────┤
│        │   姓名    │   职位    │   薪资统计     │
│        ├────┬─────┼────┬─────┼──────┬────────┤
│        │姓名│职位  │姓名│职位  │ 基本 │ 绩效   │
└────────┴────┴─────┴────┴─────┴──────┴────────┘
*/
```

## 注意事项

### 1. 第一列的特殊处理

如果第一列在第二行是空字符串，会与上行合并，但**不会与报表标题行合并**：

```java
// ✅ 正确：第一列的空会与"部门"合并
Arrays.asList("部门", "员工信息")
Arrays.asList("", "姓名")

// ⚠️ 注意：不会与报表标题"员工薪资统计表"合并
```

### 2. 连续空单元格

系统会向下查找所有连续的空单元格：

```java
// 3 行表头
Arrays.asList("分类", "数据 1", "数据 2")
Arrays.asList("", "子项 1", "子项 2")    // 第 1 列继续为空
Arrays.asList("", "明细 1", "明细 2")    // 第 1 列继续为空

// 结果："分类"会跨 3 行合并（A1:A3）
```

### 3. 不规则表头

每行的列数可以不同：

```java
// 第 1 行只有 2 列
Arrays.asList("基本信息", "联系信息")

// 第 2 行有 7 列
Arrays.asList("姓名", "年龄", "性别", "城市", "电话", "邮箱", "地址")

// "基本信息"会自动跨 4 列合并（A-D）
// "联系信息"会自动跨 3 列合并（E-G）
```

## 技术实现

### calculateRowspan 方法

```java
private static int calculateRowspan(List<List<String>> headers, int startRow, int col) {
    int rowspan = 1;
    
    // 从下一行开始，向下查找连续的空单元格
    for (int rowIdx = startRow + 1; rowIdx < headers.size(); rowIdx++) {
        List<String> currentRow = headers.get(rowIdx);
        
        // 如果当前行没有这一列，或者这一列是空字符串，则继续
        if (col >= currentRow.size() || 
            currentRow.get(col) == null || 
            currentRow.get(col).isEmpty()) {
            rowspan++;
        } else {
            // 遇到非空单元格，停止
            break;
        }
    }
    
    return rowspan;
}
```

### 合并逻辑

```java
// 计算跨行数
int rowspan = calculateRowspan(headers, rowIdx, col);

// 如果有跨行且不在标题行，添加合并区域
if (rowspan > 1 && currentRowNum - rowspan > 0) {
    CellRangeAddress mergeAddress = new CellRangeAddress(
        currentRowNum - rowspan,  // 起始行
        currentRowNum - 1,        // 结束行
        col,                      // 起始列
        col                       // 结束列
    );
    sheet.addMergedRegion(mergeAddress);
}
```

## 测试验证

运行以下测试查看效果：

```bash
# 运行复杂表头测试
mvn test -Dtest=ComplexHeaderTest

# 查看生成的 Excel 文件
open /tmp/merged_complex_header_test.xlsx
open /tmp/groups_with_complex_header_test.xlsx
```

## 常见问题

### Q1: 如何避免合并单元格？

A: 在所有位置都填充具体的值，不要使用空字符串：

```java
// ❌ 会合并
Arrays.asList("A", "B")
Arrays.asList("", "C")

// ✅ 不会合并
Arrays.asList("A", "B")
Arrays.asList("D", "C")
```

### Q2: 可以跨列合并吗？

A: 当前版本只支持**跨行合并**。跨列合并需要通过相邻列使用相同的值来实现：

```java
// 实现跨列效果
Arrays.asList("大分类", "大分类", "大分类")
Arrays.asList("子 1", "子 2", "子 3")
```

### Q3: 最多支持多少行表头？

A: 理论上无限制，但建议不超过 **5 行**，否则影响美观和可读性。

## 总结

通过简单地在表头中使用空字符串 `""`，即可实现优雅的跨行合并效果：

- ✅ **自动检测**：系统自动计算跨行数
- ✅ **智能避让**：不会与报表标题冲突
- ✅ **灵活配置**：支持不规则表头结构
- ✅ **一次构建**：符合"build-complete-in-one-pass"原则
