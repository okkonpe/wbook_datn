import { Component } from '@angular/core';
import { BookListComponent } from '../../features/books/components/book-list/book-list.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [BookListComponent,CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent  {

  
}

