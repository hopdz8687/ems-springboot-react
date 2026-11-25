package com.company.ems.controller;

import com.company.ems.exception.EmsException;
import com.company.ems.exception.ValidationException;
import com.company.ems.model.BoLoc;
import com.company.ems.model.NhanVien;
import com.company.ems.model.PhongBan;
import com.company.ems.model.SapXep;
import com.company.ems.model.ThongKePhongBan;
import com.company.ems.service.NhanVienService;
import com.company.ems.service.PhongBanService;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/** Tầng giao diện console: đọc nhập liệu, gọi Service, in kết quả. Không chứa quy tắc nghiệp vụ. */
public class NhanVienController {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final NumberFormat MONEY = NumberFormat.getInstance(Locale.of("vi", "VN"));

    private final NhanVienService service;
    private final PhongBanService phongBanService;
    private final Scanner in;
    private final PrintStream out;

    public NhanVienController(NhanVienService service, PhongBanService phongBanService, Scanner in, PrintStream out) {
        this.service = service;
        this.phongBanService = phongBanService;
        this.in = in;
        this.out = out;
    }

    public void run() {
        while (true) {
            menu();
            String choice = ask("Chọn chức năng");
            if (choice == null || choice.equals("0")) { out.println("Tạm biệt!"); return; }
            try {
                switch (choice) {
                    case "1" -> them();
                    case "2" -> hienThi(service.timKiem(BoLoc.tatCa()), "Danh sách nhân viên");
                    case "3" -> xemChiTiet();
                    case "4" -> capNhat();
                    case "5" -> xoa();
                    case "6" -> timKiem();
                    case "7" -> locPhongBan();
                    case "8" -> locTrangThai();
                    case "9" -> sapXep();
                    case "10" -> thongKe();
                    case "11" -> luongTrungBinh();
                    case "12" -> quanLyPhongBan();
                    default -> out.println("Lựa chọn không hợp lệ, vui lòng nhập số từ 0 đến 12.");
                }
            } catch (ValidationException e) {
                out.println("\n[Không lưu được] " + e.getMessage());
            } catch (EmsException e) {
                out.println("\n[Lỗi] " + e.getMessage());
            }
            ask("\nNhấn Enter để tiếp tục");
        }
    }

    private void menu() {
        out.println("""

                ===== HỆ THỐNG QUẢN LÝ NHÂN SỰ (EMS) =====
                 1. Thêm nhân viên            7. Lọc theo phòng ban
                 2. Danh sách nhân viên       8. Lọc theo trạng thái
                 3. Xem chi tiết              9. Sắp xếp (họ tên / lương)
                 4. Cập nhật nhân viên       10. Thống kê theo phòng ban
                 5. Xóa nhân viên            11. Lương trung bình
                 6. Tìm theo mã / họ tên     12. Quản lý phòng ban
                 0. Thoát
                """);
    }

    // ---------- chức năng nhân viên ----------

    private void them() {
        out.println("\n--- Thêm nhân viên mới ---");
        List<PhongBan> pbs = phongBanService.danhSach();
        NhanVien nv = new NhanVien(
                askText("Mã nhân viên (vd NV009)", null),
                askText("Họ tên", null),
                askDate("Ngày sinh (dd/MM/yyyy, Enter để bỏ qua)", null, true),
                askChoice("Giới tính", NhanVien.GIOI_TINH, null),
                askText("Email", null),
                askText("Số điện thoại (Enter để bỏ qua)", null),
                askText("Địa chỉ (Enter để bỏ qua)", null),
                askDepartment(pbs, null),
                null,
                askText("Chức vụ", null),
                askMoney("Lương cơ bản (VND)", null),
                askDate("Ngày vào làm (dd/MM/yyyy)", null, false),
                askChoice("Trạng thái", NhanVien.TRANG_THAI, NhanVien.DANG_LAM));
        NhanVien saved = service.them(nv);
        out.println("\nĐã thêm nhân viên " + saved.maNhanVien() + " - " + saved.hoTen() + ".");
    }

