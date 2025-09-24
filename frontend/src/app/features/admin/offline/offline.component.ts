import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import jsQR from 'jsqr';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import { RouterTestingHarness } from '@angular/router/testing';

interface PendingInvoice {
  id: string;
  orderNumber: number;
  hdNumber: any;
  items: InvoiceItem[];
  customer: Customer;
  totalAmount: number;
  createdAt: Date;
}
interface DecodedToken {
  sub: string;
  id: number;
  role: string;
  iat: number;
  exp: number;
}

interface InvoiceItem {
  id: number;
  maSanPhamChiTiet: string;
  tenSanPham: string;
  isbn: string;
  donGia: number;
  soLuong: number;
  thanhTien: number;
  hinhAnh?: string;
  tonKho?: number;
  moTa?: string;
}

interface Customer {
  id?: number;
  tenKhachHang: string;
  soDienThoai?: string;
  diaChi?: string;
  loai: 'KHACH_LE' | 'KHACH_HANG';
}

interface VariantRow {
  id: number;
  tenSanPham: string;
  maSanPhamChiTiet: string;
  isbn: string;
  theLoai: string;
  nhaXuatBan: string;
  hinhAnh?: string | null;
  donGia: number;
  soLuong: number; // tồn kho
  selected?: boolean;
  qty?: number; // số lượng muốn thêm
}

interface Voucher {
  id: number;
  maVoucher: string;
  moTa: string;
  loaiGiam: 'PERCENT' | 'AMOUNT';
  giaTri: number;
  giamToiDa?: number;
  donToiThieu: number;
  ngayBatDau: string;
  ngayKetThuc: string;
  soLuong: number;
  daDung: number;
  trangThai: boolean;
}

@Component({
  selector: 'app-offline',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './offline.component.html',
  styleUrls: ['./offline.component.scss']
})
export class OfflineComponent implements OnInit, AfterViewInit {
  @ViewChild('videoElement') videoElement!: ElementRef<HTMLVideoElement>;
  
  pendingInvoices: PendingInvoice[] = [];
  selectedInvoiceIndex: number = -1;
  searchQuery: string = '';
  discount: number = 0;
  paymentMethod: string = 'cash';
  customerPayment: number = 0;
  change: number = 0;

  
  // Modal states
  isCustomerModalOpen: boolean = false;
  customerSearchQuery: string = '';
  availableCustomers: Customer[] = [];
  isProductModalOpen: boolean = false;
  productSearchQuery: string = '';
  availableProducts: VariantRow[] = [];
  isQrScannerOpen: boolean = false;
  
  // Quick customer creation
  isQuickCustomerModalOpen: boolean = false;
  newCustomer: any = {
    tenKhachHang: '',
    sdt: '',
    diaChi: '',
    email: ''
  };
  
  // Product detail modal
  isProductDetailModalOpen: boolean = false;
  selectedProductDetail: InvoiceItem | null = null;
  showQrModal =false;
  // Voucher states
  showVoucherModal: boolean = false;
  availableVouchers: Voucher[] = [];
  selectedVoucher: Voucher | null = null;
  private cookieKeyInvoices = 'offline_pending_invoices';
  private cookieKeySelectedIndex = 'offline_selected_index';
  
