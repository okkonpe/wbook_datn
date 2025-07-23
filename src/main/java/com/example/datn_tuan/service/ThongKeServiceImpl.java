package com.example.datn_tuan.service;

import com.example.datn_tuan.dto.DoanhThuNgayDTO;
import com.example.datn_tuan.dto.DoanhThuTuanDTO;
import com.example.datn_tuan.dto.SachBanChayDTO;
import com.example.datn_tuan.entity.HoaDon;
import com.example.datn_tuan.entity.HoaDonChiTiet;
import com.example.datn_tuan.repository.HoaDonChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Override
    public List<SachBanChayDTO> thongKeSachBanChay() {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findAll();

        Map<String, List<HoaDonChiTiet>> grouped = list.stream()
                .collect(Collectors.groupingBy(
                        hd -> hd.getSanPhamChiTiet().getSanPham().getTenSanPham()
                ));

        List<SachBanChayDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<HoaDonChiTiet>> entry : grouped.entrySet()) {
            String tenSanPham = entry.getKey();
            long tongSoLuong = entry.getValue().stream()
                    .mapToLong(HoaDonChiTiet::getSoLuongMua)
                    .sum();
            BigDecimal gia = entry.getValue().get(0).getSanPhamChiTiet().getDonGia();

            result.add(new SachBanChayDTO(tenSanPham, tongSoLuong, gia));
        }

        result.sort((a, b) -> Long.compare(b.getTongSoLuong(), a.getTongSoLuong()));

        return result;
    }

    @Override
    public List<DoanhThuNgayDTO> thongKeDoanhThuTheoNgay() {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findAll();

        Map<LocalDate, BigDecimal> map = list.stream()
                .filter(hdct -> hdct.getHoaDon() != null && hdct.getHoaDon().getNgayTao() != null)
                .collect(Collectors.groupingBy(
                        hdct -> hdct.getHoaDon().getNgayTao(),
                        Collectors.mapping(HoaDonChiTiet::getTongTien,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return map.entrySet().stream()
                .map(entry -> new DoanhThuNgayDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DoanhThuNgayDTO::getNgay).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<DoanhThuTuanDTO> thongKeDoanhThuTheoTuan() {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findAll();

        Locale localeVN = new Locale("vi", "VN");
        WeekFields weekFields = WeekFields.of(localeVN);

        Map<String, BigDecimal> map = list.stream()
                .filter(hdct -> hdct.getHoaDon() != null && hdct.getHoaDon().getNgayTao() != null)
                .collect(Collectors.groupingBy(
                        hdct -> {
                            LocalDate ngay = hdct.getHoaDon().getNgayTao();
                            int week = ngay.get(weekFields.weekOfWeekBasedYear());
                            int year = ngay.getYear();
                            return year + "-" + week;
                        },
                        Collectors.mapping(HoaDonChiTiet::getTongTien,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return map.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("-");
                    int nam = Integer.parseInt(parts[0]);
                    int tuan = Integer.parseInt(parts[1]);
                    return new DoanhThuTuanDTO(tuan, nam, entry.getValue());
                })
                .sorted(Comparator.comparing(DoanhThuTuanDTO::getNam).reversed()
                        .thenComparing(DoanhThuTuanDTO::getSoTuan).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal tinhDoanhThuTheoNgay() {
        LocalDate today = LocalDate.now();
        return hoaDonChiTietRepository.findAll().stream()
                .filter(hdct -> {
                    HoaDon hoaDon = hdct.getHoaDon();
                    return hoaDon != null && today.equals(hoaDon.getNgayTao());
                })
                .map(HoaDonChiTiet::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal tinhDoanhThuTheoTuan() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
        int currentYear = today.getYear();

        return hoaDonChiTietRepository.findAll().stream()
                .filter(hdct -> {
                    HoaDon hoaDon = hdct.getHoaDon();
                    if (hoaDon == null || hoaDon.getNgayTao() == null) return false;
                    LocalDate ngay = hoaDon.getNgayTao();
                    int week = ngay.get(weekFields.weekOfWeekBasedYear());
                    int year = ngay.getYear();
                    return week == currentWeek && year == currentYear;
                })
                .map(HoaDonChiTiet::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
