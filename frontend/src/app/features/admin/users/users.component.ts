import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import { User } from './users.model';
import { UserService } from './users.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule], // 👈 ĐẢM BẢO CÓ FormsModule
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  // 👈 KHAI BÁO ĐẦY ĐỦ TẤT CẢ PROPERTIES
  users: User[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  isModalOpen = false;
  message: string | null = null;
  isEditMode = false;
  searchKeyword = '';

  user: User = {
    tenKhachHang: '',
    email: '',
    sdt: '',
    diaChi: '',
    ngaySinh: '',
    gioiTinh: true,
    trangThai: "Hoạt động"
  };

  constructor(private userService: UserService, private cd: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadPage(0);
  }

  // 👈 ĐỊNH NGHĨA ĐẦY ĐỦ TẤT CẢ METHODS
  loadPage(page: number) {
    this.currentPage = page;
    this.userService.getAllPaging(page, this.pageSize).subscribe(res => {
      console.log('API trả về:', res);
      this.users = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
    });
  }

  nextPage() {
    if (this.currentPage + 1 < this.totalPages) {
      this.loadPage(this.currentPage + 1);
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.loadPage(this.currentPage - 1);
    }
  }

  openModal(): void {
    this.isEditMode = false;
    this.isModalOpen = true;
    this.user = {
      tenKhachHang: '',
      email: '',
      sdt: '',
      diaChi: '',
      ngaySinh: '',
      gioiTinh: true,
      trangThai: "Hoạt động"
    };
    this.cd.detectChanges();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveUser() {
    console.log('User đang lưu:', JSON.stringify(this.user, null, 2));

    if (this.user.id) {
      this.userService.update(this.user.id, this.user).subscribe(() => {
        alert('Cập nhật thành công!');
        this.loadPage(this.currentPage);
        this.resetForm();
        this.closeModal();
      });
    } else {
      this.userService.create(this.user).subscribe({
        next: (res) => {
          console.log('Kết quả từ backend:', res);
          alert('Thêm khách hàng thành công!');
          this.loadPage(this.currentPage);
          this.resetForm();
          this.closeModal();
        },
        error: (error) => {
          console.error('Lỗi:', error);
          alert('Thêm khách hàng thất bại!');
        }
      });
    }
  }

  editUser(u: User) {
    this.isEditMode = true;
    console.log("Đang sửa user:", u);
    this.user = { ...u };
    this.isModalOpen = true;
  }

  deleteUser(id: number) {
    console.log('Đang xóa ID:', id);
    if (confirm('Bạn có chắc muốn xóa?')) {
      this.userService.delete(id).subscribe(() => this.loadPage(this.currentPage));
    }
  }

  resetForm() {
    this.user = {
      tenKhachHang: '',
      email: '',
      sdt: '',
      diaChi: '',
      ngaySinh: '',
      gioiTinh: true,
      trangThai: "Hoạt động"
    };
    this.isEditMode = false;
  }

  filteredUsers() {
    const keyword = this.searchKeyword.toLowerCase().trim();
    return this.users.filter(u =>
      u.tenKhachHang.toLowerCase().includes(keyword)
    );
  }

  // Debug methods
  onGioiTinhChange(value: any) {
    console.log('Giới tính thay đổi:', value, typeof value);
    this.user.gioiTinh = value;
  }

  onTrangThaiChange(value: any) {
    console.log('Trạng thái thay đổi:', value, typeof value);
    this.user.trangThai = value;
  }
}
