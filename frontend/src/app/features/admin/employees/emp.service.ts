import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Employee } from "./emp.model";
import { Page } from "./emp.page";

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private baseUrl = 'http://localhost:8080/api/nhan-vien';
  constructor(private http: HttpClient) {}

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.baseUrl);
  }

  create(author: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.baseUrl, author);
  }

  update(id: number, author: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/${id}`, author);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
getAllPaging(page: number, size: number): Observable<Page<Employee>> {
  const params = { page, size };
  return this.http.get<Page<Employee>>(this.baseUrl, { params });
}


}
