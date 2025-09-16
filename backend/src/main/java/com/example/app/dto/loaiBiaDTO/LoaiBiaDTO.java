package com.example.app.dto.loaiBiaDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiBiaDTO {
        private Integer id;
        private String maBia;
        private String tenBia;
        private String mauSac;
        private Boolean trangThai;
    }
