(window as any).global = window;
import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
    private stompClient: Client;
private orderSubject = new Subject<any>(); // không cần BehaviorSubject
  newOrder$ = this.orderSubject.asObservable(); // cho nhiều component cùng subscribe
private donHangDaXacNhanSubject = new Subject<any>();
donHangDaXacNhan$ = this.donHangDaXacNhanSubject.asObservable();
private myOrderSubject = new Subject<any>();
myOrder$ = this.myOrderSubject.asObservable();
private removeOrderSubject = new Subject<any>();
removeOrder$ = this.removeOrderSubject.asObservable();
private daGiaoShipperSubject = new Subject<any>();
daGiaoShipper$ = this.daGiaoShipperSubject.asObservable();
  constructor() {
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws', // URL WebSocket
      reconnectDelay: 5000, // tự động reconnect
      debug: (msg) => console.log('STOMP: ', msg),
      onConnect: () => {
        console.log('✅ WebSocket Connected');
        
        this.subscribeOrders();
        this.subscribedonHangDaXacNhan();
        this.subscribeMyOrders();
      
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error: ', frame.headers['message']);
      },
      webSocketFactory: () => new WebSocket('ws://localhost:8080/ws')
    });

    // Bắt đầu kết nối
    this.stompClient.activate();
  }

  private subscribeOrders() {
    this.stompClient.subscribe('/topic/admin/don-hang-moi', (message: IMessage) => {
        const donHangMoi = JSON.parse(message.body);
      console.log('📦 Đơn hàng mới:', message.body);
      this.orderSubject.next(donHangMoi); // phát thông báo cho tất cả subscriber
    });
  }
  
private subscribedonHangDaXacNhan() {
  this.stompClient.subscribe('/topic/admin/da-xac-nhan', (message: IMessage) => {
    const trangThaiGiaoHang = JSON.parse(message.body);
    console.log('🚚 Trạng thái giao hàng mới:', trangThaiGiaoHang);
    this.donHangDaXacNhanSubject.next(trangThaiGiaoHang);
  });
}
private subscribeMyOrders() {
  this.stompClient.subscribe('/user/queue/don-duoc-nhan', (message: IMessage) => {
    const don = JSON.parse(message.body);
    this.myOrderSubject.next(don);
  });
}


}
