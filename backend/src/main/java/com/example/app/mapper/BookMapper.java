package com.example.app.mapper;

import com.example.app.dto.bookDTO.ListAllBookDTO;
import com.example.app.dto.sanPhamDTO.GetAllSanPhamDTO;
import com.example.app.entity.Book;
import com.example.app.entity.SanPham;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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


}
//    GetAllSanPhamDTO getAllSanPhamtoDTO(SanPham sanPham);

