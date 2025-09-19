package com.example.app.service;

import com.example.app.controller.KhachHangController.QuickCustomerDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.khachHangDTO.KhachHangInfoDTO;
import com.example.app.dto.khachHangDTO.KhachHangRegisterDTO;
import com.example.app.entity.KhachHang;
import com.example.app.mapper.KhachHangMapper;
import com.example.app.mapper.banHangMapper.HoaDonMapper;
import com.example.app.repository.HoaDonRepository;
import com.example.app.repository.KhachHangRepo;
import com.example.app.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepo repo;

    @Autowired
    private HoaDonMapper hoaDonMapper;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KhachHangMapper mapper;

    // 👈 THÊM CÁC CRUD METHODS
    public Page<KhachHangInfoDTO> getAll(Pageable pageable) {
        Page<KhachHang> khachHangPage = repo.findAll(pageable);
        return khachHangPage.map(mapper::khInfoToDTO);
    }

    public KhachHangInfoDTO save(KhachHangInfoDTO dto) {
        try {
            System.out.println("=== DEBUG: Bắt đầu save khách hàng ===");
            System.out.println("DTO nhận được: " + dto);
            
            // Validate input
            if (dto.getTenKhachHang() == null || dto.getTenKhachHang().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên khách hàng không được để trống");
            }
            
            // Validate độ dài tên khách hàng
            if (dto.getTenKhachHang().trim().length() > 30) {
                throw new IllegalArgumentException("Tên khách hàng không được vượt quá 30 ký tự");
            }
            
            // Kiểm tra SĐT trùng lặp
            if (dto.getSdt() != null && !dto.getSdt().trim().isEmpty()) {
                if (dto.getSdt().trim().length() > 13) {
                    throw new IllegalArgumentException("Số điện thoại không được vượt quá 13 ký tự");
                }
                if (repo.findBySdt(dto.getSdt()).isPresent()) {
                    throw new IllegalArgumentException("Số điện thoại đã tồn tại");
                }
            }
            
            // Validate email
            if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
                if (dto.getEmail().trim().length() > 30) {
                    throw new IllegalArgumentException("Email không được vượt quá 30 ký tự");
                }
            }
            
            // Validate địa chỉ
            if (dto.getDiaChi() != null && !dto.getDiaChi().trim().isEmpty()) {
                if (dto.getDiaChi().trim().length() > 50) {
                    throw new IllegalArgumentException("Địa chỉ không được vượt quá 50 ký tự");
                }
            }

            // Tự động sinh mã khách hàng
            String generatedMa = generateUniqueMaKhachHang();
            System.out.println("Mã khách hàng được tạo: " + generatedMa);
            
            // Validate độ dài mã khách hàng
            if (generatedMa.length() > 15) {
                throw new RuntimeException("Mã khách hàng quá dài: " + generatedMa);
            }

            KhachHang khachHang = new KhachHang();
            khachHang.setMaKhachHang(generatedMa);
            khachHang.setTenKhachHang(dto.getTenKhachHang().trim());
            khachHang.setSdt(dto.getSdt() != null ? dto.getSdt().trim() : null);
            khachHang.setNgaySinh(dto.getNgaySinh());
            khachHang.setDiaChi(dto.getDiaChi() != null ? dto.getDiaChi().trim() : null);
            khachHang.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
            khachHang.setGioiTinh(dto.getGioiTinh() != null ? dto.getGioiTinh() : true);
            khachHang.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : "Hoạt động");

            System.out.println("Entity trước khi save: " + khachHang);
            KhachHang saved = repo.save(khachHang);
            System.out.println("Entity sau khi save: " + saved);
            
            KhachHangInfoDTO result = mapper.khInfoToDTO(saved);
            System.out.println("DTO kết quả: " + result);
            System.out.println("=== DEBUG: Hoàn thành save khách hàng ===");
            
            return result;
        } catch (Exception e) {
            System.err.println("=== ERROR: Lỗi khi save khách hàng ===");
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lưu khách hàng: " + e.getMessage(), e);
        }
    }

    public KhachHangInfoDTO update(KhachHangInfoDTO dto, Integer id) {
        KhachHang entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        entity.setTenKhachHang(dto.getTenKhachHang());
        entity.setSdt(dto.getSdt());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setEmail(dto.getEmail());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setTrangThai(dto.getTrangThai());

        KhachHang saved = repo.save(entity);
        return mapper.khInfoToDTO(saved);
    }

    public void delete(Integer id) {
        KhachHang entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        repo.delete(entity);
    }

    private String generateUniqueMaKhachHang() {
        String prefix = "KH";
        String ma;
        int attempts = 0;
        do {
            // Sử dụng timestamp ngắn gọn hơn để tránh vượt quá 15 ký tự
            long timestamp = System.currentTimeMillis() % 1000000; // Chỉ lấy 6 số cuối
            int random = (int) (Math.random() * 100);
            ma = prefix + String.format("%06d%02d", timestamp, random);
            attempts++;
            if (attempts > 10) { // Tránh infinite loop
                throw new RuntimeException("Không thể tạo mã khách hàng duy nhất");
            }
        } while (repo.existsByMaKhachHang(ma));
        return ma;
    }

    // Giữ nguyên các method cũ
    public void register(KhachHangRegisterDTO dto) {
        String tk = dto.getTaiKhoan();
        if (tk != null && (repo.findByTaiKhoan(dto.getTaiKhoan()).isPresent() || nhanVienRepository.existsByTaiKhoan(tk))) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại");
        }

        KhachHang kh = mapper.khRegistertoEntity(dto);
        kh.setMatKhau(encoder.encode(dto.getMatKhau()));
        kh.setTrangThai("Hoạt động");  // 👈 Set String mặc định
        repo.save(kh);
    }

    public List<ListDonHangDTO> layDonHangTheoKhachHang(String username) {
        KhachHang kh = repo.findByTaiKhoan(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

List<Integer> list = List.of(1,2);
        return hoaDonRepository.findByKhachHangAndTrangThaiIdNotInOrderByNgayTaoDesc(kh,list).stream().map(hoaDonMapper::donHangtoDTO).collect(Collectors.toList());
    }
    public KhachHangInfoDTO thongTinKhachHang(String username){
        KhachHang kh = repo.findByTaiKhoan(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return mapper.khInfoToDTO(kh);
    }

    // Lấy danh sách tất cả khách hàng cho admin
    public Page<KhachHang> getAllCustomers(Pageable pageable) {
        return repo.findAll(pageable);
    }

    // Tạo khách hàng mới nhanh (không cần tài khoản)
    public KhachHang createQuickCustomer(QuickCustomerDTO dto) {
        // Kiểm tra SĐT đã tồn tại chưa
        if (dto.getSdt() != null && !dto.getSdt().trim().isEmpty()) {
            if (repo.findBySdt(dto.getSdt()).isPresent()) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại");
            }
        }

        // Tạo mã khách hàng tự động
        String maKhachHang = generateCustomerCode();

        KhachHang customer = new KhachHang();
        customer.setMaKhachHang(maKhachHang);
        customer.setTenKhachHang(dto.getTenKhachHang());
        customer.setSdt(dto.getSdt());
        customer.setDiaChi(dto.getDiaChi());
        customer.setEmail(dto.getEmail());
        customer.setTrangThai("ACTIVE");
        // Không set tài khoản và mật khẩu cho khách hàng nhanh

        return repo.save(customer);
    }

    // Tạo mã khách hàng tự động
    private String generateCustomerCode() {
        long count = repo.count();
        return String.format("KH%06d", count + 1);
    }
}
