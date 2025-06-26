import { Component } from '@angular/core';
import { SideBarComponent } from "./side-bar/side-bar.component";
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-admin',
  imports: [SideBarComponent,CommonModule,RouterModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {

}
