import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  registerData = {
    taiKhoan: '',
    matKhau: '',
    tenKhachHang: '',
    email: '',
    sdt: '',
    diaChi: '',
  ngaySinh: new Date(),
  };
  submitted = false; // biến theo dõi đã bấm nút submit

  constructor(private http: HttpClient,private router: Router) {}

  register(form:any) {
    this.submitted = true;
     if (form.valid) {
      this.http.post<any>('http://localhost:8080/api/auth/register-khach', this.registerData).subscribe({
      next: () => {alert('Đăng ký thành công! Mời bạn đăng nhập'),
      this.router.navigate(['/login']) },
     error: (err) => {
  console.error('Đăng ký lỗi:', err);
  alert('Tài khoản đã tồn tại hoặc thông tin không hợp lệ');
}
    });
    }
   
  }
}
