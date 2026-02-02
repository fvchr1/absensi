package com.attendance.app.controller;

import com.attendance.app.dto.KaryawanAbsenTodayDTO;
import com.attendance.app.dto.MelakukanDTO;
import com.attendance.app.dto.RekapAbsenDTO;
import com.attendance.app.dto.RekapAbsenKaryawanDTO;
import com.attendance.app.service.MelakukanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000", "http://127.0.0.1:3000"}, maxAge = 3600)
public class AttendanceController {

    @Autowired
    private MelakukanService melakukanService;

    @PostMapping("/check-in/{nik}")
    public ResponseEntity<?> checkIn(@PathVariable String nik) {
        try {
            MelakukanDTO dto = melakukanService.checkIn(nik);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/check-out/{nik}")
    public ResponseEntity<?> checkOut(@PathVariable String nik) {
        try {
            MelakukanDTO dto = melakukanService.checkOut(nik);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/employee/{nik}")
    public ResponseEntity<List<MelakukanDTO>> getByKaryawanNik(@PathVariable String nik) {
        return ResponseEntity.ok(melakukanService.getByKaryawanNik(nik));
    }

    @GetMapping("/employee/{nik}/today")
    public ResponseEntity<?> getTodayByNik(@PathVariable String nik) {
        return melakukanService.getTodayByNik(nik)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{nik}/range")
    public ResponseEntity<List<MelakukanDTO>> getByKaryawanNikAndRange(
            @PathVariable String nik,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(melakukanService.getByKaryawanNikAndDateRange(nik, startDate, endDate));
    }

    @GetMapping("/all-range")
    public ResponseEntity<List<MelakukanDTO>> getAllByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(melakukanService.getAllByDateRange(startDate, endDate));
    }

    @GetMapping("/today")
    public ResponseEntity<List<MelakukanDTO>> getTodayAttendance() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Jakarta"));
        return ResponseEntity.ok(melakukanService.getAllByDateRange(today, today));
    }

    /** Daftar semua karyawan dengan status absen hari ini (sudah/belum check-in). */
    @GetMapping("/today-with-karyawan")
    public ResponseEntity<List<KaryawanAbsenTodayDTO>> getTodayWithAllKaryawan() {
        return ResponseEntity.ok(melakukanService.getTodayWithAllKaryawan());
    }

    /** Rekap absensi bulanan dengan perhitungan gaji. */
    @GetMapping("/monthly-recap")
    public ResponseEntity<RekapAbsenDTO> getMonthlyRecap() {
        return ResponseEntity.ok(melakukanService.getMonthlyRecap());
    }

    /** Rekap absensi bulanan per karyawan dengan detail gaji. */
    @GetMapping("/monthly-recap-per-karyawan")
    public ResponseEntity<List<RekapAbsenKaryawanDTO>> getMonthlyRecapPerKaryawan() {
        return ResponseEntity.ok(melakukanService.getMonthlyRecapPerKaryawan());
    }
}
