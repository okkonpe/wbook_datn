import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Category } from './category.model';
import { CategoryService } from './category.service';

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {
  categories: Category[] = [];
  category: Category = { tenTheLoai: '', trangThai: true };

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  isModalOpen = false;
  isEditMode = false;
  searchKeyword = '';

  constructor(
    private categoryService: CategoryService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.categoryService.getAllPaging(page, this.pageSize).subscribe(res => {
      this.categories = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.category = { tenTheLoai: '', trangThai: true };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveCategory(): void {
    if (this.category.id) {
      this.categoryService.update(this.category.id, this.category).subscribe(() => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.categoryService.create(this.category).subscribe({
        next: res => {
          alert('Thêm thể loại thành công!');
          this.loadPage(this.currentPage);
          this.resetForm();
          this.closeModal();
        },
        error: () => {
          alert('Thêm thất bại!');
        }
      });
    }
  }

  editCategory(c: Category): void {
    this.isEditMode = true;
    this.category = { ...c };
    this.isModalOpen = true;
  }

  deleteCategory(id: number): void {
    if (confirm('Bạn có chắc muốn xoá thể loại này không?')) {
      this.categoryService.delete(id).subscribe(() => this.loadPage(this.currentPage));
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
    this.category = { tenTheLoai: '', trangThai: true };
    this.isEditMode = false;
  }

  filteredCategories(): Category[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.categories.filter(c =>
      c.tenTheLoai.toLowerCase().includes(keyword)
    );
  }
}
