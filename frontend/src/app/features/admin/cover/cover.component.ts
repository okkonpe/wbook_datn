import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cover } from './cover.model';
import { CoverService } from './cover.service';

@Component({
  selector: 'app-cover',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cover.component.html',
  styleUrls: ['./cover.component.scss']
})
export class CoverComponent implements OnInit {
  covers: Cover[] = [];
  cover: Cover = { tenBia: '', mauSac: '', trangThai: true };

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  isModalOpen = false;
  isEditMode = false;
  searchKeyword = '';

  constructor(
    private coverService: CoverService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.coverService.getAllPaging(page, this.pageSize).subscribe(res => {
      this.covers = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.cover = { tenBia: '', mauSac: '', trangThai: true };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveCover(): void {
    if (this.cover.id) {
      this.coverService.update(this.cover.id, this.cover).subscribe(() => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.coverService.create(this.cover).subscribe({
        next: res => {
          alert('Thêm loại bìa thành công!');
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

  editCover(c: Cover): void {
    this.isEditMode = true;
    this.cover = { ...c };
    this.isModalOpen = true;
  }

  deleteCover(id: number): void {
    if (confirm('Bạn có chắc muốn xoá loại bìa này không?')) {
      this.coverService.delete(id).subscribe(() => this.loadPage(this.currentPage));
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
    this.cover = { tenBia: '', mauSac: '', trangThai: true };
    this.isEditMode = false;
  }

  filteredCovers(): Cover[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.covers.filter(c =>
      c.tenBia.toLowerCase().includes(keyword)
    );
  }
}
