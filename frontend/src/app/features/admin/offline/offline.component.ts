import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-offline',
  imports: [FormsModule,CommonModule],
  templateUrl: './offline.component.html',
  styleUrl: './offline.component.scss'
})
export class OfflineComponent {
  cart: any[] = [];
hoaDonCho: any[] = [];
sanPhams: any[] = [
  { id: 1, tenSach: 'Sách A', gia: 100000, hinhAnh: 'assets/book1.jpg' },
  { id: 2, tenSach: 'Sách B', gia: 150000, hinhAnh: 'assets/book2.jpg' }
];

addToCart(sp: any) {
  const item = this.cart.find(c => c.id === sp.id);
  if (item) {
    item.soLuong++;
  } else {
    this.cart.push({ ...sp, soLuong: 1 });
  }
}

removeFromCart(i: number) {
  this.cart.splice(i, 1);
}

updateCart(item: any) {
  if (item.soLuong <= 0) {
    this.cart = this.cart.filter(c => c.id !== item.id);
  }
}

getCartTotal() {
  return this.cart.reduce((sum, i) => sum + i.soLuong * i.gia, 0);
}

chonHoaDon(hd: any) {
  // load hóa đơn vào giỏ
  this.cart = [...hd.chiTiet];
}

huyHoaDon(hd: any) {
  this.hoaDonCho = this.hoaDonCho.filter(h => h.id !== hd.id);
}


}
