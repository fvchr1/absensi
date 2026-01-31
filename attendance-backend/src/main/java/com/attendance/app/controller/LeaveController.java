package com.attendance.app.controller;

import com.attendance.app.dto.LeaveDTO;
import com.attendance.app.model.Leave;
import com.attendance.app.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000", "http://127.0.0.1:3000"}, maxAge = 3600)
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/request")
    public ResponseEntity<?> requestLeave(@RequestBody Leave leave) {
        try {
            LeaveDTO leaveDTO = leaveService.requestLeave(leave);
            return ResponseEntity.ok(leaveDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        try {
            LeaveDTO leaveDTO = leaveService.approveLeave(id);
            return ResponseEntity.ok(leaveDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id, @RequestBody HashMap<String, String> body) {
        try {
            String reason = body.get("reason");
            LeaveDTO leaveDTO = leaveService.rejectLeave(id, reason);
            return ResponseEntity.ok(leaveDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/employee/{nik}")
    public ResponseEntity<List<LeaveDTO>> getLeaveByNik(@PathVariable String nik) {
        return ResponseEntity.ok(leaveService.getLeaveByNik(nik));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveDTO>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    @GetMapping("/approved/currently-on-leave")
    public ResponseEntity<List<LeaveDTO>> getCurrentlyOnLeave() {
        return ResponseEntity.ok(leaveService.getCurrentlyOnLeave());
    }

    @GetMapping("/employee/{nik}/range")
    public ResponseEntity<List<LeaveDTO>> getLeaveByDateRange(
            @PathVariable String nik,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(leaveService.getLeaveByDateRange(nik, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveDTO> getLeaveById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(leaveService.getLeaveById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.ok("Leave request deleted successfully");
    }
}
