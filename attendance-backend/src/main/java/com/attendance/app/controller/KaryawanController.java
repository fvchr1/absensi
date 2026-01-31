package com.attendance.app.controller;

import com.attendance.app.dto.KaryawanDTO;
import com.attendance.app.model.Karyawan;
import com.attendance.app.service.KaryawanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/karyawan")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000", "http://127.0.0.1:3000"}, maxAge = 3600)
public class KaryawanController {

    @Autowired
    private KaryawanService karyawanService;

    @GetMapping
    public ResponseEntity<List<KaryawanDTO>> getAllKaryawan() {
        return ResponseEntity.ok(karyawanService.getAllKaryawan());
    }

    @GetMapping("/{nik}")
    public ResponseEntity<KaryawanDTO> getByNik(@PathVariable String nik) {
        try {
            return ResponseEntity.ok(karyawanService.getByNik(nik));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createKaryawan(@RequestBody Karyawan karyawan) {
        try {
            Karyawan created = karyawanService.createKaryawan(karyawan);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{nik}")
    public ResponseEntity<?> updateKaryawan(@PathVariable String nik, @RequestBody Karyawan karyawan) {
        try {
            Karyawan updated = karyawanService.updateKaryawan(nik, karyawan);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{nik}")
    public ResponseEntity<?> deleteKaryawan(@PathVariable String nik) {
        karyawanService.deleteKaryawan(nik);
        return ResponseEntity.ok("Karyawan deleted successfully");
    }
}
