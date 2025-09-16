import { Component, OnInit } from '@angular/core';
import { SideBarComponent } from "./side-bar/side-bar.component";
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { WebsocketService } from '../../core/services/websocket.service';
import { Toast, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-admin',
  imports: [SideBarComponent,CommonModule,RouterOutlet],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
    toastMessage: string | null = null;

constructor(private wsService: WebsocketService,
  private toastr: ToastrService
) {}

  ngOnInit() {
    this.wsService.newOrder$.subscribe(order => {
      if (order) {
          const role = localStorage.getItem('role'); // Ex: "ROLE_SHIPPER"

        if(role==='ROLE_ADMIN'||role==='ROLE_NHAN_VIEN'){

  this.showToast();
        }
            

      }
    });
  }
   showToast() {
    this.toastr.success('Có đơn hàng mới!', 'Thông báo');
  }
}
