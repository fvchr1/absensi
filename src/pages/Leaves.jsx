import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { leaveService } from "../services/api";
import "../styles/Leaves.css";

const Leaves = () => {
  const { user, logout } = useContext(AuthContext);
  const [leaves, setLeaves] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    startDate: "",
    endDate: "",
    leaveType: "ANNUAL",
    reason: "",
  });

  useEffect(() => {
    fetchLeaves();
  }, []);

  const fetchLeaves = async () => {
    setLoading(true);
    try {
      const response = await leaveService.getLeaveByEmployee(user.id);
      setLeaves(response.data);
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
    try {
      await leaveService.requestLeave({
        employee: { id: user.id },
        startDate: formData.startDate,
        endDate: formData.endDate,
        leaveType: formData.leaveType,
        reason: formData.reason,
      });
      setFormData({ startDate: "", endDate: "", leaveType: "ANNUAL", reason: "" });
      setShowForm(false);
      fetchLeaves();
    } catch (error) {
      console.error("Error requesting leave:", error);
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
        <div className="navbar-brand">Sistem Absensi</div>
        <div className="navbar-menu">
          <a href="/dashboard">Dashboard</a>
          <span className="user-info">{user?.name}</span>
          <button onClick={logout} className="logout-btn">
            Logout
          </button>
        </div>
      </nav>

      <div className="leaves-content">
        <div className="leaves-header">
          <h1>Pengajuan Cuti</h1>
          <button onClick={() => setShowForm(!showForm)} className="btn-primary">
            {showForm ? "Batal" : "Ajukan Cuti Baru"}
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleSubmit} className="leave-form">
            <div className="form-group">
              <label>Tanggal Mulai</label>
              <input type="date" name="startDate" value={formData.startDate} onChange={handleInputChange} required />
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

            <button type="submit" className="btn-submit">
              Ajukan
            </button>
          </form>
        )}

        {loading ? (
          <p>Loading...</p>
        ) : leaves.length > 0 ? (
          <table className="leaves-table">
            <thead>
              <tr>
                <th>Tanggal Mulai</th>
                <th>Tanggal Akhir</th>
                <th>Jenis Cuti</th>
                <th>Alasan</th>
                <th>Status</th>
                <th>Keterangan</th>
              </tr>
            </thead>
            <tbody>
              {leaves.map((leave) => (
                <tr key={leave.id}>
                  <td>{new Date(leave.startDate).toLocaleDateString("id-ID")}</td>
                  <td>{new Date(leave.endDate).toLocaleDateString("id-ID")}</td>
                  <td>{leave.leaveType}</td>
                  <td>{leave.reason}</td>
                  <td>
                    <span className="status-badge" style={{ backgroundColor: getStatusColor(leave.approvalStatus) }}>
                      {leave.approvalStatus}
                    </span>
                  </td>
                  <td>{leave.approverNotes || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">Tidak ada pengajuan cuti</p>
        )}
      </div>
    </div>
  );
};

export default Leaves;
