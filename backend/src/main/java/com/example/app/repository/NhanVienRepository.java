package com.example.app.repository;

import com.example.app.dto.nhanVienDTO.NhanVienDTO;
import com.example.app.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByMaNv(String maNv);
    Optional<NhanVien> findByEmail(String email);
    Optional<NhanVien> findByCccd(String cccd);
    Optional<NhanVien> findByTaiKhoan(String taiKhoan);
    List<NhanVien> findByChucVu_TenChucVu(String tenChucVu);

    @Query("""
    SELECT new com.example.app.dto.nhanVienDTO.NhanVienDTO(
        n.id,c.tenChucVu,n.ngayBatDau,
        n.maNv, n.tenNv, n.luong, n.sdt, n.ngaySinh, n.diaChi,
        n.email, n.gioiTinh, n.cccd, n.taiKhoan,
        n.trangThai
    )
    FROM NhanVien n
    JOIN n.chucVu c
""")
    Page<NhanVienDTO> getAllNhanVienDTO(Pageable pageable);

    boolean existsByMaNv(String maNv);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
    boolean existsByTaiKhoan(String taiKhoan);
    
    Page<NhanVien> findByTenNvContaining(String tenNv, Pageable pageable);
} 