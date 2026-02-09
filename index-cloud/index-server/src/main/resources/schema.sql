-- ================================================
-- 指标管理平台数据库建表脚本（双表结构）
-- 数据库：H2
-- ================================================

DROP TABLE IF EXISTS t_index_data;
DROP TABLE IF EXISTS t_index_config;
DROP TABLE IF EXISTS t_index;

-- ================================================
-- 指标配置表（静态配置信息）
-- ================================================
CREATE TABLE t_index_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT,
    level INT,
    sort_order INT,
    category VARCHAR(100),
    sub_category VARCHAR(100),
    index_code VARCHAR(50),
    index_name VARCHAR(200),
    row_type VARCHAR(20) DEFAULT 'DATA',
    section_title VARCHAR(200),
    section_note VARCHAR(500),
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_config_parent_id ON t_index_config(parent_id);
CREATE INDEX idx_config_category ON t_index_config(category);
CREATE INDEX idx_config_sort_order ON t_index_config(sort_order);
CREATE INDEX idx_config_index_code ON t_index_config(index_code);

-- ================================================
-- 指标数据表（动态数据）
-- ================================================
CREATE TABLE t_index_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT NOT NULL,
    period VARCHAR(6) NOT NULL,
    balance DECIMAL(18,2),
    mom_increment DECIMAL(18,2),
    ytd_increment DECIMAL(18,2),
    ytd_growth_rate DECIMAL(10,4),
    deposit_ratio DECIMAL(10,4),
    ratio_vs_ytd DECIMAL(10,4),
    yearly_avg DECIMAL(18,2),
    interest_income DECIMAL(18,2),
    interest_expense DECIMAL(18,2),
    interest_rate DECIMAL(10,4),
    rate_vs_mom DECIMAL(10,4),
    rate_vs_yoy DECIMAL(10,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_data_config_id ON t_index_data(config_id);
CREATE INDEX idx_data_period ON t_index_data(period);
CREATE UNIQUE INDEX idx_data_config_period ON t_index_data(config_id, period);
