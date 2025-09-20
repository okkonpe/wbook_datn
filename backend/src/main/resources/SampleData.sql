-- Dữ liệu mẫu cho hệ thống quản lý sách WBook
USE QL_Sach_WBook
GO

-- 1. Dữ liệu cho bảng chuc_vu
INSERT INTO chuc_vu (ma_chuc_vu, ten_chuc_vu) VALUES
('ADMIN', N'ADMIN'),
('NV_BH', N'NHAN_VIEN'),
('NV_KH', N'NHAN_VIEN'),
('NV_GH', N'SHIPPER'),
('QL', N'ADMIN');

-- 2. Dữ liệu cho bảng nhan_vien
INSERT INTO nhan_vien (ma_nv, ten_nv, luong, sdt, ngay_sinh, dia_chi, email, gioi_tinh, cccd, mat_khau, tai_khoan, ngay_bat_dau, chuc_vu, trang_thai) VALUES
('ADMIN001', N'Nguyễn Văn Admin', 15000000, '0123456789', '1990-01-15', N'123 Đường ABC, Quận 1, TP.HCM', 'admin@wbook.com', 1, '123456789012', '$2a$12$5qMsd3/zCnuja6djtcEG1O49rpwVifuaG6ActWaDHs12Z0xuYsTna', 'admin', '2023-01-01', 1, 1),
('NV001', N'Trần Thị Bán Hàng', 8000000, '0987654321', '1995-05-20', N'456 Đường XYZ, Quận 2, TP.HCM', 'banhang@wbook.com', 0, '234567890123', '$2a$12$5qMsd3/zCnuja6djtcEG1O49rpwVifuaG6ActWaDHs12Z0xuYsTna', 'nhanvien', '2023-02-01', 2, 1),
('NV002', N'Lê Văn Kho', 7000000, '0369852147', '1992-08-10', N'789 Đường DEF, Quận 3, TP.HCM', 'kho@wbook.com', 1, '345678901234', '$2a$12$5qMsd3/zCnuja6djtcEG1O49rpwVifuaG6ActWaDHs12Z0xuYsTna', 'kho', '2023-03-01', 3, 1),
('NV003', N'Phạm Thị Giao Hàng', 7500000, '0147258369', '1993-12-05', N'321 Đường GHI, Quận 4, TP.HCM', 'giaohang@wbook.com', 0, '456789012345', '$2a$12$5qMsd3/zCnuja6djtcEG1O49rpwVifuaG6ActWaDHs12Z0xuYsTna', 'giaohang', '2023-04-01', 4, 1);

-- 3. Dữ liệu cho bảng loai_giay
INSERT INTO loai_giay (ma_giay, ten_giay, mau_sac, trang_thai) VALUES
('LG001', N'Giấy trắng', N'Trắng', 1),
('LG002', N'Giấy ngà', N'Ngà', 1),
('LG003', N'Giấy vàng nhạt', N'Vàng nhạt', 1),
('LG004', N'Giấy xanh nhạt', N'Xanh nhạt', 1);

-- 4. Dữ liệu cho bảng the_loai
INSERT INTO the_loai (ma_the_loai, ten_the_loai, trang_thai) VALUES
('TL001', N'Tiểu thuyết', 1),
('TL002', N'Truyện ngắn', 1),
('TL003', N'Khoa học', 1),
('TL004', N'Lịch sử', 1),
('TL005', N'Kinh tế', 1),
('TL006', N'Văn học', 1),
('TL007', N'Thiếu nhi', 1),
('TL008', N'Kỹ năng sống', 1);

-- 5. Dữ liệu cho bảng chu_de
INSERT INTO chu_de (ma_chu_de, ten_chu_de, mo_ta, trang_thai) VALUES
('CD001', N'Tình yêu', N'Sách về tình yêu và các mối quan hệ', 1),
('CD002', N'Gia đình', N'Sách về gia đình và nuôi dạy con', 1),
('CD003', N'Kinh doanh', N'Sách về kinh doanh và khởi nghiệp', 1),
('CD004', N'Học tập', N'Sách giáo dục và học tập', 1),
('CD005', N'Du lịch', N'Sách về du lịch và khám phá', 1),
('CD006', N'Nấu ăn', N'Sách về nấu ăn và ẩm thực', 1);

