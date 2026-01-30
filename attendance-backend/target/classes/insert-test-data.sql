-- File ini berisi INSERT data untuk testing
-- Password: password123
-- Hash BCrypt (strength 10): $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm

-- H2 does not support USE statement, database is used automatically

-- Kosongkan table yang sudah ada (opsional)
-- DELETE FROM leaves;
-- DELETE FROM attendance;
-- DELETE FROM employees;

-- Insert employee dengan password ter-hash
INSERT INTO employees (nip, name, email, password, department, position, phone_number, role, active) 
VALUES
('001', 'Admin User', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'IT', 'Administrator', '081234567890', 'ADMIN', TRUE),
('002', 'Manager User', 'manager@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'HR', 'HR Manager', '081234567891', 'MANAGER', TRUE),
('003', 'Budi Santoso', 'budi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Sales', 'Sales Staff', '081234567892', 'EMPLOYEE', TRUE),
('004', 'Siti Nurhaliza', 'siti@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Marketing', 'Marketing Specialist', '081234567893', 'EMPLOYEE', TRUE),
('005', 'Ahmad Wijaya', 'ahmad@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Finance', 'Accountant', '081234567894', 'EMPLOYEE', TRUE);

-- Insert sample attendance data
INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status) 
VALUES
(1, CURDATE(), '08:00:00', '17:00:00', 'PRESENT'),
(2, CURDATE(), '08:15:00', '17:15:00', 'LATE'),
(3, CURDATE(), '08:05:00', '17:05:00', 'PRESENT'),
(4, CURDATE(), '08:00:00', NULL, 'PRESENT'),
(5, CURDATE(), NULL, NULL, 'ABSENT');

-- Insert sample leave data
INSERT INTO leaves (employee_id, start_date, end_date, leave_type, reason, approval_status) 
VALUES
(1, '2026-01-23', '2026-01-26', 'ANNUAL', 'Liburan ke Bali', 'APPROVED'),
(2, '2026-01-17', '2026-01-18', 'SICK', 'Demam tinggi', 'PENDING'),
(3, '2026-01-30', '2026-01-30', 'PERSONAL', 'Keperluan pribadi', 'PENDING');
