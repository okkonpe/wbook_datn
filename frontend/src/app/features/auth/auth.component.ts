import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-auth',
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.scss'
})
export class AuthComponent {
 activeTab = 'login';

  loginData = {
    username: '',
    password: ''
  };

  registerData = {
    taiKhoan: '',
    matKhau: '',
    hoTen: '',
    email: '',
    sdt: '',
    diaChi: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  login() {
    this.http.post<any>('/api/auth/login', this.loginData).subscribe({
      next: res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
        switch (res.role) {
          case 'ADMIN': this.router.navigate(['/admin']); break;
          case 'EMPLOYEE': this.router.navigate(['/employee']); break;
          default: this.router.navigate(['/home']); break;
        }
      },
      error: () => alert('Sai tài khoản hoặc mật khẩu')
    });
  }

  register() {
    this.http.post<any>('/api/auth/register-khach', this.registerData).subscribe({
      next: () => {
        alert('Đăng ký thành công! Mời bạn đăng nhập');
        this.activeTab = 'login';
      },
      error: () => alert('Tài khoản đã tồn tại hoặc thông tin không hợp lệ')
    });
  }
}
