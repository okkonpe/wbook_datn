package com.example.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tai_ban")
public class TaiBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "lan_tai_ban")
    private Integer lanTaiBan;
    @Column(name = "nam_tai_ban")
    private Integer namTaiBan;
    @ManyToMany(mappedBy = "taiBan")
    private Set<Book> books = new HashSet<>();
}
