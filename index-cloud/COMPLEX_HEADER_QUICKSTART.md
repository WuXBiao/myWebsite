# 复杂表头快速开始

## 核心概念

### ReportConfig - 使用复杂表头

```java
// 旧方式（已废弃）
List<String> headers = Arrays.asList("列 1", "列 2", "列 3");

// 新方式（支持多行合并）
List<List<String>> complexHeaders = Arrays.asList(
    Arrays.asList("列 1", "列 2", "列 3")  // 第 1 行
);
```

## 快速示例

### 1. 简单单行表头

```java
// 定义表头（二维列表）
List<List<String>> headers = Arrays.asList(
    Arrays.asList("用户ID", "用户名", "邮箱", "手机号")
);

// 准备数据
List<List<Object>> data = Arrays.asList(
    Arrays.asList(1, "张三", "zhangsan@example.com", "13800138000"),
    Arrays.asList(2, "李四", "lisi@example.com", "13900139000")
);

// 创建配置
ReportConfig config = new ReportConfig(
    "用户信息表",
    headers,
    data,
    new int[]{15, 20, 30, 20}
);

// 生成 Excel
byte[] excelBytes = MultiReportExcelUtil.mergeReportsToExcel(
    Arrays.asList(config)
);
```

### 2. 双层表头（跨列合并）

```java
/*
 * 效果：
 * ┌──────────────┬──────────────┐
 * │   第一季度    │   第二季度    │
 * ├──────┬───────┼──────┬───────┤
 * │ 销售额│销售量 │ 销售额│销售量 │
 */

List<List<String>> headers = Arrays.asList(
    // 第 1 行：季度分类
    Arrays.asList("第一季度", "第一季度", "第二季度", "第二季度"),
    // 第 2 行：具体指标
    Arrays.asList("销售额", "销售量", "销售额", "销售量")
);

List<List<Object>> data = Arrays.asList(
    Arrays.asList("100 万", "500 台", "150 万", "700 台")
);

ReportConfig config = new ReportConfig(
    "销售统计表",
    headers,
    data
);
```

### 3. 三层表头（复杂合并）

```java
/*
 * 效果：
 * ┌──────┬────────────────────────┐
 * │ 学号 │        成绩信息         │
 * │      ├────────┬───────┬───────┤
 * │      │  语文   │  数学  │ 英语  │
 * │      ├────┬───┼───┬───┼───┬───┤
 * │      │平时│期末│平时│期末│...│...│
 */

List<List<String>> headers = Arrays.asList(
    Arrays.asList("学号", "成绩信息", "成绩信息", "成绩信息", "成绩信息"),
    Arrays.asList("", "语文", "语文", "数学", "数学"),
    Arrays.asList("", "平时", "期末", "平时", "期末")
);

ReportConfig config = new ReportConfig(
    "学生成绩表",
    headers,
    data
);
```

### 4. 带业务分组

```java
// 定义表头
List<List<String>> headers = Arrays.asList(
    Arrays.asList("部门", "姓名", "职位", "基本工资")
);

// 准备数据
List<List<Object>> data = Arrays.asList(
    Arrays.asList("技术部", "张三", "工程师", "15000"),
    Arrays.asList("技术部", "李四", "高工", "20000"),
    Arrays.asList("销售部", "王五", "经理", "18000")
);

// 添加分组
List<GroupInfo> groups = new ArrayList<>();
groups.add(new GroupInfo("技术部", 0, 1));  // 前 2 行
groups.add(new GroupInfo("销售部", 2, 2));  // 第 3 行

ReportConfig config = new ReportConfig(
    "员工薪资表",
    headers,
    data,
    null,
    groups
);
```

## 关键要点

### 1. 表头结构

```java
// ✅ 正确：总是二维列表
List<List<String>> headers = Arrays.asList(
    Arrays.asList("列 1", "列 2", "列 3")
);

// ❌ 错误：一维列表（编译失败）
List<String> headers = Arrays.asList("列 1", "列 2", "列 3");
```

### 2. 空单元格处理

```java
// 使用空字符串 "" 表示与上行合并
Arrays.asList("学号", "成绩信息", "成绩信息")
Arrays.asList("", "语文", "数学")  // "" 会自动与上行"学号"合并
```

### 3. 每行列数可以不同

```java
// 第 1 行只有 2 列
Arrays.asList("基本信息", "联系信息")
// 第 2 行有 7 列
Arrays.asList("姓名", "年龄", "性别", "城市", "电话", "邮箱", "地址")
```

## 运行测试查看示例

```bash
# 运行基础测试
mvn test -Dtest=MultiReportExcelUtilTest

# 运行复杂表头测试
mvn test -Dtest=ComplexHeaderTest

# 查看生成的 Excel 文件
ls -lh /tmp/*test.xlsx
```

## 完整测试用例

查看以下测试类获取完整示例：
- `MultiReportExcelUtilTest.java` - 基础用法和分组功能
- `ComplexHeaderTest.java` - 各种复杂表头示例
