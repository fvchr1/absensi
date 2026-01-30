import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth Services
export const authService = {
  login: (email, password) => api.post("/auth/login", { email, password }),
  register: (employee) => api.post("/auth/register", employee),
};

// Employee Services
export const employeeService = {
  getAllEmployees: () => api.get("/employees"),
  getEmployeeById: (id) => api.get(`/employees/${id}`),
  createEmployee: (employee) => api.post("/employees", employee),
  updateEmployee: (id, employee) => api.put(`/employees/${id}`, employee),
  deleteEmployee: (id) => api.delete(`/employees/${id}`),
};

// Attendance Services
export const attendanceService = {
  checkIn: (employeeId) => api.post(`/attendance/check-in/${employeeId}`),
  checkOut: (employeeId) => api.post(`/attendance/check-out/${employeeId}`),
  getAttendanceByEmployee: (employeeId) => api.get(`/attendance/employee/${employeeId}`),
  getAttendanceByDateRange: (employeeId, startDate, endDate) => api.get(`/attendance/employee/${employeeId}/range`, { params: { startDate, endDate } }),
  getAllAttendanceByDateRange: (startDate, endDate) => api.get("/attendance/all-range", { params: { startDate, endDate } }),
  getAttendanceById: (id) => api.get(`/attendance/${id}`),
  updateAttendance: (id, attendance) => api.put(`/attendance/${id}`, attendance),
  markAttendance: (attendance) => api.post("/attendance", attendance),
  deleteAttendance: (id) => api.delete(`/attendance/${id}`),
};

// Leave Services
export const leaveService = {
  requestLeave: (leave) => api.post("/leaves/request", leave),
  approveLeave: (id) => api.post(`/leaves/${id}/approve`),
  rejectLeave: (id, reason) => api.post(`/leaves/${id}/reject`, { reason }),
  getLeaveByEmployee: (employeeId) => api.get(`/leaves/employee/${employeeId}`),
  getPendingLeaves: () => api.get("/leaves/pending"),
  getLeaveByDateRange: (employeeId, startDate, endDate) => api.get(`/leaves/employee/${employeeId}/range`, { params: { startDate, endDate } }),
  getLeaveById: (id) => api.get(`/leaves/${id}`),
  deleteLeave: (id) => api.delete(`/leaves/${id}`),
};

export default api;
