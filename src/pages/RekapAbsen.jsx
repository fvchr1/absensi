import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { attendanceService } from "../services/api";
import "../styles/RekapAbsen.css";

const RekapAbsen = () => {
  const { logout, isAdmin, userName } = useContext(AuthContext);
  const [rekapData, setRekapData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    if (isAdmin()) {
      fetchRekap();
    }
  }, []);

  const fetchRekap = async () => {
    setLoading(true);
    try {
      const [recapRes, summaryRes] = await Promise.all([
        attendanceService.getMonthlyRecapPerKaryawan(),
        attendanceService.getMonthlyRecap(),
      ]);
      setRekapData(recapRes.data ?? []);
      setSummary(summaryRes.data);
    } catch (error) {
      console.error("Error fetching rekap:", error);
    } finally {
      setLoading(false);
    }
  };

  const downloadCSV = () => {
    if (rekapData.length === 0) return;

    const headers = ["Kode Pegawai", "Nama", "Divisi", "Hari Absen", "Gaji Pokok", "Gaji Absensi", "Hari Lembur", "Gaji Lembur", "Total Gaji"];
    const rows = rekapData.map((r) => [
      r.nik,
      r.nama,
      r.divisi || "-",
      r.hariAbsen,
      r.gajiPokok.toLocaleString("id-ID"),
      Math.round(r.gajiAbsensi).toLocaleString("id-ID"),
      r.hariLembur,
      Math.round(r.gajiLembur).toLocaleString("id-ID"),
      Math.round(r.totalGaji).toLocaleString("id-ID"),
    ]);

    const csvContent = [
      headers.join(","),
      ...rows.map((row) => row.map((cell) => `"${cell}"`).join(",")),
    ].join("\n");

    const blob = new Blob(["\uFEFF" + csvContent], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    const url = URL.createObjectURL(blob);
    link.setAttribute("href", url);
    link.setAttribute("download", `rekap_absen_${new Date().toISOString().slice(0, 7)}.csv`);
    link.style.visibility = "hidden";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (!isAdmin()) {
    return (
      <div className="rekap-container">
        <nav className="navbar">
          <div className="navbar-brand">SSMAS</div>
          <div className="navbar-menu">
            <span className="user-info">{userName()}</span>
            <button onClick={logout} className="logout-btn">Logout</button>
          </div>
        </nav>
        <div className="rekap-content">
          <p className="no-data">Akses hanya untuk Admin.</p>
          <a href="/dashboard">Kembali ke Dashboard</a>
        </div>
      </div>
    );
  }

  return (
    <div className="rekap-container">
      <nav className="navbar">
        <div className="navbar-brand">SSMAS | Admin</div>
        <div className="navbar-menu">
          <span className="user-info">{userName()}</span>
          <button onClick={logout} className="logout-btn">Logout</button>
        </div>
      </nav>

      <div className="rekap-content">
        <a href="/dashboard" className="btn-back">← Kembali ke Dashboard</a>
        <div className="rekap-header">
          <h1>Rekap Absen Bulanan</h1>
          <button onClick={downloadCSV} className="btn-download" disabled={rekapData.length === 0}>
            📥 Download CSV
          </button>
        </div>

        {summary && (
          <div className="rekap-summary">
            <div className="summary-item">
              <span>Total Hari Kerja:</span>
              <strong>{summary.totalHariKerja} hari</strong>
            </div>
          </div>
        )}

        {loading ? (
          <p>Loading...</p>
        ) : rekapData.length > 0 ? (
          <table className="rekap-table">
            <thead>
              <tr>
                <th>Kode Pegawai</th>
                <th>Nama</th>
                <th>Divisi</th>
                <th>Hari Absen</th>
                <th>Gaji Pokok</th>
                <th>Gaji Absensi</th>
                <th>Hari Lembur</th>
                <th>Gaji Lembur</th>
                <th>Total Gaji</th>
              </tr>
            </thead>
            <tbody>
              {rekapData.map((r) => (
                <tr key={r.nik}>
                  <td>{r.nik}</td>
                  <td>{r.nama}</td>
                  <td>{r.divisi || "-"}</td>
                  <td>{r.hariAbsen} hari</td>
                  <td>Rp {r.gajiPokok.toLocaleString("id-ID")}</td>
                  <td>Rp {Math.round(r.gajiAbsensi).toLocaleString("id-ID")}</td>
                  <td>{r.hariLembur} hari</td>
                  <td>Rp {Math.round(r.gajiLembur).toLocaleString("id-ID")}</td>
                  <td className="total-gaji">Rp {Math.round(r.totalGaji).toLocaleString("id-ID")}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">Tidak ada data rekap</p>
        )}
      </div>
    </div>
  );
};

export default RekapAbsen;
