import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TheLoaiService } from './theloai.service';
@Component({
  selector: 'app-header',
  imports: [RouterModule,CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit{
  theLoais: any[] = [];

    constructor(private theLoaiService: TheLoaiService, private router: Router) {}
 ngOnInit(): void {
    this.theLoaiService.getTheLoai().subscribe(data => {
      this.theLoais = data.content ?? [];
    });
  }
   goToCategory(id: number, tenTheLoai: string) {
  this.router.navigate(
    ['/product', id],
    { queryParams: { name: tenTheLoai } }  // truyền thêm tên thể loại
  );
}

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }


  getRole(): string | null {
    return localStorage.getItem('role');
  }

  logout() {
    localStorage.clear();
    window.location.href = '/login'; // hoặc dùng this.router.navigate(['/login'])
  }
}
