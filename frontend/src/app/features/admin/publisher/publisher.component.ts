import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Publisher } from './publisher.model';
import { PublisherService } from './publisher.service';
import { Page } from './publisher.page';

@Component({
  selector: 'app-publisher',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './publisher.component.html',
  styleUrls: ['./publisher.component.scss']
})
export class PublisherComponent implements OnInit {
  publishers: Publisher[] = [];
  publisher: Publisher = { 
    tenNhaXuatBan: '', 
    ngayThanhLap: undefined,
    truSoChinh: '',
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
    private publisherService: PublisherService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.publisherService.getAllPaging(page, this.pageSize).subscribe((res: Page<Publisher>) => {
      this.publishers = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.publisher = { 
      tenNhaXuatBan: '', 
      ngayThanhLap: undefined,
      truSoChinh: '',
      moTa: '',
      trangThai: true 
    };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  savePublisher(): void {
    if (this.publisher.id) {
      this.publisherService.update(this.publisher.id, this.publisher).subscribe((res: Publisher) => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.publisherService.create(this.publisher).subscribe({
        next: (res: Publisher) => {
          alert('Thêm nhà xuất bản thành công!');
          this.loadPage(this.currentPage);
          this.resetForm();
          this.closeModal();
        },
        error: (error: any) => {
          alert('Thêm thất bại!');
          console.error('Error creating publisher:', error);
        }
      });
    }
  }

  editPublisher(p: Publisher): void {
    this.isEditMode = true;
    this.publisher = { ...p };
    this.isModalOpen = true;
  }

  deletePublisher(id: number): void {
    if (confirm('Bạn có chắc muốn xoá nhà xuất bản này không?')) {
      this.publisherService.delete(id).subscribe((res: any) => {
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
    this.publisher = { 
      tenNhaXuatBan: '', 
      ngayThanhLap: undefined,
      truSoChinh: '',
      moTa: '',
      trangThai: true 
    };
    this.isEditMode = false;
  }

  filteredPublishers(): Publisher[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.publishers.filter((p: Publisher) =>
      p.tenNhaXuatBan.toLowerCase().includes(keyword) ||
      (p.truSoChinh && p.truSoChinh.toLowerCase().includes(keyword))
    );
  }
}
