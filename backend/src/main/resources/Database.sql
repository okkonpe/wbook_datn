CREATE DATABASE QL_Sach_WBook
go 
USE QL_Sach_WBook
go 
CREATE TABLE loai_giay(
    ID INT IDENTITY (1,1) PRIMARY key not null,
    ma_giay NVARCHAR (15) not null,
    ten_giay NVARCHAR (30) not null,
	mau_sac nvarchar (15),
	trang_thai bit
);
CREATE TABLE the_loai(
    ID INT IDENTITY (1,1) PRIMARY key not null,
    ma_the_loai NVARCHAR (15) ,
    ten_the_loai NVARCHAR (30),   
	trang_thai bit
);
CREATE TABLE chu_de(
    ID INT IDENTITY (1,1) PRIMARY key not null,
    ma_chu_de NVARCHAR (15) ,
    ten_chu_de NVARCHAR (30),   
	mo_ta nvarchar(100),
	trang_thai bit
);

CREATE TABLE tac_gia(
    ID INT IDENTITY (1,1) PRIMARY key not null,
    ma_tac_gia NVARCHAR (15) not null,
    ten_tac_gia NVARCHAR (30) not null,
    gioi_tinh BIT,
    ngay_sinh DATE,
	mo_ta nvarchar(100),
	trang_thai bit
);
CREATE TABLE nha_xuat_ban(
        ID INT IDENTITY (1,1) PRIMARY key not null,
        ma_nha_xuat_ban NVARCHAR (15) not null,
        ten_nha_xuat_ban NVARCHAR (30) not null,
		ngay_thanh_lap Date,
		tru_so_chinh nvarchar (30),
		mo_ta nvarchar(100),
		trang_thai bit
);

CREATE TABLE loai_bia(
        ID INT IDENTITY (1,1) PRIMARY key not null,
        ma_bia NVARCHAR (15) not null,
        ten_bia NVARCHAR (30) not null,
		mau_sac nvarchar(15),
		trang_thai bit
);
CREATE TABLE kich_thuoc(
        ID INT IDENTITY (1,1) PRIMARY key not null,
        ma_kich_thuoc NVARCHAR (15) not null,
        chi_so_kich_thuoc NVARCHAR (10) not null,
		trang_thai bit
);
CREATE TABLE hinh_anh(
        ID INT IDENTITY (1,1) PRIMARY key not null,
        ma_hinh_anh NVARCHAR (15) not null,
        hinh_anh NVARCHAR (50)
);
CREATE TABLE voucher (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_voucher VARCHAR(50) UNIQUE NOT NULL, 
    mo_ta NVARCHAR(255),
    loai_giam VARCHAR(10) NOT NULL 
        CHECK (loai_giam IN ('PERCENT','AMOUNT')), 
    gia_tri DECIMAL(10,2) NOT NULL,         
    giam_toi_da DECIMAL(10,2),             
    don_toi_thieu DECIMAL(10,2) DEFAULT 0,  
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    so_luong INT DEFAULT 0,                 
    da_dung INT DEFAULT 0,                  
    trang_thai BIT DEFAULT 1,             
    only_new_customer BIT DEFAULT 0         
);


CREATE TABLE san_pham(
        ID INT IDENTITY (1,1) PRIMARY key not null,
        ma_san_pham NVARCHAR (15) not null,
        ten_san_pham NVARCHAR (30) not null,
        ngay_tao date, 
		mo_ta nvarchar(150),
        trang_thai bit 
		
);

