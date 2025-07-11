import { Component, OnInit } from '@angular/core';
import { CartService, ListGioHangDTO, ThemGioHangDTO } from './cart.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  cartItems: ListGioHangDTO[] = [];

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.loadCartFromBackend();
  }

  loadCartFromBackend(): void {
    const khachHangId = 11; // tạm thời, sau này lấy từ token
    this.cartService.getCartByKhachHang(khachHangId).subscribe(data => {
      this.cartItems = data;
    });
  }

  getTotal(): number {
    return this.cartItems.reduce((total, item) => total + item.tongTien, 0);
  }

  increase(item: ListGioHangDTO): void {
    const dto: ThemGioHangDTO = {
      id: item.idSanPham,
      soLuong: 1
    };
    console.log('INCREASE item:', item);

    this.cartService.addToCart(dto).subscribe(() => this.loadCartFromBackend());
  }

  decrease(item: ListGioHangDTO): void {
    if (item.soLuongMua > 1) {
      const dto: ThemGioHangDTO = {
        id: item.idSanPham,
        soLuong: -1 // bạn cần xử lý `-1` ở backend để giảm số lượng
      };
      this.cartService.addToCart(dto).subscribe(() => this.loadCartFromBackend());
    }
  }

  removeItem(item: ListGioHangDTO): void {
  const khachHangId = 11; // hoặc lấy từ token
  this.cartService.removeItemFromCart(khachHangId, item.idSanPham).subscribe(() => {
    this.loadCartFromBackend(); // cập nhật lại danh sách
  });
}

}
