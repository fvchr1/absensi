import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { karyawanService, leaveService } from "../services/api";
import "../styles/Employees.css";

const Employees = () => {
  const { user, logout, isAdmin, userName } = useContext(AuthContext);
  const [karyawan, setKaryawan] = useState([]);
  const [currentlyOnLeave, setCurrentlyOnLeave] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isAdmin()) {
      fetchKaryawan();
      fetchCurrentlyOnLeave();
    }
  }, []);

  const fetchKaryawan = async () => {
    setLoading(true);
    try {
      const response = await karyawanService.getAll();
      setKaryawan(response.data || []);
    } catch (error) {
      console.error("Error fetching karyawan:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchCurrentlyOnLeave = async () => {
    try {
      const res = await leaveService.getCurrentlyOnLeave();
      setCurrentlyOnLeave(res.data ?? []);
    } catch (e) {
      console.error("Error fetching currently on leave:", e);
    }
  };

  if (!isAdmin()) {
    return (
      <div className="employees-container">
        <nav className="navbar">
          <div className="navbar-brand">SSMAS</div>
          <div className="navbar-menu">
            <span className="user-info">{userName()}</span>
            <button onClick={logout} className="logout-btn">Logout</button>
          </div>
        </nav>
        <div className="employees-content">
          <p className="no-data">Akses hanya untuk Admin.</p>
          <a href="/dashboard">Kembali ke Dashboard</a>
        </div>
      </div>
    );
  }

  return (
    <div className="employees-container">
      <nav className="navbar">
        <div className="navbar-brand">SSMAS | Admin</div>
        <div className="navbar-menu">
          <span className="user-info">{userName()}</span>
          <button onClick={logout} className="logout-btn">Logout</button>
        </div>
      </nav>

      <div className="employees-content">
        <a href="/dashboard" className="btn-back">← Kembali ke Dashboard</a>
        <h1>Daftar Karyawan</h1>

        {loading ? (
          <p>Loading...</p>
        ) : karyawan.length > 0 ? (
          <table className="employees-table">
            <thead>
              <tr>
                <th>Kode Pegawai</th>
                <th>Nama</th>
                <th>Alamat</th>
                <th>No. Telepon</th>
                <th>Tanggal Lahir</th>
                <th>Divisi</th>
                <th>Jenis Kelamin</th>
                <th className="col-status">Status</th>
              </tr>
            </thead>
            <tbody>
              {karyawan.map((k) => (
                <tr key={k.nik}>
                  <td>{k.nik}</td>
                  <td>{k.nama}</td>
                  <td>{k.alamat || "-"}</td>
                  <td>{k.noTlp || "-"}</td>
                  <td>{k.tglLahir ? new Date(k.tglLahir).toLocaleDateString("id-ID") : "-"}</td>
                  <td>{k.divisi || "-"}</td>
                  <td>{k.jenisKel || "-"}</td>
                  <td className="col-status">{currentlyOnLeave.some((l) => l.nik === k.nik) ? "Cuti" : "Tidak Cuti"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">Tidak ada data karyawan</p>
        )}
      </div>
    </div>
  );
};

export default Employees;
