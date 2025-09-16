package com.example.app.mapper;

import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.entity.HinhAnh;
import com.example.app.entity.SanPham;
import com.example.app.entity.TacGia;
import org.mapstruct.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    // ========== DETAIL ==========
    @Mapping(source = "theLoai.tenTheLoai", target = "theLoai")
    @Mapping(source = "sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "sanPham.id", target = "idSanPham")
    @Mapping(source = "loaiGiay.tenGiay", target = "loaiGiay")
    @Mapping(source = "loaiBia.tenBia", target = "loaiBia")
    @Mapping(source = "tacGia", target = "tacGia")
    @Mapping(source = "kichThuoc.chiSoKichThuoc", target = "kichThuoc")
    @Mapping(source = "nhaXuatBan.tenNhaXuatBan", target = "nhaXuatBan")
    @Mapping(target = "hinhAnh", ignore = true)
    BookDetailDTO getBookByIDDTO(Book book);

    @AfterMapping
    default void buildImgUrlDetail(Book book, @MappingTarget BookDetailDTO detailDTO) {
        if (book.getHinhAnh() != null && book.getHinhAnh().getHinhAnh() != null) {
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
                .collect(Collectors.toList());
    }

    // ========== DTO TO ENTITY ==========
    @InheritInverseConfiguration(name = "getBookByIDDTO")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sanPham", source = "idSanPham", qualifiedByName = "mapSanPham")
    @Mapping(target = "tacGia", ignore = true)
    @Mapping(target = "hinhAnh", ignore = true)
    Book bookDetailDtoToEntity(BookDetailDTO dto);

    @Named("mapSanPham")
    default SanPham mapSanPham(Integer idSanPham) {
        if (idSanPham == null) return null;
        SanPham sanPham = new SanPham();
        sanPham.setId(idSanPham);
        return sanPham;
    }

    @AfterMapping
    default void mapHinhAnhToEntity(BookDetailDTO dto, @MappingTarget Book book) {
        if (dto.getHinhAnh() != null && !dto.getHinhAnh().isEmpty()) {
            HinhAnh hinhAnh = new HinhAnh();
            hinhAnh.setHinhAnh(dto.getHinhAnh());
            hinhAnh.setMaHinhAnh(UUID.randomUUID().toString().substring(0, 15));
            book.setHinhAnh(hinhAnh);
        }
    }
}