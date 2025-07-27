// src/app/services/book.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book } from '../model/book.model';
import { PageResponse } from './book.pagign';

@Injectable({ providedIn: 'root' })
export class BookService {
  private apiUrl = 'http://localhost:8080/api/books'; // địa chỉ backend

  constructor(private http: HttpClient) {}

getBooks(page: number): Observable<PageResponse<Book>> {
  return this.http.get<PageResponse<Book>>(`${this.apiUrl}?page=${page}`);
}



  getBookById(id: string | number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }
  
}
