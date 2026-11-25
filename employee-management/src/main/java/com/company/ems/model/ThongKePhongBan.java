package com.company.ems.model;

import java.math.BigDecimal;

/** Kết quả thống kê một phòng ban: số nhân viên và lương trung bình (null nếu phòng chưa có nhân viên). */
public record ThongKePhongBan(String maPhongBan, String tenPhongBan, int soLuong, BigDecimal luongTrungBinh) { }