-- 6. Dữ liệu cho bảng tac_gia
INSERT INTO tac_gia (ma_tac_gia, ten_tac_gia, gioi_tinh, ngay_sinh, mo_ta, trang_thai) VALUES
('TG001', N'Nguyễn Nhật Ánh', 1, '1955-05-07', N'Nhà văn nổi tiếng với các tác phẩm thiếu nhi', 1),
('TG002', N'Paulo Coelho', 1, '1947-08-24', N'Nhà văn Brazil nổi tiếng thế giới', 1),
('TG003', N'Haruki Murakami', 1, '1949-01-12', N'Nhà văn Nhật Bản nổi tiếng', 1),
('TG004', N'J.K. Rowling', 0, '1965-07-31', N'Tác giả bộ truyện Harry Potter', 1),
('TG005', N'Dale Carnegie', 1, '1888-11-24', N'Tác giả sách kỹ năng sống', 1),
('TG006', N'Robert Kiyosaki', 1, '1947-04-08', N'Tác giả sách về tài chính cá nhân', 1);

-- 7. Dữ liệu cho bảng nha_xuat_ban
INSERT INTO nha_xuat_ban (ma_nha_xuat_ban, ten_nha_xuat_ban, ngay_thanh_lap, tru_so_chinh, mo_ta, trang_thai) VALUES
('NXB001', N'NXB Trẻ', '1981-06-24', N'161B Lý Chính Thắng, Q3, HCM', N'Nhà xuất bản chuyên về sách thiếu nhi và văn học', 1),
('NXB002', N'NXB Kim Đồng', '1957-06-17', N'55 Quang Trung, Hà Nội', N'Nhà xuất bản chuyên về sách thiếu nhi', 1),
('NXB003', N'NXB Hội Nhà Văn', '1957-04-20', N'65 Nguyễn Du, Hà Nội', N'Nhà xuất bản chuyên về văn học', 1),
('NXB004', N'NXB Thế Giới', '1957-01-01', N'46 Trần Hưng Đạo, Hà Nội', N'Nhà xuất bản đa dạng các thể loại', 1),
('NXB005', N'NXB Tổng Hợp TP.HCM', '1977-01-01', N'62 Nguyễn Thị Minh Khai, Q1', N'Nhà xuất bản tổng hợp', 1);

-- 8. Dữ liệu cho bảng loai_bia
INSERT INTO loai_bia (ma_bia, ten_bia, mau_sac, trang_thai) VALUES
('LB001', N'Bìa mềm', N'Đa dạng', 1),
('LB002', N'Bìa cứng', N'Đa dạng', 1),
('LB003', N'Bìa bóng', N'Đa dạng', 1),
('LB004', N'Bìa matte', N'Đa dạng', 1);

-- 9. Dữ liệu cho bảng kich_thuoc
INSERT INTO kich_thuoc (ma_kich_thuoc, chi_so_kich_thuoc, trang_thai) VALUES
('KT001', N'13x19cm', 1),
('KT002', N'14.5x20cm', 1),
('KT003', N'16x24cm', 1),
('KT004', N'17x24cm', 1),
('KT005', N'19x27cm', 1);

-- 10. Dữ liệu cho bảng hinh_anh
INSERT INTO hinh_anh (ma_hinh_anh, hinh_anh) VALUES
('HA001', 'sach-ky-nang-cu-lam-di.webp'),
('HA002', 'checklists-thong-minh.webp'),
('HA003', 'default-book.jpg'),
('HA004', 'default-book2.jpg'),
('HA005', 'default-book3.jpg');

