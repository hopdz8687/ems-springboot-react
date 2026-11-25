package com.company.ems.model;

/** Các kiểu sắp xếp được phép. Dùng enum (whitelist) để không bao giờ nối chuỗi người dùng vào ORDER BY. */
public enum SapXep {
    MA_TANG("Mã nhân viên (A → Z)", "nv.ma_nhan_vien ASC"),
    TEN_TANG("Họ tên (A → Z)", "nv.ho_ten ASC, nv.ma_nhan_vien ASC"),
    TEN_GIAM("Họ tên (Z → A)", "nv.ho_ten DESC, nv.ma_nhan_vien ASC"),
    LUONG_TANG("Lương (thấp → cao)", "nv.luong_co_ban ASC, nv.ma_nhan_vien ASC"),
    LUONG_GIAM("Lương (cao → thấp)", "nv.luong_co_ban DESC, nv.ma_nhan_vien ASC");

    private final String nhan;
    private final String orderBy;

    SapXep(String nhan, String orderBy) { this.nhan = nhan; this.orderBy = orderBy; }

    public String nhan() { return nhan; }
    public String orderBy() { return orderBy; }
}
