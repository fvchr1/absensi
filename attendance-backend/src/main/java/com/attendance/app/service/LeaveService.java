package com.attendance.app.service;

import com.attendance.app.dto.LeaveDTO;
import com.attendance.app.model.Leave;
import com.attendance.app.model.Employee;
import com.attendance.app.repository.LeaveRepository;
import com.attendance.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public LeaveDTO requestLeave(Leave leave) {
        Employee employee = employeeRepository.findById(leave.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        leave.setEmployee(employee);
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

    public List<LeaveDTO> getLeaveByEmployeeId(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getPendingLeaves() {
        return leaveRepository.findByApprovalStatus(Leave.ApprovalStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getLeaveByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return leaveRepository.findByStartDateBetweenAndEmployeeId(startDate, endDate, employeeId)
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
                leave.getEmployee().getId(),
                leave.getEmployee().getName(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getLeaveType().name(),
                leave.getReason(),
                leave.getApprovalStatus().name(),
                leave.getApproverNotes());
    }
}
