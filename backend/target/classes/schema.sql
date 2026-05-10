-- 药品分类表
CREATE TABLE IF NOT EXISTS drug_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品分类表';

-- 药品字典表
CREATE TABLE IF NOT EXISTS drug_dict (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_code VARCHAR(50) NOT NULL COMMENT '药品编码',
    generic_name VARCHAR(200) NOT NULL COMMENT '通用名',
    trade_name VARCHAR(200) COMMENT '商品名',
    specification VARCHAR(100) NOT NULL COMMENT '规格',
    dosage_form VARCHAR(50) NOT NULL COMMENT '剂型',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    manufacturer VARCHAR(200) COMMENT '生产厂家',
    approval_number VARCHAR(100) COMMENT '批准文号',
    category_id BIGINT COMMENT '药品分类ID',
    storage_condition VARCHAR(200) COMMENT '贮存条件',
    warning_level TINYINT DEFAULT 1 COMMENT '预警等级 1-低 2-中 3-高',
    minimum_stock INT DEFAULT 0 COMMENT '最低库存',
    maximum_stock INT COMMENT '最高库存',
    price DECIMAL(10,2) COMMENT '参考价格',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_drug_code (drug_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品字典表';

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    supplier_name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(50) COMMENT '联系电话',
    address VARCHAR(500) COMMENT '地址',
    business_license VARCHAR(100) COMMENT '营业执照号',
    qualification VARCHAR(200) COMMENT '资质证书',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_supplier_code (supplier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 采购计划表
CREATE TABLE IF NOT EXISTS purchase_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_no VARCHAR(50) NOT NULL COMMENT '计划编号',
    plan_name VARCHAR(200) COMMENT '计划名称',
    apply_department VARCHAR(100) COMMENT '申请部门',
    applicant VARCHAR(50) COMMENT '申请人',
    plan_date DATE COMMENT '计划日期',
    status TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-待审核 2-已审核 3-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_plan_no (plan_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划表';

-- 采购计划明细
CREATE TABLE IF NOT EXISTS purchase_plan_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL COMMENT '采购计划ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    quantity INT NOT NULL COMMENT '采购数量',
    estimated_price DECIMAL(10,2) COMMENT '预估单价',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    INDEX idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划明细表';

-- 入库单表
CREATE TABLE IF NOT EXISTS stock_in (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    in_no VARCHAR(50) NOT NULL COMMENT '入库单号',
    in_type TINYINT DEFAULT 1 COMMENT '入库类型 1-采购入库 2-退货入库 3-调拨入库',
    source_type TINYINT DEFAULT 1 COMMENT '来源类型 1-采购计划 2-其他',
    source_id BIGINT COMMENT '来源ID',
    supplier_id BIGINT COMMENT '供应商ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    warehouse_keeper VARCHAR(50) COMMENT '仓库管理员',
    in_time DATETIME COMMENT '入库时间',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待入库 1-已入库 2-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_in_no (in_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 入库明细
CREATE TABLE IF NOT EXISTS stock_in_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    in_id BIGINT NOT NULL COMMENT '入库单ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(100) NOT NULL COMMENT '批次号',
    quantity INT NOT NULL COMMENT '入库数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE NOT NULL COMMENT '效期',
    location VARCHAR(100) COMMENT '货位',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    INDEX idx_in_id (in_id),
    INDEX idx_drug_id (drug_id),
    INDEX idx_batch_no (batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细表';

-- 出库单表
CREATE TABLE IF NOT EXISTS stock_out (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    out_no VARCHAR(50) NOT NULL COMMENT '出库单号',
    out_type TINYINT DEFAULT 1 COMMENT '出库类型 1-领用出库 2-销售出库 3-调拨出库 4-报损',
    apply_department VARCHAR(100) COMMENT '申请科室',
    applicant VARCHAR(50) COMMENT '申请人',
    reviewer VARCHAR(50) COMMENT '审核人',
    review_time DATETIME COMMENT '审核时间',
    out_time DATETIME COMMENT '出库时间',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-审核通过 2-已出库 3-已拒绝',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_out_no (out_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 出库明细
CREATE TABLE IF NOT EXISTS stock_out_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    out_id BIGINT NOT NULL COMMENT '出库单ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(100) COMMENT '批次号',
    quantity INT NOT NULL COMMENT '出库数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    INDEX idx_out_id (out_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细表';

-- 库存表
CREATE TABLE IF NOT EXISTS stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(100) NOT NULL COMMENT '批次号',
    quantity INT NOT NULL COMMENT '库存数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE NOT NULL COMMENT '效期',
    location VARCHAR(100) COMMENT '货位',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    INDEX idx_drug_id (drug_id),
    INDEX idx_batch_no (batch_no),
    INDEX idx_expiry_date (expiry_date),
    UNIQUE KEY uk_drug_batch (drug_id, batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 盘点任务表
CREATE TABLE IF NOT EXISTS inventory_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_no VARCHAR(50) NOT NULL COMMENT '任务编号',
    task_name VARCHAR(200) COMMENT '任务名称',
    task_type TINYINT DEFAULT 1 COMMENT '盘点类型 1-全盘 2-抽盘',
    creator VARCHAR(50) COMMENT '创建人',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待开始 1-进行中 2-已完成',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_task_no (task_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点任务表';

-- 盘点明细表
CREATE TABLE IF NOT EXISTS inventory_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(100) COMMENT '批次号',
    system_quantity INT NOT NULL COMMENT '系统数量',
    actual_quantity INT COMMENT '实际数量',
    diff_quantity INT COMMENT '差异数量',
    diff_reason VARCHAR(500) COMMENT '差异原因',
    status TINYINT DEFAULT 0 COMMENT '状态 0-未盘点 1-已盘点 2-已处理',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    INDEX idx_task_id (task_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细表';

-- 近效期预警表
CREATE TABLE IF NOT EXISTS expiry_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    batch_no VARCHAR(100) NOT NULL COMMENT '批次号',
    stock_id BIGINT COMMENT '库存ID',
    expiry_date DATE NOT NULL COMMENT '效期',
    remaining_days INT COMMENT '剩余天数',
    quantity INT NOT NULL COMMENT '数量',
    alert_level TINYINT DEFAULT 1 COMMENT '预警等级 1-30天 2-60天',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待处理 1-已退货 2-已销毁 3-已使用',
    handler VARCHAR(50) COMMENT '处理人',
    handle_time DATETIME COMMENT '处理时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    INDEX idx_drug_id (drug_id),
    INDEX idx_batch_no (batch_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='近效期预警表';

-- 科室表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(50) NOT NULL COMMENT '科室编码',
    dept_name VARCHAR(100) NOT NULL COMMENT '科室名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父科室ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_dept_code (dept_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- 货位表
CREATE TABLE IF NOT EXISTS warehouse_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_code VARCHAR(50) NOT NULL COMMENT '货位编码',
    location_name VARCHAR(100) NOT NULL COMMENT '货位名称',
    warehouse_name VARCHAR(100) COMMENT '仓库名称',
    area VARCHAR(50) COMMENT '区域',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_location_code (location_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货位表';
