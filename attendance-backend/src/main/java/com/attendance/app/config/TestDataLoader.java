package com.attendance.app.config;

import com.attendance.app.model.Admin;
import com.attendance.app.model.Absen;
import com.attendance.app.model.Karyawan;
import com.attendance.app.repository.AbsenRepository;
import com.attendance.app.repository.AdminRepository;
import com.attendance.app.repository.KaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Inserts test accounts on startup using the same PasswordEncoder as login,
 * so username/password always match. Test password: password123
 */
@Component
public class TestDataLoader implements ApplicationRunner {

    private static final String TEST_PASSWORD = "password123";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private AbsenRepository absenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Admin admin = insertAdminIfMissing();
        insertKaryawanIfMissing();
        insertAbsenIfMissing(admin);
    }

    private Admin insertAdminIfMissing() {
        String encoded = passwordEncoder.encode(TEST_PASSWORD);
        Admin admin = adminRepository.findByNama("admin").orElse(null);
        if (admin == null) {
            admin = new Admin();
            admin.setNama("admin");
            admin.setPassword(encoded);
            admin = adminRepository.save(admin);
        } else {
            admin.setPassword(encoded);
            admin = adminRepository.save(admin);
        }
        return admin;
    }

    private void insertKaryawanIfMissing() {
        String encoded = passwordEncoder.encode(TEST_PASSWORD);
        for (String nik : new String[]{"EMP001", "EMP002", "EMP003"}) {
            karyawanRepository.findByNik(nik).ifPresent(k -> {
                k.setPassword(encoded);
                karyawanRepository.save(k);
            });
        }
        if (karyawanRepository.existsByNik("EMP001")) return;

        Karyawan k1 = new Karyawan();
        k1.setNik("EMP001");
        k1.setNama("Dandi Hamdalah");
        k1.setAlamat("Jl. Contoh No. 1");
        k1.setNoTlp("081234567890");
        k1.setPassword(encoded);
        k1.setTglLahir(LocalDate.of(1990, 1, 15));
        k1.setDivisi("Sales");
        k1.setJenisKel("L");
        karyawanRepository.save(k1);

        Karyawan k2 = new Karyawan();
        k2.setNik("EMP002");
        k2.setNama("Syahdan Maulana");
        k2.setAlamat("Jl. Merdeka No. 10");
        k2.setNoTlp("081234567891");
        k2.setPassword(encoded);
        k2.setTglLahir(LocalDate.of(1992, 5, 20));
        k2.setDivisi("Marketing");
        k2.setJenisKel("P");
        karyawanRepository.save(k2);

        Karyawan k3 = new Karyawan();
        k3.setNik("EMP003");
        k3.setNama("Hendra Wijaya");
        k3.setAlamat("Jl. Sudirman No. 5");
        k3.setNoTlp("081234567892");
        k3.setPassword(encoded);
        k3.setTglLahir(LocalDate.of(1988, 11, 8));
        k3.setDivisi("IT");
        k3.setJenisKel("L");
        karyawanRepository.save(k3);
    }

    private void insertAbsenIfMissing(Admin admin) {
        if (absenRepository.findByTgl(LocalDate.now()).isPresent()) return;
        Absen absen = new Absen();
        absen.setTgl(LocalDate.now());
        absen.setAdmin(admin);
        absenRepository.save(absen);
    }
}
