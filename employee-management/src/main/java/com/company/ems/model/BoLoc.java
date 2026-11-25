package com.company.ems.model;

/** Điều kiện tìm kiếm / lọc. Giá trị null hoặc rỗng nghĩa là không áp dụng điều kiện đó. */
public record BoLoc(String tuKhoa, String maPhongBan, String trangThai, SapXep sapXep) {

    public static BoLoc tatCa() { return new BoLoc(null, null, null, SapXep.MA_TANG); }

    public BoLoc voiTuKhoa(String v)      { return new BoLoc(v, maPhongBan, trangThai, sapXep); }
    public BoLoc voiPhongBan(String v)    { return new BoLoc(tuKhoa, v, trangThai, sapXep); }
    public BoLoc voiTrangThai(String v)   { return new BoLoc(tuKhoa, maPhongBan, v, sapXep); }
    public BoLoc voiSapXep(SapXep v)      { return new BoLoc(tuKhoa, maPhongBan, trangThai, v); }
}
