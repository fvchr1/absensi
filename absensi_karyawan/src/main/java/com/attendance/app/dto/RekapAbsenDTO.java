package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapAbsenDTO {
    private int totalHariKerja; // Total hari kerja dalam bulan (misal 22 hari)
    private int totalHariAbsen; // Total hari karyawan absen dalam bulan
    private double gajiPokok; // 4,500,000
    private double gajiPerHari; // Gaji absensi per hari jika full (1jt / totalHariKerja)
    private double gajiAbsensi; // Total gaji absensi (hari absen * gajiPerHari)
    private int hariLembur; // Jumlah hari lembur
    private double gajiLembur; // Total gaji lembur (hariLembur * 100,000)
    private double totalGaji; // Gaji pokok + gaji absensi + gaji lembur
}
