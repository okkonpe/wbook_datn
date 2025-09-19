import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductBookService } from '../product-book/product-book.service';
import { Page } from '../product-book/product-book.page';
import { ProductBook } from '../product-book/product-book.model';
import { FilterPipe } from '../../../shared/pipes/filter.pipe';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-book-management',
  standalone:true,
  imports: [CommonModule, FilterPipe, FormsModule],
  templateUrl: './book-management.component.html',
  styleUrl: './book-management.component.scss'
})
export class BookManagementComponent implements OnInit {
  productBooks: ProductBook[] = [];
  filteredBooks: ProductBook[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  // Filter states
  isFilterOpen = false;
  searchTerm = '';
  statusFilter = 'all'; // 'all', 'active', 'inactive'
  sortBy = 'tenSanPham'; // 'tenSanPham', 'ngayTao', 'maSanPham'
  sortOrder = 'asc'; // 'asc', 'desc'

  // Modal states
  isQrOpen = false;
  qrSrc: string | null = null;
  isAddModalOpen = false;
  
  // New product form
  newProduct: ProductBook = {
    tenSanPham: '',
    moTa: '',
    trangThai: true
  };

  // Math helper for template
  Math = Math;

  constructor(private service: ProductBookService, private router: Router) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number) {
    this.service.getAllPaging(page, this.pageSize).subscribe((res: Page<ProductBook>) => {
      this.productBooks = res.content ?? [];
      this.applyFilters();
      this.totalPages = res.totalPages ?? 0;
      this.currentPage = page;
    });
  }

  // Filter methods
  toggleFilter() {
    this.isFilterOpen = !this.isFilterOpen;
  }

  applyFilters() {
    let filtered = [...this.productBooks];

    // Search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(book => 
        book.tenSanPham?.toLowerCase().includes(term) ||
        book.maSanPham?.toLowerCase().includes(term) ||
        book.moTa?.toLowerCase().includes(term)
      );
    }

    // Status filter
    if (this.statusFilter !== 'all') {
      const isActive = this.statusFilter === 'active';
      filtered = filtered.filter(book => book.trangThai === isActive);
    }

    // Sort
    filtered.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (this.sortBy) {
        case 'tenSanPham':
          aValue = a.tenSanPham || '';
          bValue = b.tenSanPham || '';
          break;
        case 'maSanPham':
          aValue = a.maSanPham || '';
          bValue = b.maSanPham || '';
          break;
        case 'ngayTao':
          aValue = new Date(a.ngayTao || '');
          bValue = new Date(b.ngayTao || '');
          break;
        default:
          aValue = a.tenSanPham || '';
          bValue = b.tenSanPham || '';
      }

      if (aValue < bValue) return this.sortOrder === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortOrder === 'asc' ? 1 : -1;
      return 0;
    });

    this.filteredBooks = filtered;
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
    this.sortBy = 'tenSanPham';
    this.sortOrder = 'asc';
    this.applyFilters();
  }

  prevPage() { if (this.currentPage > 0) { this.loadPage(this.currentPage - 1); } }
  nextPage() { if (this.currentPage + 1 < this.totalPages) { this.loadPage(this.currentPage + 1); } }

  openQr(pb: ProductBook) {
    if (!pb.id) return;
    this.qrSrc = `http://localhost:8080/api/san-pham/${pb.id}/qr`;
    this.isQrOpen = true;
  }
  closeQr() { this.isQrOpen = false; this.qrSrc = null; }

  goToVariants(pb: ProductBook) {
    if (!pb.id) return;
    this.router.navigate(['/admin/products/book', pb.id, 'variants']);
  }

  toggleStatus(pb: ProductBook) {
    if (!pb.id) return;
    
    const newStatus = !pb.trangThai;
    const action = newStatus ? 'chuyển sang Hoạt động' : 'chuyển sang Ngừng hoạt động';
    
    if (confirm(`Bạn có chắc chắn muốn ${action} sản phẩm "${pb.tenSanPham}"?`)) {
      this.service.updateStatus(pb.id, newStatus).subscribe({
        next: () => {
          pb.trangThai = newStatus;
          console.log(`✅ Đã ${action} sản phẩm:`, pb.tenSanPham);
        },
        error: (error) => {
          console.error('Error updating status:', error);
          alert(`Lỗi khi đổi trạng thái sản phẩm! Vui lòng thử lại.`);
        }
      });
    }
  }

  addProduct() {
    this.resetForm();
    this.isAddModalOpen = true;
  }
  
  closeAddModal() {
    this.isAddModalOpen = false;
  }
  
  resetForm() {
    this.newProduct = {
      tenSanPham: '',
      moTa: '',
      trangThai: true
    };
  }
  
  saveProduct() {
    if (!this.newProduct.tenSanPham?.trim()) {
      alert('Vui lòng nhập tên sản phẩm!');
      return;
    }
    
    this.service.create(this.newProduct).subscribe({
      next: (result) => {
        console.log('✅ Đã thêm sản phẩm mới:', result);
        this.loadPage(0); // Reload first page to show new product
        this.closeAddModal();
      },
      error: (error) => {
        console.error('❌ Lỗi khi thêm sản phẩm:', error);
        alert('Thêm sản phẩm thất bại! Vui lòng thử lại.');
      }
    });
  }
}
