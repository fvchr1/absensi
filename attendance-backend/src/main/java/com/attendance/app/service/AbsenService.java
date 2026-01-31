package com.attendance.app.service;

import com.attendance.app.model.Absen;
import com.attendance.app.model.Admin;
import com.attendance.app.repository.AbsenRepository;
import com.attendance.app.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AbsenService {

    @Autowired
    private AbsenRepository absenRepository;

    @Autowired
    private AdminRepository adminRepository;

    /** Get or create Absen for today. Uses first admin as default. */
    public Absen getOrCreateAbsenForDate(LocalDate date) {
        Optional<Absen> existing = absenRepository.findByTgl(date);
        if (existing.isPresent()) {
            return existing.get();
        }
        Admin defaultAdmin = adminRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No admin found"));
        Absen absen = new Absen();
        absen.setTgl(date);
        absen.setAdmin(defaultAdmin);
        return absenRepository.save(absen);
    }

    public Optional<Absen> findByTgl(LocalDate date) {
        return absenRepository.findByTgl(date);
    }
}
