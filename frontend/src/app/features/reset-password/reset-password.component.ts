import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent implements OnInit{
 token: string = '';
  newPassword: string = '';
  message: string = '';
  error: string = '';
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}
   ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  onSubmit() {
    this.resetPassword(this.token, this.newPassword).subscribe({
      next: (res) => {
        this.message = res;
        this.error = '';
      },
      error: (err) => {
        this.error = 'Token không hợp lệ hoặc đã hết hạn';
        this.message = '';
      }
    });
  }
  resetPassword(token: string, newPassword: string): Observable<any> {
    let params = new HttpParams()
      .set('token', token)
      .set('newPassword', newPassword);

    return this.http.post(`${this.apiUrl}/reset-password`, null, {
      params,
      responseType: 'text'
    });
  }
}
