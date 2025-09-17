export interface User {
  id?: number;
  maKhachHang?: string;
  tenKhachHang: string;
  sdt?: string;
  ngaySinh?: string;
  diaChi?: string;
  email?: string;
  matKhau?: string;
  taiKhoan?: string;
  gioiTinh?: boolean;
  trangThai?: string;  // 👈 Đổi thành string
}
