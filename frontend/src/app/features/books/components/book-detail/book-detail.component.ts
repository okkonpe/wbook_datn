import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService } from '../../services/book.services';
import { CartService, ThemGioHangDTO } from '../../../cart/cart.service';
import { FormsModule } from '@angular/forms';
import { jwtDecode } from 'jwt-decode';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './book-detail.component.html',
  styleUrl: './book-detail.component.scss'
})
export class BookDetailComponent implements OnInit {
  book: any;
  soLuong: number = 1;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.bookService.getBookById(id).subscribe((data) => {
        this.book = data;
      });
    }
  }
blockNegative(event: KeyboardEvent) {
  if (event.key === '-' || event.key === '+' || event.key === 'e') {
    event.preventDefault();
  }
}

  getKhachHangIdFromToken(): number | null {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = jwtDecode(token);
      return decoded.id;
    }
    return null;
  }

  addToCart() {
    const khachHangId = this.getKhachHangIdFromToken();
    if (!khachHangId) {
      alert('Bạn cần đăng nhập trước');
      return;
    }

    const dto: ThemGioHangDTO = {
      khachHangId,
      id: this.book.id,
      soLuong: this.soLuong
    };

    this.cartService.addToCart(dto).subscribe({
next:() => {
      alert('✅ Đã thêm vào giỏ hàng!');
      this.router.navigate(['/cart'])
    },
    error:(err)=>{
    const errorMsg = err.error?.message || '❌ Có lỗi xảy ra!';
    alert(errorMsg);
    }
    }
      );
  }
}
