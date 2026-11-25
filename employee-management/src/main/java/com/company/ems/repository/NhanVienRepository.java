package com.company.ems.repository;

import com.company.ems.config.DatabaseConnection;
import com.company.ems.exception.DataAccessException;
import com.company.ems.model.BoLoc;
import com.company.ems.model.NhanVien;
import com.company.ems.model.SapXep;
import com.company.ems.model.ThongKePhongBan;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Truy cập bảng nhan_vien bằng JDBC. Toàn bộ SQL dùng PreparedStatement để chống SQL injection. */
public class NhanVienRepository {

    private static final String SELECT =
            "SELECT nv.*, pb.ten_phong_ban FROM nhan_vien nv "
          + "JOIN phong_ban pb ON pb.ma_phong_ban = nv.ma_phong_ban";

    public void insert(NhanVien nv) {
        String sql = "INSERT INTO nhan_vien (ma_nhan_vien, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, "
                   + "dia_chi, ma_phong_ban, chuc_vu, luong_co_ban, ngay_vao_lam, trang_thai) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nv.maNhanVien());
            bindCommon(ps, nv, 2);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translate("thêm nhân viên", e);
        }
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE nhan_vien SET ho_ten=?, ngay_sinh=?, gioi_tinh=?, email=?, so_dien_thoai=?, "
                   + "dia_chi=?, ma_phong_ban=?, chuc_vu=?, luong_co_ban=?, ngay_vao_lam=?, trang_thai=? "
                   + "WHERE ma_nhan_vien=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bindCommon(ps, nv, 1);
            ps.setString(12, nv.maNhanVien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw translate("cập nhật nhân viên", e);
        }
    }

    public boolean deleteByMa(String ma) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM nhan_vien WHERE ma_nhan_vien = ?")) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw translate("xóa nhân viên", e);
        }
    }

    public Optional<NhanVien> findByMa(String ma) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE nv.ma_nhan_vien = ?")) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw translate("đọc nhân viên", e);
        }
    }

    /** Tìm kiếm + lọc + sắp xếp trong một câu truy vấn. */
    public List<NhanVien> search(BoLoc loc) {
        List<Object> params = new ArrayList<>();
        SapXep sx = loc.sapXep() == null ? SapXep.MA_TANG : loc.sapXep();
        String sql = SELECT + where(loc, params) + " ORDER BY " + sx.orderBy();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<NhanVien> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw translate("tải danh sách nhân viên", e);
        }
    }

    /** Lương trung bình của đúng tập nhân viên khớp bộ lọc; rỗng nếu không có nhân viên nào. */
    public Optional<BigDecimal> averageSalary(BoLoc loc) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT AVG(nv.luong_co_ban) FROM nhan_vien nv" + where(loc, params);
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.ofNullable(rs.getBigDecimal(1)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw translate("tính lương trung bình", e);
        }
    }

    /** Thống kê theo phòng ban; dùng LEFT JOIN để phòng ban chưa có nhân viên vẫn hiện số 0. */
    public List<ThongKePhongBan> countByDepartment() {
        String sql = "SELECT pb.ma_phong_ban, pb.ten_phong_ban, COUNT(nv.ma_nhan_vien) AS so_luong, "
                   + "AVG(nv.luong_co_ban) AS luong_tb FROM phong_ban pb "
                   + "LEFT JOIN nhan_vien nv ON nv.ma_phong_ban = pb.ma_phong_ban "
                   + "GROUP BY pb.ma_phong_ban, pb.ten_phong_ban ORDER BY so_luong DESC, pb.ten_phong_ban";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ThongKePhongBan> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new ThongKePhongBan(rs.getString("ma_phong_ban"), rs.getString("ten_phong_ban"),
                        rs.getInt("so_luong"), rs.getBigDecimal("luong_tb")));
            }
            return list;
        } catch (SQLException e) {
            throw translate("thống kê theo phòng ban", e);
        }
    }

    public int countByDepartmentCode(String maPhongBan) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM nhan_vien WHERE ma_phong_ban = ?")) {
            ps.setString(1, maPhongBan);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw translate("đếm nhân viên của phòng ban", e);
        }
    }

    public boolean existsByMa(String ma) {
        return exists("SELECT 1 FROM nhan_vien WHERE ma_nhan_vien = ?", ma);
    }

    /** Kiểm tra trùng email, bỏ qua chính nhân viên đang sửa (excludeMa có thể null khi thêm mới). */
    public boolean existsByEmail(String email, String excludeMa) {
        String sql = "SELECT 1 FROM nhan_vien WHERE email = ? AND (? IS NULL OR ma_nhan_vien <> ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, excludeMa);
            ps.setString(3, excludeMa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw translate("kiểm tra email", e);
        }
    }

    // ---------- helpers ----------

    private boolean exists(String sql, String value) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw translate("kiểm tra dữ liệu", e);
        }
    }

    private static String where(BoLoc loc, List<Object> params) {
        StringBuilder w = new StringBuilder(" WHERE 1=1");
        if (notBlank(loc.tuKhoa())) {
            // Escape ký tự đặc biệt của LIKE để "%" hay "_" người dùng gõ được hiểu là chữ thường.
            String like = "%" + loc.tuKhoa().trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            w.append(" AND (nv.ma_nhan_vien LIKE ? OR nv.ho_ten LIKE ?)");
            params.add(like);
            params.add(like);
        }
        if (notBlank(loc.maPhongBan())) { w.append(" AND nv.ma_phong_ban = ?"); params.add(loc.maPhongBan().trim()); }
        if (notBlank(loc.trangThai()))  { w.append(" AND nv.trang_thai = ?");  params.add(loc.trangThai().trim()); }
        return w.toString();
    }

    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private static void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
    }

    /** Gán 11 cột dùng chung của INSERT/UPDATE (không gồm mã), bắt đầu từ vị trí start. */
    private static void bindCommon(PreparedStatement ps, NhanVien nv, int start) throws SQLException {
        int i = start;
        ps.setString(i++, nv.hoTen());
        setDate(ps, i++, nv.ngaySinh());
        ps.setString(i++, nv.gioiTinh());
        ps.setString(i++, nv.email());
        setNullable(ps, i++, nv.soDienThoai());
        setNullable(ps, i++, nv.diaChi());
        ps.setString(i++, nv.maPhongBan());
        ps.setString(i++, nv.chucVu());
        ps.setBigDecimal(i++, nv.luongCoBan());
        setDate(ps, i++, nv.ngayVaoLam());
        ps.setString(i, nv.trangThai());
    }

    private static void setDate(PreparedStatement ps, int idx, LocalDate d) throws SQLException {
        if (d == null) ps.setNull(idx, Types.DATE); else ps.setDate(idx, Date.valueOf(d));
    }

    private static void setNullable(PreparedStatement ps, int idx, String s) throws SQLException {
        if (s == null || s.isBlank()) ps.setNull(idx, Types.VARCHAR); else ps.setString(idx, s);
    }

    private static NhanVien map(ResultSet rs) throws SQLException {
        Date ns = rs.getDate("ngay_sinh");
        Date nvl = rs.getDate("ngay_vao_lam");
        return new NhanVien(
                rs.getString("ma_nhan_vien"), rs.getString("ho_ten"),
                ns == null ? null : ns.toLocalDate(),
                rs.getString("gioi_tinh"), rs.getString("email"), rs.getString("so_dien_thoai"),
                rs.getString("dia_chi"), rs.getString("ma_phong_ban"), rs.getString("ten_phong_ban"),
                rs.getString("chuc_vu"), rs.getBigDecimal("luong_co_ban"),
                nvl == null ? null : nvl.toLocalDate(), rs.getString("trang_thai"));
    }

    static DataAccessException translate(String action, SQLException e) {
        String state = e.getSQLState();
        if (state != null && state.startsWith("23")) {
            return new DataAccessException("Không thể " + action
                    + ": dữ liệu bị trùng hoặc vi phạm ràng buộc của cơ sở dữ liệu.", e);
        }
        if (state != null && state.startsWith("08")) {
            return new DataAccessException("Không kết nối được MySQL. Hãy kiểm tra MySQL đã chạy và cấu hình db.properties.", e);
        }
        return new DataAccessException("Không thể " + action + " (lỗi cơ sở dữ liệu: " + e.getMessage() + ").", e);
    }
}
