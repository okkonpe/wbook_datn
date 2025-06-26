package com.example.app.dto.bookDTO;

import com.example.app.dto.sanPhamDTO.GetAllSanPhamDTO;
import com.example.app.entity.*;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListAllBookDTO {
    private Integer id;
    private String tenSanPham;
    private String hinhAnh;
    private BigDecimal donGia;


}
