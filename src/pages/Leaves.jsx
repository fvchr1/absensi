import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { leaveService, attendanceService } from "../services/api";
import "../styles/Leaves.css";

const Leaves = () => {
  const { user, logout, isAdmin, userName, userNik } = useContext(AuthContext);
  const [leaves, setLeaves] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [karyawanSedangCuti, setKaryawanSedangCuti] = useState(false);
  const [hasAttendedToday, setHasAttendedToday] = useState(false);
  const [submitError, setSubmitError] = useState("");
  const [formData, setFormData] = useState({
    startDate: "",
    endDate: "",
    leaveType: "ANNUAL",
    reason: "",
  });

  useEffect(() => {
    fetchLeaves();
    if (!isAdmin()) {
      checkKaryawanOnLeave();
      checkAttendedToday();
    }
  }, []);

  const checkAttendedToday = async () => {
    const nik = userNik();
    if (!nik) return;
    try {
      const res = await attendanceService.getTodayByNik(nik);
      setHasAttendedToday(!!res.data);
    } catch {
      setHasAttendedToday(false);
    }
  };

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

  const fetchLeaves = async () => {
    setLoading(true);
    try {
      if (isAdmin()) {
        const response = await leaveService.getPendingLeaves();
        setLeaves(response.data || []);
      } else {
        const response = await leaveService.getByNik(userNik());
        setLeaves(response.data || []);
      }
    } catch (error) {
      console.error("Error fetching leaves:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError("");
    try {
      await leaveService.requestLeave({
        karyawan: { nik: userNik() },
        startDate: formData.startDate,
        endDate: formData.endDate,
        leaveType: formData.leaveType,
        reason: formData.reason,
      });
      setFormData({ startDate: "", endDate: "", leaveType: "ANNUAL", reason: "" });
      setShowForm(false);
      fetchLeaves();
    } catch (error) {
      const msg = error.response?.data ?? error.message ?? "Gagal mengajukan cuti.";
      setSubmitError(typeof msg === "string" ? msg : "Gagal mengajukan cuti.");
      console.error("Error requesting leave:", error);
    }
  };

  const minStartDate = (() => {
    const d = new Date();
    if (hasAttendedToday) {
      d.setDate(d.getDate() + 1);
    }
    return d.toISOString().slice(0, 10);
  })();

  const handleApprove = async (id) => {
    try {
      await leaveService.approveLeave(id);
      fetchLeaves();
    } catch (e) {
      console.error(e);
    }
  };

  const handleReject = async (id) => {
    const reason = window.prompt("Alasan penolakan (opsional):");
    try {
      await leaveService.rejectLeave(id, reason || "");
      fetchLeaves();
    } catch (e) {
      console.error(e);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "APPROVED":
        return "#4caf50";
      case "REJECTED":
        return "#f44336";
      case "PENDING":
        return "#ff9800";
      default:
        return "#666";
    }
  };

  return (
    <div className="leaves-container">
      <nav className="navbar">
        <div className="navbar-brand">SSMAS</div>
        <div className="navbar-menu">
          <span className="user-info">{userName()}</span>
          <button onClick={logout} className="logout-btn">Logout</button>
        </div>
      </nav>

      <div className="leaves-content">
        <a href="/dashboard" className="btn-back">← Kembali ke Dashboard</a>
        <div className="leaves-header">
          <h1>{isAdmin() ? "Pengajuan Cuti (Pending)" : "Pengajuan Cuti"}</h1>
          {!isAdmin() && !karyawanSedangCuti && (
            <button onClick={() => setShowForm(!showForm)} className="btn-primary">
              {showForm ? "Batal" : "Ajukan Cuti Baru"}
            </button>
          )}
        </div>

        {!isAdmin() && karyawanSedangCuti && (
          <div className="on-leave-notice">
            Anda sedang cuti hari ini. Pengajuan cuti baru tidak tersedia.
          </div>
        )}

        {!isAdmin() && showForm && !karyawanSedangCuti && (
          <form onSubmit={handleSubmit} className="leave-form">
            {submitError && (
              <div className="form-error">{submitError}</div>
            )}
            {hasAttendedToday && (
              <p className="form-hint">Anda sudah absen hari ini. Tanggal mulai cuti minimal besok (H+1).</p>
            )}
            <div className="form-group">
              <label>Tanggal Mulai</label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleInputChange}
                min={minStartDate}
                required
              />
            </div>
            <div className="form-group">
              <label>Tanggal Akhir</label>
              <input type="date" name="endDate" value={formData.endDate} onChange={handleInputChange} required />
            </div>
            <div className="form-group">
              <label>Jenis Cuti</label>
              <select name="leaveType" value={formData.leaveType} onChange={handleInputChange}>
                <option value="ANNUAL">Cuti Tahunan</option>
                <option value="SICK">Sakit</option>
                <option value="PERSONAL">Pribadi</option>
                <option value="BEREAVEMENT">Duka Cita</option>
                <option value="OTHER">Lainnya</option>
              </select>
            </div>
            <div className="form-group">
              <label>Alasan</label>
              <textarea name="reason" value={formData.reason} onChange={handleInputChange} required></textarea>
            </div>
            <button type="submit" className="btn-submit">Ajukan</button>
          </form>
        )}

        {loading ? (
          <p>Loading...</p>
        ) : leaves.length > 0 ? (
          <table className="leaves-table">
            <thead>
              <tr>
                {isAdmin() && <th>Kode Pegawai</th>}
                {isAdmin() && <th>Nama</th>}
                <th>Tanggal Mulai</th>
                <th>Tanggal Akhir</th>
                <th>Jenis Cuti</th>
                <th>Alasan</th>
                <th>Status</th>
                {isAdmin() && <th>Aksi</th>}
                {!isAdmin() && <th>Keterangan</th>}
              </tr>
            </thead>
            <tbody>
              {leaves.map((leave) => (
                <tr key={leave.id}>
                  {isAdmin() && <td>{leave.nik}</td>}
                  {isAdmin() && <td>{leave.employeeName}</td>}
                  <td>{new Date(leave.startDate).toLocaleDateString("id-ID")}</td>
                  <td>{new Date(leave.endDate).toLocaleDateString("id-ID")}</td>
                  <td>{leave.leaveType}</td>
                  <td>{leave.reason}</td>
                  <td>
                    <span className="status-badge" style={{ backgroundColor: getStatusColor(leave.approvalStatus) }}>
                      {leave.approvalStatus}
                    </span>
                  </td>
                  {isAdmin() && (
                    <td>
                      {leave.approvalStatus === "PENDING" && (
                        <>
                          <button onClick={() => handleApprove(leave.id)} className="btn-approve">Setuju</button>
                          <button onClick={() => handleReject(leave.id)} className="btn-reject">Tolak</button>
                        </>
                      )}
                    </td>
                  )}
                  {!isAdmin() && <td>{leave.approverNotes || "-"}</td>}
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">
            {isAdmin() ? "Tidak ada pengajuan cuti pending" : "Tidak ada pengajuan cuti"}
          </p>
        )}
      </div>
    </div>
  );
};

export default Leaves;
