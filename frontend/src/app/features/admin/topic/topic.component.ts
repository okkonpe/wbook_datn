import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topic } from './topic.model';
import { TopicService } from './topic.service';

@Component({
  selector: 'app-topic',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './topic.component.html',
  styleUrls: ['./topic.component.scss']
})
export class TopicComponent implements OnInit {
  topics: Topic[] = [];
  topic: Topic = { tenChuDe: '', moTa: '', trangThai: true };

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  isModalOpen = false;
  isEditMode = false;
  searchKeyword = '';

  constructor(
    private topicService: TopicService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.currentPage = page;
    this.topicService.getAllPaging(page, this.pageSize).subscribe(res => {
      this.topics = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.topic = { tenChuDe: '', moTa: '', trangThai: true };
    this.isModalOpen = true;
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveTopic(): void {
    if (this.topic.id) {
      this.topicService.update(this.topic.id, this.topic).subscribe(() => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.topicService.create(this.topic).subscribe({
        next: res => {
          alert('Thêm chủ đề thành công!');
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

  editTopic(t: Topic): void {
    this.isEditMode = true;
    this.topic = { ...t };
    this.isModalOpen = true;
  }

  deleteTopic(id: number): void {
    if (confirm('Bạn có chắc muốn xoá chủ đề này không?')) {
      this.topicService.delete(id).subscribe(() => this.loadPage(this.currentPage));
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
    this.topic = { tenChuDe: '', moTa: '', trangThai: true };
    this.isEditMode = false;
  }

  filteredTopics(): Topic[] {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.topics.filter(t =>
      t.tenChuDe.toLowerCase().includes(keyword)
    );
  }
}
