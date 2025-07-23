package com.example.datn_tuan.service;
import com.example.datn_tuan.dto.DoanhThuNgayDTO;
import com.example.datn_tuan.dto.DoanhThuTuanDTO;
import com.example.datn_tuan.dto.SachBanChayDTO;

import java.math.BigDecimal;
import java.util.List;
public interface ThongKeService {
    List<SachBanChayDTO> thongKeSachBanChay();
    List<DoanhThuNgayDTO> thongKeDoanhThuTheoNgay();   // nhiều ngày
    List<DoanhThuTuanDTO> thongKeDoanhThuTheoTuan();   // nhiều tuần

    BigDecimal tinhDoanhThuTheoNgay();  // chỉ hôm nay
    BigDecimal tinhDoanhThuTheoTuan();  // chỉ tuần hiện tại
}
