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
        for (String nik : new String[]{"EMP001", "EMP002", "EMP003", "EMP004", "EMP005", "EMP006", "EMP007", "EMP008", "EMP009", "EMP010"}) {
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

        Karyawan k4 = new Karyawan();
        k4.setNik("EMP004");
        k4.setNama("Fariza Harimas Putra");
        k4.setAlamat("Jl. Gatot Subroto No. 22");
        k4.setNoTlp("081234567893");
        k4.setPassword(encoded);
        k4.setTglLahir(LocalDate.of(1991, 3, 10));
        k4.setDivisi("Operations");
        k4.setJenisKel("L");
        karyawanRepository.save(k4);

        Karyawan k5 = new Karyawan();
        k5.setNik("EMP005");
        k5.setNama("Dewi Lestari");
        k5.setAlamat("Jl. Thamrin No. 15");
        k5.setNoTlp("081234567894");
        k5.setPassword(encoded);
        k5.setTglLahir(LocalDate.of(1993, 7, 25));
        k5.setDivisi("Finance");
        k5.setJenisKel("P");
        karyawanRepository.save(k5);

        Karyawan k6 = new Karyawan();
        k6.setNik("EMP006");
        k6.setNama("Budi Santoso");
        k6.setAlamat("Jl. Pemuda No. 8");
        k6.setNoTlp("081234567895");
        k6.setPassword(encoded);
        k6.setTglLahir(LocalDate.of(1989, 12, 5));
        k6.setDivisi("Human Resources");
        k6.setJenisKel("L");
        karyawanRepository.save(k6);

        Karyawan k7 = new Karyawan();
        k7.setNik("EMP007");
        k7.setNama("Siti Nurhaliza");
        k7.setAlamat("Jl. Diponegoro No. 33");
        k7.setNoTlp("081234567896");
        k7.setPassword(encoded);
        k7.setTglLahir(LocalDate.of(1994, 4, 18));
        k7.setDivisi("Marketing");
        k7.setJenisKel("P");
        karyawanRepository.save(k7);

        Karyawan k8 = new Karyawan();
        k8.setNik("EMP008");
        k8.setNama("Eko Prasetyo");
        k8.setAlamat("Jl. Ahmad Yani No. 12");
        k8.setNoTlp("081234567897");
        k8.setPassword(encoded);
        k8.setTglLahir(LocalDate.of(1987, 9, 30));
        k8.setDivisi("IT Support");
        k8.setJenisKel("L");
        karyawanRepository.save(k8);

        Karyawan k9 = new Karyawan();
        k9.setNik("EMP009");
        k9.setNama("Rina Wulandari");
        k9.setAlamat("Jl. Mangga Dua No. 7");
        k9.setNoTlp("081234567898");
        k9.setPassword(encoded);
        k9.setTglLahir(LocalDate.of(1995, 2, 14));
        k9.setDivisi("Sales");
        k9.setJenisKel("P");
        karyawanRepository.save(k9);

        Karyawan k10 = new Karyawan();
        k10.setNik("EMP010");
        k10.setNama("Fajar Nugroho");
        k10.setAlamat("Jl. Kebon Jeruk No. 19");
        k10.setNoTlp("081234567899");
        k10.setPassword(encoded);
        k10.setTglLahir(LocalDate.of(1992, 11, 22));
        k10.setDivisi("Operations");
        k10.setJenisKel("L");
        karyawanRepository.save(k10);
    }

    private void insertAbsenIfMissing(Admin admin) {
        if (absenRepository.findByTgl(LocalDate.now()).isPresent()) return;
        Absen absen = new Absen();
        absen.setTgl(LocalDate.now());
        absen.setAdmin(admin);
        absenRepository.save(absen);
    }
}
