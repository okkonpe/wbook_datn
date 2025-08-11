import { Component, OnInit } from '@angular/core';
import { SideBarComponent } from "./side-bar/side-bar.component";
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { WebsocketService } from '../../core/services/websocket.service';

@Component({
  selector: 'app-admin',
  imports: [SideBarComponent,CommonModule,RouterOutlet],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
constructor(private wsService: WebsocketService) {}

  ngOnInit() {
    this.wsService.order$.subscribe(order => {
      if (order) {
        alert('📢 Có đơn hàng mới!');
      }
    });
  }
}
