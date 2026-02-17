package com.attendance.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "melakukan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Melakukan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_melakukan")
    private Long idMelakukan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_absen", nullable = false)
    private Absen absen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nik", nullable = false)
    private Karyawan karyawan;

    @Column(name = "jam_masuk")
    private LocalTime jamMasuk;

    @Column(name = "jam_keluar")
    private LocalTime jamKeluar;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
