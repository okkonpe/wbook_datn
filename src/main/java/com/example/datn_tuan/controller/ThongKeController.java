// File: controller/ThongKeController.java
package com.example.datn_tuan.controller;

import com.example.datn_tuan.dto.SachBanChayDTO;
import com.example.datn_tuan.dto.DoanhThuNgayDTO;
import com.example.datn_tuan.dto.DoanhThuTuanDTO;
import com.example.datn_tuan.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/thong-ke")
public class ThongKeController {
    @Autowired
    private ThongKeService thongKeService;

    // ✅ 1. Giao diện chính: trả về HTML
    @GetMapping("/doanhThu")
    public String viewThongKe() {
        return "thong-ke/doanhThu"; // tên file HTML đặt trong: src/main/resources/templates/tn
    }

    // ✅ 2. API: Trả về sách bán chạy
    @ResponseBody
    @GetMapping("/api/sach-ban-chay")
    public List<SachBanChayDTO> getSachBanChay() {
        return thongKeService.thongKeSachBanChay();
    }

    // ✅ 3. API: Doanh thu ngày hôm nay
    @ResponseBody
    @GetMapping("/api/doanh-thu-ngay")
    public BigDecimal getDoanhThuNgay() {
        return thongKeService.tinhDoanhThuTheoNgay();
    }

    // ✅ 4. API: Doanh thu tuần hiện tại
    @ResponseBody
    @GetMapping("/api/doanh-thu-tuan")
    public BigDecimal getDoanhThuTuan() {
        return thongKeService.tinhDoanhThuTheoTuan();
    }

    // ✅ (Tùy chọn): API trả về danh sách doanh thu theo ngày (dùng cho biểu đồ cột)
    @ResponseBody
    @GetMapping("/api/doanh-thu-theo-ngay")
    public List<DoanhThuNgayDTO> getDoanhThuTheoNgay() {
        return thongKeService.thongKeDoanhThuTheoNgay();
    }

    // ✅ (Tùy chọn): API trả về danh sách doanh thu theo tuần (dùng cho biểu đồ đường)
    @ResponseBody
    @GetMapping("/api/doanh-thu-theo-tuan")
    public List<DoanhThuTuanDTO> getDoanhThuTheoTuan() {
        return thongKeService.thongKeDoanhThuTheoTuan();
    }
}
