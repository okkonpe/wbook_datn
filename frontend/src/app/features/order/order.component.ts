import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService, ListGioHangDTO } from '../cart/cart.service';
import { jwtDecode } from 'jwt-decode';
import { map } from 'rxjs/operators'; 
  import { OrderService } from './order.service';
import { Observable } from 'rxjs';
import { VoucherService } from '../admin/voucher/voucher.service';

export interface KhachHang {
  tenKhachHang: string;
  sdt: string;
  ngaySinh: string; // Nếu backend trả kiểu ISO string, dùng string
  diaChi: string;
  email: string;
}
@Component({
  standalone:true,
  selector: 'app-order',
  imports: [FormsModule,CommonModule,ReactiveFormsModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})

export class OrderComponent implements OnInit {
checkoutForm!: FormGroup; // <-- chỉ khai báo thôi, chưa khởi tạo
  // paymentMethod: string = 'COD';
 cartItems: ListGioHangDTO[] = [];
  tongTien: number = 0; 
    phiShip: number = 30000;
     isNewCustomer: boolean = true;
      validVouchers: any[] = [];
  selectedVoucher: any = null;
  giamGia: number = 0;
 khID: number=0;
   khachHang!: KhachHang;
       private apiUrl = 'http://localhost:8080/api/payment';
hoaDon: any;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private cartService: CartService,
    private orderService: OrderService,
    private voucherService: VoucherService
  ) {}

  ngOnInit(): void {
     this.checkoutForm = this.fb.group({
      hoTen: [''],
       paymentMethod: ['COD'],
    diaChi: ['', Validators.required],
    soDienThoai: ['', [Validators.required, Validators.pattern('^0[0-9]{9,10}$')]],
    ghiChu: [''],
    // phuongThucThanhToan: ['COD', Validators.required]
  });
  this.cartService.getCartByKhachHang().subscribe({
      next: (data) => {
        this.cartItems = data;
        this.tinhTongTien();
         this.loadValidVouchers(); 
      },
      error: (err) => {
        console.error('Lỗi khi lấy giỏ hàng:', err);
      }
    });
          this.orderService.fetchKhachHang().subscribe({
    next: (data) => {
      this.khachHang = data;
      console.log('Thông tin KH:', this.khachHang);

      // Đổ dữ liệu vào form
      this.checkoutForm.patchValue({
        hoTen: this.khachHang.tenKhachHang,
        diaChi: this.khachHang.diaChi,
        soDienThoai: this.khachHang.sdt
      });
    },
    error: (err) => {
      console.error('Lỗi khi lấy thông tin KH:', err);
    }
  });
  }
 loadValidVouchers(): void {
    this.voucherService.getValidVouchers(this.tongTien, this.isNewCustomer)
      .subscribe({
        next: res => this.validVouchers = res,
        error: err => console.error(err)
      });
  }
   applyVoucher(): void {
    if (!this.selectedVoucher) {
this.giamGia=0;
return;
    }

    // Tính số tiền giảm
    if (this.selectedVoucher.loaiGiam === 'PERCENT') {
      this.giamGia = this.tongTien * this.selectedVoucher.giaTri / 100;
      if (this.selectedVoucher.giamToiDa && this.giamGia > this.selectedVoucher.giamToiDa) {
        this.giamGia = this.selectedVoucher.giamToiDa;
      }
    } else {
      this.giamGia = this.selectedVoucher.giaTri;
    }
  }

  getTongTienSauGiam(): number {
    return this.tongTien+this.phiShip - this.giamGia;
  }

   tinhTongTien() {
    this.tongTien = this.cartItems.reduce((total, item) => total + item.tongTien, 0);
  }

  onSubmit() {
    if (this.checkoutForm.invalid){
 alert('Không để trống !');
      return;
    }
     const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.khID=decoded.id;
          console.log('Decoded khachHangId:', this.khID);
          console.log(localStorage.getItem('token'));}

    this.hoaDon = {
      maVoucher:this.selectedVoucher.maVoucher,
      tongTien:this.tongTien,
      tongTienSauGiam:this.getTongTienSauGiam(),
      khachHangID: this.khID, // hoặc token decode nếu dùng JWT
      diaChi: this.checkoutForm.value.diaChi,
      soDienThoai: this.checkoutForm.value.soDienThoai,
      ghiChu: this.checkoutForm.value.ghiChu,
      phuongThucThanhToan: this.checkoutForm.value.paymentMethod,
      cartItems: this.cartItems.map(item => ({
        sanPhamId: item.idSanPham,
        soLuong: item.soLuongMua
      }))
    };
      const method = this.checkoutForm.value.paymentMethod; // Lấy từ form

if (method === 'COD') {
      // Gọi API backend tạo đơn hàng COD
       this.http.put('http://localhost:8080/api/hoa-don/thanh-toan-cod', this.hoaDon).subscribe({
      next: () => {
        alert('🛍️ Đặt hàng thành công!');
        // this.cartService.clearCart();
        this.router.navigate(['/']);
      },
      error: err => {
        console.error(err);
        alert('❌ Đã xảy ra lỗi khi đặt hàng.');
      }
    });
    } else if (method === 'VNPAY') {
      // Gọi API backend để lấy link VNPay
      this.getVNPayUrl(this.getTongTienSauGiam(), 'Thanh toan',this.hoaDon.khachHangID).subscribe(url => {
                console.log(url)

        window.location.href = url; // chuyển hướng sang VNPAY
      });
    }
   
  }

    getVNPayUrl(amount: number, orderInfo: string,khid: number): Observable<string> {
    return this.http.post<{ url: string }>(
      `${this.apiUrl}/create`,this.hoaDon,
      { params: { amount, orderInfo,khid} }
    ).pipe(map(res => res.url));
  }
}
