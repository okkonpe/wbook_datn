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

 khachHangCopy:any;
 passWord={oldPassword:'',newPassword:''};
   khachHang = {
  tenKhachHang: '',
  sdt: '',
  ngaySinh: new Date(),
  diaChi: '',
  email: ''
};

isModal=false;

modalMK=false;


openModal() {
  this.khachHangCopy = { ...this.khachHang }; // copy tạm
  this.isModal = true;
    this.modalMK = false; // đảm bảo không mở 2 modal cùng lúc

}
colseModal() {
  this.khachHang = { ...this.khachHangCopy }; // lưu lại
  this.isModal = false;
}

openModalDoiMK(){
this.modalMK=true;
  this.isModal = false;

}
closeModalDoiMK(){
this.modalMK=false;
}
doiMK(){
  if(this.passWord.oldPassword&&this.passWord.newPassword){
  this.http.put<any>('http://localhost:8080/api/khach-hang/change-password',this.passWord,{ responseType: 'text'as 'json' }).subscribe({
      next:(res)=>{
alert(res)
this.modalMK=false      
      },
      error:()=>{
alert('Mật khẩu cũ sai!')

      }
     }

    );}else{
alert('Xin hãy nhập thông tin!')

    }
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
  update(){
    if(this.khachHang!=this.khachHangCopy){
      this.http.put<any>('http://localhost:8080/api/khach-hang/updateInfo',this.khachHang).subscribe({
      next:()=>{
alert('Sửa thông tin thành công')
this.getKhachHang().subscribe(data =>{
      this.khachHang=data;
    }

    )
this.isModal=false
      },
      error:()=>{

      }
     }

    );
    }
    this.isModal=false

     
    
  }
}
