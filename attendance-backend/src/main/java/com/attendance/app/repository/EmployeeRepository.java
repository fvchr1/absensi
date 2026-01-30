package com.attendance.app.repository;

import com.attendance.app.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByNip(String nip);

    boolean existsByEmail(String email);

    boolean existsByNip(String nip);
}
