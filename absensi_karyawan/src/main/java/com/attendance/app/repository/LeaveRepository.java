package com.attendance.app.repository;

import com.attendance.app.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByKaryawan_Nik(String nik);

    List<Leave> findByStartDateBetweenAndKaryawan_Nik(LocalDate startDate, LocalDate endDate, String nik);

    List<Leave> findByApprovalStatus(Leave.ApprovalStatus approvalStatus);

    /** Approved leaves where today is between startDate and endDate (karyawan sedang cuti hari ini). */
    List<Leave> findByApprovalStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Leave.ApprovalStatus approvalStatus, LocalDate startOnOrBefore, LocalDate endOnOrAfter);

    List<Leave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
}
