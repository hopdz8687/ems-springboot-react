package com.company.ems.repository;

import com.company.ems.config.DatabaseConnection;
import com.company.ems.model.PhongBan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhongBanRepository {

    public List<PhongBan> findAll() {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ma_phong_ban, ten_phong_ban, mo_ta FROM phong_ban ORDER BY ten_phong_ban");
             ResultSet rs = ps.executeQuery()) {
            List<PhongBan> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw NhanVienRepository.translate("tải danh sách phòng ban", e);
        }
    }

    public Optional<PhongBan> findByMa(String ma) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT ma_phong_ban, ten_phong_ban, mo_ta FROM phong_ban WHERE ma_phong_ban = ?")) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw NhanVienRepository.translate("đọc phòng ban", e);
        }
    }

    public void insert(PhongBan pb) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO phong_ban (ma_phong_ban, ten_phong_ban, mo_ta) VALUES (?,?,?)")) {
            ps.setString(1, pb.maPhongBan());
            ps.setString(2, pb.tenPhongBan());
            ps.setString(3, pb.moTa() == null || pb.moTa().isBlank() ? null : pb.moTa());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw NhanVienRepository.translate("thêm phòng ban", e);
        }
    }

    public boolean deleteByMa(String ma) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM phong_ban WHERE ma_phong_ban = ?")) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw NhanVienRepository.translate("xóa phòng ban", e);
        }
    }

    private static PhongBan map(ResultSet rs) throws SQLException {
        return new PhongBan(rs.getString(1), rs.getString(2), rs.getString(3));
    }
}
