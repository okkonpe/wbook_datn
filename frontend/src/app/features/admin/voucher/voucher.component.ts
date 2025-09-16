import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VoucherService } from './voucher.service';

@Component({
  selector: 'app-voucher',
  imports: [CommonModule,FormsModule],
  templateUrl: './voucher.component.html',
  styleUrl: './voucher.component.scss'
})
export class VoucherComponent implements OnInit {
vouchers: any[] = [];
 showForm: boolean = false;   // kiểm soát hiển thị form
  isEdit: boolean = false;     // phân biệt thêm hay sửa
  currentVoucher: any = {};  
  constructor(private voucherService: VoucherService) { }

  ngOnInit(): void {
    this.loadVouchers();
  }

  loadVouchers(): void {
    this.voucherService.getVouchers().subscribe({
      next: res => this.vouchers = res,
      error: err => console.error(err)
    });
  }

  deleteVoucher(id: number): void {
    if (confirm('Bạn có chắc muốn xóa voucher này?')) {
      this.voucherService.deleteVoucher(id).subscribe({
        next: () => this.loadVouchers(),
        error: err => console.error(err)
      });
    }
  }
   addVoucher(): void {
    this.isEdit = false;
    this.currentVoucher = {};
    this.showForm = true;
  }

  // Mở form sửa voucher
  editVoucher(voucher: any): void {
    this.isEdit = true;
    this.currentVoucher = { ...voucher }; // copy object
    this.showForm = true;
  }

  // Lưu voucher (thêm hoặc cập nhật)
  saveVoucher(): void {
    if (this.isEdit) {
      // cập nhật
      this.voucherService.updateVoucher(this.currentVoucher.id, this.currentVoucher).subscribe({
        next: () => {
          this.showForm = false;
          this.loadVouchers();
        },
        error: err => console.error(err)
      });
    } else {
      // thêm mới
      this.voucherService.createVoucher(this.currentVoucher).subscribe({
        next: () => {
          this.showForm = false;
          this.loadVouchers();
        },
        error: err => console.error(err)
      });
    }
  }

  // Hủy form
  cancel(): void {
    this.showForm = false;
    this.currentVoucher = {};
  }

}
