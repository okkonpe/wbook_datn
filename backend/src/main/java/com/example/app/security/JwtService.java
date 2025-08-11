package com.example.app.security;

import com.example.app.entity.KhachHang;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "your-256-bit-secret-your-256-bit-secret";
    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        Integer userId = null;

        if (userDetails instanceof NhanVienUserDetails ){
            NhanVienUserDetails nv = (NhanVienUserDetails) userDetails;
            userId = nv.getId();
        } else if (userDetails instanceof KhachHangUserDetails) {
            KhachHangUserDetails kh = (KhachHangUserDetails) userDetails;
            userId = kh.getId();
        }

        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 ngày
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


//    public String generateToken(KhachHang khachHang) {
//        return Jwts.builder()
//                .setSubject(khachHang.getTaiKhoan())
//                .claim("id", khachHang.getId()) // 👈 cái này frontend cần
//                .claim("role", "ROLE_KHACH_HANG")
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
//                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("id", Integer.class);
    }


    public boolean isValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}

