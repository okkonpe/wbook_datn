import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Author } from './author.model';
import { AuthorService } from './author.service';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-authors',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './authors.component.html',
  styleUrl: './authors.component.scss'
})
export class AuthorsComponent implements OnInit{
authors: Author[] = [];
totalPages = 0;
currentPage = 0; // page backend bắt đầu từ 0
pageSize = 10;
isModalOpen = false;
message: string | null = null;
isEditMode = false;


  author: Author = { tenTacGia: '', moTa: '' };
    searchKeyword = '';
  constructor(private authorService: AuthorService,private cd: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadPage(0);
    // this.loadAuthors();
}
loadPage(page: number) {
  this.currentPage = page;
  this.authorService.getAllPaging(page, this.pageSize).subscribe(res => {
     console.log('API trả về:', res);
    this.authors = res.content; // ⬅ đảm bảo đúng
    this.totalPages = res.totalPages;
    this.currentPage = res.number;
  });
}

nextPage() {
  if (this.currentPage + 1 < this.totalPages) {
    this.loadPage(this.currentPage + 1);
  }
}
openModal(): void {
   this.isEditMode = false;

    this.isModalOpen = true;
    this.author = { tenTacGia: '', moTa: '' };
   this.cd.detectChanges(); // ép Angular cập nhật view

  }

  closeModal(): void {
    this.isModalOpen = false;
  }

prevPage() {
  if (this.currentPage > 0) {
    this.loadPage(this.currentPage - 1);
  }
}
// loadAuthors() {
//     this.authorService.getAll().subscribe(data => this.authors = data);
//   }

 saveAuthor() {
  console.log('Author đang lưu:', this.author); // 👈 kiểm tra id có tồn tại không

  if (this.author.id) {
    this.authorService.update(this.author.id, this.author).subscribe(() => {
      alert('Cập nhật thành công!');
      this.loadPage(this.currentPage);
      this.resetForm();
      this.closeModal(); // nếu bạn muốn ẩn modal sau khi cập nhật
    });
  } else {
    this.authorService.create(this.author).subscribe({
      next: (res) => {
        console.log('Kết quả từ backend:', res);
        alert('Thêm tác giả thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal(); // thêm dòng này nếu muốn ẩn modal sau khi thêm
      },
      error: () => {
        alert('Thêm tác giả thất bại!');
      }
    });
  }
}


  editAuthor(a: Author) {
   this.isEditMode = true;
  console.log("Đang sửa:", a); // Kiểm tra trong console
  this.author = { ...a };      // Gán trước
  this.isModalOpen = true;     // Rồi mới mở modal
}


  deleteAuthor(id: number) {
    if (confirm('Bạn có chắc muốn xóa?')) {
      this.authorService.delete(id).subscribe(() => this.loadPage(this.currentPage));
    }
  }

  resetForm() {
  this.author = { tenTacGia: '', moTa: '' };
  this.isEditMode = false;
}

  filteredAuthors() {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.authors.filter(a =>
      a.tenTacGia.toLowerCase().includes(keyword)
    );
  }
}
