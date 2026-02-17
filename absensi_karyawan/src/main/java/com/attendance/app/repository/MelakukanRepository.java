package com.attendance.app.repository;

import com.attendance.app.model.Melakukan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MelakukanRepository extends JpaRepository<Melakukan, Long> {
    List<Melakukan> findByKaryawan_Nik(String nik);

    Optional<Melakukan> findByAbsen_IdAbsenAndKaryawan_Nik(Long idAbsen, String nik);

    List<Melakukan> findByAbsen_TglBetweenAndKaryawan_Nik(LocalDate start, LocalDate end, String nik);

    List<Melakukan> findByAbsen_TglBetween(LocalDate start, LocalDate end);

    List<Melakukan> findByAbsen_IdAbsen(Long idAbsen);
}
