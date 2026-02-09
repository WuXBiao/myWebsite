-- ================================================
-- 指标管理平台初始化数据（双表结构）
-- ================================================

-- 清空数据
DELETE FROM t_index_data;
DELETE FROM t_index_config;

-- ================================================
-- 插入配置数据
-- ================================================

-- 横向分类标题：贷款结构：分条线
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(1, NULL, 0, 1, NULL, NULL, 'SEC001', NULL, 'SECTION', '贷款结构：分条线', '利息收入/年初至期末累计 天数/当年天数/年日均', NULL);

-- 贷款结构：分条线 下的配置
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(2, 1, 1, 1, NULL, NULL, 'LOAN001', '各项贷款', 'DATA', NULL, NULL, '管会系统-报表管理-贷款'),
(3, 1, 1, 2, NULL, NULL, 'LOAN002', '人民币公司表内信贷资产（不含票据）', 'DATA', NULL, NULL, '管会系统-报表管理'),
(4, 1, 1, 3, NULL, NULL, 'LOAN003', '行标小贷（不含贴现）', 'DATA', NULL, NULL, '管会系统-资产负债'),
(5, 1, 1, 4, NULL, NULL, 'LOAN004', '零售信贷（不含20%网贷部分）', 'DATA', NULL, NULL, '管会系统-报表管理'),
(6, 1, 1, 5, NULL, NULL, 'LOAN005', '互联网贷款贴贷20%部分', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(7, 1, 1, 6, NULL, NULL, 'LOAN006', '外币贷款', 'DATA', NULL, NULL, '管会系统-报表管理');

-- 横向分类标题：贷款结构：重点产品/客户
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(8, NULL, 0, 2, NULL, NULL, 'SEC002', NULL, 'SECTION', '贷款结构：重点产品/客户', NULL, NULL);

-- 中长期贷款
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(9, 8, 1, 1, '中长期贷款', NULL, 'LOAN007', NULL, 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(10, 9, 2, 1, '中长期贷款', NULL, 'LOAN008', '1.流动资金贷款', 'DATA', NULL, NULL, '管会-产品分析报表'),
(11, 9, 2, 2, '中长期贷款', NULL, 'LOAN009', '2.三年期以上流动资金贷款', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(12, 9, 2, 3, '中长期贷款', NULL, 'LOAN010', '3.项目贷款', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(13, 9, 2, 4, '中长期贷款', NULL, 'LOAN011', '4.银团及并购', 'DATA', NULL, NULL, 'A0001-管会产品分析'),
(14, 9, 2, 5, '中长期贷款', NULL, 'LOAN012', '5.按揭贷款', 'DATA', NULL, NULL, 'A0001-管会产品分析');

-- 短期贷款
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(15, 8, 1, 2, '短期贷款', NULL, 'LOAN013', NULL, 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(16, 15, 2, 1, '短期贷款', NULL, 'LOAN014', '6.短贷', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(17, 15, 2, 2, '短期贷款', NULL, 'LOAN015', '7.超短贷', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(18, 15, 2, 3, '短期贷款', NULL, 'LOAN016', '8.房地产开发项目贷款', 'DATA', NULL, NULL, 'A0001-管会产品分析报表');

-- 房地产和平台
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(19, 8, 1, 3, '房地产和平台', NULL, 'LOAN017', NULL, 'DATA', NULL, NULL, 'A0001-管会产品分析'),
(20, 19, 2, 1, '房地产和平台', NULL, 'LOAN018', '9.经营性物业贷款', 'DATA', NULL, NULL, NULL),
(21, 19, 2, 2, '房地产和平台', NULL, 'LOAN019', '10.城建公共类国企', 'DATA', NULL, NULL, '宁线提供');

-- 小贷其他重点产品
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(22, 8, 1, 4, '小贷其他重点产品', NULL, 'LOAN020', NULL, 'DATA', NULL, NULL, NULL),
(23, 22, 2, 1, '小贷其他重点产品', NULL, 'LOAN021', '16.园区（标准厂房）', 'DATA', NULL, NULL, '易链十台-行标规模'),
(24, 22, 2, 2, '小贷其他重点产品', NULL, 'LOAN022', '17.一抵类房抵贷', 'DATA', NULL, NULL, '易链十台-行标规模'),
(25, 22, 2, 3, '小贷其他重点产品', NULL, 'LOAN023', '18.二抵类房抵贷（余值贷）', 'DATA', NULL, NULL, '易链十台-行标规模');

-- 其他指标
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(26, 8, 1, 5, '其他指标', NULL, 'LOAN024', '19.对公贷款分行前十大客户合计值', 'DATA', NULL, NULL, '管会系统-首页');

-- 横向分类标题：债券资产结构
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(27, NULL, 0, 3, NULL, NULL, 'SEC003', NULL, 'SECTION', '债券资产结构', NULL, '汇总');

-- 债务投资
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(28, 27, 1, 1, '债务投资', NULL, 'BOND001', NULL, 'DATA', NULL, NULL, NULL),
(29, 28, 2, 1, '债务投资', '三分类', 'BOND002', 'FVTPL', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(30, 28, 2, 2, '债务投资', '三分类', 'BOND003', 'FVOCI', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(31, 28, 2, 3, '债务投资', '三分类', 'BOND004', 'AC', 'DATA', NULL, NULL, 'A0001-管会产品分析报表');

-- 横向分类标题：同业资产结构
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(32, NULL, 0, 4, NULL, NULL, 'SEC004', NULL, 'SECTION', '同业资产结构', NULL, NULL);

-- 同业资产
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(33, 32, 1, 1, NULL, NULL, 'INTER001', '存放同业', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(34, 32, 1, 2, NULL, NULL, 'INTER002', '银行同业借款', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(35, 32, 1, 3, NULL, NULL, 'INTER003', '同业投资', 'DATA', NULL, NULL, NULL);

-- 横向分类标题：票证资产
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(36, NULL, 0, 5, NULL, NULL, 'SEC005', NULL, 'SECTION', '票证资产', NULL, '汇总');

-- 表内持票
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(37, 36, 1, 1, '表内持票', NULL, 'BILL001', NULL, 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(38, 37, 2, 1, '表内持票', '持票分类', 'BILL002', '1.商票直贴', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(39, 37, 2, 2, '表内持票', '持票分类', 'BILL003', '2.银票直贴', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(40, 37, 2, 3, '表内持票', '持票分类', 'BILL004', '3.转贴现', 'DATA', NULL, NULL, NULL);

-- 国内贸易融资
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(41, 36, 1, 2, '国内贸易融资', NULL, 'TRADE001', NULL, 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(42, 41, 2, 1, '国内贸易融资', '国内贸易融资分类', 'TRADE002', '1.议付', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(43, 41, 2, 2, '国内贸易融资', '国内贸易融资分类', 'TRADE003', '2.贷链', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(44, 41, 2, 3, '国内贸易融资', '国内贸易融资分类', 'TRADE004', '3.福费廷', 'DATA', NULL, NULL, NULL);

-- 横向分类标题：其他资产
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(45, NULL, 0, 6, NULL, NULL, 'SEC006', NULL, 'SECTION', '其他资产', NULL, NULL);

-- 其他资产
INSERT INTO t_index_config (id, parent_id, level, sort_order, category, sub_category, index_code, index_name, row_type, section_title, section_note, remark) VALUES
(46, 45, 1, 1, NULL, NULL, 'OTHER001', '缴存中央银行财政性存款', 'DATA', NULL, NULL, 'A0001-管会产品分析报表'),
(47, 45, 1, 2, NULL, NULL, 'OTHER002', '现金', 'DATA', NULL, NULL, '建议取用分支机构'),
(48, 45, 1, 3, NULL, NULL, 'TOTAL001', '总资产', 'DATA', NULL, NULL, NULL);

-- ================================================
-- 插入数据（期间：202507）
-- ================================================

INSERT INTO t_index_data (config_id, period, balance, mom_increment, ytd_increment, ytd_growth_rate, deposit_ratio, ratio_vs_ytd, yearly_avg, interest_income, interest_expense, interest_rate, rate_vs_mom, rate_vs_yoy) VALUES
-- 贷款结构：分条线
(2, '202507', 5000000.00, 150000.00, 300000.00, 6.38, 100.00, 0.00, 4800000.00, 173250.00, NULL, 4.50, -0.05, -0.30),
(3, '202507', 3200000.00, 80000.00, 180000.00, 5.96, 64.00, -0.50, 3100000.00, 108000.00, NULL, 4.50, -0.01, -0.12),
(4, '202507', 1800000.00, 50000.00, 120000.00, 7.14, 36.00, 0.50, 1700000.00, 76500.00, NULL, 4.50, -0.03, -0.18),
(5, '202507', 1200000.00, 30000.00, 80000.00, 7.14, 24.00, 0.10, 1150000.00, 51750.00, NULL, 4.50, 0.00, -0.25),
(6, '202507', 300000.00, 10000.00, 30000.00, 11.11, 6.00, 0.20, 290000.00, 13050.00, NULL, 4.50, -0.05, -0.30),
(7, '202507', 200000.00, 5000.00, 15000.00, 8.11, 4.00, 0.10, 195000.00, 8775.00, NULL, 4.50, -0.02, -0.20),

-- 中长期贷款
(9, '202507', 2500000.00, 60000.00, 150000.00, 6.38, NULL, NULL, 2400000.00, 108000.00, NULL, 4.50, -0.05, -0.28),
(10, '202507', 1200000.00, 30000.00, 80000.00, 7.14, NULL, NULL, 1150000.00, 51750.00, NULL, 4.50, -0.03, -0.25),
(11, '202507', 500000.00, 15000.00, 40000.00, 8.70, NULL, NULL, 480000.00, 21600.00, NULL, 4.50, -0.05, -0.30),
(12, '202507', 800000.00, 15000.00, 30000.00, 3.90, NULL, NULL, 770000.00, 34650.00, NULL, 4.50, -0.05, -0.30),
(13, '202507', 300000.00, 8000.00, 20000.00, 7.14, NULL, NULL, 290000.00, 13050.00, NULL, 4.50, -0.08, -0.35),
(14, '202507', 400000.00, 10000.00, 25000.00, 6.67, NULL, NULL, 385000.00, 17325.00, NULL, 4.50, -0.05, -0.28),

-- 短期贷款
(15, '202507', 800000.00, 20000.00, 50000.00, 6.67, NULL, NULL, 770000.00, 34650.00, NULL, 4.50, -0.05, -0.32),
(16, '202507', 500000.00, 12000.00, 30000.00, 6.38, NULL, NULL, 480000.00, 21600.00, NULL, 4.50, -0.05, -0.30),
(17, '202507', 300000.00, 8000.00, 20000.00, 7.14, NULL, NULL, 290000.00, 13050.00, NULL, 4.50, -0.05, -0.35),
(18, '202507', 200000.00, 5000.00, 15000.00, 8.11, NULL, NULL, 192500.00, 8663.00, NULL, 4.50, -0.10, -0.50),

-- 房地产和平台
(19, '202507', 600000.00, 15000.00, 40000.00, 7.14, NULL, NULL, 580000.00, 26100.00, NULL, 4.50, -0.08, -0.40),
(20, '202507', 300000.00, 8000.00, 20000.00, 7.14, NULL, NULL, 290000.00, 13050.00, NULL, 4.50, -0.08, -0.40),
(21, '202507', 300000.00, 7000.00, 20000.00, 7.14, NULL, NULL, 290000.00, 13050.00, NULL, 4.50, -0.08, -0.40),

-- 小贷其他重点产品
(22, '202507', 400000.00, 10000.00, 30000.00, 8.11, NULL, NULL, 385000.00, 17325.00, NULL, 4.50, -0.05, -0.30),
(23, '202507', 150000.00, 4000.00, 12000.00, 8.70, NULL, NULL, 145000.00, 6525.00, NULL, 4.50, -0.05, -0.30),
(24, '202507', 150000.00, 4000.00, 10000.00, 7.14, NULL, NULL, 145000.00, 6525.00, NULL, 4.50, -0.05, -0.30),
(25, '202507', 100000.00, 2000.00, 8000.00, 8.70, NULL, NULL, 95000.00, 4275.00, NULL, 4.50, -0.05, -0.30),

-- 其他指标
(26, '202507', 800000.00, 20000.00, 50000.00, 6.67, NULL, NULL, 770000.00, 34650.00, NULL, 4.50, -0.05, -0.30),

-- 债务投资
(28, '202507', 800000.00, 20000.00, 50000.00, 6.67, NULL, NULL, 780000.00, 27300.00, NULL, 3.50, -0.02, -0.10),
(29, '202507', 200000.00, 5000.00, 12000.00, 6.38, NULL, NULL, 195000.00, 6825.00, NULL, 3.50, -0.02, -0.10),
(30, '202507', 300000.00, 8000.00, 20000.00, 7.14, NULL, NULL, 292000.00, 10220.00, NULL, 3.50, -0.02, -0.10),
(31, '202507', 300000.00, 7000.00, 18000.00, 6.38, NULL, NULL, 293000.00, 10255.00, NULL, 3.50, -0.02, -0.10),

-- 同业资产
(33, '202507', 200000.00, 5000.00, 15000.00, 8.11, NULL, NULL, 195000.00, 5850.00, NULL, 3.00, -0.05, -0.15),
(34, '202507', 200000.00, 5000.00, 12000.00, 6.38, NULL, NULL, 195000.00, 5850.00, NULL, 3.00, -0.05, -0.15),
(35, '202507', 200000.00, 5000.00, 13000.00, 6.96, NULL, NULL, 195000.00, 5850.00, NULL, 3.00, -0.05, -0.15),

-- 表内持票
(37, '202507', 300000.00, 8000.00, 20000.00, 7.14, NULL, NULL, 292000.00, 8760.00, NULL, 3.00, -0.03, -0.12),
(38, '202507', 100000.00, 3000.00, 7000.00, 7.53, NULL, NULL, 97000.00, 2910.00, NULL, 3.00, -0.03, -0.12),
(39, '202507', 120000.00, 3000.00, 8000.00, 7.14, NULL, NULL, 117000.00, 3510.00, NULL, 3.00, -0.03, -0.12),
(40, '202507', 80000.00, 2000.00, 5000.00, 6.67, NULL, NULL, 78000.00, 2340.00, NULL, 3.00, -0.03, -0.12),

-- 国内贸易融资
(41, '202507', 150000.00, 4000.00, 10000.00, 7.14, NULL, NULL, 146000.00, 5110.00, NULL, 3.50, -0.02, -0.10),
(42, '202507', 50000.00, 1500.00, 3500.00, 7.53, NULL, NULL, 48500.00, 1698.00, NULL, 3.50, -0.02, -0.10),
(43, '202507', 50000.00, 1500.00, 3500.00, 7.53, NULL, NULL, 48500.00, 1698.00, NULL, 3.50, -0.02, -0.10),
(44, '202507', 50000.00, 1000.00, 3000.00, 6.38, NULL, NULL, 49000.00, 1714.00, NULL, 3.50, -0.02, -0.10),

-- 其他资产
(46, '202507', 100000.00, 2000.00, 5000.00, 5.26, NULL, NULL, 98000.00, NULL, NULL, NULL, NULL, NULL),
(47, '202507', 50000.00, 1000.00, 2000.00, 4.17, NULL, NULL, 49000.00, NULL, NULL, NULL, NULL, NULL),
(48, '202507', 12000000.00, 300000.00, 700000.00, 6.19, NULL, NULL, 11700000.00, 409500.00, 85000.00, NULL, NULL, NULL);
