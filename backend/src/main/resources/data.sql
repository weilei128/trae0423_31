-- 药品分类
INSERT INTO drug_category (category_name, parent_id, sort_order) VALUES 
('抗生素', 0, 1),
('解热镇痛药', 0, 2),
('消化系统', 0, 3);

-- 药品字典
INSERT INTO drug_dict (drug_code, generic_name, trade_name, specification, dosage_form, unit, manufacturer, warning_level, minimum_stock, price) VALUES 
('YP001', '青霉素', '青霉素钠', '80万U', '注射剂', '支', '华北制药', 1, 100, 6.00),
('YP002', '阿莫西林', '阿莫西林胶囊', '0.5g*24粒', '胶囊剂', '盒', '石药集团', 1, 50, 15.00),
('YP003', '布洛芬', '布洛芬缓释胶囊', '0.3g*20粒', '胶囊剂', '盒', '中美史克', 2, 30, 25.00),
('YP004', '奥美拉唑', '奥美拉唑肠溶胶囊', '20mg*14粒', '胶囊剂', '盒', '阿斯利康', 2, 20, 45.00);

-- 供应商
INSERT INTO supplier (supplier_code, supplier_name, contact_person, contact_phone, address, status) VALUES 
('GYS001', '北京医药集团', '张经理', '13800000001', '北京市朝阳区', 1),
('GYS002', '国药集团', '李经理', '13800000002', '北京市海淀区', 1),
('GYS003', '上药控股', '王经理', '13800000003', '上海市浦东新区', 1);

-- 科室
INSERT INTO department (dept_code, dept_name, parent_id, sort_order, status) VALUES 
('DEPT001', '内科', 0, 1, 1),
('DEPT002', '外科', 0, 2, 1),
('DEPT003', '药房', 0, 3, 1);

-- 货位
INSERT INTO warehouse_location (location_code, location_name, warehouse_name, area, sort_order, status) VALUES 
('LOC001', 'A-01-01', '药品仓库', 'A区', 1, 1),
('LOC002', 'A-01-02', '药品仓库', 'A区', 2, 1),
('LOC003', 'B-01-01', '药品仓库', 'B区', 3, 1);

-- 入库单
INSERT INTO stock_in (in_no, in_type, source_type, supplier_id, total_amount, warehouse_keeper, in_time, status) VALUES 
('RK20260509001', 1, 1, 1, 1500.00, '管理员', NOW(), 1),
('RK20260509002', 1, 1, 2, 2000.00, '管理员', NOW(), 1);

-- 入库明细
INSERT INTO stock_in_item (in_id, drug_id, batch_no, quantity, unit_price, production_date, expiry_date, location) VALUES 
(1, 1, 'B20260501', 100, 6.00, '2026-01-01', '2028-01-01', 'LOC001'),
(1, 2, 'B20260502', 50, 15.00, '2026-02-01', '2028-02-01', 'LOC001'),
(2, 3, 'B20260503', 80, 25.00, '2026-03-01', '2027-06-01', 'LOC002'),
(2, 4, 'B20260504', 30, 45.00, '2026-04-01', '2027-12-01', 'LOC003');

-- 库存
INSERT INTO stock (drug_id, batch_no, quantity, unit_price, production_date, expiry_date, location, status) VALUES 
(1, 'B20260501', 100, 6.00, '2026-01-01', '2028-01-01', 'LOC001', 1),
(2, 'B20260502', 50, 15.00, '2026-02-01', '2028-02-01', 'LOC001', 1),
(3, 'B20260503', 80, 25.00, '2026-03-01', '2027-06-01', 'LOC002', 1),
(4, 'B20260504', 30, 45.00, '2026-04-01', '2027-12-01', 'LOC003', 1);
