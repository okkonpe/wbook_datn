package com.example.app.service;

import com.example.app.dto.banHangDTO.*;
import com.example.app.entity.*;
import com.example.app.mapper.banHangMapper.GioHangMapper;
import com.example.app.mapper.banHangMapper.HoaDonMapper;
import com.example.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HoaDonService {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    TrangThaiHoaDonRepo trangThaiHoaDonRepo;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @Autowired
    HoaDonChiTietRepo gioHangRepo;
    @Autowired
    GioHangMapper gioHangMapper;
    @Autowired
    HoaDonMapper hoaDonMapper;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    KhachHangRepo khachHangRepo;
    @Autowired
VoucherRepo voucherRepo;
    @Autowired
    private SimpMessagingTemplate   messagingTemplate;
    public HoaDon createHoaDon(KhachHang khachHang){
        HoaDon hoaDon = new HoaDon();
                hoaDon.setKhachHang(khachHang);
        //        hoaDon.setNhanVien(nhanVien);
        hoaDon.setMaHoaDon(taoMaHoaDonTuDong());
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setTrangThai(trangThaiHoaDonRepo.findByTrangThai("GIO_HANG") .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIO_HANG"))); // FK
        hoaDon = hoaDonRepository.save(hoaDon);
    return hoaDon;
}

public Page<ListDonHangDTO> getAllOrder(Pageable pageable){
    List<Integer> list = List.of(1,12,13);
        return hoaDonRepository.findByTrangThaiIdNotIn(list,pageable).map(hoaDonMapper::donHangtoDTO);
}


    public Page<ListDonHangDTO> getHoaDon(Pageable pageable){
        List<Integer> list = List.of(1,2,3,11);
        return hoaDonRepository.findByTrangThaiIdNotIn(list,pageable).map(hoaDonMapper::donHangtoDTO);

    }

public List<ListGioHangDTO> getListItemHD(Integer id){
      return   gioHangRepo.findByHoaDonId(id).stream().map(gioHangMapper::toDTOListGH).collect(Collectors.toList());

}
    
    
@Transactional
public HoaDonRequestDTO thanhToanCOD(HoaDonRequestDTO request){
    System.out.println(request.getKhachHangID());
    HoaDon hoaDon = hoaDonRepository.findByKhachHangIdAndTrangThaiId(
            request.getKhachHangID(), 1).
            orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn giỏ hàng"));
if (request.getMaVoucher()!=null){
    Voucher voucher = voucherRepo.findByMaVoucher(request.getMaVoucher()).orElseThrow();
    hoaDon.setVoucher(voucher);
}
    // 2. Cập nhật thông tin từ FE
    hoaDon.setHoTenNguoiNhan(request.getHoTen());
    hoaDon.setDiaChiGiaoHang(request.getDiaChi());
    hoaDon.setSdtNguoiNhan(request.getSoDienThoai());
    hoaDon.setGhiChu(request.getGhiChu());
    hoaDon.setLoaiThanhToan(request.getPhuongThucThanhToan());
    hoaDon.setTongTien(request.getTongTien());
    hoaDon.setPhiShip(new BigDecimal("30000"));

    hoaDon.setTongTienSauGiam(request.getTongTienSauGiam()); // hoặc request.getTongTienSauGiam()

    // 3. Set trạng thái = 2 (đã đat hang)
    TrangThaiHoaDon trangThaiDaDatHang = trangThaiHoaDonRepo.findById(2)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái"));

    hoaDon.setTrangThai(trangThaiDaDatHang);

    // 4. Lưu lại
    hoaDonRepository.save(hoaDon);
    messagingTemplate.convertAndSend("/topic/admin/don-hang-moi", hoaDonMapper.donHangtoDTO(hoaDon));

    // 5. Trả lại thông tin xác nhận (hoặc bạn có thể tạo HoaDonResponseDTO)
    return request;

}
    public void updateDatHang(Integer idHD,HoaDonRequestDTO request) {
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        hd.setHoTenNguoiNhan(request.getHoTen());
        hd.setDiaChiGiaoHang(request.getDiaChi());
        hd.setSdtNguoiNhan(request.getSoDienThoai());
        hd.setGhiChu(request.getGhiChu());
        hd.setPhiShip(new BigDecimal("30000"));
        if (request.getMaVoucher()!=null){
            Voucher voucher = voucherRepo.findByMaVoucher(request.getMaVoucher()).orElseThrow();
            hd.setVoucher(voucher);
        }
hd.setTongTien(request.getTongTien());
hd.setTongTienSauGiam(request.getTongTienSauGiam());
        hoaDonRepository.save(hd);
    }
    public void updateTrangThaiDaThanhToan(Integer idHD,String trangThai) {
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        hd.setLoaiThanhToan(trangThai);
        TrangThaiHoaDon trangThaiDaDatHang = trangThaiHoaDonRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái"));

        hd.setTrangThai(trangThaiDaDatHang);
        hoaDonRepository.save(hd);
        messagingTemplate.convertAndSend("/topic/admin/don-hang-moi",hoaDonMapper.donHangtoDTO(hd));


    }
    public void updateTrangThaiChuaThanhToan(Integer idHD,String trangThai) {
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        hd.setLoaiThanhToan(trangThai);
        hoaDonRepository.save(hd);
    }
    public ListDonHangDTO chuyenTrangThaiDaXacNhan(Integer idHD,Integer idNhanVien){
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));

        TrangThaiHoaDon trangThaiHoaDon = trangThaiHoaDonRepo.findById(11).orElseThrow(() -> new RuntimeException("Không tìm trạng thái"));
        if (hd.getTrangThai().getId()==4){
            throw new IllegalArgumentException("Đơn hàng đã giao!");
        }
        if (hd.getTrangThai().getId()==11){
            throw new IllegalArgumentException("Đơn hàng đã xác nhận!");
        }
        if (hd.getTrangThai().getId()==5){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==14){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==15){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==3){
            throw new IllegalArgumentException("Đơn hàng đang giao!");
        }
        hd.setTrangThai(trangThaiHoaDon);
        NhanVien nv = nhanVienRepository.findById(idNhanVien).orElseThrow();
        hd.setNhanVien(nv);
        hoaDonRepository.save(hd);
        messagingTemplate.convertAndSend("/topic/admin/da-xac-nhan", hoaDonMapper.donHangtoDTO(hd));

        return hoaDonMapper.donHangtoDTO(hd);
    }

