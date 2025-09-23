import { Component, OnInit } from '@angular/core';
import { Book } from '../../model/book.model';
import { BookService } from '../../services/book.services';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Params, RouterModule } from '@angular/router';
import { TheLoaiService } from '../../../../shared/components/header/theloai.service';
import { FormsModule } from '@angular/forms';


@Component({
  standalone:true,
  selector: 'app-book-list',
  imports: [CommonModule,RouterModule,FormsModule],
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  books: Book[]=[];
  filterBook: Book[]=[];
  currentPage = 0;
  searchKey: string='';
totalPages = 0;
  products: any[] = [];
  categoryId!: number;
  pageSize = 10;
categoryName: string = 'Sách đề xuất cho bạn';

  constructor(private bookService: BookService,
    private route: ActivatedRoute,
    private theLoaiService: TheLoaiService
  ){}
 ngOnInit(): void {
  this.route.params.subscribe(params => {
    this.categoryId = params['id'];

    this.currentPage = 0;

    if (this.categoryId) {
      // Nếu có id thể loại -> load sách theo thể loại
   
this.route.queryParams.subscribe((query: Params) => {
      this.categoryName = 'Thể loại '+query['name'] || '';
      console.log('CategoryName:', this.categoryName);
    });     
     this.loadProducts();
    } else {
      // Nếu không có id -> load tất cả sách
      this.categoryName = 'Sách đề xuất cho bạn';

      this.loadBooks();
    }
  });
}

onSearchChange(){
      let filtered = [...this.books];
 if (this.searchKey.trim()) {
      const term = this.searchKey.toLowerCase();
      filtered = filtered.filter(book => 
        book.tenSanPham?.toLowerCase().includes(term) 
      );
    }
    this.filterBook=filtered;
}
loadBooks(page: number = 0) {
  this.bookService.getBooks(page).subscribe(res => {
    this.books = res.content;
    this.onSearchChange();
    this.totalPages = res.totalPages;
    this.currentPage = res.number;
  });
}
loadProducts() {
    this.bookService.getSachByTheLoai(this.categoryId, this.currentPage, this.pageSize)
      .subscribe(res => {
        this.books = res.content;
        this.onSearchChange();
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
