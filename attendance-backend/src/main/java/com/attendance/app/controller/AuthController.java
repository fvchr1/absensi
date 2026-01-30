package com.attendance.app.controller;

import com.attendance.app.dto.LoginRequest;
import com.attendance.app.dto.LoginResponse;
import com.attendance.app.model.Employee;
import com.attendance.app.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = employeeService.login(loginRequest);
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Employee employee) {
        try {
            Employee registeredEmployee = employeeService.registerEmployee(employee);
            return ResponseEntity.ok(new HashMap<String, Object>() {
                {
                    put("message", "Employee registered successfully");
                    put("employee", registeredEmployee);
                }
            });
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {
                {
                    put("error", e.getMessage());
                }
            });
        }
    }
}
