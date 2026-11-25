package com.company.ems.config;

import com.company.ems.exception.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Cấp kết nối JDBC. Ưu tiên biến môi trường, sau đó đến db.properties. */
public final class DatabaseConnection {
    private static final Properties PROPS = load();

    private DatabaseConnection() { }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new DataAccessException("Không đọc được file cấu hình db.properties.", e);
        }
        return p;
    }

    private static String setting(String env, String key) {
        String v = System.getenv(env);
        return (v != null && !v.isBlank()) ? v : PROPS.getProperty(key, "");
    }

    /** Mỗi lần gọi trả về một kết nối mới; nơi gọi phải đóng bằng try-with-resources. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                setting("EMS_DB_URL", "db.url"),
                setting("EMS_DB_USER", "db.user"),
                setting("EMS_DB_PASSWORD", "db.password"));
    }
}
