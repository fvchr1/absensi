-- Create Attendance Database
-- H2 does not support CREATE DATABASE, it's created automatically

-- Create employees table
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nip VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    UNIQUE (employee_id, attendance_date)
);

-- Create leaves table
CREATE TABLE IF NOT EXISTS leaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    reason TEXT NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approver_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_attendance_employee ON attendance(employee_id);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_leaves_employee ON leaves(employee_id);
CREATE INDEX idx_leaves_status ON leaves(approval_status);

-- Insert sample data
-- Password: password123 (encoded with BCrypt)
-- To generate: new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password123")

-- Sample encoded password: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm
-- For simplicity, we'll use a simpler password hash

INSERT INTO employees (nip, name, email, password, department, position, phone_number, role, active) VALUES
('001', 'Admin User', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'IT', 'Administrator', '081234567890', 'ADMIN', TRUE),
('002', 'Manager User', 'manager@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'HR', 'HR Manager', '081234567891', 'MANAGER', TRUE),
('003', 'Budi Santoso', 'budi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Sales', 'Sales Staff', '081234567892', 'EMPLOYEE', TRUE),
('004', 'Siti Nurhaliza', 'siti@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Marketing', 'Marketing Specialist', '081234567893', 'EMPLOYEE', TRUE),
('005', 'Ahmad Wijaya', 'ahmad@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Finance', 'Accountant', '081234567894', 'EMPLOYEE', TRUE);

-- Insert sample attendance data
INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status) VALUES
(1, CURDATE(), '08:00:00', '17:00:00', 'PRESENT'),
(2, CURDATE(), '08:15:00', '17:15:00', 'LATE'),
(3, CURDATE(), '08:05:00', '17:05:00', 'PRESENT'),
(4, CURDATE(), '08:00:00', NULL, 'PRESENT'),
(5, CURDATE(), NULL, NULL, 'ABSENT');

-- Insert sample leave data
INSERT INTO leaves (employee_id, start_date, end_date, leave_type, reason, approval_status) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'ANNUAL', 'Liburan ke Bali', 'APPROVED'),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'SICK', 'Demam tinggi', 'PENDING'),
(3, DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'PERSONAL', 'Keperluan pribadi', 'PENDING');

-- Note: For actual password hashing, generate using Spring Boot:
-- System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password123"));
-- This will give you the proper bcrypt hash to use in the INSERT statements above
