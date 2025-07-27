import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-customer-info',
  imports: [CommonModule,FormsModule],
  templateUrl: './customer-info.component.html',
  styleUrl: './customer-info.component.scss'
})
export class CustomerInfoComponent {

 constructor(private http: HttpClient) {}

 
   khachHang = {
  tenKhachHang: '',
  sdt: '',
  ngaySinh: new Date(),
  diaChi: '',
  email: ''
};

editMode = false;

saveChanges() {
  // gọi API cập nhật
  console.log('Lưu khách hàng:', this.khachHang);
  this.editMode = false;
}


ngOnInit() {
    this.getKhachHang().subscribe(data =>{
      this.khachHang=data;
    }

    )
  }
    getKhachHang(): Observable<any> {
  return this.http.get<any>('http://localhost:8080/api/khach-hang/info');
  }
}
