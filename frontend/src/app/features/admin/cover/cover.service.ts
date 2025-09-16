import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Cover } from "./cover.model";
import { Page } from "./cover.page";

@Injectable({
  providedIn: 'root'
})
export class CoverService {
  private baseUrl = 'http://localhost:8080/api/loai-bia';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Cover[]> {
    return this.http.get<Cover[]>(this.baseUrl);
  }

  create(cover: Cover): Observable<Cover> {
    return this.http.post<Cover>(this.baseUrl, cover);
  }

  update(id: number, cover: Cover): Observable<Cover> {
    return this.http.put<Cover>(`${this.baseUrl}/${id}`, cover);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllPaging(page: number, size: number): Observable<Page<Cover>> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<Page<Cover>>(this.baseUrl, { params });
  }
}
