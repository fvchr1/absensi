import React, { createContext, useState, useEffect } from "react";
import { authService } from "../services/api";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [role, setRole] = useState(null); // "ADMIN" | "KARYAWAN"
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(localStorage.getItem("token"));

  useEffect(() => {
    if (token) {
      const userData = localStorage.getItem("user");
      const savedRole = localStorage.getItem("role");
      if (userData) setUser(JSON.parse(userData));
      if (savedRole) setRole(savedRole);
    }
    setLoading(false);
  }, [token]);

  const login = async (username, password) => {
    const response = await authService.login(username, password);
    const data = response.data;
    if (data.token) {
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data.user));
      localStorage.setItem("role", data.role);
      setToken(data.token);
      setUser(data.user);
      setRole(data.role);
      return data;
    }
    throw new Error(data.message || "Invalid credentials");
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    localStorage.removeItem("role");
    setToken(null);
    setUser(null);
    setRole(null);
  };

  const isAdmin = () => role === "ADMIN";
  const isKaryawan = () => role === "KARYAWAN";
  const userNik = () => (user && user.nik ? user.nik : null);
  const userName = () => (user && (user.nama || user.name)) ? (user.nama || user.name) : "";

  return (
    <AuthContext.Provider
      value={{
        user,
        role,
        token,
        login,
        logout,
        loading,
        isAdmin,
        isKaryawan,
        userNik,
        userName,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