  // Camera states
  private mediaStream: MediaStream | null = null;
  private scanInterval: any;
  private scanRaf: number | null = null;
  private offscreenCanvas: HTMLCanvasElement | null = null;
  private offscreenCtx: CanvasRenderingContext2D | null = null;
  private isDecoding: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.restoreFromCookie();
    
  }

  ngAfterViewInit(): void {
  }

  get selectedInvoice(): PendingInvoice | null {
    return this.selectedInvoiceIndex >= 0 ? this.pendingInvoices[this.selectedInvoiceIndex] : null;
  }

  createNewInvoice(): void {
    if (this.pendingInvoices.length >= 5) return;
 const maxOrderNumber =
    this.pendingInvoices.length > 0
      ? Math.max(...this.pendingInvoices.map(inv => inv.orderNumber))
      : 0;
    const newInvoice: PendingInvoice = {
      id: this.generateId(),
      orderNumber: maxOrderNumber+1,
      hdNumber:'HD'+maxOrderNumber,
      items: [],
      customer: {
        tenKhachHang: 'Khách lẻ',
        loai: 'KHACH_LE'
      },
      totalAmount: 0,
      createdAt: new Date()
    };

    this.pendingInvoices.push(newInvoice);
    this.selectedInvoiceIndex = this.pendingInvoices.length - 1;
    
    // Reset voucher và discount cho hóa đơn mới
    this.selectedVoucher = null;
    this.discount = 0;
    this.customerPayment = 0;
    this.change = 0;
    
    console.log('🆕 Created new invoice - reset voucher and discount');
    this.saveToCookie();
  }

  selectInvoice(index: number): void {
    this.selectedInvoiceIndex = index;
    
    // Reset voucher và discount khi chuyển hóa đơn
    this.selectedVoucher = null;
    this.discount = 0;
    this.customerPayment = 0;
    this.change = 0;
    console.log(this.selectedInvoice);

    console.log('🔄 Switched to invoice:', index, '- reset voucher and discount');
    this.saveSelectedIndex();
  }

  closeInvoice(index: number, skipConfirm: boolean = false): void {
    if (!skipConfirm && this.pendingInvoices[index].items.length > 0) {
      if (!confirm('Hóa đơn này có sản phẩm. Bạn có chắc muốn đóng?')) {
        return;
      }
    }
    
    this.pendingInvoices.splice(index, 1);
    
    if (this.selectedInvoiceIndex >= index) {
      this.selectedInvoiceIndex = Math.max(0, this.selectedInvoiceIndex - 1);
    }
    
    if (this.pendingInvoices.length === 0) {
      this.selectedInvoiceIndex = -1;
    }
    this.saveToCookie();
  }

  searchProduct(): void {
    if (!this.searchQuery.trim() || !this.selectedInvoice) return;

    this.http.get<any>(`http://localhost:8080/api/books/search?q=${this.searchQuery}`).subscribe({
      next: (product) => {
        this.addProductToInvoice(product);
        this.searchQuery = '';
      },
      error: () => {
        alert('Không tìm thấy sản phẩm với mã: ' + this.searchQuery);
        this.searchQuery = '';
      }
    });
  }

  async openQrScanner(): Promise<void> {
    this.isQrScannerOpen = true;
    
    setTimeout(async () => {
      await this.startCamera();
    }, 300);
  }

  closeQrScanner(): void {
    this.isQrScannerOpen = false;
    this.stopCamera();
  }

  async startCamera(): Promise<void> {
    try {
      const constraints = {
        video: {
          facingMode: 'environment',
          width: { ideal: 640 },
          height: { ideal: 480 }
        }
      };

      this.mediaStream = await navigator.mediaDevices.getUserMedia(constraints);
      
      if (this.videoElement) {
        this.videoElement.nativeElement.srcObject = this.mediaStream;
        this.videoElement.nativeElement.play();
        
        if (!this.offscreenCanvas) {
          this.offscreenCanvas = document.createElement('canvas');
          this.offscreenCtx = this.offscreenCanvas.getContext('2d');
        }

        this.startQrScanning();
      }
    } catch (error) {
      alert('Không thể truy cập camera. Vui lòng kiểm tra quyền truy cập và thử lại.');
    }
  }

  stopCamera(): void {
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.mediaStream = null;
    }
    
    if (this.scanInterval) { clearInterval(this.scanInterval); this.scanInterval = null; }
    if (this.scanRaf) { cancelAnimationFrame(this.scanRaf); this.scanRaf = null; }
    this.isDecoding = false;
  }

  startQrScanning(): void {
    if (!this.videoElement) return;

    const scan = () => {
      if (!this.videoElement || !this.videoElement.nativeElement || !this.offscreenCanvas || !this.offscreenCtx) {
        this.scanRaf = requestAnimationFrame(scan);
        return;
      }

      const video = this.videoElement.nativeElement;
      const width = video.videoWidth;
      const height = video.videoHeight;

      if (width === 0 || height === 0) {
        this.scanRaf = requestAnimationFrame(scan);
        return;
      }

      this.offscreenCanvas.width = width;
      this.offscreenCanvas.height = height;
      this.offscreenCtx.drawImage(video, 0, 0, width, height);

      const imageData = this.offscreenCtx.getImageData(0, 0, width, height);
      const code = jsQR(imageData.data, width, height, { inversionAttempts: 'dontInvert' });

      if (code && code.data && !this.isDecoding) {
        this.isDecoding = true;
        try {
          const ctx = new (window as any).AudioContext();
          const o = ctx.createOscillator();
          const g = ctx.createGain();
          o.type = 'sine';
          o.connect(g); g.connect(ctx.destination);
          o.frequency.value = 880; g.gain.value = 0.05; o.start();
          setTimeout(() => { o.stop(); ctx.close(); }, 120);
        } catch {}
        
        const content = (code.data || '').trim();
        this.onQrCodeScanned(content);
        return;
      }

      this.scanRaf = requestAnimationFrame(scan);
    };

    if (this.scanRaf) cancelAnimationFrame(this.scanRaf);
    this.scanRaf = requestAnimationFrame(scan);
  }

  onQrCodeScanned(code: string): void {
    this.searchQuery = code;
    this.searchProduct();
    this.closeQrScanner();
  }

  openProductSearch(): void {
    this.loadAvailableProducts();
    this.isProductModalOpen = true;
  }

  closeProductModal(): void {
    this.isProductModalOpen = false;
    this.productSearchQuery = '';
  }

  loadAvailableProducts(): void {
    this.http.get<any>(`http://localhost:8080/api/books?page=0&size=50`).subscribe({
      next: (response) => {
        const raw = Array.isArray(response) ? response : (response?.content ?? []);
        this.availableProducts = raw.map((item: any) => ({
          id: item.id,
          tenSanPham: item.tenSanPham ?? item.sanPham?.tenSanPham ?? item.ten_san_pham ?? '',
          maSanPhamChiTiet: item.maSanPhamChiTiet ?? item.ma_spct ?? item.code ?? '',
          isbn: item.isbn ?? item.ISBN ?? '',
          theLoai: (typeof item.theLoai === 'object' ? (item.theLoai?.ten || item.theLoai?.name || item.theLoai?.tenTheLoai) : item.theLoai)
                   ?? item.theLoaiName ?? item.tenTheLoai ?? item.the_loai ?? '-',
          nhaXuatBan: (typeof item.nhaXuatBan === 'object' ? (item.nhaXuatBan?.ten || item.nhaXuatBan?.name || item.nhaXuatBan?.tenNxb) : item.nhaXuatBan)
                       ?? item.nxbName ?? item.tenNxb ?? item.nxb ?? item.nha_xuat_ban ?? '-',
          hinhAnh: item.hinhAnh ?? item.imageUrl ?? item.image ?? null,
          donGia: item.donGia ?? item.giaBan ?? item.price ?? 0,
          soLuong: item.soLuong ?? item.soLuongTon ?? item.so_luong ?? item.tonKho ?? item.ton ?? item.stock ?? 0,
          selected: false,
          qty: 1
        }));
      },
      error: () => {}
    });
  }

  filterAvailableProducts(term: string): VariantRow[] {
    if (!term) return this.availableProducts;
    const t = term.toLowerCase();
    return this.availableProducts.filter(p =>
      (p.tenSanPham || '').toLowerCase().includes(t) ||
      (p.maSanPhamChiTiet || '').toLowerCase().includes(t) ||
      (p.isbn || '').toLowerCase().includes(t)
    );
  }

  addProductFromModal(product: any): void {
    const rawQty = Number(product?.qty ?? 1);
    const stock = Number(product?.soLuong ?? 0);
    let qty = Number.isFinite(rawQty) ? Math.trunc(rawQty) : 1;
    if (qty < 1) qty = 1;
    if (stock > 0 && qty > stock) qty = stock;

    const productWithQty = { ...product, soLuongChon: qty };
    this.addProductToInvoice(productWithQty);
    const idx = this.availableProducts.findIndex(p => p.id === product.id);
    if (idx >= 0) {
      const remain = Math.max(0, Number(this.availableProducts[idx].soLuong || 0) - qty);
      this.availableProducts[idx].soLuong = remain;
      if ((this.availableProducts[idx].qty || 1) > remain) {
        this.availableProducts[idx].qty = Math.max(1, remain);
      }
    }
    this.saveToCookie();
  }

  addSelectedFromModal(): void {
    const selected = this.availableProducts.filter(p => p.selected);
    if (!selected.length) return;
    selected.forEach(p => {
      const rawQty = Number(p.qty || 1);
      const stock = Number(p.soLuong || 0);
      let qty = Number.isFinite(rawQty) ? Math.trunc(rawQty) : 1;
      if (qty < 1) qty = 1;
      if (stock > 0 && qty > stock) qty = stock;
      const payload: any = { ...p, soLuongChon: qty };
      this.addProductToInvoice(payload);
      p.soLuong = Math.max(0, Number(p.soLuong || 0) - qty);
      if ((p.qty || 1) > p.soLuong) {
        p.qty = Math.max(1, p.soLuong || 1);
      }
    });
    this.saveToCookie();
  }

  addProductToInvoice(product: any): void {
    if (!this.selectedInvoice) return;

    const existingItemIndex = this.selectedInvoice.items.findIndex(
      item => item.maSanPhamChiTiet === product.maSanPhamChiTiet
    );

    if (existingItemIndex >= 0) {
      const addQty = product.soLuongChon ? Number(product.soLuongChon) : 1;
      const current = this.selectedInvoice.items[existingItemIndex];
      const stock = Number(current.tonKho || 0);
      const nextQty = current.soLuong + addQty;
      current.soLuong = stock > 0 ? Math.min(nextQty, stock) : nextQty;
      this.selectedInvoice.items[existingItemIndex].thanhTien = 
        this.selectedInvoice.items[existingItemIndex].soLuong * 
        this.selectedInvoice.items[existingItemIndex].donGia;
  } else {
      const newItem: InvoiceItem = {
        id: product.id,
        maSanPhamChiTiet: product.maSanPhamChiTiet,
        tenSanPham: product.tenSanPham || product.sanPham?.tenSanPham,
        isbn: product.isbn,
        donGia: product.donGia,
        soLuong: (() => { const q = product.soLuongChon ? Number(product.soLuongChon) : 1; const stock = Number(product.soLuong || 0); return stock > 0 ? Math.min(q, stock) : q; })(),
        thanhTien: 0,
        hinhAnh: product.hinhAnh,
        tonKho: Number(product.soLuong || 0)
      };
      newItem.thanhTien = newItem.soLuong * newItem.donGia;
      this.selectedInvoice.items.push(newItem);
    }

    this.updateInvoiceTotal();
    this.saveToCookie();
  }

  increaseQuantity(index: number): void {
    if (!this.selectedInvoice) return;
    
    const item = this.selectedInvoice.items[index];
    const stock = Number(item.tonKho || 0);
    if (stock > 0 && item.soLuong >= stock) {
      return;
    }
    item.soLuong++;
    item.thanhTien = item.soLuong * item.donGia;
    
    this.updateInvoiceTotal();
  }

  decreaseQuantity(index: number): void {
    if (!this.selectedInvoice) return;
    
    if (this.selectedInvoice.items[index].soLuong > 1) {
      this.selectedInvoice.items[index].soLuong--;
      this.selectedInvoice.items[index].thanhTien = 
        this.selectedInvoice.items[index].soLuong * this.selectedInvoice.items[index].donGia;
      
      this.updateInvoiceTotal();
    }
    this.saveToCookie();
  }

  removeItem(index: number): void {
    if (!this.selectedInvoice) return;
    
    this.selectedInvoice.items.splice(index, 1);
    this.updateInvoiceTotal();
    this.saveToCookie();
  }

  updateInvoiceTotal(): void {
    if (!this.selectedInvoice) return;
    
    this.selectedInvoice.totalAmount = this.selectedInvoice.items.reduce(
      (total, item) => total + item.thanhTien, 0
    );
    
    this.calculateChange();
  }

  selectCustomer(): void {
    this.loadAvailableCustomers();
    this.isCustomerModalOpen = true;
  }

  closeCustomerModal(): void {
    this.isCustomerModalOpen = false;
    this.customerSearchQuery = '';
  }

  loadAvailableCustomers(): void {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getAuthToken()}`,
      'Content-Type': 'application/json'
    });

    this.http.get<any>('http://localhost:8080/api/khach-hang?page=0&size=100', { headers }).subscribe({
      next: (response) => {
        const customers = Array.isArray(response) ? response : (response?.content ?? []);
        this.availableCustomers = customers.map((customer: any) => ({
          id: customer.id,
          tenKhachHang: customer.tenKhachHang || customer.hoTen || customer.ten_khach_hang || '',
          soDienThoai: customer.sdt || customer.soDienThoai || customer.so_dien_thoai || '',
          diaChi: customer.diaChi || customer.dia_chi || '',
          loai: customer.taiKhoan ? 'KHACH_HANG' : 'KHACH_LE' // Có tài khoản = khách hàng, không có = khách lẻ
        }));
        console.log('👥 Customers loaded:', this.availableCustomers);
      },
      error: (error) => {
        console.error('❌ Error loading customers:', error);
        this.availableCustomers = [];
      }
    });
  }

  filterCustomers(query: string): Customer[] {
    if (!query) return this.availableCustomers;
    const q = query.toLowerCase();
    return this.availableCustomers.filter(customer =>
      (customer.tenKhachHang || '').toLowerCase().includes(q) ||
      (customer.soDienThoai || '').toLowerCase().includes(q)
    );
  }

  selectCustomerFromList(customer: Customer): void {
    if (this.selectedInvoice) {
      this.selectedInvoice.customer = customer;
    }
    this.closeCustomerModal();
    this.saveToCookie();
  }

  selectGuestCustomer(): void {
    if (this.selectedInvoice) {
      this.selectedInvoice.customer = {
        tenKhachHang: 'Khách lẻ',
        loai: 'KHACH_LE'
      };
    }
    this.closeCustomerModal();
    this.saveToCookie();
  }

  // Quick customer creation methods
  openQuickCustomerModal(): void {
    this.newCustomer = {
      tenKhachHang: '',
      sdt: '',
      diaChi: '',
      email: ''
    };
    this.isQuickCustomerModalOpen = true;
  }

  closeQuickCustomerModal(): void {
    this.isQuickCustomerModalOpen = false;
  }

  // Product detail modal methods
  viewProductDetail(item: InvoiceItem): void {
    this.selectedProductDetail = item;
    this.isProductDetailModalOpen = true;
  }

  closeProductDetailModal(): void {
    this.isProductDetailModalOpen = false;
    this.selectedProductDetail = null;
  }

  createQuickCustomer(): void {
    if (!this.newCustomer.tenKhachHang.trim()) {
      alert('Vui lòng nhập tên khách hàng');
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getAuthToken()}`,
      'Content-Type': 'application/json'
    });

    this.http.post<any>('http://localhost:8080/api/khach-hang/quick-create', this.newCustomer, { headers }).subscribe({
      next: (customer) => {
        // Thêm khách hàng mới vào danh sách
        const newCustomerData: Customer = {
          id: customer.id,
          tenKhachHang: customer.tenKhachHang,
          soDienThoai: customer.sdt,
          diaChi: customer.diaChi,
          loai: 'KHACH_HANG'
        };
        
        this.availableCustomers.unshift(newCustomerData);
        
        // Chọn khách hàng mới tạo
        this.selectCustomerFromList(newCustomerData);
        
        this.closeQuickCustomerModal();
        console.log('✅ Quick customer created:', customer);
      },
      error: (error) => {
        console.error('❌ Error creating quick customer:', error);
        alert('Lỗi khi tạo khách hàng: ' + (error?.error?.message || 'Lỗi không xác định'));
      }
    });
  }

  calculateChange(): void {
    const total = this.selectedInvoice ? this.selectedInvoice.totalAmount - this.discount : 0;
    this.change = this.customerPayment - total;
  }

  canCompletePayment(): boolean {
    if (!this.selectedInvoice || this.selectedInvoice.items.length === 0) return false;
    const total = this.selectedInvoice.totalAmount - this.discount;
    
    return this.customerPayment >= total && this.change >= 0;
  }
