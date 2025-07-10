package com.example.app.mapper;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.dto.sanPhamDTO.GetAllSanPhamDTO;
import com.example.app.entity.Book;
import com.example.app.entity.SanPham;
import com.example.app.entity.TacGia;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BookMapper {
//    @Mapping(source = "donGia", target = "donGia")
//    @Mapping(source = "id", target = "id")
@Mapping(source = "sanPham.tenSanPham", target = "tenSanPham")
@Mapping(target = "hinhAnh", ignore = true)
ListAllBookDTO listAllBooktoDTO(Book book);
@AfterMapping
    default void buildImgUrl(Book book, @MappingTarget ListAllBookDTO listAllBookDTO){
    if (book.getHinhAnh().getHinhAnh() != null && !book.getHinhAnh().getHinhAnh().isEmpty()) {
        String imageUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/uploads/")
                .path(book.getHinhAnh().getHinhAnh())
                .toUriString();
        listAllBookDTO.setHinhAnh(imageUrl);
    }
}
    @Mapping(source = "theLoai.tenTheLoai", target = "theLoai")
    @Mapping(source = "kichThuoc.chiSoKichThuoc", target = "kichThuoc")
    @Mapping(source = "nhaXuatBan.tenNhaXuatBan", target = "nhaXuatBan")
    @Mapping(source = "hinhAnh.hinhAnh", target = "hinhAnh")
    BookDetailDTO getBookByIDDTO(Book book);
    @AfterMapping
    default void buildImgUrlDetail(Book book, @MappingTarget BookDetailDTO detailDTO){
        if (book.getHinhAnh().getHinhAnh() != null && !book.getHinhAnh().getHinhAnh().isEmpty()) {
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/")
                    .path(book.getHinhAnh().getHinhAnh())
                    .toUriString();
            detailDTO.setHinhAnh(imageUrl);
        }
    }
    default List<String> map(Set<TacGia> tacGiaSet) {
        if (tacGiaSet == null) return null;
        return tacGiaSet.stream()
                .map(TacGia::getTenTacGia)
                .toList();
    }
}
//    GetAllSanPhamDTO getAllSanPhamtoDTO(SanPham sanPham);

