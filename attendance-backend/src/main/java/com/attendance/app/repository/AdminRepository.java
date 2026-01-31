package com.attendance.app.repository;

import com.attendance.app.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByIdAdmin(Long idAdmin);

    Optional<Admin> findByNama(String nama);
}
