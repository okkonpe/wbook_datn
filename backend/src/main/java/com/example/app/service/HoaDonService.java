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
    if (hd.getNhanVien().getId()!=null&hd.getNhanVien().getId()!=idNhanVien){
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
@Transactional
public ThemGioHangDTO themGioHang(ThemGioHangDTO gioHangDTO){
    System.out.println("🛒 === THÊM VÀO GIỎ HÀNG (VERSION 2.0) ===");
    System.out.println("📦 Sản phẩm ID: " + gioHangDTO.getId());
    System.out.println("👤 Khách hàng ID: " + gioHangDTO.getKhachHangId());
    System.out.println("📊 Số lượng thêm: " + gioHangDTO.getSoLuong());

KhachHang kh =khachHangRepo.findById(gioHangDTO.getKhachHangId()).orElseThrow(()-> new RuntimeException("Không tìm khách hàng"));
    Book book =  bookRepository.findById(gioHangDTO.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
    
    // Kiểm tra số lượng tồn kho trước khi thêm vào giỏ hàng
    int currentStock = book.getSoLuong() == null ? 0 : book.getSoLuong();
    System.out.println("📦 Tồn kho hiện tại: " + currentStock + " (Mã: " + book.getMaSanPhamChiTiet() + ")");
    
    // Chỉ kiểm tra tồn kho khi thêm sản phẩm (soLuong > 0)
    if (gioHangDTO.getSoLuong() > 0 && currentStock < gioHangDTO.getSoLuong()) {
        throw new RuntimeException("Số lượng tồn không đủ cho mã: " + book.getMaSanPhamChiTiet() + 
                                 ". Tồn kho hiện tại: " + currentStock + ", yêu cầu: " + gioHangDTO.getSoLuong());
    }
    
    TrangThaiHoaDon trangThaiGio = trangThaiHoaDonRepo.findByTrangThai("GIO_HANG")
            .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái GIỎ_HÀNG"));
    HoaDon hoaDon = hoaDonRepository.findByKhachHangAndTrangThai(kh, trangThaiGio)
            .orElseGet(() -> {
            return    createHoaDon(kh);
            });
    Optional<HoaDonChiTiet> existingCT = gioHangRepo.findByHoaDonAndAndBook(hoaDon, book);

    int soLuongThucTeThem = gioHangDTO.getSoLuong(); // Số lượng thực tế được thêm vào

    if (existingCT.isPresent()) {
        HoaDonChiTiet ct = existingCT.get();
        int oldSoLuong = ct.getSoLuongMua();
        int newSoLuong = ct.getSoLuongMua() + gioHangDTO.getSoLuong();
        System.out.println("🔄 Sản phẩm đã có trong giỏ: " + oldSoLuong + " + " + gioHangDTO.getSoLuong() + " = " + newSoLuong);
        
        if (newSoLuong <= 0) {
            gioHangRepo.delete(ct);
            soLuongThucTeThem = -ct.getSoLuongMua(); // Nếu xóa hoàn toàn, số lượng thực tế là âm của số cũ
            System.out.println("🗑️ Xóa sản phẩm khỏi giỏ hàng, hoàn trả tồn kho: " + (-soLuongThucTeThem));
        } else {
            // Kiểm tra tổng số lượng trong giỏ hàng không vượt quá tồn kho (chỉ khi thêm)
            if (gioHangDTO.getSoLuong() > 0 && newSoLuong > currentStock) {
                throw new RuntimeException("Số lượng trong giỏ hàng vượt quá tồn kho cho mã: " + book.getMaSanPhamChiTiet() + 
                                         ". Tồn kho: " + currentStock + ", trong giỏ: " + newSoLuong);
            }
            ct.setSoLuongMua(newSoLuong);
            ct.setTongTien(book.getDonGia().multiply(BigDecimal.valueOf(newSoLuong)));
            gioHangRepo.save(ct);
            System.out.println("✅ Cập nhật số lượng trong giỏ hàng: " + newSoLuong);
        }
    } else {
        // Chỉ thêm sản phẩm mới khi soLuong > 0
        if (gioHangDTO.getSoLuong() > 0) {
            HoaDonChiTiet newCT = new HoaDonChiTiet();
            newCT.setHoaDon(hoaDon);
            newCT.setBook(book);
            newCT.setSoLuongMua(gioHangDTO.getSoLuong());
            newCT.setTongTien(book.getDonGia().multiply(BigDecimal.valueOf(gioHangDTO.getSoLuong())));
            newCT.setTrangThai(true);
            gioHangRepo.save(newCT);
            System.out.println("➕ Thêm sản phẩm mới vào giỏ hàng: " + gioHangDTO.getSoLuong());
        } else {
            System.out.println("⚠️ Không thể thêm sản phẩm với số lượng <= 0");
            soLuongThucTeThem = 0; // Không thay đổi tồn kho
        }
    }

    // Cập nhật số lượng tồn kho với số lượng thực tế được thêm/xóa
    int newStock = currentStock - soLuongThucTeThem;
    System.out.println("🔄 Cập nhật tồn kho: " + currentStock + " - " + soLuongThucTeThem + " = " + newStock);
    book.setSoLuong(newStock);
    bookRepository.save(book);
    System.out.println("✅ Đã lưu tồn kho mới: " + newStock);

    List<HoaDonChiTiet> chiTietList = gioHangRepo.findByHoaDon(hoaDon);

    BigDecimal tongTien = chiTietList.stream()
            .map(HoaDonChiTiet::getTongTien)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    hoaDon.setTongTien(tongTien);

    hoaDonRepository.save(hoaDon);
    System.out.println("🎉 === HOÀN THÀNH THÊM VÀO GIỎ HÀNG ===");
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

    // ================= OFFLINE CHECKOUT =================
    
    
    @Transactional(rollbackFor = Exception.class)
    public com.example.app.dto.banHangDTO.OfflinePaymentResponseDTO thanhToanOffline(
            com.example.app.dto.banHangDTO.OfflinePaymentRequestDTO request) {
        // Tạo hóa đơn độc lập, không phụ thuộc giỏ hàng hay khách hàng tồn tại
        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(null); // khách lẻ
        hoaDon.setMaHoaDon(taoMaHoaDonTuDong());
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setHoTenNguoiNhan(request.getHoTen());
        hoaDon.setDiaChiGiaoHang(request.getDiaChi());
        hoaDon.setSdtNguoiNhan(request.getSoDienThoai());
        hoaDon.setLoaiThanhToan("TAI_QUAY");

        TrangThaiHoaDon trangThai = trangThaiHoaDonRepo.findById(16)
                .orElseGet(() -> trangThaiHoaDonRepo.findById(4)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái HOÀN THÀNH (id 16/4)")));
        hoaDon.setTrangThai(trangThai);
        hoaDon = hoaDonRepository.save(hoaDon);

        java.math.BigDecimal tong = java.math.BigDecimal.ZERO;
        if (request.getItems() != null) {
            for (com.example.app.dto.banHangDTO.OfflinePaymentRequestDTO.Item it : request.getItems()) {
                Book book = bookRepository.findById(it.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sách id=" + it.getId()));
                int remain = (book.getSoLuong() == null ? 0 : book.getSoLuong()) - it.getSoLuong();
                if (remain < 0) {
                    throw new RuntimeException("Số lượng tồn không đủ cho mã: " + book.getMaSanPhamChiTiet());
                }
                book.setSoLuong(remain);
                bookRepository.save(book);
                HoaDonChiTiet ct = new HoaDonChiTiet();
                ct.setHoaDon(hoaDon);
                ct.setBook(book);
                ct.setSoLuongMua(it.getSoLuong());
                java.math.BigDecimal donGia = it.getDonGia() != null ? it.getDonGia() : book.getDonGia();
                ct.setTongTien(donGia.multiply(java.math.BigDecimal.valueOf(it.getSoLuong())));
                ct.setTrangThai(true);
                gioHangRepo.save(ct);
                tong = tong.add(ct.getTongTien());
            }
        }

        hoaDon.setTongTien(tong);
        
        // Sử dụng tongTienSauGiam từ frontend nếu có, nếu không thì tính từ voucher
        java.math.BigDecimal tongTienSauGiam = request.getTongTienSauGiam() != null ? 
            request.getTongTienSauGiam() : tong;
        
        // Xử lý phiếu giảm giá (chỉ khi có voucher được chọn)
        Voucher appliedVoucher = null;
        
        System.out.println("🔍 Request voucherId: " + request.getVoucherId());
        System.out.println("🔍 Request tongTienSauGiam: " + request.getTongTienSauGiam());
        System.out.println("🔍 Request giamGia: " + request.getGiamGia());
        
        if (request.getVoucherId() != null) {
            // Sử dụng voucher được chọn từ frontend
            System.out.println("🎫 Sử dụng voucher được chọn: " + request.getVoucherId());
            appliedVoucher = voucherRepo.findById(request.getVoucherId()).orElse(null);
            
            if (appliedVoucher != null) {
                System.out.println(" Thông tin voucher: " + appliedVoucher.getMaVoucher() + 
                                 " - Trạng thái: " + appliedVoucher.getTrangThai() +
                                 " - Số lượng: " + appliedVoucher.getSoLuong() +
                                 " - Đã dùng: " + appliedVoucher.getDaDung() +
                                 " - Đơn tối thiểu: " + appliedVoucher.getDonToiThieu() +
                                 " - Tổng tiền: " + tong);
                
                // Kiểm tra điều kiện voucher
                boolean isValid = appliedVoucher.getTrangThai() && 
                    appliedVoucher.getNgayBatDau().compareTo(java.time.LocalDate.now()) <= 0 &&
                    appliedVoucher.getNgayKetThuc().compareTo(java.time.LocalDate.now()) >= 0 &&
                    appliedVoucher.getSoLuong() > appliedVoucher.getDaDung() &&
                    tong.compareTo(appliedVoucher.getDonToiThieu()) >= 0;
                
                System.out.println(" Voucher hợp lệ: " + isValid);
                
                if (isValid) {
                    hoaDon.setVoucher(appliedVoucher);
                    
                    // Chỉ tính lại tongTienSauGiam nếu frontend không gửi
                    if (request.getTongTienSauGiam() == null) {
                        // Tính tiền giảm
                        java.math.BigDecimal discount = java.math.BigDecimal.ZERO;
                        if (appliedVoucher.getLoaiGiam() == com.example.app.entity.LoaiGiam.PERCENT) {
                            discount = tong.multiply(appliedVoucher.getGiaTri()).divide(new java.math.BigDecimal("100"));
                            if (appliedVoucher.getGiamToiDa() != null && discount.compareTo(appliedVoucher.getGiamToiDa()) > 0) {
                                discount = appliedVoucher.getGiamToiDa();
                            }
                        } else if (appliedVoucher.getLoaiGiam() == com.example.app.entity.LoaiGiam.AMOUNT) {
                            discount = appliedVoucher.getGiaTri();
                        }
                        
                        tongTienSauGiam = tong.subtract(discount);
                    }
                    
                    System.out.println("✅ Voucher áp dụng thành công: " + appliedVoucher.getMaVoucher() + 
                                     " - Tổng tiền: " + tong + " - Sau giảm: " + tongTienSauGiam);
                    
                    // Giảm số lượng voucher đã sử dụng
                    try {
                        int oldDaDung = appliedVoucher.getDaDung();
                        appliedVoucher.setDaDung(appliedVoucher.getDaDung() + 1);
                        Voucher savedVoucher = voucherRepo.save(appliedVoucher);
                        System.out.println("💾 Đã cập nhật voucher: " + appliedVoucher.getMaVoucher() + 
                                         " - Trước: " + oldDaDung + " - Sau: " + savedVoucher.getDaDung());
                    } catch (Exception e) {
                        System.out.println("❌ Lỗi khi cập nhật voucher: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Không thể cập nhật voucher: " + e.getMessage());
                    }
                } else {
                    System.out.println("❌ Voucher không đáp ứng điều kiện");
                    appliedVoucher = null;
                }
            } else {
                System.out.println("❌ Không tìm thấy voucher với ID: " + request.getVoucherId());
            }
        } else {
            System.out.println("ℹ️ Không có voucher nào được chọn");
        }
        
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        hoaDonRepository.save(hoaDon);

        // Tạo response với thông tin phiếu giảm giá
        com.example.app.dto.banHangDTO.OfflinePaymentResponseDTO resp = 
                new com.example.app.dto.banHangDTO.OfflinePaymentResponseDTO();
        resp.setHoaDonId(hoaDon.getId());
        resp.setMaHoaDon(hoaDon.getMaHoaDon());
        resp.setTongTien(tong);
        resp.setTongTienSauGiam(tongTienSauGiam);
        resp.setKhachThanhToan(request.getKhachThanhToan());
        resp.setTienThua(request.getTienThua());
        
        if (appliedVoucher != null) {
            resp.setVoucherApDung(appliedVoucher.getMaVoucher());
            resp.setTienGiam(tong.subtract(tongTienSauGiam));
            resp.setMessage("Đã áp dụng phiếu giảm giá: " + appliedVoucher.getMaVoucher() + 
                          " - Tiết kiệm: " + tong.subtract(tongTienSauGiam) + " VNĐ");
        } else {
            resp.setVoucherApDung(null);
            resp.setTienGiam(java.math.BigDecimal.ZERO);
            resp.setMessage("Không có phiếu giảm giá được áp dụng");
        }
        
        return resp;
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

        // Lấy số lượng sản phẩm trong giỏ hàng trước khi xóa để hoàn trả tồn kho
        Optional<HoaDonChiTiet> existingCT = gioHangRepo.findByHoaDonAndAndBook(hoaDon, book);
        if (existingCT.isPresent()) {
            int soLuongTrongGio = existingCT.get().getSoLuongMua();
            
            // Hoàn trả số lượng tồn kho
            int currentStock = book.getSoLuong() == null ? 0 : book.getSoLuong();
            book.setSoLuong(currentStock + soLuongTrongGio);
            bookRepository.save(book);
        }

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

    // Thống kê tổng quan
    public java.util.Map<String, Object> getSummaryStatistics() {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate startOfMonth = today.withDayOfMonth(1);

        summary.put("totalRevenue", hoaDonRepository.sumRevenueBetween(LocalDate.of(2000, 1, 1), today));
        summary.put("todayRevenue", hoaDonRepository.sumRevenueBetween(today, today));
        summary.put("weekRevenue", hoaDonRepository.sumRevenueBetween(startOfWeek, today));
        summary.put("monthRevenue", hoaDonRepository.sumRevenueBetween(startOfMonth, today));
        summary.put("totalOrders", hoaDonRepository.countAllOrders());
        summary.put("activeVouchers", 0); // Placeholder
        summary.put("topSellingCount", 0); // Placeholder
        summary.put("lowStockCount", 0); // Placeholder
        return summary;
    }

    // Thống kê doanh thu theo thời gian
    public java.util.List<java.util.Map<String, Object>> getRevenueStatistics(String type, LocalDate from, LocalDate to) {
        java.util.List<java.util.Map<String, Object>> data = new java.util.ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        switch (type) {
            case "day":
                data.add(java.util.Map.of("date", currentDate.format(DateTimeFormatter.ISO_DATE), 
                        "revenue", hoaDonRepository.sumRevenueBetween(currentDate, currentDate)));
                break;
            case "week":
                for (int i = 6; i >= 0; i--) {
                    LocalDate date = currentDate.minusDays(i);
                    data.add(java.util.Map.of("date", date.format(DateTimeFormatter.ISO_DATE), 
                            "revenue", hoaDonRepository.sumRevenueBetween(date, date)));
                }
                break;
            case "month":
                for (int i = 29; i >= 0; i--) {
                    LocalDate date = currentDate.minusDays(i);
                    data.add(java.util.Map.of("date", date.format(DateTimeFormatter.ISO_DATE), 
                            "revenue", hoaDonRepository.sumRevenueBetween(date, date)));
                }
                break;
            case "custom":
                if (from != null && to != null) {
                    for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
                        data.add(java.util.Map.of("date", date.format(DateTimeFormatter.ISO_DATE), 
                                "revenue", hoaDonRepository.sumRevenueBetween(date, date)));
                    }
                }
                break;
        }
        return data;
    }

    // Top sản phẩm bán chạy
    public java.util.List<java.util.Map<String, Object>> getTopSellingProducts(int limit) {
        java.util.List<Object[]> results = hoaDonRepository.findTopSellingProducts(limit);
        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        
        for (Object[] row : results) {
            java.util.Map<String, Object> product = new java.util.HashMap<>();
            product.put("id", row[0]);
            product.put("maSanPhamChiTiet", row[1]);
            product.put("tenSanPham", row[2]);
            product.put("soLuongDaBan", row[3]);
            products.add(product);
        }
        return products;
    }

    // Sản phẩm sắp hết hàng
    public java.util.List<java.util.Map<String, Object>> getLowStockProducts(int limit) {
        java.util.List<Object[]> results = hoaDonRepository.findLowStockProducts(limit);
        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        
        for (Object[] row : results) {
            java.util.Map<String, Object> product = new java.util.HashMap<>();
            product.put("id", row[0]);
            product.put("maSanPhamChiTiet", row[1]);
            product.put("tenSanPham", row[2]);
            product.put("soLuong", row[3]);
            products.add(product);
        }
        return products;
    }

}
