import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BookService } from '../../services/book.services';
import { Book } from '../../model/book.model';
import { CartItem, CartService } from '../../../cart/cart.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-book-detail',
  imports: [FormsModule],
  templateUrl: './book-detail.component.html',
  styleUrl: './book-detail.component.scss'
})
export class BookDetailComponent implements OnInit {
book: any;
 soLuong: number = 1;
 constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
   private cartService: CartService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.bookService.getBookById(id).subscribe((data) => {
        this.book = data;
      });
    }
  }

  addToCart() {
    const item: CartItem = {
      id: this.book.id,
      tenSanPham: this.book.tenSanPham,
      donGia: this.book.donGia,
      soLuongMua: this.soLuong,
      hinhAnh: this.book.hinhAnh, // nếu có
    };
   this.cartService.addToCart({ id: this.book.id, soLuong: this.soLuong }).subscribe(() => {
  alert('Đã thêm vào giỏ hàng!');
});

  }
}