public ListDonHangDTO chuyenTrangThaiDangGiaoHang(Integer idHD,Integer idNhanVien){
    HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        TrangThaiHoaDon trangThaiHoaDon = trangThaiHoaDonRepo.findById(3).orElseThrow(() -> new RuntimeException("Không tìm trạng thái"));
    if (hd.getNhanVien().getId()!=idNhanVien){
        throw new IllegalArgumentException("Đã có nhân viên xác nhận đơn này!");
    }
    if (hd.getTrangThai().getId()==4){
        throw new IllegalArgumentException("Đơn hàng đã giao!");
    }
    if (hd.getTrangThai().getId()==5){
        throw new IllegalArgumentException("Đơn hàng đã huỷ!");
    }
    if (hd.getTrangThai().getId()==2){
        throw new IllegalArgumentException("Đơn hàng cần được xác nhận!");
    }
    if (hd.getTrangThai().getId()==15){
        throw new IllegalArgumentException("Đơn hàng đã huỷ!");
    }
    if (hd.getTrangThai().getId()==14){
        throw new IllegalArgumentException("Đơn hàng đã huỷ!");
    }
    hd.setTrangThai(trangThaiHoaDon);

        hoaDonRepository.save(hd);
        return hoaDonMapper.donHangtoDTO(hd);
}
    public ListDonHangDTO chuyenTrangThaiDaGiaoHang(Integer idHD,Integer idNhanVien){
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        TrangThaiHoaDon trangThaiHoaDon = trangThaiHoaDonRepo.findById(4).orElseThrow(() -> new RuntimeException("Không tìm trạng thái"));
        if (hd.getNhanVien().getId()!=idNhanVien){
            throw new IllegalArgumentException("Đã có nhân viên xác nhận đơn này!");
        }
        if (hd.getTrangThai().getId()==5){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==15){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==14){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==2){
            throw new IllegalArgumentException("Đơn hàng cần xác nhận!");
        }
        if (hd.getTrangThai().getId()==11){
            throw new IllegalArgumentException("Đơn hàng cần phải đi giao!");
        }
        hd.setTrangThai(trangThaiHoaDon);
        hoaDonRepository.save(hd);
        return hoaDonMapper.donHangtoDTO(hd);
    }
    public ListDonHangDTO chuyenTrangThaiNhanVienHuy(Integer idHD,Integer idNhanVien){
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        TrangThaiHoaDon trangThaiHoaDon = trangThaiHoaDonRepo.findById(5).orElseThrow(() -> new RuntimeException("Không tìm trạng thái"));
        if (hd.getNhanVien().getId()!=idNhanVien){
            throw new IllegalArgumentException("Đã có nhân viên xác nhận đơn này!");
        }

        if (hd.getTrangThai().getId()==4){
            throw new IllegalArgumentException("Đơn hàng đã giao, không được huỷ!");
        }
        if (hd.getTrangThai().getId()==14){
            throw new IllegalArgumentException("Đơn đã huỷ!");
        }
        if (hd.getTrangThai().getId()==15){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        hd.setTrangThai(trangThaiHoaDon);
        hoaDonRepository.save(hd);
        return hoaDonMapper.donHangtoDTO(hd);
    }
    public ListDonHangDTO chuyenTrangThaiGHThatBai(Integer idHD,Integer idNhanVien){
        HoaDon hd = hoaDonRepository.findById(idHD).orElseThrow(() -> new RuntimeException("Không tìm thấy hoá đơn"));
        TrangThaiHoaDon trangThaiHoaDon = trangThaiHoaDonRepo.findById(14).orElseThrow(() -> new RuntimeException("Không tìm trạng thái"));
        if (hd.getNhanVien().getId()!=idNhanVien){
            throw new IllegalArgumentException("Đã có nhân viên xác nhận đơn này!");
        }
        if (hd.getTrangThai().getId()==4){
            throw new IllegalArgumentException("Đơn hàng đã giao!");
        }
        if (hd.getTrangThai().getId()==5){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==15){
            throw new IllegalArgumentException("Đơn hàng đã huỷ!");
        }
        if (hd.getTrangThai().getId()==11){
            throw new IllegalArgumentException("Đơn hàng đã xác nhận!");
        }
        if (hd.getTrangThai().getId()==2){
            throw new IllegalArgumentException("Đơn hàng chờ xác nhận!");
        }
        hd.setTrangThai(trangThaiHoaDon);
        hoaDonRepository.save(hd);
        return hoaDonMapper.donHangtoDTO(hd);
    }
public ThemGioHangDTO themGioHang(ThemGioHangDTO gioHangDTO){

KhachHang kh =khachHangRepo.findById(gioHangDTO.getKhachHangId()).orElseThrow(()-> new RuntimeException("Không tìm khách hàng"));
    Book book =  bookRepository.findById(gioHangDTO.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
    TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
            .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIỎ_HÀNG"));
    HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
            .orElseGet(() -> {
            return    createHoaDon(kh);
            });
    Optional<HoaDonChiTiet> existingCT = gioHangRepo.findByHoaDonAndAndBook(hoaDon, book);

    if (existingCT.isPresent()) {
        HoaDonChiTiet ct = existingCT.get();
        int newSoLuong = ct.getSoLuongMua() + gioHangDTO.getSoLuong();
        if (newSoLuong <= 0) {
            gioHangRepo.delete(ct);
        } else {
            ct.setSoLuongMua(newSoLuong);
            ct.setTongTien(book.getDonGia().multiply(BigDecimal.valueOf(newSoLuong)));
            gioHangRepo.save(ct);
        }
    } else {
        HoaDonChiTiet newCT = new HoaDonChiTiet();
        newCT.setHoaDon(hoaDon);
        newCT.setBook(book);
        newCT.setSoLuongMua(gioHangDTO.getSoLuong());
        newCT.setTongTien(book.getDonGia().multiply(BigDecimal.valueOf(gioHangDTO.getSoLuong())));
        newCT.setTrangThai(true);
        gioHangRepo.save(newCT);
    }


    List<HoaDonChiTiet> chiTietList = gioHangRepo.findByHoaDon(hoaDon);

    BigDecimal tongTien = chiTietList.stream()
            .map(HoaDonChiTiet::getTongTien)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    int tongSoLuong = chiTietList.stream()
            .mapToInt(HoaDonChiTiet::getSoLuongMua)
            .sum();

    hoaDon.setTongTien(tongTien);

    hoaDonRepository.save(hoaDon);
return gioHangDTO;
}
//public List<ListHoaDonDTO> getHoaDonByKhachHangID(){
//
//}

    public List<ListGioHangDTO> getGioHangByKhachHangId(Integer khachHangId) {
        KhachHang kh = khachHangRepo.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIO_HANG"));

        HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
                .orElseThrow(() -> new RuntimeException("Khách hàng chưa có giỏ hàng"));

        List<ListGioHangDTO> chiTietList = gioHangRepo.getGioHangDTOByHoaDon(hoaDon);

        return chiTietList;
    }
    @Transactional
    public void xoaSanPhamKhoiGioHang(Integer khachHangId, Integer idSanPham) {
        KhachHang kh = khachHangRepo.findById(khachHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIỎ_HÀNG"));

        HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        Book book = bookRepository.findById(idSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        gioHangRepo.deleteByHoaDonAndBook(hoaDon, book);
    }


    public String taoMaHoaDonTuDong() {
        String prefix = "HD";
        String ngayHomNay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseMa = prefix + ngayHomNay;
        int stt = 1;
        String maHoaDon;

        do {
            String soThuTu = String.format("%04d", stt); // VD: 0001
            maHoaDon = baseMa + soThuTu;
            stt++;
        } while (hoaDonRepository.existsByMaHoaDon(maHoaDon));

        return maHoaDon;
    }

}