CREATE TABLE san_pham_chi_tiet (
    ID INT IDENTITY (1,1) PRIMARY KEY not null,
    ID_san_pham  Int not null,
	isbn varchar(13) not null,
    loai_giay Int,
    loai_bia Int ,
	the_loai Int, 
	so_trang int ,
	ma_san_pham_chi_tiet nvarchar(15) not null,
	nha_xuat_ban int,
	id_kich_thuoc int,
	khoi_luong_tinh float,
	so_luong int not null,
	so_lan_tai_ban int,
	ngay_xuat_ban  date,
	hinh_anh  int,
	don_gia money,
	mo_ta nvarchar(100),
	trang_thai bit,

	 
	 FOREIGN KEY (ID_san_pham) REFERENCES san_pham (ID),
	 FOREIGN KEY (loai_giay) REFERENCES loai_giay (ID),
	 FOREIGN KEY (loai_bia) REFERENCES loai_bia (ID),
	 FOREIGN KEY (the_loai) REFERENCES the_loai (ID),
	 FOREIGN KEY (nha_xuat_ban) REFERENCES nha_xuat_ban (ID),
	 FOREIGN KEY (id_kich_thuoc) REFERENCES kich_thuoc(ID),
	 FOREIGN KEY (hinh_anh) REFERENCES hinh_anh(ID)
);	
CREATE TABLE sach_chu_de(
    ID INT IDENTITY(1,1) PRIMARY KEY not null,
    ID_chu_de INT, 
    ID_san_pham_chi_tiet INT,
    FOREIGN KEY (ID_chu_de) REFERENCES chu_de (ID),
	FOREIGN KEY (ID_san_pham_chi_tiet) REFERENCES san_pham_chi_tiet (ID)
);
CREATE TABLE sach_tac_gia(
    ID INT IDENTITY(1,1) PRIMARY KEY not null,
    ID_tac_gia INT, 
    ID_san_pham_chi_tiet INT,
    FOREIGN KEY (ID_tac_gia) REFERENCES tac_gia (ID),
	FOREIGN KEY (ID_san_pham_chi_tiet) REFERENCES san_pham_chi_tiet (ID)
);
CREATE TABLE tai_ban(
    ID INT IDENTITY (1,1) PRIMARY key not null,
    lan_tai_ban int ,
    nam_tai_ban int,
);
CREATE TABLE sach_tai_ban(
    ID INT IDENTITY (1,1) PRIMARY key not null,
    sach int,
	tai_ban int,
	 FOREIGN KEY (sach) REFERENCES san_pham_chi_tiet (ID),
	FOREIGN KEY (tai_ban) REFERENCES tai_ban (ID)
);


CREATE TABLE chuc_vu (
    ID INT IDENTITY (1,1) PRIMARY KEY,
    ma_chuc_vu NVARCHAR (15) ,
    ten_chuc_vu NVARCHAR (50),
);	
CREATE TABLE nhan_vien (
    ID INT IDENTITY (1,1) PRIMARY KEY,
    ma_nv nvarchar(15),
    ten_nv nvarchar(30) ,
	luong money, 
	sdt nvarchar(13) ,
	ngay_sinh date,
	dia_chi nvarchar(50),
	email nvarchar(50),
	gioi_tinh bit,
	cccd nvarchar(13) not null,
	mat_khau nvarchar(100),
	tai_khoan nvarchar(50),
	ngay_bat_dau date,
	chuc_vu int,
	trang_thai bit,
	FOREIGN KEY (chuc_vu) REFERENCES chuc_vu(ID)
);

CREATE TABLE trang_thai_hoa_don(
        ID INT IDENTITY (1,1) PRIMARY key not null,
        ma_trang_thai NVARCHAR (15),
        trang_thai NVARCHAR (30),
);
CREATE TABLE khach_hang (
    ID INT IDENTITY (1,1) PRIMARY KEY not null,
    ma_khach_hang nvarchar(15),
    ten_khach_hang nvarchar(30) ,
    sdt nvarchar(13), 
	ngay_sinh date,
	dia_chi nvarchar(50),
	email nvarchar(30),
	mat_khau nvarchar(100),
	tai_khoan nvarchar(50),
	gioi_tinh bit,
	trang_thai NVARCHAR (30),
);

CREATE TABLE hoa_don (
    ID INT IDENTITY (1,1) PRIMARY KEY not null,
    ma_hoa_don nvarchar(15) not null,
    tong_tien money ,
	nhan_vien int, 
	khach_hang int ,
	ngay_tao Date,
	ngay_nhan_hang Date,
	phi_ship money,
	tong_tien_sau_giam money,
	voucher bigint,
	so_luong_mua int,
    trang_thai int	,
	hinh_thuc varchar(8),
	ho_ten_nguoi_nhan nvarchar(100),
	dia_chi_giao_hang nvarchar(200),
	sdt_nguoi_nhan varchar(10),
	ghi_chu nvarchar(200),
	loai_thanh_toan varchar(50),
	ly_do_huy nvarchar(50),
	 FOREIGN KEY (nhan_vien) REFERENCES nhan_vien (ID),
	 FOREIGN KEY (khach_hang) REFERENCES khach_hang (ID),
	 FOREIGN KEY (voucher) REFERENCES voucher (ID),
	 FOREIGN KEY (trang_thai) REFERENCES trang_thai_hoa_don (ID)
);	

CREATE TABLE hoa_don_chi_tiet (
    ID INT IDENTITY (1,1) PRIMARY KEY,
    ma_hoa_don int,
    ma_san_pham int ,
    tong_tien money,
	so_luong_mua int,
	ma_hoa_don_chi_tiet nvarchar(15),
	mo_ta nvarchar(100),
	trang_thai bit,

	 FOREIGN KEY (ma_hoa_don) REFERENCES hoa_don (ID),
	 FOREIGN KEY (ma_san_pham) REFERENCES san_pham_chi_tiet (ID)
)