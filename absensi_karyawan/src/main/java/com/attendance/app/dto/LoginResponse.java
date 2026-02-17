package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
    private String role;       // "ADMIN" or "KARYAWAN"
    private Object user;       // AdminDTO or KaryawanDTO
}
