package com.example.app.mapper.banHangMapper;

import com.example.app.dto.banHangDTO.ListGioHangDTO;
import com.example.app.dto.banHangDTO.ThemGioHangDTO;
import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.entity.Book;
import com.example.app.entity.HoaDon;
import com.example.app.entity.HoaDonChiTiet;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring")
public interface GioHangMapper {
    ThemGioHangDTO toDTOThemGH(HoaDonChiTiet chiTiet);
    HoaDonChiTiet toEntityThemGH(ThemGioHangDTO gioHangDTO);
    @Mapping(source = "book.id", target = "idSanPham")
    @Mapping(source = "book.sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "book.donGia", target = "donGia")
    @Mapping(source = "book.hinhAnh.hinhAnh", target = "hinhAnh")
    @Mapping(source = "soLuongMua", target = "soLuongMua")
    @Mapping(source = "tongTien", target = "tongTien")
    ListGioHangDTO toDTOListGH(HoaDonChiTiet chiTiet);
    @AfterMapping
    default void buildImgUrlDetail(HoaDonChiTiet book, @MappingTarget ListGioHangDTO detailDTO){
        if (book.getBook().getHinhAnh().getHinhAnh() != null && !book.getBook().getHinhAnh().getHinhAnh().isEmpty()) {
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/")
                    .path(book.getBook().getHinhAnh().getHinhAnh())
                    .toUriString();
            detailDTO.setHinhAnh(imageUrl);
        }
    }
}