    private void xemChiTiet() {
        NhanVien nv = service.layTheoMa(ask("Nhập mã nhân viên"));
        out.println("\n--- Chi tiết nhân viên ---");
        out.println("Mã nhân viên : " + nv.maNhanVien());
        out.println("Họ tên       : " + nv.hoTen());
        out.println("Ngày sinh    : " + fmt(nv.ngaySinh()));
        out.println("Giới tính    : " + nv.gioiTinh());
        out.println("Email        : " + nv.email());
        out.println("Điện thoại   : " + nz(nv.soDienThoai()));
        out.println("Địa chỉ      : " + nz(nv.diaChi()));
        out.println("Phòng ban    : " + nv.tenPhongBan() + " (" + nv.maPhongBan() + ")");
        out.println("Chức vụ      : " + nv.chucVu());
        out.println("Lương cơ bản : " + money(nv.luongCoBan()));
        out.println("Ngày vào làm : " + fmt(nv.ngayVaoLam()));
        out.println("Trạng thái   : " + nv.trangThai());
    }

    private void capNhat() {
        NhanVien cu = service.layTheoMa(ask("Nhập mã nhân viên cần sửa"));
        out.println("\nĐang sửa " + cu.maNhanVien() + " - " + cu.hoTen() + ". Nhấn Enter để giữ giá trị hiện tại.");
        NhanVien moi = new NhanVien(
                cu.maNhanVien(),
                askText("Họ tên", cu.hoTen()),
                askDate("Ngày sinh (dd/MM/yyyy)", cu.ngaySinh(), true),
                askChoice("Giới tính", NhanVien.GIOI_TINH, cu.gioiTinh()),
                askText("Email", cu.email()),
                askText("Số điện thoại", cu.soDienThoai()),
                askText("Địa chỉ", cu.diaChi()),
                askDepartment(phongBanService.danhSach(), cu.maPhongBan()),
                null,
                askText("Chức vụ", cu.chucVu()),
                askMoney("Lương cơ bản (VND)", cu.luongCoBan()),
                askDate("Ngày vào làm (dd/MM/yyyy)", cu.ngayVaoLam(), false),
                askChoice("Trạng thái", NhanVien.TRANG_THAI, cu.trangThai()));
        service.capNhat(moi);
        out.println("\nĐã cập nhật nhân viên " + cu.maNhanVien() + ".");
    }

    private void xoa() {
        NhanVien nv = service.layTheoMa(ask("Nhập mã nhân viên cần xóa"));
        String c = ask("Xóa vĩnh viễn " + nv.maNhanVien() + " - " + nv.hoTen() + "? Gõ 'y' để xác nhận");
        if (c != null && c.equalsIgnoreCase("y")) {
            service.xoa(nv.maNhanVien());
            out.println("Đã xóa nhân viên " + nv.maNhanVien() + ".");
        } else {
            out.println("Đã hủy, không xóa nhân viên nào.");
        }
    }

    private void timKiem() {
        String kw = ask("Nhập mã hoặc họ tên cần tìm");
        hienThi(service.timKiem(BoLoc.tatCa().voiTuKhoa(kw)), "Kết quả tìm \"" + kw + "\"");
    }

    private void locPhongBan() {
        String ma = askDepartment(phongBanService.danhSach(), null);
        hienThi(service.timKiem(BoLoc.tatCa().voiPhongBan(ma)), "Nhân viên phòng " + ma);
    }

    private void locTrangThai() {
        String tt = askChoice("Trạng thái", NhanVien.TRANG_THAI, null);
        hienThi(service.timKiem(BoLoc.tatCa().voiTrangThai(tt)), "Nhân viên " + tt);
    }

    private void sapXep() {
        SapXep[] all = SapXep.values();
        for (int i = 0; i < all.length; i++) out.println(" " + (i + 1) + ". " + all[i].nhan());
        int idx = askInt("Chọn kiểu sắp xếp", 1, all.length);
        hienThi(service.timKiem(BoLoc.tatCa().voiSapXep(all[idx - 1])), "Sắp xếp: " + all[idx - 1].nhan());
    }

