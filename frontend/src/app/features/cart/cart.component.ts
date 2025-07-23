import { Component, OnInit } from '@angular/core';
import { CartService, ListGioHangDTO, ThemGioHangDTO } from './cart.service';
import { CommonModule } from '@angular/common';
import { jwtDecode } from 'jwt-decode';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cartItems: ListGioHangDTO[] = [];
  khachHangId: number | null = null;

  constructor(
    private cartService: CartService,
    private router: Router

  ) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = jwtDecode(token);
      this.khachHangId = decoded.id;
      this.loadCartFromBackend();
          console.log('Decoded khachHangId:', this.khachHangId);
          console.log(localStorage.getItem('token'));


    }else{
      this.router.navigate(['/login'])
    }
  }

  loadCartFromBackend(): void {
    
      this.cartService.getCartByKhachHang().subscribe(data => {
        this.cartItems = data;
      })
    }
  

  getTotal(): number {
    return this.cartItems.reduce((total, item) => total + item.tongTien, 0);
  }

  increase(item: ListGioHangDTO): void {
      console.log('Gọi increase()', item); // ✅ log này cần xuất hiện

    if (!this.khachHangId) return;
    const dto: ThemGioHangDTO = {
      id: item.idSanPham,
      khachHangId: this.khachHangId,
      soLuong: 1
    };
    this.cartService.addToCart(dto).subscribe(() => this.loadCartFromBackend());
  }

  decrease(item: ListGioHangDTO): void {
    if (!this.khachHangId || item.soLuongMua <= 1) return;
    const dto: ThemGioHangDTO = {
      id: item.idSanPham,
      khachHangId: this.khachHangId,
      soLuong: -1
    };
    this.cartService.addToCart(dto).subscribe(() => this.loadCartFromBackend());
  }

  removeItem(item: ListGioHangDTO): void {
    if (!this.khachHangId) return;
    this.cartService.removeItemFromCart(this.khachHangId, item.idSanPham).subscribe(() => {
      this.loadCartFromBackend();
    });
  }
}
