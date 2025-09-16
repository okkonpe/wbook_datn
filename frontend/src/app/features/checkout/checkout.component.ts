import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-checkout',
  imports: [CommonModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit {
paymentStatus: string = '';
  amount: number = 0;

  constructor(private route: ActivatedRoute,
        private http: HttpClient

  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const responseCode = params['vnp_ResponseCode'];
      const orderID = params['vnp_TxnRef'];
      this.amount = params['vnp_Amount']/100;

      if (responseCode === '00') {
        this.paymentStatus = '✅ Thanh toán thành công!';
     this.http.put('http://localhost:8080/api/payment/da-thanh-toan',{}, { params: { orderID} } ).subscribe()
    
      } else {
        this.paymentStatus = '❌ Thanh toán thất bại!';
            //  this.http.put('http://localhost:8080/api/payment/chua-thanh-toan',{}, { params: { orderID} } ).subscribe()

      }
    });
  }
}
