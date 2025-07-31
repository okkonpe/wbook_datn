import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Paper } from './paper.model';
import { PaperService } from './paper.service';

@Component({
  selector: 'app-paper',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './paper.component.html',
  styleUrls: ['./paper.component.scss']
})
export class PaperComponent implements OnInit {
  papers: Paper[] = [];
  paper: Paper = { tenGiay: '', mauSac: '', trangThai: true };

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  isModalOpen = false;
  isEditMode = false;
  searchKeyword = '';

  constructor(
    private paperService: PaperService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.paperService.getAllPaging(page, this.pageSize).subscribe(res => {
      this.papers = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.paper = { tenGiay: '', mauSac: '', trangThai: true };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  savePaper(): void {
    if (this.paper.id) {
      this.paperService.update(this.paper.id, this.paper).subscribe(() => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.paperService.create(this.paper).subscribe({
        next: res => {
          alert('Thêm giấy thành công!');
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

  editPaper(p: Paper): void {
    this.isEditMode = true;
    this.paper = { ...p };
    this.isModalOpen = true;
  }

  deletePaper(id: number): void {
    if (confirm('Bạn có chắc muốn xoá loại giấy này không?')) {
      this.paperService.delete(id).subscribe(() => this.loadPage(this.currentPage));
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
    this.paper = { tenGiay: '', mauSac: '', trangThai: true };
    this.isEditMode = false;
  }

  filteredPapers(): Paper[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.papers.filter(p =>
      p.tenGiay.toLowerCase().includes(keyword)
    );
  }
}
