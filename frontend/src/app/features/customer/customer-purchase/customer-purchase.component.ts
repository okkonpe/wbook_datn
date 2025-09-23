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

   private apiUrl = 'http://localhost:8080/api/hoa-don';

   

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
huyDon(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn huỷ đơn?`;
console.log(hoaDon.id)

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/khach-hang-huy/${hoaDon.id}`;
      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}).subscribe({
        next: () => {
          this.layDonHangKhachHang().subscribe(data => {
      this.donHangs = data;
    });
                    alert(`Đã huỷ đơn ${hoaDon.maHoaDon}!`);
        },
        error: (err) => {
          if (err.error.message) {
      alert(err.error.message); // sẽ hiển thị "Đơn hàng đã giao!"
    } else {
      alert('Có lỗi xảy ra!');
    }
        }
      });
    }
}

}
