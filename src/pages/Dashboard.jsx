import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { attendanceService } from "../services/api";
import "../styles/Dashboard.css";

const Dashboard = () => {
  const { user, logout } = useContext(AuthContext);
  const [checkedIn, setCheckedIn] = useState(false);
  const [checkInTime, setCheckInTime] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    checkTodayAttendance();
  }, []);

  const checkTodayAttendance = async () => {
    try {
      const response = await attendanceService.getAttendanceByEmployee(user.id);
      const today = new Date().toISOString().split("T")[0];
      const todayRecord = response.data.find((record) => record.attendanceDate === today);
      if (todayRecord) {
        setCheckedIn(true);
        setCheckInTime(todayRecord.checkInTime);
      }
    } catch (err) {
      console.error("Error checking attendance:", err);
    }
  };

  const handleCheckIn = async () => {
    setLoading(true);
    try {
      await attendanceService.checkIn(user.id);
      setCheckedIn(true);
      setCheckInTime(new Date().toLocaleTimeString());
      setError("");
    } catch (err) {
      setError("Anda sudah check-in hari ini");
    } finally {
      setLoading(false);
    }
  };

  const handleCheckOut = async () => {
    setLoading(true);
    try {
      await attendanceService.checkOut(user.id);
      setError("");
    } catch (err) {
      setError("Error saat check-out");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-container">
      <nav className="navbar">
        <div className="navbar-brand">Sistem Absensi</div>
        <div className="navbar-menu">
          <span className="user-info">Welcome, {user?.name}</span>
          <button onClick={logout} className="logout-btn">
            Logout
          </button>
        </div>
      </nav>

      <div className="dashboard-content">
        <div className="greeting">
          <h1>Selamat Datang, {user?.name}!</h1>
          <p>
            {user?.department} - {user?.position}
          </p>
        </div>

        <div className="attendance-card">
          <h2>Absensi Hari Ini</h2>
          {error && <div className="error-message">{error}</div>}

          <div className="status-info">
            {checkedIn ? (
              <div className="checked-in">
                <p>✓ Anda sudah check-in</p>
                <p className="time">Jam check-in: {checkInTime}</p>
              </div>
            ) : (
              <p>Anda belum check-in</p>
            )}
          </div>

          <div className="button-group">
            <button onClick={handleCheckIn} disabled={checkedIn || loading} className={checkedIn ? "disabled" : ""}>
              {loading ? "Processing..." : "Check-In"}
            </button>
            <button onClick={handleCheckOut} disabled={!checkedIn || loading} className={!checkedIn ? "disabled" : ""}>
              {loading ? "Processing..." : "Check-Out"}
            </button>
          </div>
        </div>

        <div className="quick-links">
          <div className="link-card">
            <h3>📊 Riwayat Absensi</h3>
            <a href="/attendance">Lihat Riwayat</a>
          </div>
          <div className="link-card">
            <h3>📝 Pengajuan Cuti</h3>
            <a href="/leaves">Kelola Cuti</a>
          </div>
          <div className="link-card">
            <h3>👥 Data Karyawan</h3>
            <a href="/employees">Lihat Data</a>
          </div>
          <div className="link-card">
            <h3>⚙️ Profil</h3>
            <a href="/profile">Edit Profil</a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
