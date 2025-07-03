import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Author } from "./author.model";
import { Page } from "./author.page";

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  private baseUrl = 'http://localhost:8080/api/tac-gia';
  constructor(private http: HttpClient) {}

  getAll(): Observable<Author[]> {
    return this.http.get<Author[]>(this.baseUrl);
  }

  create(author: Author): Observable<Author> {
    return this.http.post<Author>(this.baseUrl, author);
  }

  update(id: number, author: Author): Observable<Author> {
    return this.http.put<Author>(`${this.baseUrl}/${id}`, author);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
getAllPaging(page: number, size: number): Observable<Page<Author>> {
  const params = { page, size };
  return this.http.get<Page<Author>>(this.baseUrl, { params });
}


}
