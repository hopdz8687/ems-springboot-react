package com.company.ems.service;

import com.company.ems.model.NhanVien;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Kiểm tra định dạng/quy tắc của một nhân viên, KHÔNG đụng tới cơ sở dữ liệu nên test được độc lập.
 * Việc kiểm tra trùng mã/email và phòng ban tồn tại do NhanVienService làm (cần truy vấn DB).
 */
public final class NhanVienValidator {
    private static final Pattern MA = Pattern.compile("^[A-Za-z0-9_-]{1,20}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^(0|\\+84)\\d{9}$");

    private NhanVienValidator() { }

    public static List<String> validate(NhanVien nv, LocalDate today) {
        List<String> errors = new ArrayList<>();

        if (isBlank(nv.maNhanVien())) errors.add("Mã nhân viên là bắt buộc.");
        else if (!MA.matcher(nv.maNhanVien()).matches())
            errors.add("Mã nhân viên chỉ gồm chữ, số, '-' hoặc '_' và tối đa 20 ký tự.");

        if (isBlank(nv.hoTen())) errors.add("Họ tên là bắt buộc (không chỉ chứa khoảng trắng).");
        else if (nv.hoTen().length() > 100) errors.add("Họ tên tối đa 100 ký tự.");

        if (nv.ngaySinh() != null && nv.ngaySinh().isAfter(today))
            errors.add("Ngày sinh không được là ngày trong tương lai.");

        if (nv.gioiTinh() == null || !NhanVien.GIOI_TINH.contains(nv.gioiTinh()))
            errors.add("Giới tính phải là Nam, Nữ hoặc Khác.");

        if (isBlank(nv.email())) errors.add("Email là bắt buộc.");
        else if (nv.email().length() > 100 || !EMAIL.matcher(nv.email()).matches())
            errors.add("Email không đúng định dạng (ví dụ: ten@congty.vn).");

        if (!isBlank(nv.soDienThoai()) && !PHONE.matcher(nv.soDienThoai()).matches())
            errors.add("Số điện thoại phải gồm 10 chữ số bắt đầu bằng 0 (hoặc +84 và 9 chữ số).");

        if (nv.diaChi() != null && nv.diaChi().length() > 255) errors.add("Địa chỉ tối đa 255 ký tự.");

        if (isBlank(nv.maPhongBan())) errors.add("Phòng ban là bắt buộc.");

        if (isBlank(nv.chucVu())) errors.add("Chức vụ là bắt buộc.");
        else if (nv.chucVu().length() > 100) errors.add("Chức vụ tối đa 100 ký tự.");

        if (nv.luongCoBan() == null || nv.luongCoBan().compareTo(BigDecimal.ZERO) <= 0)
            errors.add("Lương cơ bản phải lớn hơn 0.");
        else if (nv.luongCoBan().precision() - nv.luongCoBan().scale() > 13)
            errors.add("Lương cơ bản quá lớn.");

        if (nv.ngayVaoLam() == null) errors.add("Ngày vào làm là bắt buộc.");
        else if (nv.ngayVaoLam().isAfter(today)) errors.add("Ngày vào làm không được là ngày trong tương lai.");

        if (nv.ngaySinh() != null && nv.ngayVaoLam() != null && !nv.ngayVaoLam().isAfter(nv.ngaySinh()))
            errors.add("Ngày vào làm phải sau ngày sinh.");

        if (nv.trangThai() == null || !NhanVien.TRANG_THAI.contains(nv.trangThai()))
            errors.add("Trạng thái phải là 'Đang làm' hoặc 'Đã nghỉ'.");

        return errors;
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
