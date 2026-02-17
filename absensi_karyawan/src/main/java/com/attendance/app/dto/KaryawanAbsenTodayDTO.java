package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Semua karyawan dengan status absen hari ini (sudah check-in atau belum).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KaryawanAbsenTodayDTO {
    private String nik;
    private String nama;
    private String divisi;
    private LocalTime jamMasuk;
    private LocalTime jamKeluar;
    private LocalDateTime createdAt;
    private boolean sudahAbsen;
}