    private void thongKe() {
        List<ThongKePhongBan> list = service.thongKeTheoPhongBan();
        out.println("\n--- Thống kê theo phòng ban ---");
        out.printf("%-8s %-26s %8s %18s%n", "Mã", "Phòng ban", "Số NV", "Lương TB");
        int tong = 0;
        for (ThongKePhongBan t : list) {
            out.printf("%-8s %-26s %8d %18s%n", t.maPhongBan(), cut(t.tenPhongBan(), 26), t.soLuong(),
                    t.luongTrungBinh() == null ? "-" : money(t.luongTrungBinh().setScale(0, java.math.RoundingMode.HALF_UP)));
            tong += t.soLuong();
        }
        out.println("Tổng số nhân viên: " + tong);
    }

    /** Lương trung bình theo bộ lọc tùy chọn (phòng ban / trạng thái / từ khóa). */
    private void luongTrungBinh() {
        BoLoc loc = BoLoc.tatCa()
                .voiTuKhoa(ask("Từ khóa mã/họ tên (Enter để bỏ qua)"))
                .voiPhongBan(askOptionalDepartment())
                .voiTrangThai(askOptionalChoice("Trạng thái", NhanVien.TRANG_THAI));
        Optional<BigDecimal> avg = service.luongTrungBinh(loc);
        long n = service.timKiem(loc).size();
        if (avg.isEmpty()) out.println("Không có nhân viên nào phù hợp nên chưa tính được lương trung bình.");
        else out.println("Lương trung bình của " + n + " nhân viên phù hợp: " + money(avg.get()));
    }

    private void quanLyPhongBan() {
        out.println("\n--- Phòng ban ---");
        for (PhongBan p : phongBanService.danhSach())
            out.printf(" %-8s %-26s %s%n", p.maPhongBan(), cut(p.tenPhongBan(), 26), nz(p.moTa()).equals("-") ? "" : p.moTa());
        String c = ask("\n[T] Thêm  [X] Xóa  [Enter] Quay lại");
        if ("t".equalsIgnoreCase(c)) {
            phongBanService.them(ask("Mã phòng ban"), ask("Tên phòng ban"), ask("Mô tả (Enter để bỏ qua)"));
            out.println("Đã thêm phòng ban.");
        } else if ("x".equalsIgnoreCase(c)) {
            String ma = ask("Mã phòng ban cần xóa");
            if ("y".equalsIgnoreCase(ask("Xác nhận xóa " + ma + "? Gõ 'y'"))) {
                phongBanService.xoa(ma);
                out.println("Đã xóa phòng ban " + ma + ".");
            } else out.println("Đã hủy.");
        }
    }

    // ---------- hiển thị ----------

    private void hienThi(List<NhanVien> list, String tieuDe) {
        out.println("\n--- " + tieuDe + " (" + list.size() + ") ---");
        if (list.isEmpty()) { out.println("Không có nhân viên nào phù hợp."); return; }
        out.printf("%-8s %-24s %-16s %-24s %14s %-9s%n", "Mã", "Họ tên", "Phòng ban", "Chức vụ", "Lương", "Trạng thái");
        for (NhanVien n : list)
            out.printf("%-8s %-24s %-16s %-24s %14s %-9s%n", n.maNhanVien(), cut(n.hoTen(), 24),
                    cut(n.tenPhongBan(), 16), cut(n.chucVu(), 24), MONEY.format(n.luongCoBan()), n.trangThai());
    }

    // ---------- đọc dữ liệu ----------

    private String ask(String prompt) {
        out.print(prompt + ": ");
        out.flush();
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }

    /** Có giá trị hiện tại: Enter giữ nguyên. Không có: trả về chuỗi người dùng gõ (có thể rỗng, để Service báo lỗi). */
    private String askText(String label, String current) {
        String v = ask(label + (current != null && !current.isBlank() ? " [" + current + "]" : ""));
        return (v == null || v.isEmpty()) && current != null ? current : (v == null ? "" : v);
    }

