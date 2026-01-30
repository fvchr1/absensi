package com.attendance.app.service;

import com.attendance.app.dto.EmployeeDTO;
import com.attendance.app.dto.LoginRequest;
import com.attendance.app.dto.LoginResponse;
import com.attendance.app.model.Employee;
import com.attendance.app.repository.EmployeeRepository;
import com.attendance.app.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Employee> employee = employeeRepository.findByEmail(loginRequest.getEmail());

        if (employee.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), employee.get().getPassword())) {
            String token = jwtTokenProvider.generateToken(employee.get().getEmail());
            return new LoginResponse(token, "Login successful", convertToDTO(employee.get()));
        }

        return new LoginResponse(null, "Invalid credentials", null);
    }

    public Employee registerEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (employeeRepository.existsByNip(employee.getNip())) {
            throw new RuntimeException("NIP already exists");
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Optional<Employee> employee = employeeRepository.findById(id);

        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setName(employeeDetails.getName());
            emp.setEmail(employeeDetails.getEmail());
            emp.setDepartment(employeeDetails.getDepartment());
            emp.setPosition(employeeDetails.getPosition());
            emp.setPhoneNumber(employeeDetails.getPhoneNumber());
            emp.setActive(employeeDetails.getActive());

            return employeeRepository.save(emp);
        }

        throw new RuntimeException("Employee not found");
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getNip(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getPhoneNumber(),
                employee.getRole().name(),
                employee.getActive());
    }
}
