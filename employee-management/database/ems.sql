-- EMS - Employee Management System
-- Chạy: mysql -u root -p < database/ems.sql  (hoặc import bằng MySQL Workbench / phpMyAdmin của XAMPP)
-- Script chạy lại nhiều lần được mà không mất dữ liệu (IF NOT EXISTS / INSERT IGNORE).

CREATE DATABASE IF NOT EXISTS ems
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE ems;

CREATE TABLE IF NOT EXISTS phong_ban (
  ma_phong_ban  VARCHAR(20)  NOT NULL,
  ten_phong_ban VARCHAR(100) NOT NULL,
  mo_ta         VARCHAR(255) NULL,
  PRIMARY KEY (ma_phong_ban),
  UNIQUE KEY uq_phong_ban_ten (ten_phong_ban)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS nhan_vien (
  ma_nhan_vien  VARCHAR(20)   NOT NULL,
  ho_ten        VARCHAR(100)  NOT NULL,
  ngay_sinh     DATE          NULL,
  gioi_tinh     VARCHAR(10)   NOT NULL DEFAULT 'Khác',
  email         VARCHAR(100)  NOT NULL,
  so_dien_thoai VARCHAR(15)   NULL,
  dia_chi       VARCHAR(255)  NULL,
  ma_phong_ban  VARCHAR(20)   NOT NULL,
  chuc_vu       VARCHAR(100)  NOT NULL,
  luong_co_ban  DECIMAL(15,2) NOT NULL,
  ngay_vao_lam  DATE          NOT NULL,
  trang_thai    VARCHAR(20)   NOT NULL DEFAULT 'Đang làm',
  PRIMARY KEY (ma_nhan_vien),
  UNIQUE KEY uq_nhan_vien_email (email),
  KEY idx_nhan_vien_trang_thai (trang_thai),
  -- Quyết định thiết kế: KHÔNG cho xóa phòng ban còn nhân viên (RESTRICT) để tránh mất dữ liệu
  -- nhân sự ngoài ý muốn. Đổi mã phòng ban thì cập nhật theo (CASCADE).
  CONSTRAINT fk_nhan_vien_phong_ban FOREIGN KEY (ma_phong_ban)
    REFERENCES phong_ban (ma_phong_ban)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_nhan_vien_luong CHECK (luong_co_ban > 0),
  CONSTRAINT ck_nhan_vien_gioi_tinh CHECK (gioi_tinh IN ('Nam', 'Nữ', 'Khác')),
  CONSTRAINT ck_nhan_vien_trang_thai CHECK (trang_thai IN ('Đang làm', 'Đã nghỉ'))
) ENGINE=InnoDB;

-- Dữ liệu mẫu (khớp với dữ liệu mẫu của bản web prototype)
INSERT IGNORE INTO phong_ban (ma_phong_ban, ten_phong_ban, mo_ta) VALUES
  ('IT', 'Công nghệ thông tin', 'Phát triển và vận hành hệ thống'),
  ('NS', 'Nhân sự',            'Tuyển dụng, đào tạo, chính sách nhân sự'),
  ('KT', 'Kế toán',            'Tài chính, kế toán, lương thưởng'),
  ('KD', 'Kinh doanh',         'Bán hàng và chăm sóc khách hàng');

INSERT IGNORE INTO nhan_vien
  (ma_nhan_vien, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, dia_chi, ma_phong_ban, chuc_vu, luong_co_ban, ngay_vao_lam, trang_thai) VALUES
  ('NV001', 'Nguyễn Văn An',  '1995-03-12', 'Nam', 'an.nguyen@company.vn',  '0901234567', 'Nam Định',  'IT', 'Lập trình viên',          18000000, '2021-06-01', 'Đang làm'),
  ('NV002', 'Trần Thị Bình',  '1993-11-02', 'Nữ',  'binh.tran@company.vn',  '0912345678', 'Hà Nội',    'NS', 'Chuyên viên nhân sự',    14000000, '2020-02-15', 'Đang làm'),
  ('NV003', 'Lê Minh Châu',   '1990-07-25', 'Nữ',  'chau.le@company.vn',    '0923456789', 'Ninh Bình', 'KT', 'Kế toán trưởng',         25000000, '2018-09-10', 'Đang làm'),
  ('NV004', 'Phạm Quốc Dũng', '1998-01-30', 'Nam', 'dung.pham@company.vn',  '0934567890', 'Hà Nội',    'KD', 'Nhân viên kinh doanh',   12000000, '2023-03-20', 'Đang làm'),
  ('NV005', 'Hoàng Thu Hà',   '1996-05-18', 'Nữ',  'ha.hoang@company.vn',   '0945678901', 'Hải Phòng', 'IT', 'Kiểm thử phần mềm',      15000000, '2022-08-08', 'Đang làm'),
  ('NV006', 'Vũ Đức Huy',     '1989-12-09', 'Nam', 'huy.vu@company.vn',     '0956789012', 'Nam Định',  'IT', 'Trưởng nhóm kỹ thuật',   32000000, '2017-04-03', 'Đang làm'),
  ('NV007', 'Đặng Mai Lan',   '1994-09-14', 'Nữ',  'lan.dang@company.vn',   '0967890123', 'Hà Nội',    'KD', 'Trưởng phòng kinh doanh',28000000, '2019-01-21', 'Đã nghỉ'),
  ('NV008', 'Bùi Anh Tuấn',   '2000-02-27', 'Nam', 'tuan.bui@company.vn',   '0978901234', 'Thái Bình', 'KT', 'Kế toán viên',           11000000, '2024-05-06', 'Đang làm');
