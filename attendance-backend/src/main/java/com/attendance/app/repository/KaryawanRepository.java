package com.attendance.app.repository;

import com.attendance.app.model.Karyawan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KaryawanRepository extends JpaRepository<Karyawan, String> {
    Optional<Karyawan> findByNik(String nik);

    boolean existsByNik(String nik);
}
