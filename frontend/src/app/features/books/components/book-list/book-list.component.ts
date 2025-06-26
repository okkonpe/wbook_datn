import { Component, OnInit } from '@angular/core';
import { Book } from '../../model/book.model';
import { BookService } from '../../services/book.services';
import { CommonModule } from '@angular/common';


@Component({
  standalone:true,
  selector: 'app-book-list',
  imports: [CommonModule],
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  books: Book[]=[];
  constructor(private bookService: BookService){}
  ngOnInit(): void {
    this.bookService.getBooks().subscribe(data=>{
         console.log('📦 Dữ liệu sách:', data); 
      this.books=data});
  }

}
