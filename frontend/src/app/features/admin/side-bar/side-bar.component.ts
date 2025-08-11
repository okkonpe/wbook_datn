import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { WebsocketService } from '../../../core/services/websocket.service';

@Component({
  selector: 'app-side-bar',
  imports: [RouterModule,CommonModule],
  standalone:true,
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.scss'
})
export class SideBarComponent {
isProductMenuOpen = false;

hasRole(roles: string[]): boolean {
  const currentRole = localStorage.getItem('role'); // Ex: "ROLE_SHIPPER"
  return currentRole !== null && roles.includes(currentRole);
}


}
