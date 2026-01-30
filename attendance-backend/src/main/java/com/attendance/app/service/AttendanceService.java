package com.attendance.app.service;

import com.attendance.app.dto.AttendanceDTO;
import com.attendance.app.model.Attendance;
import com.attendance.app.model.Employee;
import com.attendance.app.repository.AttendanceRepository;
import com.attendance.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Attendance checkIn(Long employeeId) {
        Optional<Attendance> existingRecord = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now());

        if (existingRecord.isPresent()) {
            throw new RuntimeException("Employee already checked in today");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus(Attendance.Status.PRESENT);

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long employeeId) {
        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No check-in record found for today"));

        attendance.setCheckOutTime(LocalTime.now());
        return attendanceRepository.save(attendance);
    }

    public Attendance markAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public List<AttendanceDTO> getAttendanceByEmployeeId(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAttendanceByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByAttendanceDateBetweenAndEmployeeId(startDate, endDate, employeeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAllAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByAttendanceDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AttendanceDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));
        return convertToDTO(attendance);
    }

    public AttendanceDTO updateAttendance(Long id, Attendance attendanceDetails) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        if (attendanceDetails.getCheckInTime() != null) {
            attendance.setCheckInTime(attendanceDetails.getCheckInTime());
        }
        if (attendanceDetails.getCheckOutTime() != null) {
            attendance.setCheckOutTime(attendanceDetails.getCheckOutTime());
        }
        if (attendanceDetails.getStatus() != null) {
            attendance.setStatus(attendanceDetails.getStatus());
        }
        if (attendanceDetails.getNotes() != null) {
            attendance.setNotes(attendanceDetails.getNotes());
        }

        Attendance updated = attendanceRepository.save(attendance);
        return convertToDTO(updated);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        return new AttendanceDTO(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getEmployee().getName(),
                attendance.getEmployee().getNip(),
                attendance.getAttendanceDate(),
                attendance.getCheckInTime(),
                attendance.getCheckOutTime(),
                attendance.getStatus().name(),
                attendance.getNotes());
    }
}
