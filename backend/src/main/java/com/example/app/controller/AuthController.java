package com.example.app.controller;

import com.example.app.dto.authDTO.LoginRequestDTO;
import com.example.app.dto.authDTO.LoginResponeDTO;
import com.example.app.dto.khachHangDTO.KhachHangRegisterDTO;
import com.example.app.security.JwtService;
import com.example.app.security.KhachHangUserDetails;
import com.example.app.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired private KhachHangService khachHangService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getTaiKhoan(), request.getMatKhau())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        // Optional: Lấy role để trả về
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        System.out.println("🔐 Tạo token cho user: " + userDetails.getUsername());

        return ResponseEntity.ok(new LoginResponeDTO(token, role));
    }
    @PostMapping("/register-khach")
    public ResponseEntity<?> register(@RequestBody KhachHangRegisterDTO dto) {
        try {
            khachHangService.register(dto);
            return ResponseEntity.ok(Map.of("message", "Đăng ký thành công"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}

