package com.example.app.mapper.banHangMapper;

import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.dto.banHangDTO.ThemGioHangDTO;
import com.example.app.entity.HoaDonChiTiet;
import com.example.app.entity.TacGia;
import com.example.app.entity.ChuDe;
import com.example.app.entity.TaiBan;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GioHangMapper {
    ThemGioHangDTO toDTOThemGH(HoaDonChiTiet chiTiet);
    
    @Mapping(target = "hoaDon", ignore = true)
    @Mapping(target = "book", ignore = true)
    HoaDonChiTiet toEntityThemGH(ThemGioHangDTO gioHangDTO);
    @Mapping(source = "book.id", target = "idSanPham")
    @Mapping(source = "book.sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "book.donGia", target = "donGia")
    @Mapping(source = "book.hinhAnh.hinhAnh", target = "hinhAnh")
    @Mapping(source = "soLuongMua", target = "soLuongMua")
    @Mapping(source = "tongTien", target = "tongTien")
    @Mapping(source = "book.isbn", target = "isbn")
    @Mapping(source = "book.maSanPhamChiTiet", target = "maSanPhamChiTiet")
    @Mapping(source = "book.theLoai.tenTheLoai", target = "theLoai")
    @Mapping(source = "book.nhaXuatBan.tenNhaXuatBan", target = "nhaXuatBan")
    @Mapping(source = "book.kichThuoc.chiSoKichThuoc", target = "kichThuoc")
    @Mapping(source = "book.loaiBia.tenBia", target = "loaiBia")
    @Mapping(source = "book.loaiGiay.tenGiay", target = "loaiGiay")
    @Mapping(source = "book.soTrang", target = "soTrang")
    @Mapping(source = "book.khoiLuongTinh", target = "khoiLuongTinh")
    @Mapping(source = "book.ngayXuatBan", target = "ngayXuatBan")
    @Mapping(source = "book.moTa", target = "moTa")
    @Mapping(source = "book.tacGia", target = "tacGia", qualifiedByName = "mapTacGiaNames")
    @Mapping(source = "book.chuDes", target = "chuDe", qualifiedByName = "mapChuDeNames")
    @Mapping(source = "book.taiBans", target = "taiBans", qualifiedByName = "mapTaiBanNames")
    ListGioHangDTO toDTOListGH(HoaDonChiTiet chiTiet);
    @AfterMapping
    default void buildImgUrlDetail(HoaDonChiTiet book, @MappingTarget ListGioHangDTO detailDTO){
        if (book.getBook().getHinhAnh() != null && book.getBook().getHinhAnh().getHinhAnh() != null && !book.getBook().getHinhAnh().getHinhAnh().isEmpty()) {
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/")
                    .path(book.getBook().getHinhAnh().getHinhAnh())
                    .toUriString();
            detailDTO.setHinhAnh(imageUrl);
        }
    }

    @Named("mapTacGiaNames")
    default List<String> mapTacGiaNames(Set<TacGia> tacGiaSet) {
        if (tacGiaSet == null) return null;
        return tacGiaSet.stream()
                .map(TacGia::getTenTacGia)
                .collect(Collectors.toList());
    }

    @Named("mapChuDeNames")
    default List<String> mapChuDeNames(Set<ChuDe> chuDeSet) {
        if (chuDeSet == null) return null;
        return chuDeSet.stream()
                .map(ChuDe::getTenChuDe)
                .collect(Collectors.toList());
    }

    @Named("mapTaiBanNames")
    default Set<String> mapTaiBanNames(Set<TaiBan> taiBanSet) {
        if (taiBanSet == null) return null;
        return taiBanSet.stream()
                .map(tb -> "Lần " + tb.getLanTaiBan() + " - " + tb.getNamTaiBan())
                .collect(Collectors.toSet());
    }
}
