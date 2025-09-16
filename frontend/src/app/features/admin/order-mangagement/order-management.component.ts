import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { WebsocketService } from '../../../core/services/websocket.service';
import { ToastrService } from 'ngx-toastr';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface DecodedToken {
  sub: string;
  id: number;
  role: string;
  iat: number;
  exp: number;
}
@Component({
  selector: 'app-shipper',
  imports: [CommonModule, FormsModule],
  templateUrl: './order-management.component.html',
  styleUrl: './order-management.component.scss'
})
export class OrderManagementComponent {
  hoaDons: any[] = [];
    selectedHoaDon: any = null; // lưu hóa đơn được chọn
  totalItems = 0;
  totalPages = 0;
  chiTietDonHang: any[] = [];
  isModalOpen = false;
  currentPage = 0;
  pageSize = 10;
  status?: string | null
    loaiTT?: string | null
maHoaDon?: string|null
listNhanVien : any[] = [];
selectNhanVien :string=''
idNhanVien:number=0;

  private apiUrl = 'http://localhost:8080/api/hoa-don';
  constructor(private http: HttpClient,
    private websocketService: WebsocketService,
    private toastr: ToastrService

  ) { }




  ngOnInit(): void {
    this.locTimKiem();
    this.loadListNhanVien();
    this.idNhanVien=this.getUserIdFromToken();
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }
  getUserIdFromToken(): number {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode<DecodedToken>(token);
        return Number(decoded.id);
      } catch (e) {
        console.error('Decode token lỗi:', e);
      }
    }
    return 0; // fallback nếu lỗi
  }
  

  filter: any = {
    status: null,
    loaiTT: null,
    maHoaDon:null
  };

 locTimKiem(resetPage: boolean = false) {
  // gán shipper từ combobox
  this.filter.status=this.status;
    this.filter.loaiTT=this.loaiTT;
    this.filter.maHoaDon=this.maHoaDon;

  // có filter thì gọi API lọc
  if ( this.filter.status || this.filter.maHoaDon ||this.filter.loaiTT) {
  let params: any = {
    page: this.currentPage,
    size: this.pageSize
  };
    if (this.filter.status) params.status = this.filter.status;
    if (this.filter.maHoaDon) params.maHoaDon = this.filter.maHoaDon;
    if (this.filter.loaiTT) params.loaiTT = this.filter.loaiTT;

    this.http.get<any>(`${this.apiUrl}/loc-tim-kiem`, { params }).subscribe(res => {
      this.hoaDons = res.content;
      this.totalItems = res.totalElements;
      this.totalPages = res.totalPages;
    });
  } else {
    // không có filter thì gọi getAll
    this.http.get<any>(
      `${this.apiUrl}/order?page=${this.currentPage}&size=${this.pageSize}`
    ).subscribe(res => {
      this.hoaDons = res.content;
      this.totalItems = res.totalElements;
      this.totalPages = res.totalPages;
    });
  }
}

  getChiTietDonHang(idHoaDon: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/chi-tiet/${idHoaDon}`);
  }
  closeModal() {
    this.isModalOpen = false;
  }
  xemChiTietDonHang(id: number,hoaDon:any): void {
     this.selectedHoaDon = hoaDon;
    this.getChiTietDonHang(id).subscribe(data => {
      console.log(id)
      this.chiTietDonHang = data;
      this.isModalOpen = true;
      console.log("Chi tiết đơn hàng:", data);
      // có thể mở modal hiển thị ở đây
    });
  }
  loadListNhanVien(){
     this.http
        .get<any[]>('http://localhost:8080/api/nhan-vien/list-all')
        .subscribe(res => {
          this.listNhanVien = res;
        });
  }


  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      console.log(this.currentPage)
      this.locTimKiem();
    }
  }
 getPages(): number[] {
  return this.totalPages > 0 ? Array(this.totalPages).fill(0).map((x, i) => i) : [];
}
 applyFilter() {
  this.currentPage = 0;   // reset về trang đầu tiên
  this.locTimKiem();
}

  // Trong don-hang.component.ts
daXacNhan(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đã xác nhận"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/da-xac-nhan/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url,{}, { params: {idNhanVien:this.idNhanVien} }).subscribe({
        next: () => {
                    alert('Chuyển trạng thái thành công!');
          console.log('Cập nhật trạng thái thành công!');
          this.locTimKiem();
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

dangGiaoHang(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đang giao hàng"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/dang-giao-hang/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}, { params: {idNhanVien:this.idNhanVien} }).subscribe({
       next: () => {
                    alert('Chuyển trạng thái thành công!');
          console.log('Cập nhật trạng thái thành công!');
          this.locTimKiem();
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
daGiaoHang(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đã giao hàng"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/da-giao-hang/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}, { params: {idNhanVien:this.idNhanVien} }).subscribe({
        next: () => {
                    alert('Chuyển trạng thái thành công!');
          console.log('Cập nhật trạng thái thành công!');
          this.locTimKiem();
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
giaoHangThatBai(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đã huỷ"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/giao-hang-that-bai/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}, { params: {idNhanVien:this.idNhanVien} }).subscribe({
        next: () => {
                    alert('Chuyển trạng thái thành công!');
          console.log('Cập nhật trạng thái thành công!');
          this.locTimKiem();
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
  daHuy(hoaDon: any): void {
    // 1. Sửa lại nội dung confirm cho khớp với hành động
    const message = `Bạn có chắc muốn chuyển trạng thái đơn hàng #${hoaDon.ma} sang "Đã huỷ"?`;

    if (confirm(message)) {
      const url = `${this.apiUrl}/cap-nhat-trang-thai/nhan-vien-huy/${hoaDon.id}`;

      // 2. Thêm body rỗng {} và 3. Xử lý lỗi
      this.http.put(url, {}, { params: {idNhanVien:this.idNhanVien} }).subscribe({
       next: () => {
                    alert('Chuyển trạng thái thành công!');
          console.log('Cập nhật trạng thái thành công!');
          this.locTimKiem();
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
