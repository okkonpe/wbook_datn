package com.example.app.security;

import com.example.app.entity.KhachHang;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class KhachHangUserDetails implements UserDetails {


    private final Integer id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public KhachHangUserDetails(Integer id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
    public Integer getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_KHACH_HANG");
    }

    @Override
    public String getPassword() {
        System.out.println("⚠️ Mật khẩu từ getPassword(): " + password);

        return password;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

//    @Override
//    public boolean isEnabled() {
//        return kh.getTrangThai()||kh.getTrangThai()==null ;
//    }
}

