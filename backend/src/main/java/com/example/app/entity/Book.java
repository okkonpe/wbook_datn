package com.example.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "san_pham_chi_tiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Sản phẩm cha (có thể là product general info)
    @ManyToOne
    @JoinColumn(name = "ID_san_pham", nullable = false)
    private SanPham sanPham;

    @Column(name = "isbn", nullable = false, length = 13)
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "loai_giay")
    private LoaiGiay loaiGiay;

    @ManyToOne
    @JoinColumn(name = "loai_bia")
    private LoaiBia loaiBia;

    @ManyToOne
    @JoinColumn(name = "the_loai")
    private TheLoai theLoai;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "so_lan_tai_ban")
    private Integer soLanTaiBan;

    @Column(name = "ma_san_pham_chi_tiet", nullable = false, length = 15)
    private String maSanPhamChiTiet;

    @ManyToOne
    @JoinColumn(name = "nha_xuat_ban")
    private NhaXuatBan nhaXuatBan;

    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc")
    private KichThuoc kichThuoc;

    @Column(name = "khoi_luong_tinh")
    private Float khoiLuongTinh;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "ngay_xuat_ban")
    private LocalDate ngayXuatBan;

    @ManyToOne
    @JoinColumn(name = "hinh_anh")
    private HinhAnh hinhAnh;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "mo_ta", length = 100)
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;
    @ManyToMany
    @JoinTable(
            name = "sach_tac_gia",
            joinColumns = @JoinColumn(name = "id_tac_gia"),
            inverseJoinColumns = @JoinColumn(name = "id_san_pham_chi_tiet")
    )
    private Set<TacGia> tacGia = new HashSet<>();
    @OneToMany(mappedBy = "book")
    private List<HoaDonChiTiet> chiTietHoaDons;

}
