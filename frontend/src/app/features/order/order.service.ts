import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { KhachHang } from './order.component'; // import đúng interface

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private khachHangSubject = new BehaviorSubject<KhachHang | null>(null);
  khachHang$ = this.khachHangSubject.asObservable();

  constructor(private http: HttpClient) {}

  fetchKhachHang(): Observable<KhachHang> {
    return this.http.get<KhachHang>('http://localhost:8080/api/khach-hang/info');
  }

  setKhachHang(kh: KhachHang) {
    this.khachHangSubject.next(kh);
  }

  getKhachHang(): KhachHang | null {
    return this.khachHangSubject.value;
  }
}
