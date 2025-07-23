import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService, ListGioHangDTO } from '../cart/cart.service';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-order',
  imports: [FormsModule,CommonModule,ReactiveFormsModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent implements OnInit {
checkoutForm!: FormGroup; // <-- chỉ khai báo thôi, chưa khởi tạo

 cartItems: ListGioHangDTO[] = [];
  tongTien: number = 0;
 khID: number=0;
  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private cartService: CartService,
    // private authService: AuthService
  ) {}

  ngOnInit(): void {
     this.checkoutForm = this.fb.group({
    diaChi: ['', Validators.required],
    soDienThoai: ['', [Validators.required, Validators.pattern('^0[0-9]{9,10}$')]],
    ghiChu: [''],
    phuongThucThanhToan: ['COD', Validators.required]
  });
  this.cartService.getCartByKhachHang().subscribe({
      next: (data) => {
        this.cartItems = data;
        this.tinhTongTien();
      },
      error: (err) => {
        console.error('Lỗi khi lấy giỏ hàng:', err);
      }
    });
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

    const hoaDon = {
      khachHangID: this.khID, // hoặc token decode nếu dùng JWT
      diaChi: this.checkoutForm.value.diaChi,
      soDienThoai: this.checkoutForm.value.soDienThoai,
      ghiChu: this.checkoutForm.value.ghiChu,
      phuongThucThanhToan: this.checkoutForm.value.phuongThucThanhToan,
      cartItems: this.cartItems.map(item => ({
        sanPhamId: item.idSanPham,
        soLuong: item.soLuongMua
      }))
    };

    this.http.put('http://localhost:8080/api/hoa-don/thanh-toan', hoaDon).subscribe({
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
  }
}
