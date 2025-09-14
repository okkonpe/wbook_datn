import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  loginData = { taiKhoan: '', matKhau: '' };

  constructor(private http: HttpClient, private router: Router) {}

  login() {
    this.http.post<any>('http://localhost:8080/api/auth/login', this.loginData)
.subscribe({
      next: res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
                this.loginData = { taiKhoan: '', matKhau: '' };

        switch (res.role) {
          case 'ROLE_ADMIN': this.router.navigate(['/admin']); break;
          case 'ROLE_NHAN_VIEN': this.router.navigate(['/admin/list-order']); break;
            case 'ROLE_SHIPPER': this.router.navigate(['/admin/ship']); break;
          default: this.router.navigate(['/']); break;
        }
      },
      error: err => alert('Sai tài khoản hoặc mật khẩu')
    });
  }
}
