import { Component, OnInit } from '@angular/core';
import { Book } from '../../model/book.model';
import { BookService } from '../../services/book.services';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';


@Component({
  standalone:true,
  selector: 'app-book-list',
  imports: [CommonModule,RouterModule],
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
  slugify(title: string): string {
  return title.toLowerCase().normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "") // remove accents
    .replace(/[^a-z0-9]+/g, '-')     // replace non-alphanum with -
    .replace(/(^-|-$)/g, '');        // remove leading/trailing dashes
}


}
