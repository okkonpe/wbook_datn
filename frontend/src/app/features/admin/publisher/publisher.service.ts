import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Publisher } from "./publisher.model";
import { Page } from "./publisher.page";

@Injectable({
  providedIn: 'root'
})
export class PublisherService {
  private baseUrl = 'http://localhost:8080/api/nha-xuat-ban';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Publisher[]> {
    return this.http.get<Publisher[]>(this.baseUrl);
  }

  create(publisher: Publisher): Observable<Publisher> {
    return this.http.post<Publisher>(this.baseUrl, publisher);
  }

  update(id: number, publisher: Publisher): Observable<Publisher> {
    return this.http.put<Publisher>(`${this.baseUrl}/${id}`, publisher);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllPaging(page: number, size: number): Observable<Page<Publisher>> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<Page<Publisher>>(this.baseUrl, { params });
  }
}
