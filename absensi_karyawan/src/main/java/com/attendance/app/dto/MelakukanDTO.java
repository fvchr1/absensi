package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MelakukanDTO {
    private Long idMelakukan;
    private Long idAbsen;
    private LocalDate tgl;
    private String nik;
    private String namaKaryawan;
    private String divisiKaryawan;
    private LocalTime jamMasuk;
    private LocalTime jamKeluar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
