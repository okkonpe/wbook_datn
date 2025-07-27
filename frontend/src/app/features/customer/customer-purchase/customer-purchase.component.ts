import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-customer-purchase',
  imports: [CommonModule],
  templateUrl: './customer-purchase.component.html',
  styleUrl: './customer-purchase.component.scss'
})
export class CustomerPurchaseComponent {
 donHangs:any = [];
  idDonHangDangXem: number | null = null;
  constructor(private http: HttpClient) {}

 
   

ngOnInit() {
    this.layDonHangKhachHang().subscribe(data => {
      this.donHangs = data;
    });
  }
  layDonHangKhachHang(): Observable<any> {
  return this.http.get<any>('http://localhost:8080/api/khach-hang/acitivity');
}
  moChiTiet(id: number) {
    // Nếu đang mở => đóng lại
    if (this.idDonHangDangXem === id) {
      this.idDonHangDangXem = null;
    } else {
      this.idDonHangDangXem = id;
    }
  }


}
