import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ProductBook } from "./product-book.model";
import { Page } from "./product-book.page";

@Injectable({
  providedIn: 'root'
})
export class ProductBookService {
  private baseUrl = 'http://localhost:8080/api/san-pham';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ProductBook[]> {
    return this.http.get<ProductBook[]>(this.baseUrl);
  }

  create(productBook: ProductBook): Observable<ProductBook> {
    return this.http.post<ProductBook>(this.baseUrl, productBook);
  }

  update(id: number, productBook: ProductBook): Observable<ProductBook> {
    return this.http.put<ProductBook>(`${this.baseUrl}/${id}`, productBook);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllPaging(page: number, size: number): Observable<Page<ProductBook>> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<Page<ProductBook>>(this.baseUrl, { params });
  }
}
