import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Paper } from "./paper.model";
import { Page } from "./paper.page";

@Injectable({
  providedIn: 'root'
})
export class PaperService {
  private baseUrl = 'http://localhost:8080/api/loai-giay';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Paper[]> {
    return this.http.get<Paper[]>(this.baseUrl);
  }

  create(paper: Paper): Observable<Paper> {
    return this.http.post<Paper>(this.baseUrl, paper);
  }

  update(id: number, paper: Paper): Observable<Paper> {
    return this.http.put<Paper>(`${this.baseUrl}/${id}`, paper);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllPaging(page: number, size: number): Observable<Page<Paper>> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<Page<Paper>>(this.baseUrl, { params });
  }
}
