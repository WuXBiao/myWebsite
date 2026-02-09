# MyBatis 指标管理项目更新说明

## 项目变更概述
我们将原基于JPA的指标管理系统迁移到了MyBatis，实现了以下核心功能：

## 主要变更内容

### 1. 依赖更新
- 将Spring Data JPA替换为MyBatis Spring Boot Starter
- 保留了MySQL驱动、H2数据库等其他必要依赖

### 2. 数据访问层实现
- 创建了IndexConfigMapper和IndexDataMapper接口
- 实现了对应的XML映射文件，包含一对一关联查询
- 配置了关联查询结果映射（IndexDataWithConfigResultMap）

### 3. 服务层重构
- 创建了MyBatisIndexService接口及其实现
- 更新了IndexServiceImpl，使其基于MyBatis实现
- 保持了原有API接口不变，实现平滑迁移

### 4. 配置文件调整
- 更新了application.properties配置MyBatis参数
- 移除了JPA相关配置
- 添加了Mapper XML文件位置配置

### 5. 启动类更新
- 在IndexApplication中添加了@MapperScan注解

## 核心功能特性

### 一对一关联查询
- IndexData查询自动关联IndexConfig信息
- 使用LEFT JOIN实现高效的关联查询
- 通过association标签实现对象嵌套映射

### 指标层级处理
- 支持多级指标配置（通过parentId实现）
- 提供树形结构查询功能
- 按层级和排序号组织数据

### 数据展示
- 结合指标配置和实际数据进行展示
- 支持按期间、分类等条件查询
- 提供树形结构的综合展示

## 技术优势
- 更灵活的SQL控制能力
- 更高效的一对一关联查询
- 更好的性能表现
- 更清晰的数据关系映射


