package com.example.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordToNhanVien(String emailTo, String username, String plainPassword) {
        String subject = "Tài khoản nhân viên được tạo";
        String content = String.format("""
                Xin chào,

                Tài khoản của bạn đã được tạo thành công.

                ➤ Tài khoản: %s
                ➤ Mật khẩu: %s

                Vui lòng đăng nhập và đổi mật khẩu sau lần đầu sử dụng.

                Trân trọng,
                Hệ thống quản lý
                """, username, plainPassword);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}
