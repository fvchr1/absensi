import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { employeeService } from "../services/api";
import "../styles/Employees.css";

const Employees = () => {
  const { user, logout } = useContext(AuthContext);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    setLoading(true);
    try {
      const response = await employeeService.getAllEmployees();
      setEmployees(response.data);
    } catch (error) {
      console.error("Error fetching employees:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="employees-container">
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

      <div className="employees-content">
        <h1>Daftar Karyawan</h1>

        {loading ? (
          <p>Loading...</p>
        ) : employees.length > 0 ? (
          <table className="employees-table">
            <thead>
              <tr>
                <th>NIP</th>
                <th>Nama</th>
                <th>Email</th>
                <th>Departemen</th>
                <th>Posisi</th>
                <th>Telepon</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {employees.map((employee) => (
                <tr key={employee.id}>
                  <td>{employee.nip}</td>
                  <td>{employee.name}</td>
                  <td>{employee.email}</td>
                  <td>{employee.department}</td>
                  <td>{employee.position}</td>
                  <td>{employee.phoneNumber}</td>
                  <td>{employee.role}</td>
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
