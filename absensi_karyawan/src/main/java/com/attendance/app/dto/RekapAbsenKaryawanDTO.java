package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapAbsenKaryawanDTO {
    private String nik;
    private String nama;
    private String divisi;
    private int hariAbsen; // Jumlah hari absen dalam bulan ini
    private double gajiPokok; // 4,500,000
    private double gajiAbsensi; // hariAbsen * gajiPerHari
    private int hariLembur; // Jumlah hari lembur
    private double gajiLembur; // hariLembur * 100,000
    private double totalGaji; // gajiPokok + gajiAbsensi + gajiLembur
}
