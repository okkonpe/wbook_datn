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
  soLanTaiBan?: number;
  nhaXuatBan?: string;
  kichThuoc?: string;
  loaiBia?: string;
  loaiGiay?: string;
  khoiLuongTinh?: number;
  soLuong?: number;
  ngayXuatBan?: string;
  hinhAnh?: string; // filename sau upload
  donGia?: number;
  moTa?: string;
  trangThai?: boolean;
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

  // F1 base
  private baseVariant: any | null = null;
  inheritMode = false; // bật khi đã kế thừa từ F1

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
    const editable = new Set([
      'loaiGiay', 'loaiBia', 'nxb', 'kichThuoc', 'ngayXuatBan', 'hinhAnh', 'donGia', 'soLanTaiBan', 'soLuong', 'theLoai'
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
    this.newDraft.isbn = this.generateIsbn();
    this.newDraft.maSanPhamChiTiet = this.generateSpct();

    if (base) {
      // Copy các thuộc tính kế thừa từ F1
      this.newDraft.donGia = base.donGia ?? this.newDraft.donGia;
      this.newDraft.soLuong = base.soLuong ?? 1;
      this.newDraft.ngayXuatBan = base.ngayXuatBan ?? this.newDraft.ngayXuatBan;
      this.newDraft.khoiLuongTinh = base.khoiLuongTinh ?? this.newDraft.khoiLuongTinh;
      this.newDraft.soLanTaiBan = base.soLanTaiBan ?? this.newDraft.soLanTaiBan;
      this.newDraft.moTa = '';

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
      soLanTaiBan: d.soLanTaiBan ?? 0,
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
            // Nếu đã có danh mục, áp ngay; nếu chưa, sẽ áp khi danh mục nạp xong
            this.applyBaseIfReady();
          }
        },
        error: (err) => console.error('Không lấy được F1:', err)
      });
  }

  private applyBaseIfReady() {
    if (!this.baseVariant) return;
    // Chỉ áp khi đã có danh mục để map tên -> id
    if (!this.theLoais.length || !this.nhaXuatBans.length) return;
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

  private toArray(res: any): any[] {
    if (!res) return [];
    if (Array.isArray(res)) return res;
    if (Array.isArray(res.content)) return res.content;
    if (Array.isArray(res.items)) return res.items;
    return [];
  }
}
