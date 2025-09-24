package com.example.app.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tai_ban")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lan_tai_ban")
    private Integer lanTaiBan;

    // @ManyToMany(mappedBy = "taiBans")
    // @Builder.Default
    // private Set<Book> books = new HashSet<>();
}
