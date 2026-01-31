package com.attendance.app.service;

import com.attendance.app.dto.LeaveDTO;
import com.attendance.app.model.Leave;
import com.attendance.app.model.Karyawan;
import com.attendance.app.repository.LeaveRepository;
import com.attendance.app.repository.KaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private MelakukanService melakukanService;

    public LeaveDTO requestLeave(Leave leave) {
        Karyawan karyawan = karyawanRepository.findByNik(leave.getKaryawan().getNik())
                .orElseThrow(() -> new RuntimeException("Karyawan not found"));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Jakarta"));
        if (melakukanService.hasAttendedOnDate(karyawan.getNik(), today)
                && (leave.getStartDate().isBefore(today) || leave.getStartDate().equals(today))) {
            throw new RuntimeException("Anda sudah absen hari ini. Cuti harus dimulai besok (H+1).");
        }

        leave.setKaryawan(karyawan);
        leave.setApprovalStatus(Leave.ApprovalStatus.PENDING);

        Leave savedLeave = leaveRepository.save(leave);
        return convertToDTO(savedLeave);
    }

    public LeaveDTO approveLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        leave.setApprovalStatus(Leave.ApprovalStatus.APPROVED);
        Leave updated = leaveRepository.save(leave);
        return convertToDTO(updated);
    }

    public LeaveDTO rejectLeave(Long leaveId, String reason) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        leave.setApprovalStatus(Leave.ApprovalStatus.REJECTED);
        leave.setApproverNotes(reason);
        Leave updated = leaveRepository.save(leave);
        return convertToDTO(updated);
    }

    public List<LeaveDTO> getLeaveByNik(String nik) {
        return leaveRepository.findByKaryawan_Nik(nik).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getPendingLeaves() {
        return leaveRepository.findByApprovalStatus(Leave.ApprovalStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /** Approved leaves where today (Asia/Jakarta) is within [startDate, endDate] — status cuti = true only on these dates. */
    public List<LeaveDTO> getCurrentlyOnLeave() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Jakarta"));
        return leaveRepository
                .findByApprovalStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        Leave.ApprovalStatus.APPROVED, today, today)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getLeaveByDateRange(String nik, LocalDate startDate, LocalDate endDate) {
        return leaveRepository.findByStartDateBetweenAndKaryawan_Nik(startDate, endDate, nik)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LeaveDTO getLeaveById(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        return convertToDTO(leave);
    }

    public void deleteLeave(Long id) {
        leaveRepository.deleteById(id);
    }

    private LeaveDTO convertToDTO(Leave leave) {
        return new LeaveDTO(
                leave.getId(),
                leave.getKaryawan().getNik(),
                leave.getKaryawan().getNama(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getLeaveType().name(),
                leave.getReason(),
                leave.getApprovalStatus().name(),
                leave.getApproverNotes());
    }
}