getUserIdFromToken(): number {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode<DecodedToken>(token);
        return Number(decoded.id);
      } catch (e) {
        console.error('Decode token lỗi:', e);
      }
    }
    return 0; // fallback nếu lỗi
  }
  completePayment(): void {
    if (!this.canCompletePayment() || !this.selectedInvoice) return;

    const ok = confirm('Bạn có muốn thanh toán không?');
    if (!ok) return;

    const paymentData = {
      khachHangID: this.selectedInvoice.customer?.id || null,
      hoTen: this.selectedInvoice.customer?.tenKhachHang || 'Khách lẻ',
      soDienThoai: this.selectedInvoice.customer?.soDienThoai || '',
      diaChi: this.selectedInvoice.customer?.diaChi || '',
      ghiChu: ''    
    } as any;

    const token = this.getAuthToken();
    const baseHeaders: any = { 'Content-Type': 'application/json' };
    if (token) baseHeaders['Authorization'] = `Bearer ${token}`;
    const headers = new HttpHeaders(baseHeaders);
    const reqBody = {
            khID:paymentData.khachHangID,
      hoTen: paymentData.hoTen,
      soDienThoai: paymentData.soDienThoai,
      diaChi: paymentData.diaChi,
      tongTien: this.selectedInvoice.totalAmount,
      tongTienSauGiam: this.selectedInvoice.totalAmount - this.discount,
      giamGia: this.discount,
      khachThanhToan: this.customerPayment,
      tienThua: this.change,
      idNhanVien:this.getUserIdFromToken(),
      voucherId: this.selectedVoucher?.id || null,
      phuongThucTT:this.paymentMethod,
      items: (this.selectedInvoice.items || []).map(it => ({ id: it.id, soLuong: it.soLuong, donGia: it.donGia }))
    };

    console.log('💳 Payment request body:', reqBody);
    console.log('🎫 Selected voucher:', this.selectedVoucher);
    console.log('💰 Discount amount:', this.discount);
 this.http.post<any>('http://localhost:8080/api/hoa-don/offline/thanh-toan', reqBody, { headers }).subscribe({
      next: (resp) => {
        const closeIdx = this.selectedInvoiceIndex;
        this.closeInvoice(closeIdx, true);
        this.customerPayment = 0;
        this.change = 0;
        this.discount = 0;
        this.paymentMethod = 'cash';
        
        // Hiển thị thông báo thanh toán thành công
        alert('Thanh toán thành công!');
      },
      error: (err) => {
        const status = err?.status;
        if (status === 401 || status === 403) {
          alert('Bạn không có quyền hoặc phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        } else {
          alert('Thanh toán thất bại: ' + (err?.error?.message || 'Lỗi không xác định'));
        }
      }
    });

   
  }
  completePaymentQR(): void {
 
console.log(this.paymentMethod)
    if (!this.selectedInvoice) return;

    const ok = confirm('Bạn đã nhận được tiền chuyển khoản?');
    if (!ok) return;

    const paymentData = {
      khachHangID: this.selectedInvoice.customer?.id || null,
      hoTen: this.selectedInvoice.customer?.tenKhachHang || 'Khách lẻ',
      soDienThoai: this.selectedInvoice.customer?.soDienThoai || '',
      diaChi: this.selectedInvoice.customer?.diaChi || '',
      ghiChu: '',
    } as any;

    const token = this.getAuthToken();
    const baseHeaders: any = { 'Content-Type': 'application/json' };
    if (token) baseHeaders['Authorization'] = `Bearer ${token}`;
    const headers = new HttpHeaders(baseHeaders);
    const reqBody = {
      khID:paymentData.khachHangID,
      hoTen: paymentData.hoTen,
      soDienThoai: paymentData.soDienThoai,
      diaChi: paymentData.diaChi,
      tongTien: this.selectedInvoice.totalAmount,
      tongTienSauGiam: this.selectedInvoice.totalAmount - this.discount,
      giamGia: this.discount,
      // khachThanhToan: this.customerPayment,
      // tienThua: this.change,
      idNhanVien:this.getUserIdFromToken(),
      voucherId: this.selectedVoucher?.id || null,
            phuongThucTT:this.paymentMethod,
      items: (this.selectedInvoice.items || []).map(it => ({ id: it.id, soLuong: it.soLuong, donGia: it.donGia }))
    };

    console.log('💳 Payment request body:', reqBody);
    console.log('🎫 Selected voucher:', this.selectedVoucher);
    console.log('💰 Discount amount:', this.discount);
 this.http.post<any>('http://localhost:8080/api/hoa-don/offline/thanh-toan', reqBody, { headers }).subscribe({
      next: (resp) => {
                this.showQrModal=false;
        const closeIdx = this.selectedInvoiceIndex;
        this.closeInvoice(closeIdx, true);
        this.customerPayment = 0;
        this.change = 0;
        this.discount = 0;        

        // Hiển thị thông báo thanh toán thành công
        alert('Thanh toán thành công!');
      },
      error: (err) => {
        const status = err?.status;
        if (status === 401 || status === 403) {
          alert('Bạn không có quyền hoặc phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        } else {
          alert('Thanh toán thất bại: ' + (err?.error?.message || 'Lỗi không xác định'));
        }
      }
    });

   
  }

  private getAuthToken(): string | null {
    const keys = ['token', 'accessToken', 'access_token', 'jwt', 'authToken', 'AUTH_TOKEN'];
    for (const k of keys) {
      const v = localStorage.getItem(k) || sessionStorage.getItem(k);
      if (v) return v.replace(/^"|"$/g, '');
    }
    const cookieKeys = ['token', 'accessToken', 'access_token', 'jwt', 'authToken'];
    for (const ck of cookieKeys) {
      const val = this.getCookie(ck);
      if (val) return val.replace(/^"|"$/g, '');
    }
    return null;
  }

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }

  // Chuẩn hóa số lượng khi nhập trong modal
  onQtyChange(row: VariantRow): void {
    const stock = Number(row?.soLuong ?? 0);
    let qty = Number(row?.qty ?? 1);
    if (!Number.isFinite(qty)) qty = 1;
    qty = Math.trunc(qty);
    if (qty < 1) qty = 1;
    if (stock > 0 && qty > stock) qty = stock;
    row.qty = qty;
  }

  // ===== Cookie persistence =====
  private saveToCookie(): void {
    try {
      const payload = {
        pendingInvoices: this.pendingInvoices,
        discount: this.discount
      };
      this.setCookie(this.cookieKeyInvoices, encodeURIComponent(JSON.stringify(payload)), 7);
      this.saveSelectedIndex();
    } catch {}
  }

  private saveSelectedIndex(): void {
    try {
      this.setCookie(this.cookieKeySelectedIndex, String(this.selectedInvoiceIndex), 7);
    } catch {}
  }

  private restoreFromCookie(): void {
    try {
      const raw = this.getCookie(this.cookieKeyInvoices);
      if (raw) {
        const data = JSON.parse(decodeURIComponent(raw));
        this.pendingInvoices = Array.isArray(data?.pendingInvoices) ? data.pendingInvoices : [];
        this.discount = Number(data?.discount || 0);
        // Recalculate totals for safety
        this.pendingInvoices.forEach(inv => {
          inv.totalAmount = (inv.items || []).reduce((t: number, it: any) => t + (it.thanhTien || 0), 0);
        });
      }
      const sel = this.getCookie(this.cookieKeySelectedIndex);
      if (sel !== null && sel !== undefined) {
        const idx = Number(sel);
        this.selectedInvoiceIndex = Number.isFinite(idx) ? idx : (this.pendingInvoices.length ? 0 : -1);
      } else {
        this.selectedInvoiceIndex = this.pendingInvoices.length ? 0 : -1;
      }
    } catch {
      // ignore
    }
  }

  private setCookie(name: string, value: string, days: number): void {
    const expires = new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString();
    document.cookie = `${name}=${value}; expires=${expires}; path=/; SameSite=Lax`;
  }

  private getCookie(name: string): string | null {
    const nameEQ = name + '=';
    const ca = document.cookie.split(';');
    for (let c of ca) {
      while (c.charAt(0) === ' ') c = c.substring(1);
      if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length);
    }
    return null;
  }

  // ================= VOUCHER METHODS =================
  
  openVoucherModal(): void {
    this.showVoucherModal = true;
    this.loadAvailableVouchers();
  }

  closeVoucherModal(): void {
    console.log('🔒 Closing voucher modal - selectedVoucher before:', this.selectedVoucher);
    this.showVoucherModal = false;
    // Không reset selectedVoucher khi đóng modal, chỉ khi removeVoucher()
    console.log('🔒 Voucher modal closed - selectedVoucher after:', this.selectedVoucher);
  }
 closeQRTTModal(): void {
    this.showQrModal = false;
  }
  loadAvailableVouchers(): void {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getAuthToken()}`,
      'Content-Type': 'application/json'
    });

    this.http.get<Voucher[]>('http://localhost:8080/api/voucher/active', { headers }).subscribe({
      next: (vouchers) => {
        this.availableVouchers = vouchers.filter(v => 
          v.trangThai && 
          new Date(v.ngayBatDau) <= new Date() && 
          new Date(v.ngayKetThuc) >= new Date()
        );
        console.log('📋 Vouchers loaded:', this.availableVouchers);
      },
      error: (error) => {
        console.error('❌ Error loading vouchers:', error);
        this.availableVouchers = [];
      }
    });
  }

  selectVoucher(voucher: Voucher): void {
    console.log('🔍 selectVoucher called with:', voucher);
    
    // Kiểm tra điều kiện đơn tối thiểu ngay khi chọn
    if (this.selectedInvoice && voucher.donToiThieu > this.selectedInvoice.totalAmount) {
      alert(`Voucher ${voucher.maVoucher} yêu cầu đơn tối thiểu ${voucher.donToiThieu.toLocaleString('vi-VN')} VNĐ. Đơn hàng hiện tại: ${this.selectedInvoice.totalAmount.toLocaleString('vi-VN')} VNĐ`);
      return;
    }
    
    this.selectedVoucher = voucher;
    console.log('🎫 Selected voucher set to:', this.selectedVoucher);
  }

  applyVoucher(): void {
    console.log('applyVoucher called - selectedVoucher:', this.selectedVoucher);
    console.log('applyVoucher called - selectedInvoice:', this.selectedInvoice);
    
    if (!this.selectedVoucher || !this.selectedInvoice) {
      console.log('Cannot apply voucher - missing data');
      return;
    }

    const totalAmount = this.selectedInvoice.totalAmount;
    
    // Kiểm tra điều kiện đơn tối thiểu
    if (this.selectedVoucher.donToiThieu > totalAmount) {
      alert(`Voucher ${this.selectedVoucher.maVoucher} yêu cầu đơn tối thiểu ${this.selectedVoucher.donToiThieu.toLocaleString('vi-VN')} VNĐ`);
      // Reset voucher nếu không đáp ứng điều kiện
      this.selectedVoucher = null;
      this.discount = 0;
      console.log('Voucher removed due to minimum order requirement');
      return;
    }

    // Tính tiền giảm
    let discountAmount = 0;
    if (this.selectedVoucher.loaiGiam === 'PERCENT') {
      discountAmount = (totalAmount * this.selectedVoucher.giaTri) / 100;
      if (this.selectedVoucher.giamToiDa && discountAmount > this.selectedVoucher.giamToiDa) {
        discountAmount = this.selectedVoucher.giamToiDa;
      }
    } else if (this.selectedVoucher.loaiGiam === 'AMOUNT') {
      discountAmount = this.selectedVoucher.giaTri;
    }

    this.discount = discountAmount;
    this.closeVoucherModal();
    
    console.log('✅ Voucher applied:', {
      voucher: this.selectedVoucher?.maVoucher || 'Unknown',
      discount: discountAmount,
      total: totalAmount,
      final: totalAmount - discountAmount
    });
  }

  removeVoucher(): void {
    console.log('🗑️ Removing voucher:', this.selectedVoucher);
    this.selectedVoucher = null;
    this.discount = 0;
    console.log('🗑️ Voucher removed - selectedVoucher is now:', this.selectedVoucher);
  }
}
