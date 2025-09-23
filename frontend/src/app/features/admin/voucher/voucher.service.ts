import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VoucherService {

  private apiUrl = 'http://localhost:8080/api/admin/voucher';

  constructor(private http: HttpClient) { }

  // Lấy danh sách voucher
  getVouchers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Lấy chi tiết voucher theo ID
  getVoucherById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Tạo voucher mới
  createVoucher(voucher: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, voucher);
  }

  // Cập nhật voucher
  updateVoucher(id: number, voucher: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, voucher);
  }

  // Xóa voucher
  deleteVoucher(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
  getValidVouchers(tongTien: number): Observable<any[]> {
    const params = new HttpParams()
      .set('tongTien', tongTien.toString())
    return this.http.get<any[]>(`${this.apiUrl}/apply`, { params });
  }
}
