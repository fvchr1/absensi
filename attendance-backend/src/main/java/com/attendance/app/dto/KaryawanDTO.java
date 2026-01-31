package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KaryawanDTO {
    private String nik;
    private String nama;
    private String alamat;
    private String noTlp;
    private LocalDate tglLahir;
    private String divisi;
    private String jenisKel;
    private boolean status;
}
