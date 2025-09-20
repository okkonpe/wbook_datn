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
  drafts: VariantDraft[] = [];
  newDraft: VariantDraft = { trangThai: true };
  uploading = false;
  uploadingIndex = -1;
  saving = false;

  theLoais: any[] = [];
  nhaXuatBans: any[] = [];
  kichThuocs: any[] = [];
  loaiBiaList: any[] = [];
  loaiGiayList: any[] = [];
  tacGias: any[] = [];
  chuDes: any[] = [];
  taiBans: any[] = [];
  
  // Selected values for dropdowns
  uploadingImage = false;

  // F1 base
  private baseVariant: any | null = null;
  inheritMode = false; // bật khi đã kế thừa từ F1
  hasF1Data = false; // kiểm tra xem có dữ liệu F1 không

  // Multi-select selections
  selectedTheLoaiIds: number[] = [];
  selectedNxbIds: number[] = [];
  chosenTheLoaiId?: number;
  chosenNxbId?: number;

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    console.log('Product ID:', this.productId);
  }

  get totalEstimatedValue(): number {
    return this.drafts.reduce((sum, draft) => {
      const price = draft.donGia || 0;
      const quantity = draft.soLuong || 0;
      return sum + (price * quantity);
    }, 0);
  }

  // Khóa/mở các trường khi kế thừa từ F1
  isDisabled(field: string): boolean {
    if (!this.inheritMode) return false;
    
    // Các trường kế thừa từ F1 và không cho sửa
    const inheritedFromF1 = new Set([
      'isbn', 'soTrang', 'taiBanIds', 'khoiLuongTinh', 'tacGiaIds', 'chuDeIds'
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

  addDraft() {
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
    
    // Check duplicate ISBN or SPCT code
    const duplicate = this.drafts.find(d => 
      d.isbn === this.newDraft.isbn || 
      d.maSanPhamChiTiet === this.newDraft.maSanPhamChiTiet
    );
    
    if (duplicate) {
      alert('ISBN hoặc Mã SPCT đã tồn tại trong danh sách!');
      return;
    }

    if (this.selectedTheLoaiIds.length && this.selectedNxbIds.length) {
      if (this.generateVariants()) {
        this.resetForm();
        console.log(`✅ Đã thêm ${this.selectedTheLoaiIds.length * this.selectedNxbIds.length} biến thể theo tổ hợp`);
        return;
      }
    }
    
    this.drafts.push({ ...this.newDraft });
    this.resetForm();
    
    console.log('✅ Đã thêm biến thể:', this.newDraft.maSanPhamChiTiet);
  }

  private generateVariants() {
    if (this.selectedTheLoaiIds.length && this.selectedNxbIds.length) {
      for (const tl of this.selectedTheLoaiIds) {
        for (const nxb of this.selectedNxbIds) {
          const draft: VariantDraft = {
            ...this.newDraft,
            isbn: this.generateIsbn(),
            maSanPhamChiTiet: this.generateSpct(),
            theLoai: tl as any,
            nhaXuatBan: nxb as any
          };
          this.drafts.push(draft);
        }
      }
      return true;
    }
    return false;
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

      this.chosenTheLoaiId = tl?.id;
      this.chosenNxbId = nxb?.id;
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

  removeDraft(i: number) { 
    this.drafts.splice(i, 1); 
  }

  clearAll() {
    if (confirm('Bạn có chắc chắn muốn xóa tất cả biến thể?')) {
      this.drafts = [];
    }
  }

  onFileChange(event: any, idx: number) {
    const file: File = event.target.files?.[0];
    if (!file) return;
    
    if (!file.type.startsWith('image/')) {
      alert('Vui lòng chọn file hình ảnh!');
      return;
    }
    
    if (file.size > 10 * 1024 * 1024) {
      alert('File quá lớn! Vui lòng chọn file nhỏ hơn 10MB.');
      return;
    }

    const form = new FormData();
    form.append('file', file);
    this.uploading = true;
    this.uploadingIndex = idx;
    
    console.log(`Đang tải lên ảnh: ${file.name} (${file.size} bytes)`);
    
    // Đảm bảo gửi form đúng định dạng multipart/form-data
    this.http.post('http://localhost:8080/api/books/upload-image', form, { 
      responseType: 'text',
      headers: { 'Accept': 'text/plain' }
    })
      .subscribe({
        next: (filename) => {
          console.log('✅ Upload thành công:', filename);
          this.drafts[idx].hinhAnh = filename;
          this.uploading = false;
          this.uploadingIndex = -1;
        },
        error: (error) => { 
          console.error('❌ Upload thất bại:', error);
          alert(`Tải ảnh thất bại! Lỗi: ${error.error || error.message || 'Không xác định'}`);
          this.uploading = false;
          this.uploadingIndex = -1;
        }
      });
  }

  saveAll() {
    if (!this.drafts.length) return;
    
    this.saving = true;
    const payload = this.drafts.map(d => ({ 
      sanPhamId: this.productId,
      isbn: (d.isbn || '').toString(),
      maSanPhamChiTiet: (d.maSanPhamChiTiet || '').toString(),
      theLoaiId: this.toId(d.theLoai, this.theLoais),
      nhaXuatBanId: this.toId(d.nhaXuatBan, this.nhaXuatBans),
      kichThuocId: this.toId(d.kichThuoc, this.kichThuocs),
      loaiBiaId: this.toId(d.loaiBia, this.loaiBiaList),
      loaiGiayId: this.toId(d.loaiGiay, this.loaiGiayList),
      soTrang: d.soTrang ?? 0,
      taiBanIds: d.taiBanIds ? [d.taiBanIds] : [],
      tacGiaIds: d.tacGiaIds ? [d.tacGiaIds] : [],
      chuDeIds: d.chuDeIds ? [d.chuDeIds] : [],
      khoiLuongTinh: d.khoiLuongTinh ?? 0,
      soLuong: d.soLuong ?? 0,
      ngayXuatBan: d.ngayXuatBan as any,
      hinhAnh: d.hinhAnh,
      donGia: d.donGia ?? 0,
      moTa: d.moTa,
      trangThai: d.trangThai
    }));

    const invalid = payload.find(p => !p.isbn || !p.maSanPhamChiTiet || (p.soLuong as number) <= 0 || (p.donGia as number) <= 0);
    if (invalid) {
      this.saving = false;
      alert('Vui lòng nhập đầy đủ:Số lượng > 0, Đơn giá > 0');
      return;
    }

    console.log('Payload bulk variants:', payload);
    
    this.http.post('http://localhost:8080/api/books/bulk-create', payload, {
      headers: { 'Content-Type': 'application/json' }
    })
      .subscribe({
        next: () => {
          this.saving = false;
          this.router.navigate(['/admin/products/book']);
        },
        error: (error) => {
          console.error('Save failed:', error);
          alert(`Lưu thất bại! Lỗi: ${error.error?.message || error.message || 'Không xác định'}`);
          this.saving = false;
        }
      });
  }

  private toId(val: any, list: any[]): number | undefined {
    if (val == null) return undefined;
    if (typeof val === 'number') return val;
    // Nếu bound nhầm tên -> tìm theo tên
    const item = list?.find((x: any) => x.tenTheLoai === val || x.tenNhaXuatBan === val || x.chiSoKichThuoc === val || x.tenBia === val || x.tenGiay === val);
    return item?.id;
  }

  addTheLoai() {
    if (!this.chosenTheLoaiId) return;
    if (!this.selectedTheLoaiIds.includes(this.chosenTheLoaiId)) {
      this.selectedTheLoaiIds.push(this.chosenTheLoaiId);
    }
    this.chosenTheLoaiId = undefined;
  }

  removeTheLoai(id: number) {
    this.selectedTheLoaiIds = this.selectedTheLoaiIds.filter(x => x !== id);
  }

  addNxb() {
    if (!this.chosenNxbId) return;
    if (!this.selectedNxbIds.includes(this.chosenNxbId)) {
      this.selectedNxbIds.push(this.chosenNxbId);
    }
    this.chosenNxbId = undefined;
  }

  removeNxb(id: number) {
    this.selectedNxbIds = this.selectedNxbIds.filter(x => x !== id);
  }

  // Helpers for template display
  getTheLoaiName(id: number | undefined): string {
    if (!id) return '';
    const item = this.theLoais?.find((t: any) => t.id === id);
    return item?.tenTheLoai ?? String(id);
  }

  getNxbName(id: number | undefined): string {
    if (!id) return '';
    const item = this.nhaXuatBans?.find((n: any) => n.id === id);
    return item?.tenNhaXuatBan ?? String(id);
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
    const namTaiBan = prompt('Nhập năm tái bản:');
    
    if (lanTaiBan && namTaiBan && lanTaiBan.trim() && namTaiBan.trim()) {
      const newTaiBan = { 
        lanTaiBan: parseInt(lanTaiBan.trim()), 
        namTaiBan: parseInt(namTaiBan.trim()) 
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
