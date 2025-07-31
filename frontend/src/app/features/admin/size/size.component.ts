import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Size } from './size.model';
import { SizeService } from './size.service';
import { Page } from './size.page';

@Component({
  selector: 'app-size',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './size.component.html',
  styleUrls: ['./size.component.scss']
})
export class SizeComponent implements OnInit {
  sizes: Size[] = [];
  size: Size = { chiSoKichThuoc: '', trangThai: true };

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  isModalOpen = false;
  isEditMode = false;
  searchKeyword = '';

  constructor(
    private sizeService: SizeService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.sizeService.getAllPaging(page, this.pageSize).subscribe((res: Page<Size>) => {
      this.sizes = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.size = { chiSoKichThuoc: '', trangThai: true };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveSize(): void {
    if (this.size.id) {
      this.sizeService.update(this.size.id, this.size).subscribe((res: Size) => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.sizeService.create(this.size).subscribe({
        next: (res: Size) => {
          alert('Thêm kích thước thành công!');
          this.loadPage(this.currentPage);
          this.resetForm();
          this.closeModal();
        },
        error: (error: any) => {
          alert('Thêm thất bại!');
          console.error('Error creating size:', error);
        }
      });
    }
  }

  editSize(s: Size): void {
    this.isEditMode = true;
    this.size = { ...s };
    this.isModalOpen = true;
  }

  deleteSize(id: number): void {
    if (confirm('Bạn có chắc muốn xoá kích thước này không?')) {
      this.sizeService.delete(id).subscribe((res: any) => {
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
    this.size = { chiSoKichThuoc: '', trangThai: true };
    this.isEditMode = false;
  }

  filteredSizes(): Size[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.sizes.filter((s: Size) =>
      s.chiSoKichThuoc.toLowerCase().includes(keyword)
    );
  }
}
