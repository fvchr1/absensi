import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { karyawanService, leaveService } from "../services/api";
import "../styles/Employees.css";

const Employees = () => {
  const { user, logout, isAdmin, userName } = useContext(AuthContext);
  const [karyawan, setKaryawan] = useState([]);
  const [currentlyOnLeave, setCurrentlyOnLeave] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingKaryawan, setEditingKaryawan] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [karyawanToDelete, setKaryawanToDelete] = useState(null);
  const [formData, setFormData] = useState({
    nik: "",
    nama: "",
    alamat: "",
    noTlp: "",
    tglLahir: "",
    divisi: "",
    jenisKel: "",
    password: "",
  });
  const [formError, setFormError] = useState("");

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

  const resetForm = () => {
    setFormData({
      nik: "",
      nama: "",
      alamat: "",
      noTlp: "",
      tglLahir: "",
      divisi: "",
      jenisKel: "",
      password: "",
    });
    setEditingKaryawan(null);
    setFormError("");
    setShowForm(false);
  };

  const handleEdit = (k) => {
    setEditingKaryawan(k);
    setFormData({
      nik: k.nik,
      nama: k.nama || "",
      alamat: k.alamat || "",
      noTlp: k.noTlp || "",
      tglLahir: k.tglLahir ? k.tglLahir.split("T")[0] : "",
      divisi: k.divisi || "",
      jenisKel: k.jenisKel || "",
      password: "",
    });
    setShowForm(true);
    setFormError("");
  };

  const handleDeleteClick = (k) => {
    setKaryawanToDelete(k);
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!karyawanToDelete) return;
    try {
      await karyawanService.delete(karyawanToDelete.nik);
      setShowDeleteModal(false);
      setKaryawanToDelete(null);
      fetchKaryawan();
      fetchCurrentlyOnLeave();
    } catch (error) {
      console.error("Error deleting karyawan:", error);
      alert("Gagal menghapus karyawan: " + (error.response?.data || error.message));
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError("");

    if (!formData.nik || !formData.nama) {
      setFormError("Kode Pegawai dan Nama wajib diisi");
      return;
    }

    if (!editingKaryawan && !formData.password) {
      setFormError("Password wajib diisi untuk karyawan baru");
      return;
    }

    try {
      const karyawanData = {
        nik: formData.nik,
        nama: formData.nama,
        alamat: formData.alamat || null,
        noTlp: formData.noTlp || null,
        tglLahir: formData.tglLahir || null,
        divisi: formData.divisi || null,
        jenisKel: formData.jenisKel || null,
        password: formData.password || undefined,
      };

      if (editingKaryawan) {
        await karyawanService.update(formData.nik, karyawanData);
      } else {
        await karyawanService.create(karyawanData);
      }

      resetForm();
      fetchKaryawan();
      fetchCurrentlyOnLeave();
    } catch (error) {
      const msg = error.response?.data || error.message || "Gagal menyimpan data karyawan";
      setFormError(typeof msg === "string" ? msg : "Gagal menyimpan data karyawan");
      console.error("Error saving karyawan:", error);
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
        <div className="employees-header">
          <h1>Daftar Karyawan</h1>
          <button onClick={() => { resetForm(); setShowForm(true); }} className="btn-primary">
            + Tambah Karyawan
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleSubmit} className="employee-form">
            <h2>{editingKaryawan ? "Edit Karyawan" : "Tambah Karyawan Baru"}</h2>
            {formError && <div className="form-error">{formError}</div>}
            <div className="form-row">
              <div className="form-group">
                <label>Kode Pegawai *</label>
                <input
                  type="text"
                  name="nik"
                  value={formData.nik}
                  onChange={handleInputChange}
                  required
                  disabled={!!editingKaryawan}
                />
              </div>
              <div className="form-group">
                <label>Nama *</label>
                <input
                  type="text"
                  name="nama"
                  value={formData.nama}
                  onChange={handleInputChange}
                  required
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Alamat</label>
                <input
                  type="text"
                  name="alamat"
                  value={formData.alamat}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-group">
                <label>No. Telepon</label>
                <input
                  type="text"
                  name="noTlp"
                  value={formData.noTlp}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Tanggal Lahir</label>
                <input
                  type="date"
                  name="tglLahir"
                  value={formData.tglLahir}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-group">
                <label>Divisi</label>
                <input
                  type="text"
                  name="divisi"
                  value={formData.divisi}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Jenis Kelamin</label>
                <select name="jenisKel" value={formData.jenisKel} onChange={handleInputChange}>
                  <option value="">Pilih</option>
                  <option value="L">Laki-laki</option>
                  <option value="P">Perempuan</option>
                </select>
              </div>
              <div className="form-group">
                <label>Password {!editingKaryawan && "*"}</label>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder={editingKaryawan ? "Kosongkan jika tidak ingin mengubah" : ""}
                  required={!editingKaryawan}
                />
              </div>
            </div>
            <div className="form-actions">
              <button type="submit" className="btn-submit">
                {editingKaryawan ? "Update" : "Simpan"}
              </button>
              <button type="button" onClick={resetForm} className="btn-cancel">
                Batal
              </button>
            </div>
          </form>
        )}

        {showDeleteModal && (
          <div className="modal-overlay" onClick={() => setShowDeleteModal(false)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <h3>Konfirmasi Hapus</h3>
              <p>Apakah Anda yakin ingin menghapus karyawan <strong>{karyawanToDelete?.nama}</strong> (Kode: {karyawanToDelete?.nik})?</p>
              <p className="modal-warning">Tindakan ini tidak dapat dibatalkan.</p>
              <div className="modal-actions">
                <button onClick={handleDeleteConfirm} className="btn-delete-confirm">
                  Ya, Hapus
                </button>
                <button onClick={() => { setShowDeleteModal(false); setKaryawanToDelete(null); }} className="btn-cancel">
                  Batal
                </button>
              </div>
            </div>
          </div>
        )}

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
                <th>Aksi</th>
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
                  <td className="action-buttons">
                    <button onClick={() => handleEdit(k)} className="btn-edit">Edit</button>
                    <button onClick={() => handleDeleteClick(k)} className="btn-delete">Hapus</button>
                  </td>
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
