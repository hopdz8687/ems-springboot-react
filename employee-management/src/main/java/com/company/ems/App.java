package com.company.ems;

import com.company.ems.controller.NhanVienController;
import com.company.ems.repository.NhanVienRepository;
import com.company.ems.repository.PhongBanRepository;
import com.company.ems.service.NhanVienService;
import com.company.ems.service.PhongBanService;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Điểm khởi chạy: lắp ghép các tầng Repository → Service → Controller (dependency injection thủ công). */
public class App {
    public static void main(String[] args) {
        // Ép UTF-8 để hiển thị/nhập tiếng Việt đúng trên console (đặc biệt trên Windows: chạy "chcp 65001" trước).
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);

        NhanVienRepository nvRepo = new NhanVienRepository();
        PhongBanRepository pbRepo = new PhongBanRepository();
        NhanVienService nvService = new NhanVienService(nvRepo, pbRepo);
        PhongBanService pbService = new PhongBanService(pbRepo, nvRepo);

        new NhanVienController(nvService, pbService, in, out).run();
    }
}
