package com.attendance.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "karyawan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Karyawan {
    @Id
    @Column(name = "nik", length = 20)
    private String nik;

    @Column(nullable = false)
    private String nama;

    private String alamat;

    @Column(name = "no_tlp", length = 30)
    private String noTlp;

    @Column(nullable = false)
    private String password;

    @Column(name = "tgl_lahir")
    private LocalDate tglLahir;

    private String divisi;

    @Column(name = "jenis_kel", length = 10)
    private String jenisKel;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean status;
}
