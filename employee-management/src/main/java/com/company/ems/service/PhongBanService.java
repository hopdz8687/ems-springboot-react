package com.company.ems.service;

import com.company.ems.exception.EmsException;
import com.company.ems.exception.ValidationException;
import com.company.ems.model.PhongBan;
import com.company.ems.repository.NhanVienRepository;
import com.company.ems.repository.PhongBanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhongBanService {
    private final PhongBanRepository phongBanRepo;
    private final NhanVienRepository nhanVienRepo;

    public PhongBanService(PhongBanRepository phongBanRepo, NhanVienRepository nhanVienRepo) {
        this.phongBanRepo = phongBanRepo;
        this.nhanVienRepo = nhanVienRepo;
    }

    public List<PhongBan> danhSach() { return phongBanRepo.findAll(); }

    public void them(String ma, String ten, String moTa) {
        String m = ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT);
        String t = ten == null ? "" : ten.trim();
        List<String> errors = new ArrayList<>();
        if (!m.matches("^[A-Z0-9_-]{1,20}$")) errors.add("Mã phòng ban chỉ gồm chữ, số, '-' hoặc '_' và tối đa 20 ký tự.");
        if (t.isEmpty() || t.length() > 100) errors.add("Tên phòng ban là bắt buộc và tối đa 100 ký tự.");
        if (errors.isEmpty() && phongBanRepo.findByMa(m).isPresent()) errors.add("Mã phòng ban " + m + " đã tồn tại.");
        if (!errors.isEmpty()) throw new ValidationException(errors);
        phongBanRepo.insert(new PhongBan(m, t, moTa == null ? null : moTa.trim()));
    }

    /** Chỉ xóa được phòng ban không còn nhân viên (khớp ràng buộc ON DELETE RESTRICT ở CSDL). */
    public void xoa(String ma) {
        String m = ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT);
        if (phongBanRepo.findByMa(m).isEmpty()) throw new EmsException("Không tìm thấy phòng ban " + ma + ".");
        int soNv = nhanVienRepo.countByDepartmentCode(m);
        if (soNv > 0) throw new EmsException("Không thể xóa phòng ban " + m + " vì còn " + soNv
                + " nhân viên. Hãy chuyển nhân viên sang phòng ban khác trước.");
        phongBanRepo.deleteByMa(m);
    }
}
