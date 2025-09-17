package com.example.app.service;

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
        // Tự động sinh mã khách hàng
        String generatedMa = generateUniqueMaKhachHang();

        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(generatedMa);
        khachHang.setTenKhachHang(dto.getTenKhachHang());
        khachHang.setSdt(dto.getSdt());
        khachHang.setNgaySinh(dto.getNgaySinh());
        khachHang.setDiaChi(dto.getDiaChi());
        khachHang.setEmail(dto.getEmail());
        khachHang.setGioiTinh(dto.getGioiTinh() != null ? dto.getGioiTinh() : true);
        khachHang.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : "Hoạt động");  // 👈 String

        KhachHang saved = repo.save(khachHang);
        return mapper.khInfoToDTO(saved);
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
        entity.setTrangThai(dto.getTrangThai());  // 👈 String

        KhachHang updated = repo.save(entity);
        return mapper.khInfoToDTO(updated);
    }

    public void delete(Integer id) {
        KhachHang entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        repo.delete(entity);
    }

    private String generateUniqueMaKhachHang() {
        String prefix = "KH";
        int number = 1;
        String ma;
        do {
            ma = prefix + String.format("%03d", number);
            number++;
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
        List<Integer> list = List.of(1, 2);
        return hoaDonRepository.findByKhachHangAndTrangThaiIdNotInOrderByNgayTaoDesc(kh, list)
                .stream().map(hoaDonMapper::donHangtoDTO).collect(Collectors.toList());
    }

    public KhachHangInfoDTO thongTinKhachHang(String username) {
        KhachHang kh = repo.findByTaiKhoan(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        return mapper.khInfoToDTO(kh);
    }
}
