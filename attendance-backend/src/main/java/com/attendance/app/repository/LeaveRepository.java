package com.attendance.app.repository;

import com.attendance.app.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployeeId(Long employeeId);

    List<Leave> findByStartDateBetweenAndEmployeeId(LocalDate startDate, LocalDate endDate, Long employeeId);

    List<Leave> findByApprovalStatus(Leave.ApprovalStatus approvalStatus);

    List<Leave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
