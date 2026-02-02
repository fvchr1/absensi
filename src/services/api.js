import axios from "axios";

// In dev, Vite proxies /api to backend so use relative URL (avoids CORS/network error)
const API_BASE_URL = import.meta.env.DEV ? "/api" : "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth: login with username (admin: "1" or "admin", karyawan: nik) + password
export const authService = {
  login: (username, password) => api.post("/auth/login", { username, password }),
};

// Karyawan (employees) - admin only
export const karyawanService = {
  getAll: () => api.get("/karyawan"),
  getByNik: (nik) => api.get(`/karyawan/${nik}`),
  create: (karyawan) => api.post("/karyawan", karyawan),
  update: (nik, karyawan) => api.put(`/karyawan/${nik}`, karyawan),
  delete: (nik) => api.delete(`/karyawan/${nik}`),
};

// Attendance (Melakukan) - check-in/out by nik
export const attendanceService = {
  checkIn: (nik) => api.post(`/attendance/check-in/${nik}`),
  checkOut: (nik) => api.post(`/attendance/check-out/${nik}`),
  getByKaryawanNik: (nik) => api.get(`/attendance/employee/${nik}`),
  getTodayByNik: (nik) => api.get(`/attendance/employee/${nik}/today`),
  getByKaryawanNikAndRange: (nik, startDate, endDate) =>
    api.get(`/attendance/employee/${nik}/range`, { params: { startDate, endDate } }),
  getAllByRange: (startDate, endDate) =>
    api.get("/attendance/all-range", { params: { startDate, endDate } }),
  getToday: () => api.get("/attendance/today"),
  getTodayWithKaryawan: () => api.get("/attendance/today-with-karyawan"),
  getMonthlyRecap: () => api.get("/attendance/monthly-recap"),
  getMonthlyRecapPerKaryawan: () => api.get("/attendance/monthly-recap-per-karyawan"),
};

// Leaves (Cuti) - by nik
export const leaveService = {
  requestLeave: (leave) => api.post("/leaves/request", leave),
  approveLeave: (id) => api.post(`/leaves/${id}/approve`),
  rejectLeave: (id, reason) => api.post(`/leaves/${id}/reject`, { reason }),
  getByNik: (nik) => api.get(`/leaves/employee/${nik}`),
  getPendingLeaves: () => api.get("/leaves/pending"),
  getCurrentlyOnLeave: () => api.get("/leaves/approved/currently-on-leave"),
  getByNikAndRange: (nik, startDate, endDate) =>
    api.get(`/leaves/employee/${nik}/range`, { params: { startDate, endDate } }),
  getById: (id) => api.get(`/leaves/${id}`),
  deleteLeave: (id) => api.delete(`/leaves/${id}`),
};

export default api;
