import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { attendanceService } from "../services/api";
import "../styles/Attendance.css";

const Attendance = () => {
  const { user, logout } = useContext(AuthContext);
  const [attendanceList, setAttendanceList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [startDate, setStartDate] = useState(new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().split("T")[0]);
  const [endDate, setEndDate] = useState(new Date().toISOString().split("T")[0]);

  useEffect(() => {
    fetchAttendance();
  }, []);

  const fetchAttendance = async () => {
    setLoading(true);
    try {
      const response = await attendanceService.getAttendanceByDateRange(user.id, startDate, endDate);
      setAttendanceList(response.data);
    } catch (error) {
      console.error("Error fetching attendance:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDateFilter = () => {
    fetchAttendance();
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "PRESENT":
        return "#4caf50";
      case "LATE":
        return "#ff9800";
      case "ABSENT":
        return "#f44336";
      case "SICK":
        return "#2196f3";
      case "LEAVE":
        return "#9c27b0";
      default:
        return "#666";
    }
  };

  return (
    <div className="attendance-container">
      <nav className="navbar">
        <div className="navbar-brand">Sistem Absensi</div>
        <div className="navbar-menu">
          <a href="/dashboard">Dashboard</a>
          <span className="user-info">{user?.name}</span>
          <button onClick={logout} className="logout-btn">
            Logout
          </button>
        </div>
      </nav>

      <div className="attendance-content">
        <h1>Riwayat Absensi</h1>

        <div className="filter-section">
          <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
          <span>sampai</span>
          <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
          <button onClick={handleDateFilter}>Filter</button>
        </div>

        {loading ? (
          <p>Loading...</p>
        ) : attendanceList.length > 0 ? (
          <table className="attendance-table">
            <thead>
              <tr>
                <th>Tanggal</th>
                <th>Check-In</th>
                <th>Check-Out</th>
                <th>Status</th>
                <th>Keterangan</th>
              </tr>
            </thead>
            <tbody>
              {attendanceList.map((record) => (
                <tr key={record.id}>
                  <td>{new Date(record.attendanceDate).toLocaleDateString("id-ID")}</td>
                  <td>{record.checkInTime}</td>
                  <td>{record.checkOutTime || "-"}</td>
                  <td>
                    <span className="status-badge" style={{ backgroundColor: getStatusColor(record.status) }}>
                      {record.status}
                    </span>
                  </td>
                  <td>{record.notes || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">Tidak ada data absensi</p>
        )}
      </div>
    </div>
  );
};

export default Attendance;
