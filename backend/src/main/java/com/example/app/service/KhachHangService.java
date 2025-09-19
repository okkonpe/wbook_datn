package com.example.app.service;

import com.example.app.controller.KhachHangController.QuickCustomerDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.dto.khachHangDTO.KhachHangInfoDTO;
import com.example.app.dto.khachHangDTO.KhachHangRegisterDTO;
import com.example.app.entity.HoaDon;
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

import java.util.ArrayList;
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

    public void register(KhachHangRegisterDTO dto) {
        String tk = dto.getTaiKhoan();
        if (tk!=null&&(repo.findByTaiKhoan(dto.getTaiKhoan()).isPresent()||nhanVienRepository.existsByTaiKhoan(tk))) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại");
        }

        KhachHang kh = mapper.khRegistertoEntity(dto);
        kh.setMatKhau(encoder.encode(dto.getMatKhau()));

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
