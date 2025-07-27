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
  currentPage = 0;
totalPages = 0;
  constructor(private bookService: BookService){}
  ngOnInit() {
  this.loadBooks();
}

loadBooks(page: number = 0) {
  this.bookService.getBooks(page).subscribe(res => {
    this.books = res.content;
    this.totalPages = res.totalPages;
    this.currentPage = res.number;
  });
}

changePage(page: number) {
  if (page >= 0 && page < this.totalPages) {
    this.loadBooks(page);
  }
}

  
  slugify(title: string): string {
  return title.toLowerCase().normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "") // remove accents
    .replace(/[^a-z0-9]+/g, '-')     // replace non-alphanum with -
    .replace(/(^-|-$)/g, '');        // remove leading/trailing dashes
}


}
