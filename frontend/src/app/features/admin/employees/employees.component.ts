import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Employee } from './emp.model';
import { EmployeeService } from './emp.service';

@Component({
  selector: 'app-employees',
  imports: [CommonModule,FormsModule],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.scss'
})
export class EmployeesComponent {
emps: Employee[] = [];
totalPages = 0;
currentPage = 0; // page backend bắt đầu từ 0
pageSize = 10;
isModalOpen = false;
message: string | null = null;
isEditMode = false;
backendErrors: any ;
errorMessage: string = '';




  emp: Employee = { tenNv: '', sdt: '' };
    searchKeyword = '';
  constructor(private empService: EmployeeService,private cd: ChangeDetectorRef,
) {}

  ngOnInit() {
    this.loadPage(0);
    // this.loadAuthors();
}
loadPage(page: number) {
  this.currentPage = page;
  this.empService.getAllPaging(page, this.pageSize).subscribe(res => {
     console.log('API trả về:', res);
    this.emps = res.content; // ⬅ đảm bảo đúng
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
    this.emp = { tenNv: '', sdt: '' };
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
  console.log('Author đang lưu:', this.emp); // 👈 kiểm tra id có tồn tại không

  if (this.emp.id) {
    this.empService.update(this.emp.id, this.emp).subscribe(() => {
      alert('Cập nhật thành công!');
      this.loadPage(this.currentPage);
      this.resetForm();
      this.closeModal(); // nếu bạn muốn ẩn modal sau khi cập nhật
    });
  } else {
    this.empService.create(this.emp).subscribe({
      next: res => {
         console.log("✅ API thành công:", res);
window.alert("Thêm nhân viên thành công!");
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal(); // thêm dòng này nếu muốn ẩn modal sau khi thêm
      },
        error: err => {
      if (err.status === 400) {
        if (err.error.message) {
          // ❌ Trường hợp lỗi chung (IllegalArgumentException)
          this.errorMessage = err.error.message;
          this.backendErrors = {};
        } else {
          // ❌ Trường hợp lỗi validation theo field
          this.backendErrors = err.error;
          this.errorMessage = '';
        }
      } else {
        this.errorMessage = 'Có lỗi xảy ra!';
        this.backendErrors = {};
      }
    }
    });
  }
}


  editAuthor(a: Employee) {
   this.isEditMode = true;
  console.log("Đang sửa:", a); // Kiểm tra trong console
  this.emp = { ...a };      // Gán trước
  this.isModalOpen = true;     // Rồi mới mở modal
}


  deleteAuthor(id: number) {
    if (confirm('Bạn có chắc muốn xóa?')) {
      this.empService.delete(id).subscribe(() => this.loadPage(this.currentPage));
    }
  }

  resetForm() {
this.backendErrors = {};
  this.emp = { tenNv: '', sdt: '' };
  this.isEditMode = false;
}

  filteredAuthors() {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.emps.filter(a =>
      a.tenNv.toLowerCase().includes(keyword)
    );
  }
}
