import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-list-order',
  imports: [CommonModule],
  templateUrl: './list-order.component.html',
  styleUrl: './list-order.component.scss'
})
export class ListOrderComponent {
  hoaDons: any[]=[];
   totalItems = 0;
  totalPages = 0;
  chiTietDonHang: any[] = [];
  isModalOpen = false;
  currentPage = 0;
  pageSize = 10;
    private apiUrl = 'http://localhost:8080/api/hoa-don';
     constructor(private http: HttpClient) {}

 
   

ngOnInit(): void {
  this.loadDanhSachDonHang();
}
getChiTietDonHang(idHoaDon: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/chi-tiet/${idHoaDon}`);
  }
 closeModal() {
    this.isModalOpen = false;
  }
xemChiTietDonHang(id: number): void {
  this.getChiTietDonHang(id).subscribe(data => {
    console.log(id)
    this.chiTietDonHang = data;
     this.isModalOpen = true;
    console.log("Chi tiết đơn hàng:", data);
    // có thể mở modal hiển thị ở đây
  });
}

loadDanhSachDonHang(): void {
    this.http
      .get<any>(`${this.apiUrl}/don-hang?page=${this.currentPage}&size=${this.pageSize}`)
      .subscribe(res => {
        this.hoaDons = res.content;
        this.totalItems = res.totalElements;
        this.totalPages = res.totalPages;
      });
  }
  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadDanhSachDonHang();
    }
  }
  getPages(): number[] {
    return Array(this.totalPages).fill(0);
  }
    
// Trong don-hang.component.ts

dangGiaoHang(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đang giao hàng"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/dang-giao-hang/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}).subscribe({
        next: () => {
          console.log('Cập nhật trạng thái thành công!');
          this.loadDanhSachDonHang();
        },
        error: (err) => {
          console.error('Có lỗi xảy ra khi cập nhật trạng thái:', err);
          alert('Thao tác thất bại, vui lòng thử lại!');
        }
      });
    }
}
daGiaoHang(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đã giao hàng"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/da-giao-hang/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}).subscribe({
        next: () => {
          console.log('Cập nhật trạng thái thành công!');
          this.loadDanhSachDonHang();
        },
        error: (err) => {
          console.error('Có lỗi xảy ra khi cập nhật trạng thái:', err);
          alert('Thao tác thất bại, vui lòng thử lại!');
        }
      });
    }
}
daHuy(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đã huỷ"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/da-huy/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}).subscribe({
        next: () => {
          console.log('Cập nhật trạng thái thành công!');
          this.loadDanhSachDonHang();
        },
        error:err => {
          console.error('Có lỗi xảy ra khi cập nhật trạng thái:', err);
          alert(err.error.message);
          
          
        }
      });
    }
}



}
