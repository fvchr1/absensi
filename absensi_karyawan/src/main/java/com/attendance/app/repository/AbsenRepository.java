package com.attendance.app.repository;

import com.attendance.app.model.Absen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbsenRepository extends JpaRepository<Absen, Long> {
    Optional<Absen> findByTgl(LocalDate tgl);

    List<Absen> findByTglBetween(LocalDate start, LocalDate end);

    List<Absen> findByAdmin_IdAdmin(Long idAdmin);
}
