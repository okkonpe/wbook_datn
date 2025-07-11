package com.example.app.dto.nhanVienDTO;

import com.example.app.entity.ChucVu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienDTO {
    private Integer id;
    
    private String maNv;

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(max = 30, message = "Tên nhân viên không được vượt quá 30 ký tự")
    private String tenNv;

    private BigDecimal luong;

    @Pattern(regexp = "(^$|[0-9]{10,13})", message = "Số điện thoại không hợp lệ")
    private String sdt;

    private Date ngaySinh;

    @Size(max = 50, message = "Địa chỉ không được vượt quá 50 ký tự")
    private String diaChi;

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email không hợp lệ")
    @Size(max = 50, message = "Email không được vượt quá 50 ký tự")
    private String email;

    private Boolean gioiTinh;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "[0-9]{12,13}", message = "CCCD không hợp lệ")
    private String cccd;

    @Size(max = 20, message = "Mật khẩu không được vượt quá 20 ký tự")
    private String matKhau;

    @Size(max = 20, message = "Tài khoản không được vượt quá 20 ký tự")
    private String taiKhoan;

    private Date ngayBatDau;

    @NotNull(message = "Chức vụ không được để trống")
    private Integer chucVuId;

    private Boolean trangThai;
} 