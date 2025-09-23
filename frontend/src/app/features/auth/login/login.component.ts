import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  loginData = { taiKhoan: '', matKhau: '' };
isEmailModal = false;
email: string = '';
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private router: Router) {}


openEmailModal() {
  this.isEmailModal = true;
}

closeEmailModal() {
  this.isEmailModal = false;
  this.email = ''; // reset input
}

submitEmail() {
  if (!this.email) {
    alert('Vui lòng nhập email!');
    return;
  }
  this.http.post(`${this.apiUrl}/forgot-password`, null, {
      params: new HttpParams().set('email', this.email),
      responseType: 'text'   // vì backend trả ResponseEntity<String>
    }).subscribe({
      next: (res) => {
alert(res)
      },
      error: (err) => {
       alert('Không tìm thấy Email')
      }
    });

  this.closeEmailModal();
}

  login() {
    this.http.post<any>('http://localhost:8080/api/auth/login', this.loginData)
.subscribe({
      next: res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
                this.loginData = { taiKhoan: '', matKhau: '' };

        switch (res.role) {
          case 'ROLE_ADMIN': this.router.navigate(['/admin']); break;
          case 'ROLE_NHAN_VIEN': this.router.navigate(['/admin/order-management']); break;
          default: this.router.navigate(['/']); break;
        }
      },
      error: err => alert('Sai tài khoản hoặc mật khẩu')
    });
  }
}
