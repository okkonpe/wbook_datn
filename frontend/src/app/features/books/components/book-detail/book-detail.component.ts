import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BookService } from '../../services/book.services';
import { Book } from '../../model/book.model';

@Component({
  selector: 'app-book-detail',
  imports: [],
  templateUrl: './book-detail.component.html',
  styleUrl: './book-detail.component.scss'
})
export class BookDetailComponent implements OnInit {
book: any;
 constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    // private cartService: CartService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.bookService.getBookById(id).subscribe((data) => {
        this.book = data;
      });
    }
  }

  // themVaoGio(book: any): void {
  //   this.cartService.addToCart(book);
  //   alert('✅ Đã thêm vào giỏ hàng!');
  // }
}
