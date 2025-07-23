package com.example.app.service;

import com.example.app.entity.KhachHang;
import com.example.app.entity.NhanVien;
import com.example.app.repository.KhachHangRepo;
import com.example.app.repository.NhanVienRepository;
import com.example.app.security.KhachHangUserDetails;
import com.example.app.security.NhanVienUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KhachHangUserDetailsService implements UserDetailsService {
    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Check bảng nhân viên
        Optional<NhanVien> optNV = nhanVienRepository.findByTaiKhoan(username);
        if (optNV.isPresent()) {
            NhanVien nv = optNV.get();
            System.out.println(nv.getId());
            String role =  nv.getChucVu().getTenChucVu().toUpperCase(); // đảm bảo đúng format
            return new NhanVienUserDetails(
                    nv.getId(),
                    nv.getTaiKhoan(),
                    nv.getMatKhau(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // 2. Check bảng khách hàng
        Optional<KhachHang> optKH = khachHangRepo.findByTaiKhoan(username);
        if (optKH.isPresent()) {
            KhachHang kh = optKH.get();
            return new KhachHangUserDetails(
                    kh.getId(),
                    kh.getTaiKhoan(),
                    kh.getMatKhau(),
                    List.of(new SimpleGrantedAuthority("ROLE_KHACH_HANG"))
            );
        }

        // 3. Không tìm thấy tài khoản ở cả hai bảng
        throw new UsernameNotFoundException("Không tìm thấy tài khoản trong hệ thống");
    }


}
