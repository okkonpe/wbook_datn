import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Topic } from "./topic.model";
import { Page } from "./topic.page";

@Injectable({
  providedIn: 'root'
})
export class TopicService {
  private baseUrl = 'http://localhost:8080/api/chu-de';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Topic[]> {
    return this.http.get<Topic[]>(this.baseUrl);
  }

  create(topic: Topic): Observable<Topic> {
    return this.http.post<Topic>(this.baseUrl, topic);
  }

  update(id: number, topic: Topic): Observable<Topic> {
    return this.http.put<Topic>(`${this.baseUrl}/${id}`, topic);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllPaging(page: number, size: number): Observable<Page<Topic>> {
    const params = {
      page: page.toString(),
      size: size.toString()
    };
    return this.http.get<Page<Topic>>(this.baseUrl, { params });
  }
}
