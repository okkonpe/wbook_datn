package com.example.app.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class TestBcryptConfig {
    @Bean
    CommandLineRunner testBcrypt() {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String raw = "123";
            String encoded = encoder.encode(raw);

            System.out.println("🔐 Encoded password: " + encoded);
            System.out.println("✅ Match? " + encoder.matches("123", encoded));
        };
    }
}