-- 11. Dữ liệu cho bảng trang_thai_hoa_don (trạng thái cố định)
SET IDENTITY_INSERT trang_thai_hoa_don ON;
INSERT INTO trang_thai_hoa_don (ID, ma_trang_thai, trang_thai) VALUES
(1, 'TT01', N'GIO_HANG'),
(2, 'TT02', N'CHO_XAC_NHAN'),
(3, 'TT03', N'DANG_GIAO_HANG'),
(4, 'TT04', N'GIAO_HANG_THANH_CONG'),
(5, 'TT05', N'NHAN_VIEN_HUY'),
(11, 'TT06', N'DA_XAC_NHAN'),
(12, 'TT07', N'CHO_LAY_HANG'),
(13, 'TT08', N'DA_LAY_HANG'),
(14, 'TT09', N'GIAO_HANG_THAT_BAI'),
(15, 'TT11', N'KHACH_HANG_DA_HUY'),
(16, 'TT16', N'HOÀN THÀNH');
SET IDENTITY_INSERT trang_thai_hoa_don OFF;

-- 12. Dữ liệu cho bảng voucher
IF NOT EXISTS (SELECT 1 FROM voucher WHERE ma_voucher = 'VOUCHER10')
INSERT INTO voucher (ma_voucher, mo_ta, loai_giam, gia_tri, giam_toi_da, don_toi_thieu, ngay_bat_dau, ngay_ket_thuc, so_luong, da_dung, trang_thai, only_new_customer) VALUES
('VOUCHER10', N'Giảm 10% cho đơn hàng từ 200k', 'PERCENT', 10.00, 50000.00, 200000.00, '2024-01-01', '2024-12-31', 100, 0, 1, 0);

IF NOT EXISTS (SELECT 1 FROM voucher WHERE ma_voucher = 'VOUCHER50K')
INSERT INTO voucher (ma_voucher, mo_ta, loai_giam, gia_tri, giam_toi_da, don_toi_thieu, ngay_bat_dau, ngay_ket_thuc, so_luong, da_dung, trang_thai, only_new_customer) VALUES
('VOUCHER50K', N'Giảm 50k cho đơn hàng từ 300k', 'AMOUNT', 50000.00, 50000.00, 300000.00, '2024-01-01', '2024-12-31', 50, 0, 1, 0);

IF NOT EXISTS (SELECT 1 FROM voucher WHERE ma_voucher = 'NEWUSER20')
INSERT INTO voucher (ma_voucher, mo_ta, loai_giam, gia_tri, giam_toi_da, don_toi_thieu, ngay_bat_dau, ngay_ket_thuc, so_luong, da_dung, trang_thai, only_new_customer) VALUES
('NEWUSER20', N'Giảm 20% cho khách hàng mới', 'PERCENT', 20.00, 100000.00, 100000.00, '2024-01-01', '2024-12-31', 200, 0, 1, 1);

-- 13. Dữ liệu cho bảng san_pham
INSERT INTO san_pham (ma_san_pham, ten_san_pham, ngay_tao, mo_ta, trang_thai) VALUES
('SP001', N'Cửa Sổ Vàng', '2024-01-15', N'Tác phẩm nổi tiếng của Nguyễn Nhật Ánh', 1),
('SP002', N'Nhà Giả Kim', '2024-01-20', N'Tác phẩm kinh điển của Paulo Coelho', 1),
('SP003', N'Rừng Na Uy', '2024-01-25', N'Tác phẩm nổi tiếng của Haruki Murakami', 1),
('SP004', N'Harry Potter', '2024-02-01', N'Bộ truyện nổi tiếng của J.K. Rowling', 1),
('SP005', N'Đắc Nhân Tâm', '2024-02-05', N'Sách kỹ năng sống kinh điển', 1),
('SP006', N'Cha Giàu Cha Nghèo', '2024-02-10', N'Sách về tài chính cá nhân', 1);

