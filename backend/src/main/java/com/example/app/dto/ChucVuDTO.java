package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChucVuDTO {
    private Integer id;
    
    @NotBlank(message = "Mã chức vụ không được để trống")
    @Size(max = 15, message = "Mã chức vụ không được vượt quá 15 ký tự")
    private String maChucVu;
    
    @NotBlank(message = "Tên chức vụ không được để trống")
    @Size(max = 50, message = "Tên chức vụ không được vượt quá 50 ký tự")
    private String tenChucVu;
} 