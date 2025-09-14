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
  order$ = this.orderSubject.asObservable(); // cho nhiều component cùng subscribe

  constructor() {
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws', // URL WebSocket
      reconnectDelay: 5000, // tự động reconnect
      debug: (msg) => console.log('STOMP: ', msg),
      onConnect: () => {
        console.log('✅ WebSocket Connected');
        this.subscribeOrders();
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
}
