import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

interface VariantDraft {
  tenSanPham?: string;
  isbn?: string;
  maSanPhamChiTiet?: string;
  theLoai?: string;
  soTrang?: number;
  taiBanIds?: number;
  nhaXuatBan?: string;
  kichThuoc?: string;
  loaiBia?: string;
  loaiGiay?: string;
  khoiLuongTinh?: number;
  soLuong?: number;
  ngayXuatBan?: string;
  hinhAnh?: string | null; // filename sau upload
  donGia?: number;
  moTa?: string;
  trangThai?: boolean;
  tacGiaIds?: number;
  chuDeIds?: number;
}

@Component({
  selector: 'app-variants',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './variants.component.html',
  styleUrls: ['./variants.component.scss']
})
export class VariantsComponent {
  productId!: number;
  newDraft: VariantDraft = { trangThai: true };
  uploadingImage = false;
  saving = false;

  theLoais: any[] = [];
  nhaXuatBans: any[] = [];
  kichThuocs: any[] = [];
  loaiBiaList: any[] = [];
  loaiGiayList: any[] = [];
  tacGias: any[] = [];
  chuDes: any[] = [];
  taiBans: any[] = [];
  

  // F1 base
  private baseVariant: any | null = null;
  inheritMode = false; // bật khi đã kế thừa từ F1
  hasF1Data = false; // kiểm tra xem có dữ liệu F1 không


  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    console.log('Product ID:', this.productId);
  }


  // Khóa/mở các trường khi kế thừa từ F1
  isDisabled(field: string): boolean {
    if (!this.inheritMode) return false;
    
    // Các trường kế thừa từ F1 và không cho sửa
    const inheritedFromF1 = new Set([
      'tacGiaIds', 'theLoai','chuDe'
    ]);
    
    // Chỉ disable các trường kế thừa từ F1 khi đã có dữ liệu F1 và có giá trị thực
    if (inheritedFromF1.has(field)) {
      if (!this.hasF1Data || !this.baseVariant) return false;
      
      // Kiểm tra từng trường có giá trị thực hay không
      switch (field) {
        case 'khoiLuongTinh':
          return this.baseVariant.khoiLuongTinh != null && this.baseVariant.khoiLuongTinh !== undefined;
        case 'soTrang':
          return this.baseVariant.soTrang != null && this.baseVariant.soTrang !== undefined;
        case 'taiBanIds':
          return this.baseVariant.taiBans && Array.isArray(this.baseVariant.taiBans) && this.baseVariant.taiBans.length > 0;
        case 'tacGiaIds':
          return this.baseVariant.tacGiaIds && Array.isArray(this.baseVariant.tacGiaIds) && this.baseVariant.tacGiaIds.length > 0;
        case 'chuDeIds':
          return this.baseVariant.chuDeIds && Array.isArray(this.baseVariant.chuDeIds) && this.baseVariant.chuDeIds.length > 0;
        case 'isbn':
          return this.baseVariant.isbn != null && this.baseVariant.isbn !== '';
        default:
          return true;
      }
    }
    
    // Các trường có thể chỉnh sửa
    const editable = new Set([
      'loaiGiay', 'loaiBia', 'nxb', 'kichThuoc', 'ngayXuatBan', 'donGia', 'soLuong', 'theLoai', 'moTa', 'hinhAnh'
    ]);
    
    return !editable.has(field);
  }

  saveVariant() {
    // Validation
    if (!this.newDraft.isbn?.trim() || !this.newDraft.maSanPhamChiTiet?.trim()) {
      // Tự sinh nếu thiếu
      if (!this.newDraft.isbn) {
        this.newDraft.isbn = this.generateIsbn();
      }
      if (!this.newDraft.maSanPhamChiTiet) {
        this.newDraft.maSanPhamChiTiet = this.generateSpct();
      }
    }
    
    if (!this.newDraft.donGia || this.newDraft.donGia <= 0) {
      alert('Vui lòng nhập đơn giá hợp lệ!');
      return;
    }
    
    if (!this.newDraft.soLuong || this.newDraft.soLuong <= 0) {
      alert('Vui lòng nhập số lượng hợp lệ!');
      return;
    }

    this.saving = true;
    const payload = { 
      sanPhamId: this.productId,
      isbn: (this.newDraft.isbn || '').toString(),
      maSanPhamChiTiet: (this.newDraft.maSanPhamChiTiet || '').toString(),
      theLoaiId: this.toId(this.newDraft.theLoai, this.theLoais),
      nhaXuatBanId: this.toId(this.newDraft.nhaXuatBan, this.nhaXuatBans),
      kichThuocId: this.toId(this.newDraft.kichThuoc, this.kichThuocs),
      loaiBiaId: this.toId(this.newDraft.loaiBia, this.loaiBiaList),
      loaiGiayId: this.toId(this.newDraft.loaiGiay, this.loaiGiayList),
      soTrang: this.newDraft.soTrang ?? 0,
      taiBanIds: this.newDraft.taiBanIds ? [this.newDraft.taiBanIds] : [],
      tacGiaIds: this.newDraft.tacGiaIds ? [this.newDraft.tacGiaIds] : [],
      chuDeIds: this.newDraft.chuDeIds ? [this.newDraft.chuDeIds] : [],
      khoiLuongTinh: this.newDraft.khoiLuongTinh ?? 0,
      soLuong: this.newDraft.soLuong ?? 0,
      ngayXuatBan: this.newDraft.ngayXuatBan as any,
      hinhAnh: this.newDraft.hinhAnh,
      donGia: this.newDraft.donGia ?? 0,
      moTa: this.newDraft.moTa,
      trangThai: this.newDraft.trangThai
    };

    console.log('Payload single variant:', payload);
    
    this.http.post('http://localhost:8080/api/books/bulk-create', [payload], {
      headers: { 'Content-Type': 'application/json' }
    })
      .subscribe({
        next: () => {
          this.saving = false;
          alert('Lưu biến thể thành công!');
          // Chuyển về trang danh sách biến thể
          this.router.navigate(['/admin/products/book', this.productId, 'variants']);
        },
        error: (error) => {
          console.error('Save failed:', error);
          alert(`Lưu thất bại! Lỗi: ${error.error?.message || error.message || 'Không xác định'}`);
          this.saving = false;
        }
      });
  }


  resetForm() {
    // Khởi tạo theo F1 nếu có
    const base = this.baseVariant;
    this.newDraft = { trangThai: true };
    // ISBN kế thừa từ F1, SPCT tự tạo
    this.newDraft.isbn = base?.isbn || this.generateIsbn();
    this.newDraft.maSanPhamChiTiet = this.generateSpct();

    if (base) {
      console.log('=== DEBUG: Dữ liệu F1 base ===', base);
      console.log('taiBans:', base.taiBans);
      console.log('tacGiaIds:', base.tacGiaIds);
      console.log('chuDeIds:', base.chuDeIds);
      console.log('tacGia (names):', base.tacGia);
      console.log('chuDe (names):', base.chuDe);
      
      // Copy các thuộc tính kế thừa từ F1
      this.newDraft.donGia = base.donGia ?? this.newDraft.donGia;
      this.newDraft.soLuong = base.soLuong ?? 1;
      this.newDraft.ngayXuatBan = base.ngayXuatBan ?? this.newDraft.ngayXuatBan;
      
      // Chỉ gán khoiLuongTinh nếu có giá trị thực
      if (base.khoiLuongTinh != null && base.khoiLuongTinh !== undefined) {
        this.newDraft.khoiLuongTinh = base.khoiLuongTinh;
      }
      
      // Xử lý tái bản từ Set<TaiBanDTO>
      if (base.taiBans && Array.isArray(base.taiBans) && base.taiBans.length > 0) {
        this.newDraft.taiBanIds = base.taiBans[0].id;
      }
      
      // Chỉ gán soTrang nếu có giá trị thực
      if (base.soTrang != null && base.soTrang !== undefined) {
        this.newDraft.soTrang = base.soTrang;
      }
      this.newDraft.moTa = '';

      // Copy các trường mới từ F1 (kế thừa và không cho sửa)
      if (base.tacGiaIds && Array.isArray(base.tacGiaIds) && base.tacGiaIds.length > 0) {
        this.newDraft.tacGiaIds = base.tacGiaIds[0];
      }
      
      if (base.chuDeIds && Array.isArray(base.chuDeIds) && base.chuDeIds.length > 0) {
        this.newDraft.chuDeIds = base.chuDeIds[0];
      }
      
      this.newDraft.hinhAnh = base.hinhAnh || null;

      // Map tên -> id cho các dropdown nếu có
      const tl = this.theLoais.find(t => t.tenTheLoai === base.theLoai);
      const nxb = this.nhaXuatBans.find(n => n.tenNhaXuatBan === base.nhaXuatBan);
      const kt = this.kichThuocs.find(k => (k.chiSoKichThuoc || k.name) === base.kichThuoc);
      const lb = this.loaiBiaList.find(b => (b.tenBia || b.name) === base.loaiBia);
      const lg = this.loaiGiayList.find(g => (g.tenGiay || g.name) === base.loaiGiay);

      this.newDraft.theLoai = tl?.id;
      this.newDraft.nhaXuatBan = nxb?.id;
      this.newDraft.kichThuoc = kt?.id;
      this.newDraft.loaiBia = lb?.id;
      this.newDraft.loaiGiay = lg?.id;

      this.inheritMode = true;
      
      console.log('=== DEBUG: Sau khi gán giá trị ===');
      console.log('newDraft.taiBanIds:', this.newDraft.taiBanIds);
      console.log('newDraft.tacGiaIds:', this.newDraft.tacGiaIds);
      console.log('newDraft.chuDeIds:', this.newDraft.chuDeIds);
      console.log('newDraft.khoiLuongTinh:', this.newDraft.khoiLuongTinh);
    }
  }




  private toId(val: any, list: any[]): number | undefined {
    if (val == null) return undefined;
    if (typeof val === 'number') return val;
    // Nếu bound nhầm tên -> tìm theo tên
    const item = list?.find((x: any) => x.tenTheLoai === val || x.tenNhaXuatBan === val || x.chiSoKichThuoc === val || x.tenBia === val || x.tenGiay === val);
    return item?.id;
  }


  ngOnInit(): void {
    // Nạp danh mục thuộc tính
    this.fetchAttributes();
    // Tải F1 và áp thuộc tính kế thừa
    this.fetchBaseVariant();
  }

  private fetchBaseVariant() {
    if (!this.productId) return;
    this.http.get<any[]>(`http://localhost:8080/api/books/by-product/${this.productId}`)
      .subscribe({
        next: (list) => {
          if (Array.isArray(list) && list.length) {
            this.baseVariant = list[0]; // F1 là phần tử đầu tiên
            this.hasF1Data = true;
            // Nếu đã có danh mục, áp ngay; nếu chưa, sẽ áp khi danh mục nạp xong
            this.applyBaseIfReady();
          } else {
            this.hasF1Data = false;
            this.baseVariant = null;
          }
        },
        error: (err) => {
          console.error('Không lấy được F1:', err);
          this.hasF1Data = false;
          this.baseVariant = null;
        }
      });
  }

  private applyBaseIfReady() {
    if (!this.baseVariant) return;
    // Chỉ áp khi đã có danh mục để map tên -> id
    if (!this.theLoais.length || !this.nhaXuatBans.length || !this.tacGias.length || !this.chuDes.length) return;
    this.resetForm();
  }

  private fetchAttributes() {
    this.http.get<any>('http://localhost:8080/api/the-loai')
      .subscribe(res => { this.theLoais = this.toArray(res); this.applyBaseIfReady(); });
    this.http.get<any>('http://localhost:8080/api/nha-xuat-ban')
      .subscribe(res => { this.nhaXuatBans = this.toArray(res); this.applyBaseIfReady(); });
    this.http.get<any>('http://localhost:8080/api/kich-thuoc')
      .subscribe(res => { this.kichThuocs = this.toArray(res); this.applyBaseIfReady(); });
    this.http.get<any>('http://localhost:8080/api/loai-bia')
      .subscribe(res => { this.loaiBiaList = this.toArray(res); this.applyBaseIfReady(); });
    this.http.get<any>('http://localhost:8080/api/loai-giay')
      .subscribe(res => { this.loaiGiayList = this.toArray(res); this.applyBaseIfReady(); });
    this.http.get<any>('http://localhost:8080/api/tac-gia')
      .subscribe(res => { this.tacGias = this.toArray(res); this.applyBaseIfReady(); });
    this.http.get<any>('http://localhost:8080/api/chu-de')
      .subscribe(res => { this.chuDes = this.toArray(res); this.applyBaseIfReady(); });
    this.loadTaiBans();
  }

  // Load tái bản từ server
  loadTaiBans(): void {
    this.http.get<any>('http://localhost:8080/api/tai-ban').subscribe({
      next: (response: any) => {
        console.log('Loaded tái bản in variants:', response);
        this.taiBans = Array.isArray(response) ? response : [];
      },
      error: (error) => {
        console.error('Error loading tái bản in variants:', error);
        this.taiBans = [];
      }
    });
  }

  private generateSpct(): string {
    const rand = Math.floor(Math.random() * 9000) + 1000; 
    return `SPCT${this.productId}${rand}`;
  }

  private generateIsbn(): string {
    const base = '978';
    let body = '';
    for (let i = 0; i < 9; i++) body += Math.floor(Math.random() * 10);
    const provisional = base + body + Math.floor(Math.random() * 10);
    return provisional;
  }

  private generateIsbnFromF1(f1Isbn: string): string {
    // Gen ISBN dựa trên F1: thay đổi 2-3 chữ số cuối
    if (!f1Isbn || f1Isbn.length < 13) {
      return this.generateIsbn();
    }
    
    const base = f1Isbn.substring(0, 10); // Giữ 10 chữ số đầu
    const randomSuffix = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
    return base + randomSuffix;
  }


  // Quick add tái bản
  quickAddTaiBan(): void {
    const lanTaiBan = prompt('Nhập lần tái bản (số):');
    
    if (lanTaiBan  && lanTaiBan.trim() ) {
      const newTaiBan = { 
        lanTaiBan: parseInt(lanTaiBan.trim()), 
      };
      this.http.post('http://localhost:8080/api/tai-ban', newTaiBan).subscribe({
        next: (response: any) => {
          console.log('Tái bản được tạo trong variants:', response);
          // Reload danh sách tái bản từ server
          this.loadTaiBans();
          alert('Thêm tái bản thành công!');
        },
        error: (error) => {
          console.error('Error adding tái bản in variants:', error);
          alert('Lỗi khi thêm tái bản: ' + (error.error?.message || error.message));
        }
      });
    }
  }

  // Quick add kích thước
  quickAddKichThuoc(): void {
    const chiSoKichThuoc = prompt('Nhập chỉ số kích thước (ví dụ: 14x20cm):');
    if (!chiSoKichThuoc || !chiSoKichThuoc.trim()) return;
    
    const newKichThuoc = { 
      chiSoKichThuoc: chiSoKichThuoc.trim(),
      trangThai: true 
    };
    this.http.post('http://localhost:8080/api/kich-thuoc', newKichThuoc).subscribe({
      next: (response: any) => {
        console.log('Kích thước được tạo:', response);
        this.fetchAttributes();
        alert('Thêm kích thước thành công!');
      },
      error: (error) => {
        console.error('Error adding kích thước:', error);
        alert('Lỗi khi thêm kích thước: ' + (error.error?.message || error.message));
      }
    });
  }

  // Quick add loại bìa
  quickAddLoaiBia(): void {
    const tenBia = prompt('Nhập tên loại bìa:');
    if (!tenBia || !tenBia.trim()) return;
    
    const newLoaiBia = { 
      tenBia: tenBia.trim(),
      trangThai: true 
    };
    this.http.post('http://localhost:8080/api/loai-bia', newLoaiBia).subscribe({
      next: (response: any) => {
        console.log('Loại bìa được tạo:', response);
        this.fetchAttributes();
        alert('Thêm loại bìa thành công!');
      },
      error: (error) => {
        console.error('Error adding loại bìa:', error);
        alert('Lỗi khi thêm loại bìa: ' + (error.error?.message || error.message));
      }
    });
  }

  // Quick add loại giấy
  quickAddLoaiGiay(): void {
    const tenGiay = prompt('Nhập tên loại giấy:');
    if (!tenGiay || !tenGiay.trim()) return;
    
    const mauSac = prompt('Nhập màu sắc (tùy chọn):');
    
    const newLoaiGiay = { 
      tenGiay: tenGiay.trim(),
      mauSac: mauSac?.trim() || null,
      trangThai: true 
    };
    this.http.post('http://localhost:8080/api/loai-giay', newLoaiGiay).subscribe({
      next: (response: any) => {
        console.log('Loại giấy được tạo:', response);
        this.fetchAttributes();
        alert('Thêm loại giấy thành công!');
      },
      error: (error) => {
        console.error('Error adding loại giấy:', error);
        alert('Lỗi khi thêm loại giấy: ' + (error.error?.message || error.message));
      }
    });
  }

  // Set current date
  setCurrentDate(): void {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    this.newDraft.ngayXuatBan = `${year}-${month}-${day}`;
  }

  // Quick add thể loại
  quickAddTheLoai(): void {
    const tenTheLoai = prompt('Nhập tên thể loại:');
    if (!tenTheLoai || !tenTheLoai.trim()) return;
    
    const newTheLoai = { 
      tenTheLoai: tenTheLoai.trim(),
      trangThai: true 
    };
    this.http.post('http://localhost:8080/api/the-loai', newTheLoai).subscribe({
      next: (response: any) => {
        console.log('Thể loại được tạo:', response);
        this.fetchAttributes();
        alert('Thêm thể loại thành công!');
      },
      error: (error) => {
        console.error('Error adding thể loại:', error);
        alert('Lỗi khi thêm thể loại: ' + (error.error?.message || error.message));
      }
    });
  }

  // Quick add nhà xuất bản
  quickAddNxb(): void {
    const tenNhaXuatBan = prompt('Nhập tên nhà xuất bản:');
    if (!tenNhaXuatBan || !tenNhaXuatBan.trim()) return;
    
    const truSoChinh = prompt('Nhập trụ sở chính (tùy chọn):');
    const moTa = prompt('Nhập mô tả (tùy chọn):');
    
    const newNxb = { 
      tenNhaXuatBan: tenNhaXuatBan.trim(),
      maNhaXuatBan: this.genCode('NXB'),
      truSoChinh: truSoChinh?.trim() || null,
      moTa: moTa?.trim() || null,
      trangThai: true 
    };
    this.http.post('http://localhost:8080/api/nha-xuat-ban', newNxb).subscribe({
      next: (response: any) => {
        console.log('Nhà xuất bản được tạo:', response);
        this.fetchAttributes();
        alert('Thêm nhà xuất bản thành công!');
      },
      error: (error) => {
        console.error('Error adding nhà xuất bản:', error);
        alert('Lỗi khi thêm nhà xuất bản: ' + (error.error?.message || error.message));
      }
    });
  }

  // Generate code helper
  private genCode(prefix: string): string {
    const timestamp = Date.now().toString().slice(-6);
    return `${prefix}${timestamp}`;
  }

  // Hình ảnh methods
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

      // Upload to server
      const formData = new FormData();
      formData.append('image', file);

      this.http.post('http://localhost:8080/api/books/upload-image', formData, {
        responseType: 'text'
      }).subscribe({
        next: (filename: string) => {
          console.log('Image uploaded successfully:', filename);
          this.newDraft.hinhAnh = filename;
          this.uploadingImage = false;
        },
        error: (error) => {
          console.error('Error uploading image:', error);
          alert('Lỗi khi upload hình ảnh: ' + (error.error?.message || error.message));
          this.uploadingImage = false;
        }
      });
    }
  }

  removeImage(): void {
    this.newDraft.hinhAnh = null;
  }

  getImageUrl(filename: string): string {
    if (!filename) return '';
    if (filename.startsWith('http')) return filename;
    return `http://localhost:8080/uploads/${filename}`;
  }

  private toArray(res: any): any[] {
    if (!res) return [];
    if (Array.isArray(res)) return res;
    if (Array.isArray(res.content)) return res.content;
    if (Array.isArray(res.items)) return res.items;
    return [];
  }

}