-- 14. Dữ liệu cho bảng san_pham_chi_tiet
INSERT INTO san_pham_chi_tiet (ID_san_pham, isbn, loai_giay, loai_bia, the_loai, so_trang, ma_san_pham_chi_tiet, nha_xuat_ban, id_kich_thuoc, khoi_luong_tinh, so_luong, so_lan_tai_ban, ngay_xuat_ban, hinh_anh, don_gia, mo_ta, trang_thai) VALUES
(1, '9786041000001', 1, 1, 1, 200, 'SPCT001', 1, 2, 0.3, 50, 1, '2024-01-15', 1, 89000, N'Bản in lần 1', 1),
(2, '9786041000002', 2, 2, 1, 180, 'SPCT002', 2, 3, 0.4, 30, 2, '2024-01-20', 2, 120000, N'Bản in lần 2', 1),
(3, '9786041000003', 1, 1, 1, 350, 'SPCT003', 3, 4, 0.5, 25, 1, '2024-01-25', 3, 150000, N'Bản in lần 1', 1),
(4, '9786041000004', 3, 2, 7, 300, 'SPCT004', 4, 3, 0.4, 40, 3, '2024-02-01', 4, 180000, N'Bản in lần 3', 1),
(5, '9786041000005', 2, 1, 8, 250, 'SPCT005', 5, 2, 0.3, 60, 5, '2024-02-05', 5, 95000, N'Bản in lần 5', 1),
(6, '9786041000006', 1, 2, 5, 280, 'SPCT006', 1, 4, 0.4, 35, 2, '2024-02-10', 1, 110000, N'Bản in lần 2', 1);

-- 15. Dữ liệu cho bảng sach_tac_gia (sau khi san_pham_chi_tiet đã được tạo)
INSERT INTO sach_tac_gia (ID_tac_gia, ID_san_pham_chi_tiet) VALUES
(1, 1), -- Nguyễn Nhật Ánh - Cửa Sổ Vàng
(2, 2), -- Paulo Coelho - Nhà Giả Kim
(3, 3), -- Haruki Murakami - Rừng Na Uy
(4, 4), -- J.K. Rowling - Harry Potter
(5, 5), -- Dale Carnegie - Đắc Nhân Tâm
(6, 6); -- Robert Kiyosaki - Cha Giàu Cha Nghèo

-- 16. Dữ liệu cho bảng sach_chu_de (sau khi san_pham_chi_tiet đã được tạo)
INSERT INTO sach_chu_de (ID_chu_de, ID_san_pham_chi_tiet) VALUES
(1, 1), -- Tình yêu - Cửa Sổ Vàng
(2, 2), -- Gia đình - Nhà Giả Kim
(3, 3), -- Kinh doanh - Rừng Na Uy
(4, 4), -- Học tập - Harry Potter
(5, 5), -- Du lịch - Đắc Nhân Tâm
(6, 6); -- Nấu ăn - Cha Giàu Cha Nghèo

-- 17. Dữ liệu cho bảng khach_hang
INSERT INTO khach_hang (ma_khach_hang, ten_khach_hang, sdt, ngay_sinh, dia_chi, email, mat_khau, tai_khoan, gioi_tinh, trang_thai) VALUES
('KH001', N'Nguyễn Văn A', '0123456789', '1990-05-15', N'123 Đường ABC, Quận 1, TP.HCM', 'nguyenvana@gmail.com', '$2a$10$XpiQsDgnCt8O.N/C9n4KVe6OUL6JQUJe.CFzkmgyZ3vwYsy8TD3Ty', 'khachhang1', 1, N'Hoạt động'),
('KH002', N'Trần Thị B', '0987654321', '1995-08-20', N'456 Đường XYZ, Quận 2, TP.HCM', 'tranthib@gmail.com', '$2a$10$XpiQsDgnCt8O.N/C9n4KVe6OUL6JQUJe.CFzkmgyZ3vwYsy8TD3Ty', 'khachhang2', 0, N'Hoạt động'),
('KH003', N'Lê Văn C', '0369852147', '1988-12-10', N'789 Đường DEF, Quận 3, TP.HCM', 'levanc@gmail.com', '$2a$10$XpiQsDgnCt8O.N/C9n4KVe6OUL6JQUJe.CFzkmgyZ3vwYsy8TD3Ty', 'khachhang3', 1, N'Hoạt động');

