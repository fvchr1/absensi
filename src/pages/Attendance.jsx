import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { attendanceService } from "../services/api";
import "../styles/Attendance.css";

const Attendance = () => {
  const { user, logout, isAdmin, userName, userNik } = useContext(AuthContext);
  const [attendanceList, setAttendanceList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [startDate, setStartDate] = useState(
    new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().split("T")[0]
  );
  const [endDate, setEndDate] = useState(new Date().toISOString().split("T")[0]);

  useEffect(() => {
    fetchAttendance();
  }, []);

  const fetchAttendance = async () => {
    setLoading(true);
    try {
      if (isAdmin()) {
        const response = await attendanceService.getAllByRange(startDate, endDate);
        setAttendanceList(response.data || []);
      } else {
        const response = await attendanceService.getByKaryawanNikAndRange(
          userNik(),
          startDate,
          endDate
        );
        setAttendanceList(response.data || []);
      }
    } catch (error) {
      console.error("Error fetching attendance:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDateFilter = () => {
    fetchAttendance();
  };

  const formatTime = (t) => (t ? String(t).substring(0, 5) : "-");

  return (
    <div className="attendance-container">
      <nav className="navbar">
        <div className="navbar-brand">Sistem Absensi</div>
        <div className="navbar-menu">
          <span className="user-info">{userName()}</span>
          <button onClick={logout} className="logout-btn">Logout</button>
        </div>
      </nav>

      <div className="attendance-content">
        <a href="/dashboard" className="btn-back">← Kembali ke Dashboard</a>
        <h1>{isAdmin() ? "Semua Riwayat Absen" : "Riwayat Absensi"}</h1>

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
                {isAdmin() && <th>Kode Pegawai</th>}
                {isAdmin() && <th>Nama</th>}
                {isAdmin() && <th>Divisi</th>}
                <th>Tanggal</th>
                <th>Jam Masuk</th>
                <th>Jam Keluar</th>
                {isAdmin() && <th>Waktu Login</th>}
              </tr>
            </thead>
            <tbody>
              {attendanceList.map((record) => (
                <tr key={record.idMelakukan || record.tgl + record.nik}>
                  {isAdmin() && <td>{record.nik}</td>}
                  {isAdmin() && <td>{record.namaKaryawan}</td>}
                  {isAdmin() && <td>{record.divisiKaryawan || "-"}</td>}
                  <td>{record.tgl ? new Date(record.tgl).toLocaleDateString("id-ID") : "-"}</td>
                  <td>{formatTime(record.jamMasuk)}</td>
                  <td>{formatTime(record.jamKeluar)}</td>
                  {isAdmin() && <td>{record.createdAt ? new Date(record.createdAt).toLocaleString("id-ID") : "-"}</td>}
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
