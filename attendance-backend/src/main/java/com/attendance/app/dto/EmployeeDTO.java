package com.attendance.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;
    private String nip;
    private String name;
    private String email;
    private String department;
    private String position;
    private String phoneNumber;
    private String role;
    private Boolean active;
}
