package com.example.app.mapper;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.entity.SanPham;
import com.example.app.entity.TacGia;
import org.mapstruct.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface BookMapper {

    // ========== LIST ALL ==========
    @Mapping(source = "sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(target = "hinhAnh", ignore = true)
    ListAllBookDTO listAllBookToDTO(Book book);

    @AfterMapping
    default void mapHinhAnh(Book book, @MappingTarget ListAllBookDTO dto) {
        if (book.getHinhAnh() != null && book.getHinhAnh().getHinhAnh() != null) {
            String imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/")
                    .path(book.getHinhAnh().getHinhAnh())
                    .toUriString();
            dto.setHinhAnh(imageUrl);
        }
    }

    @Mapping(source = "theLoai.tenTheLoai", target = "theLoai")
    @Mapping(source = "sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "loaiGiay.tenGiay", target = "loaiGiay")
    @Mapping(source = "loaiBia.tenBia", target = "loaiBia")
    @Mapping(target = "hinhAnh", expression = "java(buildImgUrlDetail(book.getHinhAnh()))")
    @Mapping(source = "tacGia", target = "tacGia")
    @Mapping(source = "kichThuoc.chiSoKichThuoc", target = "kichThuoc")
    @Mapping(source = "nhaXuatBan.tenNhaXuatBan", target = "nhaXuatBan")
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
                .map(TacGia::getTenTacGia).collect(Collectors.toList());
    }


    // ========== DTO TO ENTITY ==========
    @InheritInverseConfiguration(name = "getBookByIDDTO")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sanPham", ignore = true)
    @Mapping(target = "tacGia", ignore = true) // ánh xạ ngược cần xử lý riêng nếu cần
    @Mapping(target = "hinhAnh", ignore = true)
    Book bookDetailDtoToEntity(BookDetailDTO dto);
}