-- 18. Dữ liệu cho bảng tai_ban
INSERT INTO tai_ban (lan_tai_ban, nam_tai_ban) VALUES
(1, 2024),
(2, 2024),
(3, 2024),
(4, 2024),
(5, 2024);

-- 19. Dữ liệu cho bảng sach_tai_ban (sau khi san_pham_chi_tiet và tai_ban đã được tạo)
INSERT INTO sach_tai_ban (sach, tai_ban) VALUES
(1, 1), -- Cửa Sổ Vàng - Tái bản lần 1
(2, 2), -- Nhà Giả Kim - Tái bản lần 2
(3, 1), -- Rừng Na Uy - Tái bản lần 1
(4, 3), -- Harry Potter - Tái bản lần 3
(5, 5), -- Đắc Nhân Tâm - Tái bản lần 5
(6, 2); -- Cha Giàu Cha Nghèo - Tái bản lần 2

-- 20. Dữ liệu mẫu cho bảng hoa_don (sau khi nhan_vien, khach_hang, voucher, trang_thai_hoa_don đã được tạo)
INSERT INTO hoa_don (ma_hoa_don, tong_tien, nhan_vien, khach_hang, ngay_tao, ngay_nhan_hang, phi_ship, tong_tien_sau_giam, voucher, so_luong_mua, trang_thai, hinh_thuc, ho_ten_nguoi_nhan, dia_chi_giao_hang, sdt_nguoi_nhan, ghi_chu, loai_thanh_toan, ly_do_huy) VALUES
('HD001', 89000, 2, 1, '2024-03-01', '2024-03-03', 30000, 119000, NULL, 1, 11, 'ONLINE', N'Nguyễn Văn A', N'123 Đường ABC, Quận 1, TP.HCM', '0123456789', N'Giao hàng trong giờ hành chính', 'COD', NULL),
('HD002', 240000, 2, 2, '2024-03-02', '2024-03-04', 30000, 270000, 1, 2, 4, 'ONLINE', N'Trần Thị B', N'456 Đường XYZ, Quận 2, TP.HCM', '0987654321', N'Giao hàng ngoài giờ', 'VNPAY', NULL),
('HD003', 150000, 1, 3, '2024-03-03', '2024-03-05', 30000, 180000, NULL, 1, 3, 'OFFLINE', N'Lê Văn C', N'789 Đường DEF, Quận 3, TP.HCM', '0369852147', N'Khách hàng đến mua trực tiếp', 'TIEN_MAT', NULL);

-- 21. Dữ liệu mẫu cho bảng hoa_don_chi_tiet (sau khi hoa_don và san_pham_chi_tiet đã được tạo)
INSERT INTO hoa_don_chi_tiet (ma_hoa_don, ma_san_pham, tong_tien, so_luong_mua, ma_hoa_don_chi_tiet, mo_ta, trang_thai) VALUES
(1, 1, 89000, 1, 'HDCT001', N'Cửa Sổ Vàng - Bản in lần 1', 1),
(2, 2, 120000, 1, 'HDCT002', N'Nhà Giả Kim - Bản in lần 2', 1),
(2, 3, 120000, 1, 'HDCT003', N'Rừng Na Uy - Bản in lần 1', 1),
(3, 4, 150000, 1, 'HDCT004', N'Harry Potter - Bản in lần 3', 1);

IF NOT EXISTS (SELECT 1 FROM tai_ban)
BEGIN
    INSERT INTO tai_ban (lan_tai_ban, nam_tai_ban) VALUES 
    (1, 2024),
    (2, 2024),
    (1, 2023),
    (3, 2024),
    (2, 2023),
    (1, 2025);
    
    PRINT 'Đã thêm dữ liệu mẫu vào bảng tai_ban';
END
ELSE
BEGIN
    PRINT 'Bảng tai_ban đã có dữ liệu';
END


PRINT N'✅ Đã thêm thành công dữ liệu mẫu cho tất cả các bảng!'

select * from nhan_vien