package com.attendance.app.service;

import com.attendance.app.dto.AdminDTO;
import com.attendance.app.dto.KaryawanDTO;
import com.attendance.app.dto.LoginRequest;
import com.attendance.app.dto.LoginResponse;
import com.attendance.app.model.Admin;
import com.attendance.app.model.Karyawan;
import com.attendance.app.repository.AdminRepository;
import com.attendance.app.repository.KaryawanRepository;
import com.attendance.app.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : null;
        String password = request.getPassword() != null ? request.getPassword() : null;
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return new LoginResponse(null, "Username and password required", null, null);
        }

        // Try Admin: username can be id_admin as string (e.g. "1") or "admin"
        Optional<Admin> adminOpt = tryAdminLogin(username, password);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            String subject = "ADMIN:" + admin.getIdAdmin();
            String token = jwtTokenProvider.generateToken(subject, "ADMIN");
            AdminDTO dto = new AdminDTO(admin.getIdAdmin(), admin.getNama());
            return new LoginResponse(token, "Login successful", "ADMIN", dto);
        }

        // Try Karyawan: username = nik
        Optional<Karyawan> karyawanOpt = karyawanRepository.findByNik(username);
        if (karyawanOpt.isPresent() && passwordEncoder.matches(password, karyawanOpt.get().getPassword())) {
            Karyawan k = karyawanOpt.get();
            String subject = "KARYAWAN:" + k.getNik();
            String token = jwtTokenProvider.generateToken(subject, "KARYAWAN");
            KaryawanDTO dto = toKaryawanDTO(k);
            return new LoginResponse(token, "Login successful", "KARYAWAN", dto);
        }

        return new LoginResponse(null, "Invalid username or password", null, null);
    }

    private Optional<Admin> tryAdminLogin(String username, String password) {
        // Try by id_admin (numeric string)
        try {
            Long id = Long.parseLong(username);
            Optional<Admin> a = adminRepository.findById(id);
            if (a.isPresent() && passwordEncoder.matches(password, a.get().getPassword())) {
                return a;
            }
        } catch (NumberFormatException ignored) {}
        // Try by nama (e.g. "admin")
        Optional<Admin> byNama = adminRepository.findByNama(username);
        if (byNama.isPresent() && passwordEncoder.matches(password, byNama.get().getPassword())) {
            return byNama;
        }
        return Optional.empty();
    }

    private KaryawanDTO toKaryawanDTO(Karyawan k) {
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
