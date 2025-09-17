import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-variant-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <!-- Header Section -->
    <div class="variants-hero">
      <div class="container-fluid">
        <div class="row align-items-center">
          <div class="col-md-8">
            <div class="breadcrumb-nav">
              <a routerLink="/admin/products/book" class="breadcrumb-link">
                <i class="bi bi-arrow-left me-2"></i>
                Quản lý sách
              </a>
              <span class="breadcrumb-separator">/</span>
              <span class="breadcrumb-current">Danh sách biến thể</span>
            </div>
            <h1 class="hero-title">
              <i class="bi bi-layers-fill me-3"></i>
              Danh sách biến thể
            </h1>
            <p class="hero-subtitle">Quản lý các biến thể của sản phẩm {{ productName }}</p>
          </div>
          <div class="col-md-4 text-end">
            <button class="btn btn-add-variant" (click)="addNewVariant()">
              <i class="bi bi-plus-lg me-2"></i>
              Thêm biến thể mới
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="variants-content">
      <div class="container-fluid">
        <!-- Product Info Card -->
        <div class="product-info-card mb-4">
          <div class="row">
            <div class="col-md-9">
              <div class="product-details">
                <h2 class="product-title">{{ productName || 'Chưa có tên sản phẩm' }}</h2>
                <div class="product-meta">
                  <span class="product-code">
                    <i class="bi bi-tag-fill me-2"></i>
                    {{ productCode || 'Chưa có mã' }}
                  </span>
                  <span class="product-status" [class.active]="productStatus">
                    <i class="bi" [class.bi-check-circle-fill]="productStatus" [class.bi-x-circle-fill]="!productStatus"></i>
                    {{ productStatus ? 'Hoạt động' : 'Ngừng hoạt động' }}
                  </span>
                </div>
                <div class="product-description-container">
                  <p class="product-description-label">Mô tả:</p>
                  <p class="product-description">{{ productDescription || 'Chưa có mô tả cho sản phẩm này' }}</p>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="product-actions">
                <button class="btn btn-edit-product" (click)="editProduct()">
                  <i class="bi bi-pencil-square me-2"></i>
                  Sửa thông tin sản phẩm
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Variants Table Card -->
        <div class="data-card">
          <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">
              <h5 class="card-title">
                <i class="bi bi-table me-2"></i>
                Danh sách biến thể
              </h5>
              <div class="card-actions">
                <button class="btn btn-outline-primary btn-sm me-2">
                  <i class="bi bi-funnel me-1"></i>
                  Lọc
                </button>
                <button class="btn btn-add-variant" (click)="addNewVariant()">
                  <i class="bi bi-plus-circle me-2"></i>
                  Thêm biến thể
                </button>
              </div>
            </div>
          </div>
          
          <!-- Empty State -->
          <div class="empty-state" *ngIf="!variants.length">
            <div class="empty-icon">
              <i class="bi bi-inbox"></i>
            </div>
            <h4>Chưa có biến thể nào</h4>
            <p>Thêm biến thể đầu tiên bằng nút "Thêm biến thể mới"</p>
          </div>
          
          <!-- Variants Table -->
          <div class="table-container" *ngIf="variants.length">
            <table class="modern-table">
              <thead>
                <tr>
                  <th>Mã SPCT</th>
                  <th>ISBN</th>
                  <th>Thể loại</th>
                  <th>NXB</th>
                  <th>Số lượng</th>
                  <th>Đơn giá</th>
                  <th>Hình ảnh</th>
                  <th>Trạng thái</th>
                  <th class="text-center">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let variant of variants; index as i" class="table-row" [class.even]="i % 2 === 0">
                  <td>{{ variant.maSanPhamChiTiet }}</td>
                  <td>{{ variant.isbn }}</td>
                  <td>{{ variant.theLoai }}</td>
                  <td>{{ variant.nhaXuatBan }}</td>
                  <td>{{ variant.soLuong }}</td>
                  <td>{{ variant.donGia | currency:'VND':'symbol':'1.0-0' }}</td>
                  <td>
                    <div class="variant-image">
                      <img *ngIf="variant.hinhAnh" [src]="variant.hinhAnh" alt="Hình ảnh sản phẩm" />
                      <div *ngIf="!variant.hinhAnh" class="no-image">
                        <i class="bi bi-image"></i>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span class="status-badge" [class.active]="variant.trangThai" [class.inactive]="!variant.trangThai">
                      <i class="bi" [class.bi-check-circle-fill]="variant.trangThai" [class.bi-x-circle-fill]="!variant.trangThai"></i>
                      {{ variant.trangThai ? 'Hoạt động' : 'Ngừng hoạt động' }}
                    </span>
                  </td>
                  <td class="text-center">
                    <div class="action-buttons">
                      <button class="btn btn-action qr" (click)="openQr(variant)" title="Xem mã QR">
                        <i class="bi bi-qr-code"></i>
                      </button>
                      <button class="btn btn-action edit" (click)="editVariant(variant)" title="Sửa biến thể">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-action toggle-status" (click)="toggleStatus(variant)" [title]="'Đổi trạng thái ' + variant.maSanPhamChiTiet">
                        <i class="bi" [class.bi-toggle-on]="variant.trangThai" [class.bi-toggle-off]="!variant.trangThai"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- QR Modal -->
    <div class="modal-overlay" *ngIf="isQrOpen" (click)="closeQr()">
      <div class="qr-modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>
            <i class="bi bi-qr-code me-2"></i>
            Mã QR biến thể
          </h3>
          <button class="btn-close-modal" (click)="closeQr()">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        
        <div class="modal-content">
          <div class="qr-container">
            <div class="qr-frame">
              <img *ngIf="qrSrc" [src]="qrSrc" alt="QR Code" class="qr-image" />
              <div class="qr-loading" *ngIf="!qrSrc">
                <div class="spinner"></div>
                <p>Đang tải mã QR...</p>
              </div>
            </div>
            
            <div class="qr-info">
              <h4>Thông tin biến thể</h4>
              <ul class="qr-details">
                <li><strong>Mã SPCT:</strong> {{ selectedVariant?.maSanPhamChiTiet }}</li>
                <li><strong>ISBN:</strong> {{ selectedVariant?.isbn }}</li>
                <li><strong>Thể loại:</strong> {{ selectedVariant?.theLoai }}</li>
                <li><strong>NXB:</strong> {{ selectedVariant?.nhaXuatBan }}</li>
                <li><strong>Đơn giá:</strong> {{ selectedVariant?.donGia | currency:'VND':'symbol':'1.0-0' }}</li>
              </ul>
              <p class="mt-3">Quét mã QR để xem chi tiết biến thể sản phẩm</p>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <a *ngIf="qrSrc" [href]="qrSrc" download="qr-code.png" class="btn btn-gradient-success">
            <i class="bi bi-download me-2"></i>
            Tải xuống PNG
          </a>
          <button class="btn btn-print" onclick="window.print()">
            <i class="bi bi-printer me-2"></i>
            In mã QR
          </button>
          <button class="btn btn-secondary" (click)="closeQr()">
            <i class="bi bi-x-circle me-2"></i>
            Đóng
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .variants-hero {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 3rem 0;
      margin-bottom: 2rem;
      position: relative;
      overflow: hidden;
      color: white;
    }
    
    .hero-title {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: 0.5rem;
    }
    
    .hero-subtitle {
      opacity: 0.9;
      margin-bottom: 0;
    }
    
    .breadcrumb-nav {
      margin-bottom: 1rem;
      display: flex;
      align-items: center;
    }
    
    .breadcrumb-link {
      color: rgba(255, 255, 255, 0.9);
      text-decoration: none;
      display: flex;
      align-items: center;
    }
    
    .breadcrumb-separator {
      margin: 0 0.5rem;
      color: rgba(255, 255, 255, 0.6);
    }
    
    .breadcrumb-current {
      font-weight: 600;
    }
    
    .btn-add-variant {
      background: linear-gradient(135deg, #48bb78, #38a169);
      color: white;
      border: none;
      border-radius: 12px;
      padding: 0.75rem 1.5rem;
      font-weight: 600;
      transition: all 0.3s ease;
      box-shadow: 0 4px 15px rgba(72, 187, 120, 0.3);
    }
    
    .btn-add-variant:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(72, 187, 120, 0.5);
      color: white;
    }
    
    .product-info-card {
      background: white;
      border-radius: 20px;
      padding: 2rem;
      box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      border: 1px solid #e2e8f0;
    }
    
    .product-title {
      font-size: 1.8rem;
      font-weight: 700;
      margin-bottom: 1rem;
      color: #2d3748;
    }
    
    .product-meta {
      display: flex;
      gap: 1.5rem;
      margin-bottom: 1rem;
    }
    
    .product-code {
      display: inline-flex;
      align-items: center;
      padding: 0.5rem 1rem;
      background: #edf2f7;
      border-radius: 10px;
      font-weight: 600;
      color: #4a5568;
    }
    
    .product-status {
      display: inline-flex;
      align-items: center;
      padding: 0.5rem 1rem;
      border-radius: 10px;
      font-weight: 600;
    }
    
    .product-status.active {
      background: #c6f6d5;
      color: #2f855a;
    }
    
    .product-status:not(.active) {
      background: #fed7d7;
      color: #c53030;
    }
    
    .product-status i {
      margin-right: 0.5rem;
    }
    
    .product-description-container {
      margin-top: 1rem;
    }
    
    .product-description-label {
      font-weight: 600;
      color: #4a5568;
      margin-bottom: 0.25rem;
    }
    
    .product-description {
      color: #718096;
      margin-bottom: 0;
      background: #f7fafc;
      padding: 0.75rem;
      border-radius: 8px;
      border-left: 4px solid #4299e1;
    }
    
    
    
    .product-actions {
      display: flex;
      justify-content: flex-end;
      height: 100%;
      align-items: center;
    }
    
    .btn-edit-product {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;
      border: none;
      border-radius: 12px;
      padding: 0.75rem 1.5rem;
      font-weight: 600;
      transition: all 0.3s ease;
    }
    
    .btn-edit-product:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(79, 172, 254, 0.4);
    }
    
    .btn-toggle-status {
      background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
      color: white;
      border: none;
      border-radius: 12px;
      padding: 0.75rem 1.5rem;
      font-weight: 600;
      transition: all 0.3s ease;
    }
    
    .btn-toggle-status:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(237, 137, 54, 0.4);
    }
    
    .data-card {
      background: white;
      border-radius: 20px;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
      overflow: hidden;
    }
    
    .card-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 2rem;
      border-bottom: none;
      color: white;
    }
    
    .card-title {
      font-weight: 700;
      margin-bottom: 0;
      font-size: 1.4rem;
    }
    
    .modern-table {
      width: 100%;
      margin-bottom: 0;
      border-collapse: separate;
      border-spacing: 0;
    }
    
    .modern-table thead th {
      background: #f8fafc;
      border: none;
      padding: 1.2rem 1rem;
      font-weight: 700;
      color: #475569;
    }
    
    .modern-table tbody .table-row {
      transition: all 0.3s ease;
      border-bottom: 1px solid #f1f5f9;
    }
    
    .modern-table tbody .table-row:hover {
      background: #f1f5f9;
    }
    
    .modern-table tbody .table-row td {
      padding: 1.2rem 1rem;
      vertical-align: middle;
      border: none;
    }
    
    .variant-image {
      width: 60px;
      height: 60px;
      border-radius: 10px;
      overflow: hidden;
      background: #f1f5f9;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .variant-image img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .variant-image .no-image {
      color: #a0aec0;
      font-size: 1.5rem;
    }
    
    .status-badge {
      display: inline-flex;
      align-items: center;
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-weight: 600;
      font-size: 0.8rem;
    }
    
    .status-badge.active {
      background: #c6f6d5;
      color: #2f855a;
    }
    
    .status-badge.inactive {
      background: #fed7d7;
      color: #c53030;
    }
    
    .status-badge i {
      margin-right: 0.5rem;
    }
    
    .action-buttons {
      display: flex;
      gap: 0.5rem;
      justify-content: center;
    }
    
    .btn-action {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;
    }
    
    .btn-action.qr {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }
    
    .btn-action.qr:hover {
      transform: translateY(-3px) scale(1.05);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
    }

    .btn-action.edit {
      background: #4299e1;
      color: white;
    }
    
    .btn-action.toggle-status {
      background: #ed8936;
      color: white;
    }
    
    .btn-action:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .empty-state {
      padding: 4rem 2rem;
      text-align: center;
    }
    
    .empty-icon {
      font-size: 4rem;
      color: #cbd5e0;
      margin-bottom: 1rem;
    }
    
    .empty-state h4 {
      font-weight: 600;
      margin-bottom: 0.5rem;
    }
    
    .empty-state p {
      color: #718096;
    }

    /* QR Modal */
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      backdrop-filter: blur(4px);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1050;
      animation: fadeIn 0.3s ease;
    }

    .qr-modal {
      background: white;
      border-radius: 20px;
      max-width: 800px;
      width: 90%;
      max-height: 90vh;
      overflow: hidden;
      box-shadow: 0 20px 25px rgba(0, 0, 0, 0.1);
      animation: slideUp 0.3s ease;
    }

    .modal-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 1.5rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .modal-header h3 {
      margin: 0;
      font-weight: 600;
    }

    .btn-close-modal {
      background: rgba(255, 255, 255, 0.2);
      border: none;
      color: white;
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s ease;
    }

    .btn-close-modal:hover {
      background: rgba(255, 255, 255, 0.3);
      transform: scale(1.1);
    }

    .modal-content {
      padding: 2rem;
    }

    .qr-container {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 2rem;
      align-items: center;
    }

    .qr-frame {
      background: linear-gradient(135deg, #f7fafc, #edf2f7);
      padding: 2rem;
      border-radius: 16px;
      text-align: center;
      position: relative;
    }

    .qr-image {
      max-width: 100%;
      height: auto;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .qr-loading {
      padding: 2rem;
    }

    .qr-loading .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #e2e8f0;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1rem;
    }

    .qr-info {
      padding: 1rem;
    }

    .qr-info h4 {
      color: #2d3748;
      margin-bottom: 1.5rem;
      font-weight: 600;
    }

    .qr-details {
      list-style: none;
      padding: 0;
      margin: 0 0 1.5rem 0;
    }

    .qr-details li {
      padding: 0.75rem 0;
      border-bottom: 1px solid #e2e8f0;
      display: flex;
      justify-content: space-between;
    }

    .qr-details li:last-child {
      border-bottom: none;
    }

    .modal-footer {
      background: #f8fafc;
      padding: 1.5rem;
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
    }

    .modal-footer .btn {
      border-radius: 10px;
      font-weight: 500;
      padding: 0.75rem 1.5rem;
      transition: all 0.2s ease;
    }

    .btn-gradient-success {
      background: linear-gradient(135deg, #48bb78, #38a169);
      border: none;
      color: white;
    }

    .btn-gradient-success:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(72, 187, 120, 0.4);
      color: white;
    }

    .btn-print {
      background: linear-gradient(135deg, #fa709a, #fee140);
      border: none;
      color: white;
    }

    .btn-print:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(250, 112, 154, 0.4);
      color: white;
    }

    .btn-secondary {
      background: #e2e8f0;
      border: none;
      color: #2d3748;
    }

    .btn-secondary:hover {
      background: #cbd5e0;
      transform: translateY(-2px);
    }

    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }

    @keyframes slideUp {
      from { opacity: 0; transform: translateY(30px); }
      to { opacity: 1; transform: translateY(0); }
    }

    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    @media (max-width: 768px) {
      .qr-container {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class VariantListComponent implements OnInit {
  productId: number = 0;
  productName: string = '';
  productCode: string = '';
  productDescription: string = '';
  productStatus: boolean = true;
  variants: any[] = [];

  // QR Modal
  isQrOpen: boolean = false;
  qrSrc: string | null = null;
  selectedVariant: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProductDetails();
    this.loadVariants();
  }

  loadProductDetails() {
    this.http.get<any>(`http://localhost:8080/api/san-pham/${this.productId}`).subscribe({
      next: (product) => {
        this.productName = product.tenSanPham || '';
        this.productCode = product.maSanPham || '';
        this.productDescription = product.moTa || '';
        this.productStatus = product.trangThai || false;
      },
      error: () => {
        this.productName = '';
        this.productCode = '';
        this.productDescription = '';
      }
    });
  }

  loadVariants() {
    this.http.get<any[]>(`http://localhost:8080/api/books/by-product/${this.productId}`).subscribe(
      (data) => {
        this.variants = data;
      },
      (error) => {
        console.error('Error loading variants:', error);
      }
    );
  }
  
  editProduct() {
    // Hiển thị modal sửa sản phẩm
    const newName = prompt('Nhập tên sản phẩm mới:', this.productName);
    if (newName && newName.trim() !== '') {
      const newDesc = prompt('Nhập mô tả mới:', this.productDescription || '');
      
      const product = {
        tenSanPham: newName.trim(),
        moTa: newDesc?.trim(),
        trangThai: this.productStatus
      };
      
      this.http.put(`http://localhost:8080/api/san-pham/${this.productId}`, product).subscribe({
        next: (result: any) => {
          this.productName = result.tenSanPham;
          this.productDescription = result.moTa;
          console.log('✅ Đã cập nhật thông tin sản phẩm');
        },
        error: (error) => {
          console.error('❌ Lỗi khi cập nhật sản phẩm:', error);
          alert('Cập nhật thông tin sản phẩm thất bại! Vui lòng thử lại.');
        }
      });
    }
  }
  
  // Đã xóa phương thức toggleProductStatus() vì không cần thiết

  addNewVariant() {
    this.router.navigate(['/admin/products/book', this.productId, 'variants', 'add']);
  }

  editVariant(variant: any) {
    this.router.navigate(['/admin/products/book', this.productId, 'variants', 'edit', variant.id]);
  }

  toggleStatus(variant: any) {
    const newStatus = !variant.trangThai;
    const action = newStatus ? 'chuyển sang Hoạt động' : 'chuyển sang Ngừng hoạt động';
    
    if (confirm(`Bạn có chắc chắn muốn ${action} biến thể "${variant.maSanPhamChiTiet}"?`)) {
      this.http.patch(`http://localhost:8080/api/books/${variant.id}/status`, { trangThai: newStatus }).subscribe(
        () => {
          variant.trangThai = newStatus;
          console.log(`✅ Đã ${action} biến thể:`, variant.maSanPhamChiTiet);
        },
        (error) => {
          console.error('Error updating status:', error);
          alert(`Lỗi khi đổi trạng thái biến thể! Vui lòng thử lại.`);
        }
      );
    }
  }

  openQr(variant: any) {
    this.selectedVariant = variant;
    this.qrSrc = `http://localhost:8080/api/books/${variant.id}/qr`;
    this.isQrOpen = true;
  }

  closeQr() {
    this.isQrOpen = false;
    this.qrSrc = null;
    this.selectedVariant = null;
  }
}