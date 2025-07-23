package com.example.app.dto.banHangDTO;

import com.example.app.entity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThemGioHangDTO {
    //KhachHang, NhanVien
    private Integer khachHangId;
    private Integer id;       // Sản phẩm muốn thêm vào giỏ
    private Integer soLuong;         // Số lượng mua

    // Optionally: nếu frontend gửi mã hóa đơn (giỏ hàng đã tồn tại)
    private Integer hoaDonId;        // Có thể null nếu chưa có giỏ
}
