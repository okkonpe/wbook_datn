import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Size } from "./size.model";
import { Page } from "./size.page";

@Injectable({
  providedIn: 'root'
})
export class SizeService {
  private baseUrl = 'http://localhost:8080/api/kich-thuoc';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Size[]> {
    return this.http.get<Size[]>(this.baseUrl);
  }

  create(size: Size): Observable<Size> {
    return this.http.post<Size>(this.baseUrl, size);
  }

  update(id: number, size: Size): Observable<Size> {
    return this.http.put<Size>(`${this.baseUrl}/${id}`, size);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllPaging(page: number, size: number): Observable<Page<Size>> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<Page<Size>>(this.baseUrl, { params });
  }
}
