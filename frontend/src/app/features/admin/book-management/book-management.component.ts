import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ProductBookService } from '../product-book/product-book.service';
import { Page } from '../product-book/product-book.page';
import { ProductBook } from '../product-book/product-book.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-book-management',
  standalone:true,
  imports: [CommonModule, FormsModule],
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
  uploadingImage = false;
  selectedTaiBanId: number | undefined;
  
  // New product form with variant F1 data
  newProduct: any = {
    // Product info
    tenSanPham: '',
    moTa: '',
    trangThai: true,
    
    // Variant F1 info - sẽ được tạo trong resetForm()
    isbn: '',
    maSanPhamChiTiet: '',
    soTrang: null,
    donGia: null,
    soLuong: null,
    ngayXuatBan: '',
    taiBanIds: undefined, // Mặc định không có tái bản
    theLoaiId: undefined,
    nhaXuatBanId: undefined,
    kichThuocId: undefined,
    loaiBiaId: undefined,
    loaiGiayId: undefined,
    khoiLuongTinh: null,
    moTaBienThe: '',
    tacGiaIds: undefined,
    chuDeIds: undefined,
    hinhAnh: null,
    hinhAnhFilename: null
  };

  // Dropdown data
  theLoais: any[] = [];
  nhaXuatBans: any[] = [];
  kichThuocs: any[] = [];
  loaiBiaList: any[] = [];
  loaiGiayList: any[] = [];
  tacGias: any[] = [];
  chuDes: any[] = [];
  taiBans: any[] = [];

  // Math helper for template
  Math = Math;

  constructor(
    private service: ProductBookService, 
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
    this.loadDropdownData();
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
    this.resetForm(); // Tự động tạo ISBN và mã SPCT
    this.isAddModalOpen = true;
  }
  
  closeAddModal() {
    this.isAddModalOpen = false;
  }

  loadDropdownData() {
    // Load thể loại
    this.http.get('http://localhost:8080/api/the-loai?size=1000').subscribe((response: any) => {
      this.theLoais = response.content || [];
    });

    // Load nhà xuất bản
    this.http.get('http://localhost:8080/api/nha-xuat-ban?size=1000').subscribe((response: any) => {
      this.nhaXuatBans = response.content || [];
    });

    // Load kích thước
    this.http.get('http://localhost:8080/api/kich-thuoc?size=1000').subscribe((response: any) => {
      this.kichThuocs = response.content || [];
    });

    // Load loại bìa
    this.http.get('http://localhost:8080/api/loai-bia?size=1000').subscribe((response: any) => {
      this.loaiBiaList = response.content || [];
    });

    // Load loại giấy
    this.http.get('http://localhost:8080/api/loai-giay?size=1000').subscribe((response: any) => {
      this.loaiGiayList = response.content || [];
    });

    // Load tác giả
    this.http.get('http://localhost:8080/api/tac-gia?size=1000').subscribe((response: any) => {
      this.tacGias = response.content || [];
    });

    // Load chủ đề
    this.http.get('http://localhost:8080/api/chu-de?size=1000').subscribe((response: any) => {
      this.chuDes = response.content || [];
    });

    // Load tái bản
    this.loadTaiBans();
  }

  // Load tái bản từ server
  loadTaiBans(): void {
    this.http.get('http://localhost:8080/api/tai-ban').subscribe({
      next: (response: any) => {
        console.log('Loaded tái bản:', response);
        this.taiBans = Array.isArray(response) ? response : [];
      },
      error: (error) => {
        console.error('Error loading tái bản:', error);
        this.taiBans = [];
      }
    });
  }

  // Quick add helpers
  private genCode(prefix: string): string {
    const ts = Date.now() % 1000000;
    const rnd = Math.floor(Math.random() * 100);
    return `${prefix}${ts}${rnd.toString().padStart(2,'0')}`;
  }

  quickAddTheLoai() {
    const name = prompt('Nhập tên thể loại mới');
    if (!name || !name.trim()) return;
    const body: any = { tenTheLoai: name.trim(), maTheLoai: this.genCode('TL'), trangThai: true };
    this.http.post('http://localhost:8080/api/the-loai', body).subscribe({
      next: (res: any) => {
        // Ưu tiên dùng id từ response nếu có
        const newId = res?.id;
        this.http.get('http://localhost:8080/api/the-loai?size=1000').subscribe((r: any) => {
          this.theLoais = r.content || [];
          if (newId) {
            this.newProduct.theLoaiId = newId;
          } else {
            const found = this.theLoais.find((x: any) => x.tenTheLoai?.trim() === name.trim());
            if (found) this.newProduct.theLoaiId = found.id;
          }
        });
      },
      error: (err) => {
        console.error('Thêm thể loại thất bại', err);
        alert('Thêm thể loại thất bại');
      }
    });
  }

  quickAddNxb() {
    const tenNhaXuatBan = prompt('Nhập tên nhà xuất bản:');
    if (!tenNhaXuatBan || !tenNhaXuatBan.trim()) return;
    
    const truSoChinh = prompt('Nhập trụ sở chính (tùy chọn):');
    const moTa = prompt('Nhập mô tả (tùy chọn):');
    
    const body: any = { 
      tenNhaXuatBan: tenNhaXuatBan.trim(), 
      maNhaXuatBan: this.genCode('NXB'), 
      truSoChinh: truSoChinh?.trim() || null,
      moTa: moTa?.trim() || null,
      trangThai: true 
    };
    
    this.http.post('http://localhost:8080/api/nha-xuat-ban', body).subscribe({
      next: (res: any) => {
        const newId = res?.id;
        this.http.get('http://localhost:8080/api/nha-xuat-ban?size=1000').subscribe((r: any) => {
          this.nhaXuatBans = r.content || [];
          if (newId) this.newProduct.nhaXuatBanId = newId; else {
            const found = this.nhaXuatBans.find((x: any) => x.tenNhaXuatBan?.trim() === tenNhaXuatBan.trim());
            if (found) this.newProduct.nhaXuatBanId = found.id;
          }
        });
        alert('Đã thêm nhà xuất bản mới');
      },
      error: (err) => { 
        console.error('Thêm NXB thất bại', err); 
        alert('Thêm NXB thất bại'); 
      }
    });
  }

  quickAddKichThuoc() {
    const value = prompt('Nhập chỉ số kích thước (ví dụ 14x20cm)');
    if (!value || !value.trim()) return;
    const body: any = { chiSoKichThuoc: value.trim(), maKichThuoc: this.genCode('KT'), trangThai: true };
    this.http.post('http://localhost:8080/api/kich-thuoc', body).subscribe({
      next: (res: any) => {
        const newId = res?.id;
        this.http.get('http://localhost:8080/api/kich-thuoc?size=1000').subscribe((r: any) => {
          this.kichThuocs = r.content || [];
          if (newId) this.newProduct.kichThuocId = newId; else {
            const found = this.kichThuocs.find((x: any) => (x.chiSoKichThuoc || x.name)?.trim() === value.trim());
            if (found) this.newProduct.kichThuocId = found.id;
          }
        });
      },
      error: (err) => { console.error('Thêm kích thước thất bại', err); alert('Thêm kích thước thất bại'); }
    });
  }

  quickAddLoaiBia() {
    const tenBia = prompt('Nhập tên loại bìa:');
    if (!tenBia || !tenBia.trim()) return;
    
    const mauSac = prompt('Nhập màu sắc bìa (tùy chọn):');
    
    const body: any = { 
      tenBia: tenBia.trim(), 
      maBia: this.genCode('LB'), 
      mauSac: mauSac?.trim() || null,
      trangThai: true 
    };
    
    this.http.post('http://localhost:8080/api/loai-bia', body).subscribe({
      next: (res: any) => {
        const newId = res?.id;
        this.http.get('http://localhost:8080/api/loai-bia?size=1000').subscribe((r: any) => {
          this.loaiBiaList = r.content || [];
          if (newId) this.newProduct.loaiBiaId = newId; else {
            const found = this.loaiBiaList.find((x: any) => (x.tenBia || x.name)?.trim() === tenBia.trim());
            if (found) this.newProduct.loaiBiaId = found.id;
          }
        });
        alert('Đã thêm loại bìa mới');
      },
      error: (err) => { 
        console.error('Thêm loại bìa thất bại', err); 
        alert('Thêm loại bìa thất bại'); 
      }
    });
  }

  quickAddLoaiGiay() {
    const tenGiay = prompt('Nhập tên loại giấy:');
    if (!tenGiay || !tenGiay.trim()) return;
    
    const mauSac = prompt('Nhập màu sắc giấy (tùy chọn):');
    
    const body: any = { 
      tenGiay: tenGiay.trim(), 
      maGiay: this.genCode('LG'), 
      mauSac: mauSac?.trim() || null,
      trangThai: true 
    };
    
    this.http.post('http://localhost:8080/api/loai-giay', body).subscribe({
      next: (res: any) => {
        const newId = res?.id;
        this.http.get('http://localhost:8080/api/loai-giay?size=1000').subscribe((r: any) => {
          this.loaiGiayList = r.content || [];
          if (newId) this.newProduct.loaiGiayId = newId; else {
            const found = this.loaiGiayList.find((x: any) => (x.tenGiay || x.name)?.trim() === tenGiay.trim());
            if (found) this.newProduct.loaiGiayId = found.id;
          }
        });
        alert('Đã thêm loại giấy mới');
      },
      error: (err) => { 
        console.error('Thêm loại giấy thất bại', err); 
        alert('Thêm loại giấy thất bại'); 
      }
    });
  }
  
  resetForm() {
    // Tự động tạo mã SPCT và ISBN
    const timestamp = Date.now();
    const randomNum = Math.floor(Math.random() * 1000);
    
    this.newProduct = {
      // Product info
      tenSanPham: '',
      moTa: '',
      trangThai: true,
      
      // Variant F1 info - tự động tạo
      isbn: '', // Người dùng nhập thủ công
      maSanPhamChiTiet: this.generateSPCT(),
      soTrang: null,
      donGia: null,
      soLuong: null,
      ngayXuatBan: '',
      taiBanIds: [], // Mặc định không có tái bản
      theLoaiId: undefined,
      nhaXuatBanId: undefined,
      kichThuocId: undefined,
      loaiBiaId: undefined,
      loaiGiayId: undefined,
      khoiLuongTinh: null,
      moTaBienThe: '',
      tacGiaIds: undefined,
      chuDeIds: undefined,
      hinhAnh: null,
      hinhAnhFilename: null
    };
  }

  private generateISBN(): string {
    // Tạo ISBN 13 số theo format: 978-0-XXXXX-X-X (tổng 13 ký tự)
    const prefix = '978';
    const group = '0';
    const publisher = Math.floor(Math.random() * 100000).toString().padStart(5, '0'); // Giảm xuống 5 số
    const title = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    
    // Tính check digit (đơn giản hóa)
    const checkDigit = Math.floor(Math.random() * 10);
    
    return `${prefix}${group}${publisher}${title}${checkDigit}`;
  }

  private generateSPCT(): string {
    // Tạo mã SPCT theo format: SPCT + YYYYMMDD + XXX
    const now = new Date();
    const year = now.getFullYear();
    const month = (now.getMonth() + 1).toString().padStart(2, '0');
    const day = now.getDate().toString().padStart(2, '0');
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    
    return `SPCT${year}${month}${day}${random}`;
  }

  regenerateISBN() {
    this.newProduct.isbn = this.generateISBN();
  }

  regenerateSPCT() {
    this.newProduct.maSanPhamChiTiet = this.generateSPCT();
  }
  
  saveProduct() {
    // Validate required fields
    if (!this.newProduct.tenSanPham?.trim()) {
      alert('Vui lòng nhập tên sản phẩm!');
      return;
    }
    
    // ISBN và mã SPCT đã được tự động tạo, không cần validate
    
    if (!this.newProduct.donGia || this.newProduct.donGia <= 0) {
      alert('Vui lòng nhập đơn giá hợp lệ!');
      return;
    }
    
    if (!this.newProduct.soLuong || this.newProduct.soLuong <= 0) {
      alert('Vui lòng nhập số lượng hợp lệ!');
      return;
    }
    
    if (!this.newProduct.theLoaiId) {
      alert('Vui lòng chọn thể loại!');
      return;
    }
    
    if (!this.newProduct.nhaXuatBanId) {
      alert('Vui lòng chọn nhà xuất bản!');
      return;
    }
    
    // Prepare data for API
    const productData = {
      // Product info
      tenSanPham: this.newProduct.tenSanPham.trim(),
      moTa: this.newProduct.moTa?.trim() || '',
      trangThai: this.newProduct.trangThai,
      
      // Variant F1 info
      isbn: this.newProduct.isbn.trim(),
      maSanPhamChiTiet: this.newProduct.maSanPhamChiTiet.trim(),
      soTrang: this.newProduct.soTrang,
      donGia: this.newProduct.donGia,
      soLuong: this.newProduct.soLuong,
      ngayXuatBan: this.newProduct.ngayXuatBan || null,
      taiBanIds: this.newProduct.taiBanIds ? [this.newProduct.taiBanIds] : [],
      theLoaiId: this.newProduct.theLoaiId,
      nhaXuatBanId: this.newProduct.nhaXuatBanId,
      kichThuocId: this.newProduct.kichThuocId || null,
      loaiBiaId: this.newProduct.loaiBiaId || null,
      loaiGiayId: this.newProduct.loaiGiayId || null,
      khoiLuongTinh: this.newProduct.khoiLuongTinh || null,
      moTaBienThe: this.newProduct.moTaBienThe?.trim() || '',
      tacGiaIds: this.newProduct.tacGiaIds ? [this.newProduct.tacGiaIds] : [],
      chuDeIds: this.newProduct.chuDeIds ? [this.newProduct.chuDeIds] : [],
      hinhAnh: this.newProduct.hinhAnhFilename
    };
    
    console.log('Sending product data:', productData);
    
    // Call API to create product with variant F1
    this.http.post('http://localhost:8080/api/books/create-with-variant', productData).subscribe({
      next: (result) => {
        console.log('✅ Đã thêm sản phẩm mới với biến thể F1:', result);
        this.loadPage(0); // Reload first page to show new product
        this.closeAddModal();
        alert('Thêm sản phẩm và biến thể F1 thành công!');
      },
      error: (error) => {
        console.error('❌ Lỗi khi thêm sản phẩm:', error);
        alert('Thêm sản phẩm thất bại! Vui lòng thử lại.');
      }
    });
  }

  // Image handling methods
  onImageSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        alert('Vui lòng chọn file hình ảnh hợp lệ!');
        return;
      }

      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('Kích thước file không được vượt quá 5MB!');
        return;
      }

      this.uploadingImage = true;

      // Show preview immediately
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.newProduct.hinhAnh = e.target.result; // For preview
      };
      reader.readAsDataURL(file);

      // Upload to server
      const formData = new FormData();
      formData.append('image', file);

      this.http.post('http://localhost:8080/api/books/upload-image', formData, {
        responseType: 'text'
      }).subscribe({
        next: (filename: string) => {
          console.log('Image uploaded successfully:', filename);
          // Store the filename for API call
          this.newProduct.hinhAnhFilename = filename;
          this.uploadingImage = false;
        },
        error: (error) => {
          console.error('Error uploading image:', error);
          alert('Lỗi khi upload hình ảnh: ' + (error.error?.message || error.message));
          // Reset preview on error
          this.newProduct.hinhAnh = null;
          this.newProduct.hinhAnhFilename = null;
          this.uploadingImage = false;
        }
      });
    }
  }

  removeImage(): void {
    this.newProduct.hinhAnh = null;
    this.newProduct.hinhAnhFilename = null;
  }

  // Quick add methods for new attributes
  quickAddTacGia(): void {
    const tenTacGia = prompt('Nhập tên tác giả:');
    if (tenTacGia && tenTacGia.trim()) {
      const newTacGia = {
        tenTacGia: tenTacGia.trim(),
        moTa: '',
        trangThai: true
      };
      
      this.http.post('http://localhost:8080/api/tac-gia', newTacGia).subscribe({
        next: (result: any) => {
          this.tacGias.push(result);
          this.newProduct.tacGiaIds = result.id;
          alert('Thêm tác giả thành công!');
        },
        error: (error) => {
          console.error('Lỗi khi thêm tác giả:', error);
          alert('Thêm tác giả thất bại!');
        }
      });
    }
  }

  quickAddChuDe(): void {
    const tenChuDe = prompt('Nhập tên chủ đề:');
    if (tenChuDe && tenChuDe.trim()) {
      const newChuDe = {
        tenChuDe: tenChuDe.trim(),
        moTa: '',
        trangThai: true
      };
      
      this.http.post('http://localhost:8080/api/chu-de', newChuDe).subscribe({
        next: (result: any) => {
          this.chuDes.push(result);
          this.newProduct.chuDeIds = result.id;
          alert('Thêm chủ đề thành công!');
        },
        error: (error) => {
          console.error('Lỗi khi thêm chủ đề:', error);
          alert('Thêm chủ đề thất bại!');
        }
      });
    }
  }


  quickAddTaiBan(): void {
    const lanTaiBan = prompt('Nhập lần tái bản (số):');
    const namTaiBan = prompt('Nhập năm tái bản:');
    
    if (lanTaiBan && namTaiBan && lanTaiBan.trim() && namTaiBan.trim()) {
      const newTaiBan = { 
        lanTaiBan: parseInt(lanTaiBan.trim()), 
        namTaiBan: parseInt(namTaiBan.trim()) 
      };
      this.http.post('http://localhost:8080/api/tai-ban', newTaiBan).subscribe({
        next: (response: any) => {
          console.log('Tái bản được tạo:', response);
          // Reload danh sách tái bản từ server
          this.loadTaiBans();
          alert('Thêm tái bản thành công!');
        },
        error: (error) => {
          console.error('Error adding tái bản:', error);
          alert('Lỗi khi thêm tái bản: ' + (error.error?.message || error.message));
        }
      });
    }
  }

}
