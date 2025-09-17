import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProductBook } from './product-book.model';
import { ProductBookService } from './product-book.service';
import { Page } from './product-book.page';

@Component({
  selector: 'app-product-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-book.component.html',
  styleUrls: ['./product-book.component.scss']
})
export class ProductBookComponent implements OnInit {
  productBooks: ProductBook[] = [];
  productBook: ProductBook = { 
    tenSanPham: '', 
    ngayTao: new Date(),
    moTa: '',
    trangThai: true 
  };

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  isModalOpen = false;
  isEditMode = false;
  searchKeyword = '';

  constructor(
    private productBookService: ProductBookService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.productBookService.getAllPaging(page, this.pageSize).subscribe((res: Page<ProductBook>) => {
      this.productBooks = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.productBook = { 
      tenSanPham: '', 
      ngayTao: new Date(),
      moTa: '',
      trangThai: true 
    };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveProductBook(): void {
    if (this.productBook.id) {
      this.productBookService.update(this.productBook.id, this.productBook).subscribe((res: ProductBook) => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.productBookService.create(this.productBook).subscribe({
        next: (res: ProductBook) => {
          alert('Thêm sản phẩm sách thành công!');
          this.loadPage(this.currentPage);
          this.resetForm();
          this.closeModal();
        },
        error: (error: any) => {
          alert('Thêm thất bại!');
          console.error('Error creating product book:', error);
        }
      });
    }
  }

  editProductBook(pb: ProductBook): void {
    this.isEditMode = true;
    this.productBook = { ...pb };
    this.isModalOpen = true;
  }

  deleteProductBook(id: number): void {
    if (confirm('Bạn có chắc muốn xoá sản phẩm sách này không?')) {
      this.productBookService.delete(id).subscribe((res: any) => {
        this.loadPage(this.currentPage);
      });
    }
  }

  nextPage(): void {
    if (this.currentPage + 1 < this.totalPages) {
      this.loadPage(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.loadPage(this.currentPage - 1);
    }
  }

  resetForm(): void {
    this.productBook = { 
      tenSanPham: '', 
      ngayTao: new Date(),
      moTa: '',
      trangThai: true 
    };
    this.isEditMode = false;
  }

  filteredProductBooks(): ProductBook[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.productBooks.filter((pb: ProductBook) =>
      pb.tenSanPham.toLowerCase().includes(keyword) ||
      (pb.moTa && pb.moTa.toLowerCase().includes(keyword))
    );
  }
}
