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
  khachHangId: number;
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
  private apiUrl = 'http://localhost:8080/api/gio-hang';

  constructor(private http: HttpClient) {}

  addToCart(dto: ThemGioHangDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/add`, dto);
  }

  getCartByKhachHang(): Observable<ListGioHangDTO[]> {
    return this.http.get<ListGioHangDTO[]>(`${this.apiUrl}/khach-hang`);
  }

  removeItemFromCart(khachHangId: number, idSanPham: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete`, {
      params: {
        khachHangId,
        idSanPham
      }
    });
  }
}
