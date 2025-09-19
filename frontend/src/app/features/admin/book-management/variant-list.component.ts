import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-variant-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
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
                <button class="btn btn-outline-primary btn-sm me-2" (click)="toggleFilter()" [class.active]="isFilterOpen">
                  <i class="bi bi-funnel me-1"></i>
                  Lọc
                  <span class="filter-badge" *ngIf="searchTerm || statusFilter !== 'all'">●</span>
                </button>
                <button class="btn btn-add-variant" (click)="addNewVariant()">
                  <i class="bi bi-plus-circle me-2"></i>
                  Thêm biến thể
                </button>
              </div>
            </div>
          </div>

          <!-- Filter Panel -->
          <div class="filter-panel" *ngIf="isFilterOpen">
            <div class="filter-content">
              <div class="row g-3">
                <div class="col-md-4">
                  <label class="form-label">
                    <i class="bi bi-search me-1"></i>
                    Tìm kiếm
                  </label>
                  <input 
                    type="text" 
                    class="form-control" 
                    placeholder="Mã SPCT, ISBN, thể loại, NXB..." 
                    [(ngModel)]="searchTerm"
                    (input)="onSearchChange()">
                </div>
                <div class="col-md-3">
                  <label class="form-label">
                    <i class="bi bi-funnel me-1"></i>
                    Trạng thái
                  </label>
                  <select class="form-select" [(ngModel)]="statusFilter" (change)="onStatusFilterChange()">
                    <option value="all">Tất cả</option>
                    <option value="active">Hoạt động</option>
                    <option value="inactive">Ngừng hoạt động</option>
                  </select>
                </div>
                <div class="col-md-3">
                  <label class="form-label">
                    <i class="bi bi-sort-alpha-down me-1"></i>
                    Sắp xếp theo
                  </label>
                  <select class="form-select" [(ngModel)]="sortBy" (change)="onSortChange()">
                    <option value="maSanPhamChiTiet">Mã SPCT</option>
                    <option value="isbn">ISBN</option>
                    <option value="theLoai">Thể loại</option>
                    <option value="nhaXuatBan">NXB</option>
                    <option value="donGia">Đơn giá</option>
                  </select>
                </div>
                <div class="col-md-2">
                  <label class="form-label">
                    <i class="bi bi-arrow-up-down me-1"></i>
                    Thứ tự
                  </label>
                  <select class="form-select" [(ngModel)]="sortOrder" (change)="onSortChange()">
                    <option value="asc">Tăng dần</option>
                    <option value="desc">Giảm dần</option>
                  </select>
                </div>
              </div>
              <div class="filter-actions mt-3">
                <button class="btn btn-outline-secondary btn-sm" (click)="clearFilters()">
                  <i class="bi bi-x-circle me-1"></i>
                  Xóa bộ lọc
                </button>
                <span class="filter-results ms-3">
                  Hiển thị {{ filteredVariants.length }} / {{ variants.length }} kết quả
                </span>
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

          <!-- No Results State -->
          <div class="empty-state" *ngIf="!filteredVariants.length && variants.length">
            <div class="empty-icon">
              <i class="bi bi-search"></i>
            </div>
            <h4>Không tìm thấy kết quả</h4>
            <p>Không có biến thể nào phù hợp với bộ lọc hiện tại</p>
            <button class="btn btn-outline-primary" (click)="clearFilters()">
              <i class="bi bi-x-circle me-2"></i>
              Xóa bộ lọc
            </button>
          </div>
          
          <!-- Variants Table -->
          <div class="table-container" *ngIf="filteredVariants.length">
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
                <tr *ngFor="let variant of filteredVariants; index as i" class="table-row" [class.even]="i % 2 === 0">
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

    <!-- Edit Variant Modal -->
    <div class="modal-overlay" *ngIf="isEditModalOpen" (click)="closeEditModal()">
      <div class="edit-modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>
            <i class="bi bi-pencil-square me-2"></i>
            Sửa thông tin biến thể
          </h3>
          <button class="btn-close-modal" (click)="closeEditModal()">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        
        <div class="modal-content">
          <form (ngSubmit)="saveVariant()" #editForm="ngForm">
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-tag me-1"></i>
                  Mã sản phẩm chi tiết
                </label>
                <input 
                  type="text" 
                  class="form-control" 
                  [(ngModel)]="editingVariant.maSanPhamChiTiet" 
                  name="maSanPhamChiTiet"
                  required>
              </div>
              
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-book me-1"></i>
                  ISBN
                </label>
                <input 
                  type="text" 
                  class="form-control" 
                  [(ngModel)]="editingVariant.isbn" 
                  name="isbn"
                  required>
              </div>
              
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-collection me-1"></i>
                  Thể loại
                </label>
                <select 
                  class="form-control" 
                  [(ngModel)]="editingVariant.theLoaiId" 
                  name="theLoaiId"
                  required>
                  <option value="">-- Chọn thể loại --</option>
                  <option *ngFor="let tl of theLoais" [value]="tl.id">{{ tl.tenTheLoai }}</option>
                </select>
              </div>
              
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-building me-1"></i>
                  Nhà xuất bản
                </label>
                <select 
                  class="form-control" 
                  [(ngModel)]="editingVariant.nhaXuatBanId" 
                  name="nhaXuatBanId"
                  required>
                  <option value="">-- Chọn nhà xuất bản --</option>
                  <option *ngFor="let nxb of nhaXuatBans" [value]="nxb.id">{{ nxb.tenNhaXuatBan }}</option>
                </select>
              </div>
              
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-currency-dollar me-1"></i>
                  Đơn giá (VNĐ)
                </label>
                <input 
                  type="number" 
                  class="form-control" 
                  [(ngModel)]="editingVariant.donGia" 
                  name="donGia"
                  min="0"
                  required>
              </div>
              
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-box me-1"></i>
                  Số lượng
                </label>
                <input 
                  type="number" 
                  class="form-control" 
                  [(ngModel)]="editingVariant.soLuong" 
                  name="soLuong"
                  min="0"
                  required>
              </div>
              
              <div class="col-md-6">
                <label class="form-label">
                  <i class="bi bi-calendar-event me-1"></i>
                  Ngày xuất bản
                </label>
                <input 
                  type="date" 
                  class="form-control" 
                  [(ngModel)]="editingVariant.ngayXuatBan" 
                  name="ngayXuatBan">
              </div>
              
              <div class="col-md-12">
                <label class="form-label">
                  <i class="bi bi-image me-1"></i>
                  Hình ảnh
                </label>
                <div class="image-upload-container">
                  <div class="current-image" *ngIf="editingVariant.hinhAnh && editingVariant.hinhAnh !== ''">
                    <img [src]="editingVariant.hinhAnh" alt="Hình ảnh hiện tại" class="preview-image">
                    <button type="button" class="btn btn-sm btn-outline-danger remove-image" (click)="removeImage()">
                      <i class="bi bi-x"></i>
                    </button>
                  </div>
                  <div class="upload-area" [class.has-image]="editingVariant.hinhAnh && editingVariant.hinhAnh !== ''">
                    <input 
                      type="file" 
                      class="form-control file-input" 
                      accept="image/*"
                      (change)="onImageSelected($event)"
                      #fileInput>
                    <div class="upload-placeholder" *ngIf="!editingVariant.hinhAnh || editingVariant.hinhAnh === ''">
                      <i class="bi bi-cloud-upload"></i>
                      <p>Chọn hình ảnh hoặc kéo thả vào đây</p>
                      <small>Hỗ trợ: JPG, PNG, GIF (tối đa 5MB)</small>
                    </div>
                    <div class="upload-info" *ngIf="editingVariant.hinhAnh && editingVariant.hinhAnh !== ''">
                      <i class="bi bi-check-circle text-success"></i>
                      <p>Hình ảnh đã chọn</p>
                      <button type="button" class="btn btn-sm btn-outline-primary" (click)="fileInput.click()">
                        <i class="bi bi-arrow-clockwise me-1"></i>
                        Thay đổi
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="col-md-12">
                <div class="form-check">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    [(ngModel)]="editingVariant.trangThai" 
                    name="trangThai"
                    id="trangThai">
                  <label class="form-check-label" for="trangThai">
                    <i class="bi bi-check-circle me-1"></i>
                    Trạng thái hoạt động
                  </label>
                </div>
              </div>
            </div>
          </form>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" (click)="closeEditModal()">
            <i class="bi bi-x-circle me-2"></i>
            Hủy
          </button>
          <button class="btn btn-primary" (click)="saveVariant()" [disabled]="!editForm.form.valid">
            <i class="bi bi-check-circle me-2"></i>
            Lưu thay đổi
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

    // Filter Panel
    .filter-panel {
      background: #f8fafc;
      border-top: 1px solid #e2e8f0;
      padding: 1.5rem;
      animation: slideDown 0.3s ease;
    }

    .filter-content {
      .form-label {
        font-weight: 600;
        color: #4a5568;
        margin-bottom: 0.5rem;
        display: flex;
        align-items: center;
      }

      .form-control, .form-select {
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        padding: 0.75rem;
        transition: all 0.2s ease;

        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }

    .filter-actions {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-top: 1rem;
      border-top: 1px solid #e2e8f0;
    }

    .filter-results {
      color: #718096;
      font-size: 0.9rem;
      font-weight: 500;
    }

    .filter-badge {
      color: #f56565;
      font-size: 0.8rem;
      margin-left: 0.25rem;
    }

    .btn-outline-primary.active {
      background: #667eea;
      color: white;
      border-color: #667eea;
    }

    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
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

    /* Edit Modal */
    .edit-modal {
      background: white;
      border-radius: 20px;
      max-width: 800px;
      width: 90%;
      max-height: 90vh;
      overflow: hidden;
      box-shadow: 0 20px 25px rgba(0, 0, 0, 0.1);
      animation: slideUp 0.3s ease;
    }

    .edit-modal .modal-content {
      padding: 2rem;
      max-height: 60vh;
      overflow-y: auto;
    }

    .edit-modal .form-label {
      font-weight: 600;
      color: #4a5568;
      margin-bottom: 0.5rem;
      display: flex;
      align-items: center;
    }

    .edit-modal .form-control {
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 0.75rem;
      transition: all 0.2s ease;
    }

    .edit-modal .form-control:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .edit-modal .form-check {
      padding: 1rem;
      background: #f8fafc;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
    }

    .edit-modal .form-check-input {
      margin-right: 0.75rem;
    }

    .edit-modal .form-check-label {
      font-weight: 500;
      color: #4a5568;
    }

    .edit-modal .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      color: white;
      border-radius: 10px;
      font-weight: 500;
      padding: 0.75rem 1.5rem;
      transition: all 0.2s ease;
    }

    .edit-modal .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }

    .edit-modal .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    /* Image Upload Styles */
    .image-upload-container {
      border: 2px dashed #e2e8f0;
      border-radius: 12px;
      padding: 1.5rem;
      background: #f8fafc;
      transition: all 0.3s ease;
    }

    .image-upload-container:hover {
      border-color: #667eea;
      background: #f0f4ff;
    }

    .current-image {
      position: relative;
      display: inline-block;
      margin-bottom: 1rem;
    }

    .preview-image {
      width: 120px;
      height: 120px;
      object-fit: cover;
      border-radius: 8px;
      border: 2px solid #e2e8f0;
    }

    .remove-image {
      position: absolute;
      top: -8px;
      right: -8px;
      width: 24px;
      height: 24px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0;
    }

    .upload-area {
      position: relative;
      text-align: center;
    }

    .file-input {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      opacity: 0;
      cursor: pointer;
    }

    .upload-placeholder {
      padding: 2rem;
      color: #718096;
    }

    .upload-placeholder i {
      font-size: 2rem;
      margin-bottom: 1rem;
      color: #a0aec0;
    }

    .upload-placeholder p {
      margin-bottom: 0.5rem;
      font-weight: 500;
    }

    .upload-placeholder small {
      color: #a0aec0;
    }

    .upload-info {
      padding: 1rem;
      color: #2f855a;
    }

    .upload-info i {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
    }

    .upload-info p {
      margin-bottom: 1rem;
      font-weight: 500;
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
  filteredVariants: any[] = [];

  // Filter states
  isFilterOpen = false;
  searchTerm = '';
  statusFilter = 'all'; // 'all', 'active', 'inactive'
  sortBy = 'maSanPhamChiTiet'; // 'maSanPhamChiTiet', 'isbn', 'theLoai', 'nhaXuatBan', 'donGia'
  sortOrder = 'asc'; // 'asc', 'desc'

  // QR Modal
  isQrOpen: boolean = false;
  qrSrc: string | null = null;
  selectedVariant: any = null;

  // Edit Modal
  isEditModalOpen: boolean = false;
  editingVariant: any = {};

  // Data for dropdowns
  theLoais: any[] = [];
  nhaXuatBans: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProductDetails();
    this.loadVariants();
    this.loadDropdownData();
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
        this.applyFilters();
      },
      (error) => {
        console.error('Error loading variants:', error);
      }
    );
  }

  loadDropdownData() {
    // Load thể loại
    this.http.get<any>('http://localhost:8080/api/the-loai?size=1000').subscribe({
      next: (response) => {
        this.theLoais = response.content || [];
        console.log('Loaded thể loại:', this.theLoais);
      },
      error: (error) => {
        console.error('Error loading thể loại:', error);
        this.theLoais = [];
      }
    });

    // Load nhà xuất bản
    this.http.get<any>('http://localhost:8080/api/nha-xuat-ban?size=1000').subscribe({
      next: (response) => {
        this.nhaXuatBans = response.content || [];
        console.log('Loaded nhà xuất bản:', this.nhaXuatBans);
      },
      error: (error) => {
        console.error('Error loading nhà xuất bản:', error);
        this.nhaXuatBans = [];
      }
    });
  }

  // Filter methods
  toggleFilter() {
    this.isFilterOpen = !this.isFilterOpen;
  }

  applyFilters() {
    let filtered = [...this.variants];

    // Search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(variant => 
        variant.maSanPhamChiTiet?.toLowerCase().includes(term) ||
        variant.isbn?.toLowerCase().includes(term) ||
        variant.theLoai?.toLowerCase().includes(term) ||
        variant.nhaXuatBan?.toLowerCase().includes(term)
      );
    }

    // Status filter
    if (this.statusFilter !== 'all') {
      const isActive = this.statusFilter === 'active';
      filtered = filtered.filter(variant => variant.trangThai === isActive);
    }

    // Sort
    filtered.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (this.sortBy) {
        case 'maSanPhamChiTiet':
          aValue = a.maSanPhamChiTiet || '';
          bValue = b.maSanPhamChiTiet || '';
          break;
        case 'isbn':
          aValue = a.isbn || '';
          bValue = b.isbn || '';
          break;
        case 'theLoai':
          aValue = a.theLoai || '';
          bValue = b.theLoai || '';
          break;
        case 'nhaXuatBan':
          aValue = a.nhaXuatBan || '';
          bValue = b.nhaXuatBan || '';
          break;
        case 'donGia':
          aValue = parseFloat(a.donGia) || 0;
          bValue = parseFloat(b.donGia) || 0;
          break;
        default:
          aValue = a.maSanPhamChiTiet || '';
          bValue = b.maSanPhamChiTiet || '';
      }

      if (aValue < bValue) return this.sortOrder === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortOrder === 'asc' ? 1 : -1;
      return 0;
    });

    this.filteredVariants = filtered;
  }

  onSearchChange() {
    this.applyFilters();
  }

  onStatusFilterChange() {
    this.applyFilters();
  }

  onSortChange() {
    this.applyFilters();
  }

  clearFilters() {
    this.searchTerm = '';
    this.statusFilter = 'all';
    this.sortBy = 'maSanPhamChiTiet';
    this.sortOrder = 'asc';
    this.applyFilters();
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
    // Copy variant data to editing object
    this.editingVariant = { ...variant };
    
    // Đảm bảo dữ liệu dropdown đã được load
    if (this.theLoais.length === 0 || this.nhaXuatBans.length === 0) {
      this.loadDropdownData();
      // Đợi một chút để dữ liệu được load
      setTimeout(() => {
        this.mapVariantToDropdowns(variant);
        this.isEditModalOpen = true;
      }, 100);
    } else {
      this.mapVariantToDropdowns(variant);
      this.isEditModalOpen = true;
    }
  }

  private mapVariantToDropdowns(variant: any) {
    // Map từ tên sang ID cho dropdowns
    if (variant.theLoai && Array.isArray(this.theLoais)) {
      const theLoai = this.theLoais.find(tl => tl.tenTheLoai === variant.theLoai);
      this.editingVariant.theLoaiId = theLoai ? theLoai.id : '';
    } else {
      this.editingVariant.theLoaiId = '';
    }
    
    if (variant.nhaXuatBan && Array.isArray(this.nhaXuatBans)) {
      const nxb = this.nhaXuatBans.find(n => n.tenNhaXuatBan === variant.nhaXuatBan);
      this.editingVariant.nhaXuatBanId = nxb ? nxb.id : '';
    } else {
      this.editingVariant.nhaXuatBanId = '';
    }
    
    console.log('Editing variant:', this.editingVariant);
    console.log('Variant theLoai name:', variant.theLoai);
    console.log('Variant nhaXuatBan name:', variant.nhaXuatBan);
    console.log('Available theLoais:', this.theLoais);
    console.log('Available nhaXuatBans:', this.nhaXuatBans);
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.editingVariant = {};
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        alert('Vui lòng chọn file hình ảnh!');
        return;
      }

      // Validate file size (5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('Kích thước file không được vượt quá 5MB!');
        return;
      }

      // Upload file to server
      const formData = new FormData();
      formData.append('file', file);

      this.http.post('http://localhost:8080/api/books/upload-image', formData, {
        responseType: 'text' // Đảm bảo nhận response dạng text
      }).subscribe(
        (response: any) => {
          console.log('Image uploaded successfully:', response);
          // Set the filename as the image URL
          this.editingVariant.hinhAnh = `http://localhost:8080/uploads/${response}`;
        },
        (error) => {
          console.error('Error uploading image:', error);
          console.error('Error response:', error.error);
          alert('Có lỗi xảy ra khi upload ảnh: ' + (error.error || error.message));
        }
      );
    } else {
      // Nếu không chọn file mới, giữ nguyên ảnh cũ
      console.log('No file selected, keeping current image');
    }
  }

  removeImage() {
    this.editingVariant.hinhAnh = '';
  }

  saveVariant() {
    if (!this.editingVariant.id) return;

    console.log('Saving variant:', this.editingVariant);

    // Map ID sang tên cho API
    let theLoaiName = '';
    let nhaXuatBanName = '';
    
    if (this.editingVariant.theLoaiId && Array.isArray(this.theLoais)) {
      const theLoai = this.theLoais.find(tl => tl.id == this.editingVariant.theLoaiId);
      theLoaiName = theLoai ? theLoai.tenTheLoai : '';
    }
    
    if (this.editingVariant.nhaXuatBanId && Array.isArray(this.nhaXuatBans)) {
      const nxb = this.nhaXuatBans.find(n => n.id == this.editingVariant.nhaXuatBanId);
      nhaXuatBanName = nxb ? nxb.tenNhaXuatBan : '';
    }

    // Prepare data for API - gửi đúng cấu trúc BookDetailDTO
    const updateData: any = {
      id: this.editingVariant.id,
      isbn: this.editingVariant.isbn,
      maSanPhamChiTiet: this.editingVariant.maSanPhamChiTiet,
      theLoai: theLoaiName,
      nhaXuatBan: nhaXuatBanName,
      soTrang: this.editingVariant.soTrang,
      soLanTaiBan: this.editingVariant.soLanTaiBan,
      khoiLuongTinh: this.editingVariant.khoiLuongTinh,
      soLuong: this.editingVariant.soLuong,
      ngayXuatBan: this.editingVariant.ngayXuatBan,
      donGia: this.editingVariant.donGia,
      moTa: this.editingVariant.moTa,
      trangThai: this.editingVariant.trangThai
    };

    // Xử lý ảnh - gửi tên file nếu có thay đổi
    if (this.editingVariant.hinhAnh && this.editingVariant.hinhAnh !== '') {
      // Chỉ gửi tên file, không gửi full URL
      const imageUrl = this.editingVariant.hinhAnh;
      if (imageUrl.includes('/uploads/')) {
        const filename = imageUrl.split('/uploads/')[1];
        updateData.hinhAnh = filename;
      } else {
        updateData.hinhAnh = imageUrl;
      }
    }

    console.log('Update data to send:', updateData);

    // Gửi request với headers đúng
    const headers = {
      'Content-Type': 'application/json'
    };

    this.http.put(`http://localhost:8080/api/books/${this.editingVariant.id}`, updateData, { headers }).subscribe(
      (response: any) => {
        console.log('Update response:', response);
        
        // Reload variants from server to get updated data
        this.loadVariants();
        
        this.closeEditModal();
        alert('Cập nhật biến thể thành công!');
      },
      (error) => {
        console.error('Error updating variant:', error);
        console.error('Error details:', error.error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        alert('Có lỗi xảy ra khi cập nhật biến thể: ' + (error.error?.message || error.message));
      }
    );
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