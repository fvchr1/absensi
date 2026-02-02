package com.attendance.app.service;

import com.attendance.app.dto.KaryawanDTO;
import com.attendance.app.model.Karyawan;
import com.attendance.app.repository.KaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KaryawanService {

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<KaryawanDTO> getAllKaryawan() {
        return karyawanRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public KaryawanDTO getByNik(String nik) {
        Karyawan k = karyawanRepository.findByNik(nik)
                .orElseThrow(() -> new RuntimeException("Karyawan not found"));
        return toDTO(k);
    }

    @Transactional
    public Karyawan createKaryawan(Karyawan karyawan) {
        if (karyawanRepository.existsByNik(karyawan.getNik())) {
            throw new RuntimeException("NIK already exists");
        }
        karyawan.setPassword(passwordEncoder.encode(karyawan.getPassword()));
        return karyawanRepository.save(karyawan);
    }

    @Transactional
    public Karyawan updateKaryawan(String nik, Karyawan details) {
        Karyawan k = karyawanRepository.findByNik(nik)
                .orElseThrow(() -> new RuntimeException("Karyawan not found"));
        if (details.getNama() != null) k.setNama(details.getNama());
        if (details.getAlamat() != null) k.setAlamat(details.getAlamat());
        if (details.getNoTlp() != null) k.setNoTlp(details.getNoTlp());
        if (details.getTglLahir() != null) k.setTglLahir(details.getTglLahir());
        if (details.getDivisi() != null) k.setDivisi(details.getDivisi());
        if (details.getJenisKel() != null) k.setJenisKel(details.getJenisKel());
        if (details.getPassword() != null && !details.getPassword().isEmpty()) {
            k.setPassword(passwordEncoder.encode(details.getPassword()));
        }
        return karyawanRepository.save(k);
    }

    @Transactional
    public void deleteKaryawan(String nik) {
        karyawanRepository.deleteById(nik);
    }

    private KaryawanDTO toDTO(Karyawan k) {
        return new KaryawanDTO(
                k.getNik(),
                k.getNama(),
                k.getAlamat(),
                k.getNoTlp(),
                k.getTglLahir(),
                k.getDivisi(),
                k.getJenisKel(),
                k.isStatus());

    }
}
