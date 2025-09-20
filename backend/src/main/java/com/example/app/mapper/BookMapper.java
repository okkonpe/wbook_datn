package com.example.app.mapper;

import com.example.app.dto.TaiBanDTO;
import com.example.app.dto.bookDTO.BookDetailDTO;
import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.entity.Book;
import com.example.app.entity.ChuDe;
import com.example.app.entity.TacGia;
import com.example.app.entity.TaiBan;
import org.mapstruct.*;
import org.mapstruct.Named;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "sanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "soLuong", target = "soLuong")
    @Mapping(source = "maSanPhamChiTiet", target = "maSanPhamChiTiet")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "theLoai.tenTheLoai", target = "theLoai")
    @Mapping(source = "nhaXuatBan.tenNhaXuatBan", target = "nhaXuatBan")
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
    @Mapping(source = "tacGia", target = "tacGia")
    @Mapping(source = "tacGia", target = "tacGiaIds", qualifiedByName = "mapTacGiaIds")
    @Mapping(source = "chuDes", target = "chuDe", qualifiedByName = "mapChuDe")
    @Mapping(source = "chuDes", target = "chuDeIds", qualifiedByName = "mapChuDeIds")
    @Mapping(source = "kichThuoc.chiSoKichThuoc", target = "kichThuoc")
    @Mapping(source = "nhaXuatBan.tenNhaXuatBan", target = "nhaXuatBan")
    @Mapping(source = "taiBans", target = "taiBans")
    @Mapping(source = "soLuong", target = "soLuong")
    @Mapping(target = "hinhAnh", ignore = true)
    BookDetailDTO getBookByIDDTO(Book book);
    @AfterMapping
    default void buildImgUrlDetail(Book book, @MappingTarget BookDetailDTO detailDTO){
        if (book.getHinhAnh() != null && book.getHinhAnh().getHinhAnh() != null && !book.getHinhAnh().getHinhAnh().isEmpty()) {
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

    @Named("mapTacGiaIds")
    default List<Integer> mapTacGiaIds(Set<TacGia> tacGiaSet) {
        if (tacGiaSet == null) return null;
        return tacGiaSet.stream()
                .map(TacGia::getId).collect(Collectors.toList());
    }

    @Named("mapChuDe")
    default List<String> mapChuDe(Set<ChuDe> chuDeSet) {
        if (chuDeSet == null) return null;
        return chuDeSet.stream()
                .map(ChuDe::getTenChuDe).collect(Collectors.toList());
    }

    @Named("mapChuDeIds")
    default List<Integer> mapChuDeIds(Set<ChuDe> chuDeSet) {
        if (chuDeSet == null) return null;
        return chuDeSet.stream()
                .map(ChuDe::getId).collect(Collectors.toList());
    }

    default Set<TaiBanDTO> mapTaiBans(Set<TaiBan> taiBanSet) {
        if (taiBanSet == null) return null;
        return taiBanSet.stream()
                .map(taiBan -> {
                    TaiBanDTO dto = new TaiBanDTO();
                    dto.setId(taiBan.getId());
                    dto.setLanTaiBan(taiBan.getLanTaiBan());
                    dto.setNamTaiBan(taiBan.getNamTaiBan());
                    return dto;
                })
                .collect(Collectors.toSet());
    }


    @InheritInverseConfiguration(name = "getBookByIDDTO")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sanPham", ignore = true)
    @Mapping(target = "tacGia", ignore = true)
    @Mapping(target = "hinhAnh", ignore = true)
    @Mapping(target = "taiBans", ignore = true)
    @Mapping(target = "chiTietHoaDons", ignore = true)
    @Mapping(target = "chuDes", ignore = true)
    Book bookDetailDtoToEntity(BookDetailDTO dto);
}
