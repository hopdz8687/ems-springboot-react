package com.company.ems.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Nhân viên. Dùng record (bất biến) để tránh sửa dữ liệu ngoài ý muốn giữa các tầng.
 * tenPhongBan chỉ để hiển thị (JOIN), không ghi xuống bảng nhan_vien.
 */
public record NhanVien(
        String maNhanVien,
        String hoTen,
        LocalDate ngaySinh,
        String gioiTinh,
        String email,
        String soDienThoai,
        String diaChi,
        String maPhongBan,
        String tenPhongBan,
        String chucVu,
        BigDecimal luongCoBan,
        LocalDate ngayVaoLam,
        String trangThai) {

    public static final List<String> GIOI_TINH = List.of("Nam", "Nữ", "Khác");
    public static final String DANG_LAM = "Đang làm";
    public static final String DA_NGHI = "Đã nghỉ";
    public static final List<String> TRANG_THAI = List.of(DANG_LAM, DA_NGHI);
}
