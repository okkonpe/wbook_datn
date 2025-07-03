import { Component } from '@angular/core';
import { SideBarComponent } from "./side-bar/side-bar.component";
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-admin',
  imports: [SideBarComponent,CommonModule,RouterOutlet],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {

}
