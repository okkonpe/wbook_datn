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


    @Size(max = 20, message = "Tài khoản không được vượt quá 20 ký tự")
    private String taiKhoan;

    private Date ngayBatDau;
    private String chucVu;


    private Boolean trangThai;

    public NhanVienDTO(Integer id, String maNv, @NotBlank(message = "Tên nhân viên không được để trống") @Size(max = 30, message = "Tên nhân viên không được vượt quá 30 ký tự") String tenNv, BigDecimal luong, @Pattern(regexp = "(^$|[0-9]{10,13})", message = "Số điện thoại không hợp lệ") String sdt, Date ngaySinh, @Size(max = 50, message = "Địa chỉ không được vượt quá 50 ký tự") String diaChi, @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email không hợp lệ") @Size(max = 50, message = "Email không được vượt quá 50 ký tự") String email, Boolean gioiTinh, @NotBlank(message = "CCCD không được để trống") @Pattern(regexp = "[0-9]{12,13}", message = "CCCD không hợp lệ") String cccd, @Size(max = 20, message = "Tài khoản không được vượt quá 20 ký tự") String taiKhoan, Date ngayBatDau, String chucVu, Boolean trangThai) {
        this.id = id;
        this.maNv = maNv;
        this.tenNv = tenNv;
        this.luong = luong;
        this.sdt = sdt;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.taiKhoan = taiKhoan;
        this.ngayBatDau = ngayBatDau;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }
}