import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CartItem {
  id: number;
  tenSanPham: string;
  donGia: number;
  soLuongMua: number;
  hinhAnh: string;
}

export interface ThemGioHangDTO {
  id: number;
  soLuong: number;
}

export interface ListGioHangDTO {
  idSanPham: number;
  tenSanPham: string;
  donGia: number;
  soLuongMua: number;
  tongTien: number;
  hinhAnh: string;
}

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api/gio-hang'; // đổi theo IP backend của bạn

  constructor(private http: HttpClient) {}

  addToCart(dto: ThemGioHangDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/add`, dto);
  }

  getCartByKhachHang(idKhachHang: number): Observable<ListGioHangDTO[]> {
    return this.http.get<ListGioHangDTO[]>(`${this.apiUrl}/khach-hang`);
  }
  removeItemFromCart(khachHangId: number, idSanPham: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/delete`, {
    params: {
      khachHangId: khachHangId,
      idSanPham: idSanPham
    }
  });
}

}
