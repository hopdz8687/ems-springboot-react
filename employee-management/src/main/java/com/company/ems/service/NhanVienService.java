package com.company.ems.service;

import com.company.ems.exception.EmsException;
import com.company.ems.exception.ValidationException;
import com.company.ems.model.BoLoc;
import com.company.ems.model.NhanVien;
import com.company.ems.model.ThongKePhongBan;
import com.company.ems.repository.NhanVienRepository;
import com.company.ems.repository.PhongBanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Quy tắc nghiệp vụ: chuẩn hóa dữ liệu, kiểm tra hợp lệ, chống trùng, rồi mới gọi Repository. */
public class NhanVienService {
    private final NhanVienRepository nhanVienRepo;
    private final PhongBanRepository phongBanRepo;

    public NhanVienService(NhanVienRepository nhanVienRepo, PhongBanRepository phongBanRepo) {
        this.nhanVienRepo = nhanVienRepo;
        this.phongBanRepo = phongBanRepo;
    }

    public NhanVien them(NhanVien input) {
        NhanVien nv = chuanHoa(input);
        List<String> errors = kiemTra(nv, true);
        if (!errors.isEmpty()) throw new ValidationException(errors);
        nhanVienRepo.insert(nv);
        return layTheoMa(nv.maNhanVien());
    }

    public NhanVien capNhat(NhanVien input) {
        NhanVien nv = chuanHoa(input);
        List<String> errors = kiemTra(nv, false);
        if (!errors.isEmpty()) throw new ValidationException(errors);
        if (!nhanVienRepo.update(nv)) throw new EmsException("Không tìm thấy nhân viên có mã " + nv.maNhanVien() + ".");
        return layTheoMa(nv.maNhanVien());
    }

    public void xoa(String ma) {
        if (!nhanVienRepo.deleteByMa(ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT)))
            throw new EmsException("Không tìm thấy nhân viên có mã " + ma + ".");
    }

    public NhanVien layTheoMa(String ma) {
        String key = ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT);
        return nhanVienRepo.findByMa(key)
                .orElseThrow(() -> new EmsException("Không tìm thấy nhân viên có mã " + ma + "."));
    }

    public Optional<NhanVien> timTheoMa(String ma) {
        return nhanVienRepo.findByMa(ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT));
    }

    public List<NhanVien> timKiem(BoLoc loc) { return nhanVienRepo.search(loc); }

    public List<ThongKePhongBan> thongKeTheoPhongBan() { return nhanVienRepo.countByDepartment(); }

    /** Lương trung bình của đúng danh sách khớp bộ lọc, làm tròn 0 chữ số thập phân; rỗng nếu danh sách trống. */
    public Optional<BigDecimal> luongTrungBinh(BoLoc loc) {
        return nhanVienRepo.averageSalary(loc).map(v -> v.setScale(0, RoundingMode.HALF_UP));
    }

    // ---------- nội bộ ----------

    private List<String> kiemTra(NhanVien nv, boolean laThemMoi) {
        List<String> errors = new ArrayList<>(NhanVienValidator.validate(nv, LocalDate.now()));
        if (laThemMoi && !blank(nv.maNhanVien()) && nhanVienRepo.existsByMa(nv.maNhanVien()))
            errors.add("Mã nhân viên " + nv.maNhanVien() + " đã tồn tại.");
        if (!blank(nv.email()) && nhanVienRepo.existsByEmail(nv.email(), laThemMoi ? null : nv.maNhanVien()))
            errors.add("Email " + nv.email() + " đã được nhân viên khác sử dụng.");
        if (!blank(nv.maPhongBan()) && phongBanRepo.findByMa(nv.maPhongBan()).isEmpty())
            errors.add("Phòng ban " + nv.maPhongBan() + " không tồn tại.");
        return errors;
    }

    private static NhanVien chuanHoa(NhanVien n) {
        return new NhanVien(
                up(trim(n.maNhanVien())), trim(n.hoTen()).replaceAll("\\s+", " "), n.ngaySinh(), trim(n.gioiTinh()),
                trim(n.email()).toLowerCase(Locale.ROOT), trim(n.soDienThoai()).replace(" ", ""), trim(n.diaChi()),
                up(trim(n.maPhongBan())), n.tenPhongBan(), trim(n.chucVu()), n.luongCoBan(), n.ngayVaoLam(), trim(n.trangThai()));
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static String up(String s) { return s.toUpperCase(Locale.ROOT); }
    private static boolean blank(String s) { return s == null || s.isBlank(); }
}
