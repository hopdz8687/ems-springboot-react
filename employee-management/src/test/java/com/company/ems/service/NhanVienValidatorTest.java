package com.company.ems.service;

import com.company.ems.model.NhanVien;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NhanVienValidatorTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 20);

    private static NhanVien hopLe() {
        return new NhanVien("NV009", "Nguyễn Văn A", LocalDate.of(1995, 1, 1), "Nam", "a@company.vn", "0901234567",
                "Hà Nội", "IT", null, "Lập trình viên", new BigDecimal("15000000"), LocalDate.of(2023, 1, 1), "Đang làm");
    }

    private static List<String> errorsOf(NhanVien nv) { return NhanVienValidator.validate(nv, TODAY); }

    @Test void duLieuHopLe_khongCoLoi() { assertTrue(errorsOf(hopLe()).isEmpty()); }

    @Test void hoTenToanKhoangTrang_biTuChoi() {
        NhanVien n = hopLe();
        assertEquals(1, errorsOf(new NhanVien(n.maNhanVien(), "   ", n.ngaySinh(), n.gioiTinh(), n.email(), n.soDienThoai(),
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), n.ngayVaoLam(), n.trangThai())).size());
    }

    @Test void emailSaiDinhDang_biTuChoi() {
        NhanVien n = hopLe();
        assertEquals(1, errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), n.ngaySinh(), n.gioiTinh(), "abc@", n.soDienThoai(),
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), n.ngayVaoLam(), n.trangThai())).size());
    }

    @Test void luongKhongDuong_biTuChoi() {
        NhanVien n = hopLe();
        for (String v : new String[] {"0", "-5"}) {
            assertEquals(1, errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), n.ngaySinh(), n.gioiTinh(), n.email(), n.soDienThoai(),
                    n.diaChi(), n.maPhongBan(), null, n.chucVu(), new BigDecimal(v), n.ngayVaoLam(), n.trangThai())).size());
        }
    }

    @Test void ngayTuongLai_biTuChoi() {
        NhanVien n = hopLe();
        LocalDate future = TODAY.plusDays(1);
        assertTrue(errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), future, n.gioiTinh(), n.email(), n.soDienThoai(),
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), n.ngayVaoLam(), n.trangThai())).stream()
                .anyMatch(s -> s.contains("Ngày sinh")));
        assertTrue(errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), n.ngaySinh(), n.gioiTinh(), n.email(), n.soDienThoai(),
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), future, n.trangThai())).stream()
                .anyMatch(s -> s.contains("Ngày vào làm")));
    }

    @Test void soDienThoaiSai_biTuChoi_nhungDeTrongThiHopLe() {
        NhanVien n = hopLe();
        assertTrue(errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), n.ngaySinh(), n.gioiTinh(), n.email(), "12345",
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), n.ngayVaoLam(), n.trangThai())).size() == 1);
        assertTrue(errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), n.ngaySinh(), n.gioiTinh(), n.email(), "",
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), n.ngayVaoLam(), n.trangThai())).isEmpty());
    }

    @Test void trangThaiVaGioiTinhNgoaiDanhSach_biTuChoi() {
        NhanVien n = hopLe();
        assertEquals(2, errorsOf(new NhanVien(n.maNhanVien(), n.hoTen(), n.ngaySinh(), "X", n.email(), n.soDienThoai(),
                n.diaChi(), n.maPhongBan(), null, n.chucVu(), n.luongCoBan(), n.ngayVaoLam(), "Nghỉ phép")).size());
    }
}
