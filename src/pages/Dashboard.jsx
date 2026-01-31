import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { attendanceService, karyawanService, leaveService } from "../services/api";
import "../styles/Dashboard.css";

const Dashboard = () => {
  const { user, role, logout, isAdmin, userName, userNik } = useContext(AuthContext);
  const [checkedIn, setCheckedIn] = useState(false);
  const [checkInTime, setCheckInTime] = useState(null);
  const [checkOutTime, setCheckOutTime] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  // Admin stats
  const [totalKaryawan, setTotalKaryawan] = useState(0);
  const [todayAttendance, setTodayAttendance] = useState([]);
  const [pendingLeaves, setPendingLeaves] = useState(0);
  const [currentlyOnLeave, setCurrentlyOnLeave] = useState([]);
  const [filterKodePegawai, setFilterKodePegawai] = useState("");
  const [karyawanSedangCuti, setKaryawanSedangCuti] = useState(false);

  useEffect(() => {
    if (isAdmin()) {
      loadAdminStats();
    } else {
      checkTodayAttendance();
      checkKaryawanOnLeave();
    }
  }, [role]);

  const checkKaryawanOnLeave = async () => {
    const nik = userNik();
    if (!nik) return;
    try {
      const res = await leaveService.getCurrentlyOnLeave();
      const list = res.data ?? [];
      setKaryawanSedangCuti(list.some((l) => l.nik === nik));
    } catch {
      setKaryawanSedangCuti(false);
    }
  };

  const formatDateTime = (tgl, jam) => {
    if (!tgl && !jam) return "-";
    const d = tgl ? new Date(tgl) : new Date();
    const dateStr = d.toLocaleDateString("id-ID", { weekday: "short", day: "2-digit", month: "short", year: "numeric" });
    const timeStr = jam ? String(jam).substring(0, 5) : "-";
    return `${dateStr} ${timeStr}`;
  };

  const checkTodayAttendance = async () => {
    const nik = userNik();
    if (!nik) return;
    try {
      const res = await attendanceService.getTodayByNik(nik);
      const data = res.data;
      if (data?.jamKeluar) {
        setCheckedIn(false);
        setCheckInTime(null);
        setCheckOutTime(formatDateTime(data.tgl, data.jamKeluar));
      } else if (data) {
        setCheckedIn(true);
        setCheckInTime(formatDateTime(data.tgl, data.jamMasuk));
        setCheckOutTime(null);
      } else {
        setCheckedIn(false);
        setCheckInTime(null);
        setCheckOutTime(null);
      }
    } catch {
      setCheckedIn(false);
      setCheckInTime(null);
      setCheckOutTime(null);
    }
  };

  const loadAdminStats = async () => {
    try {
      const karyawanRes = await karyawanService.getAll();
      const allKaryawan = karyawanRes.data ?? [];
      setTotalKaryawan(allKaryawan.length);
    } catch (e) {
      console.error("getKaryawan:", e);
    }
    try {
      const todayRes = await attendanceService.getToday();
      setTodayAttendance(todayRes.data ?? []);
    } catch (e) {
      console.error("getToday:", e);
    }
    try {
      const leavesRes = await leaveService.getPendingLeaves();
      setPendingLeaves(leavesRes.data?.length ?? 0);
    } catch (e) {
      console.error("getPendingLeaves:", e);
    }
    try {
      const onLeaveRes = await leaveService.getCurrentlyOnLeave();
      setCurrentlyOnLeave(onLeaveRes.data ?? []);
    } catch (e) {
      console.error("getCurrentlyOnLeave:", e);
    }
  };

  const handleCheckIn = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await attendanceService.checkIn(userNik());
      const data = res.data;
      setCheckedIn(true);
      setCheckInTime(formatDateTime(data.tgl, data.jamMasuk));
    } catch (err) {
      setError(err.response?.data || "Anda sudah check-in hari ini");
    } finally {
      setLoading(false);
    }
  };

  const handleCheckOut = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await attendanceService.checkOut(userNik());
      const data = res.data;
      setCheckedIn(false);
      setCheckInTime(null);
      setCheckOutTime(data ? formatDateTime(data.tgl, data.jamKeluar) : null);
    } catch (err) {
      setError(err.response?.data || "Error saat check-out");
    } finally {
      setLoading(false);
    }
  };

  if (isAdmin()) {
    return (
      <div className="dashboard-container">
        <nav className="navbar">
          <div className="navbar-brand">Sistem Absensi — Admin</div>
          <div className="navbar-menu">
            <button onClick={logout} className="logout-btn">Logout</button>
          </div>
        </nav>

        <div className="dashboard-content">
          <div className="greeting">
            <h1>Dashboard</h1>
            <p>Kelola karyawan, absen, dan cuti</p>
          </div>

          <div className="admin-stats">
            <div className="stat-card">
              <h3>Total Karyawan</h3>
              <p className="stat-value">{totalKaryawan}</p>
              <p className="stat-sub">Sedang cuti: {currentlyOnLeave.length}</p>
              <a href="/employees">Detail Karyawan</a>
            </div>
            <div className="stat-card">
              <h3>Cuti Pending</h3>
              <p className="stat-value">{pendingLeaves}</p>
              <a href="/leaves">Kelola Cuti</a>
            </div>
          </div>

          <div className="admin-today-attendance">
            <div className="section-header">
              <h2>Daftar Absen Hari Ini</h2>
              <div className="section-actions">
                <input
                  type="text"
                  placeholder="Filter Kode Pegawai..."
                  value={filterKodePegawai}
                  onChange={(e) => setFilterKodePegawai(e.target.value)}
                  className="filter-input"
                />
                <button
                  type="button"
                  onClick={() => {
                    setFilterKodePegawai("");
                    loadAdminStats();
                  }}
                  className="btn-refresh"
                >
                  🔄 Refresh
                </button>
              </div>
            </div>
            {todayAttendance.length > 0 ? (
              <table className="today-attendance-table">
                <thead>
                  <tr>
                    <th>Kode Pegawai</th>
                    <th>Nama</th>
                    <th>Divisi</th>
                    <th>Jam Masuk</th>
                    <th>Jam Keluar</th>
                    <th>Waktu Login</th>
                  </tr>
                </thead>
                <tbody>
                  {todayAttendance
                    .filter(
                      (row) =>
                        !filterKodePegawai.trim() ||
                        (row.nik && row.nik.toLowerCase().includes(filterKodePegawai.trim().toLowerCase()))
                    )
                    .map((row) => (
                      <tr key={row.idMelakukan ?? row.nik}>
                        <td>{row.nik}</td>
                        <td>{row.namaKaryawan || "-"}</td>
                        <td>{row.divisiKaryawan || "-"}</td>
                        <td>{row.jamMasuk ? String(row.jamMasuk).substring(0, 5) : "-"}</td>
                        <td>{row.jamKeluar ? String(row.jamKeluar).substring(0, 5) : "-"}</td>
                        <td>{row.createdAt ? new Date(row.createdAt).toLocaleString("id-ID") : "-"}</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            ) : (
              <p className="no-attendance">Tidak ada karyawan yang absen hari ini.</p>
            )}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <nav className="navbar">
        <div className="navbar-brand">Sistem Absensi</div>
        <div className="navbar-menu">
          <span className="user-info">Hello, {userName()}</span>
          <button onClick={logout} className="logout-btn">Logout</button>
        </div>
      </nav>

      <div className="dashboard-content">
        <div className="greeting">
          <h1>Selamat Datang, {userName()}!</h1>
          <p>Divisi: {user?.divisi && `${user.divisi}`}</p>
        </div>

        <div className="attendance-card">
          <h2>Absensi Hari Ini</h2>
          {error && <div className="error-message">{String(error)}</div>}

          {karyawanSedangCuti && (
            <div className="status-info on-leave-notice">
              <p>Anda sedang cuti hari ini. Check-in, check-out, dan pengajuan cuti tidak tersedia.</p>
            </div>
          )}

          {!karyawanSedangCuti && (
            <>
              <div className="status-info">
                {checkOutTime ? (
                  <div className="checked-out">
                    <p>✓ Anda sudah check-out hari ini</p>
                    <p className="time">Jam check-out: {checkOutTime}</p>
                  </div>
                ) : checkedIn ? (
                  <div className="checked-in">
                    <p>✓ Anda sudah check-in</p>
                    <p className="time">Jam check-in: {checkInTime}</p>
                  </div>
                ) : (
                  <p>Anda belum check-in</p>
                )}
              </div>

              <div className="button-group">
                <button
                  onClick={handleCheckIn}
                  disabled={checkedIn || !!checkOutTime || loading}
                  className={checkedIn || checkOutTime ? "disabled" : ""}
                >
                  {loading ? "Processing..." : "Check-In"}
                </button>
                <button
                  onClick={handleCheckOut}
                  disabled={!checkedIn || !!checkOutTime || loading}
                  className={!checkedIn || checkOutTime ? "disabled" : ""}
                >
                  {loading ? "Processing..." : "Check-Out"}
                </button>
              </div>
            </>
          )}
        </div>

        <div className="quick-links">
          <div className="link-card">
            <h3>📊 Riwayat Absensi</h3>
            <a href="/attendance">Lihat Riwayat</a>
          </div>
          <div className="link-card">
            <h3>📝 Pengajuan Cuti</h3>
            {karyawanSedangCuti ? (
              <span className="link-disabled">Kelola Cuti (tidak tersedia saat cuti)</span>
            ) : (
              <a href="/leaves">Kelola Cuti</a>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
