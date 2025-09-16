package com.example.app.controller;

import com.example.app.dto.banHangDTO.HoaDonRequestDTO;
import com.example.app.dto.banHangDTO.ListDonHangDTO;
import com.example.app.entity.HoaDon;
import com.example.app.entity.TrangThaiHoaDon;
import com.example.app.repository.HoaDonRepository;
import com.example.app.repository.TrangThaiHoaDonRepo;
import com.example.app.service.HoaDonService;
import com.example.app.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private TrangThaiHoaDonRepo trangThaiHoaDonRepo;


    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam long amount,
                                           @RequestParam String orderInfo,@RequestParam Integer khid,@RequestBody HoaDonRequestDTO dto,
                                           HttpServletRequest request) {
        HoaDon hoaDon = hoaDonRepository.findByKhachHangIdAndTrangThaiId(
                khid, 1).
                orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn giỏ hàng"));
        String paymentUrl = vnPayService.createVNPayPaymentUrl(amount, orderInfo,hoaDon.getId(),khid,request);

        hoaDonService.updateDatHang(hoaDon.getId(),dto);
        System.out.println(paymentUrl);
        return ResponseEntity.ok(Collections.singletonMap("url", paymentUrl));
    }

    @PutMapping("/da-thanh-toan")
    public ResponseEntity<?> capNhatDaThanhToan(@RequestParam Integer orderID){
        hoaDonService.updateTrangThaiDaThanhToan(orderID, "VNPAY");


        return ResponseEntity.ok("OK");
    }



//     IPN (cài đặt code then VNPay tự gọi đến API này) -> update DB, cần domain SSL và public

//    @GetMapping("/vnpay-ipn")

//    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
//        System.out.println("ok_ipn");
//        Map<String, String> fields = new HashMap<>();
//        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
//            String fieldName = params.nextElement();
//            String fieldValue = request.getParameter(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
////        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//
//        // ✅ kiểm tra chữ ký hash
////        fields.remove("vnp_SecureHash");
////        String signValue = VNPayService.hashAllFields(fields,vnp_SecureHash);
////        if (!signValue.equals(vnp_SecureHash)) {
////            return ResponseEntity.ok("97|Invalid Checksum");
////        }
//
//        String responseCode = request.getParameter("vnp_ResponseCode");
//        Integer orderId = Integer.valueOf(request.getParameter("vnp_TxnRef"));
//
//        if ("00".equals(responseCode)) {
//            // ✅ thanh toán thành công → cập nhật DB
//            hoaDonService.updateTrangThaiThanhToan(orderId, "Đã thanh toán");
//            return ResponseEntity.ok("00|Confirm Success");
//        } else {
//            // ❌ thanh toán thất bại
//            hoaDonService.updateTrangThaiThanhToan(orderId, "Thanh toán thất bại");
//            return ResponseEntity.ok("01|Payment Failed");
//        }
//    }
    @GetMapping("/mock")
    public ResponseEntity<String> mockPayment(@RequestParam String orderId, @RequestParam Long amount,@RequestParam Integer khid) {
        // Giả lập redirect về returnUrl
        HoaDon hoaDon = hoaDonRepository.findByKhachHangIdAndTrangThaiId(
                khid, 1).
                orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn giỏ hàng"));
        TrangThaiHoaDon trangThaiDaDatHang = trangThaiHoaDonRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái"));

        hoaDon.setTrangThai(trangThaiDaDatHang);
        hoaDonService.updateTrangThaiDaThanhToan(hoaDon.getId(), "Đã TT");

        hoaDonRepository.save(hoaDon);

        String txnRefEncoded = URLEncoder.encode(orderId, StandardCharsets.UTF_8);

        String fakeReturnUrl = "http://localhost:4200/checkout-success"
                + "?vnp_ResponseCode=00"
                + "&vnp_TxnRef=" + txnRefEncoded
                + "&vnp_Amount=" + amount;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(fakeReturnUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);    }
}