    private LocalDate askDate(String label, LocalDate current, boolean optional) {
        while (true) {
            String v = ask(label + (current != null ? " [" + fmt(current) + "]" : ""));
            if (v == null || v.isEmpty()) {
                if (current != null) return current;
                if (optional) return null;
                out.println("  Trường này là bắt buộc.");
                if (v == null) throw new EmsException("Dữ liệu nhập đã kết thúc.");
                continue;
            }
            try { return LocalDate.parse(v, DATE); }
            catch (DateTimeParseException e) { out.println("  Ngày không hợp lệ, hãy nhập dạng dd/MM/yyyy (vd 25/12/1995)."); }
        }
    }

    private BigDecimal askMoney(String label, BigDecimal current) {
        while (true) {
            String v = ask(label + (current != null ? " [" + money(current) + "]" : ""));
            if (v == null) throw new EmsException("Dữ liệu nhập đã kết thúc.");
            if (v.isEmpty() && current != null) return current;
            try { return new BigDecimal(v.replace(",", "").replace(".", "")); }
            catch (NumberFormatException e) { out.println("  Lương phải là số, ví dụ 15000000."); }
        }
    }

    private String askChoice(String label, List<String> options, String current) {
        while (true) {
            StringBuilder sb = new StringBuilder(label + " (");
            for (int i = 0; i < options.size(); i++) sb.append(i + 1).append("=").append(options.get(i)).append(i < options.size() - 1 ? ", " : "");
            String v = ask(sb + ")" + (current != null ? " [" + current + "]" : ""));
            if (v == null) throw new EmsException("Dữ liệu nhập đã kết thúc.");
            if (v.isEmpty() && current != null) return current;
            if (v.matches("\\d+") && Integer.parseInt(v) >= 1 && Integer.parseInt(v) <= options.size()) return options.get(Integer.parseInt(v) - 1);
            for (String o : options) if (o.equalsIgnoreCase(v)) return o;
            out.println("  Lựa chọn không hợp lệ.");
        }
    }

    private String askOptionalChoice(String label, List<String> options) {
        String v = ask(label + " " + options + " (Enter để bỏ qua)");
        if (v == null || v.isEmpty()) return null;
        for (String o : options) if (o.equalsIgnoreCase(v)) return o;
        throw new EmsException("Giá trị '" + v + "' không hợp lệ cho " + label.toLowerCase() + ".");
    }

    private String askDepartment(List<PhongBan> pbs, String current) {
        for (PhongBan p : pbs) out.println("   " + p.maPhongBan() + " - " + p.tenPhongBan());
        String v = ask("Mã phòng ban" + (current != null ? " [" + current + "]" : ""));
        return (v == null || v.isEmpty()) && current != null ? current : (v == null ? "" : v);
    }

    private String askOptionalDepartment() {
        String v = ask("Mã phòng ban (Enter để bỏ qua)");
        return v == null || v.isEmpty() ? null : v;
    }

    private int askInt(String label, int min, int max) {
        while (true) {
            String v = ask(label + " (" + min + "-" + max + ")");
            if (v == null) throw new EmsException("Dữ liệu nhập đã kết thúc.");
            try { int n = Integer.parseInt(v); if (n >= min && n <= max) return n; } catch (NumberFormatException ignored) { }
            out.println("  Vui lòng nhập số từ " + min + " đến " + max + ".");
        }
    }

    // ---------- định dạng ----------

    private static String fmt(LocalDate d) { return d == null ? "-" : d.format(DATE); }
    private static String money(BigDecimal v) { return MONEY.format(v) + " VND"; }
    private static String nz(String s) { return s == null || s.isBlank() ? "-" : s; }
    private static String cut(String s, int n) { return s == null ? "" : s.length() <= n ? s : s.substring(0, n - 1) + "…"; }
}
