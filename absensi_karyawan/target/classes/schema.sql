-- ERD: Admin, Karyawan, Absen, Melakukan (Detail Absen merged into Melakukan)
-- H2: tables created automatically; this file ensures initial schema

-- Admin: id_admin, nama, password
CREATE TABLE IF NOT EXISTS admin (
    id_admin BIGINT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Karyawan: nik, nama, alamat, no_tlp, password, tgl_lahir, divisi, jenis_kel, status
CREATE TABLE IF NOT EXISTS karyawan (
    nik VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat VARCHAR(255),
    no_tlp VARCHAR(30),
    password VARCHAR(255) NOT NULL,
    tgl_lahir DATE,
    divisi VARCHAR(100),
    jenis_kel VARCHAR(10),
    status BOOLEAN DEFAULT FALSE
);

-- Absen: id_absen, tgl, id_admin (Admin Mengolah Absen)
CREATE TABLE IF NOT EXISTS absen (
    id_absen BIGINT PRIMARY KEY AUTO_INCREMENT,
    tgl DATE NOT NULL,
    id_admin BIGINT NOT NULL,
    FOREIGN KEY (id_admin) REFERENCES admin(id_admin) ON DELETE CASCADE
);

-- Melakukan: Karyawan-Absen link with jam_masuk, jam_keluar, datetime login
CREATE TABLE IF NOT EXISTS melakukan (
    id_melakukan BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_absen BIGINT NOT NULL,
    nik VARCHAR(20) NOT NULL,
    jam_masuk TIME,
    jam_keluar TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_absen) REFERENCES absen(id_absen) ON DELETE CASCADE,
    FOREIGN KEY (nik) REFERENCES karyawan(nik) ON DELETE CASCADE,
    UNIQUE (id_absen, nik)
);

-- Leaves (Cuti) - linked to Karyawan by nik
CREATE TABLE IF NOT EXISTS leaves (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nik VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    reason TEXT NOT NULL,
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approver_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nik) REFERENCES karyawan(nik) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_absen_tgl ON absen(tgl);
CREATE INDEX IF NOT EXISTS idx_absen_admin ON absen(id_admin);
CREATE INDEX IF NOT EXISTS idx_melakukan_absen ON melakukan(id_absen);
CREATE INDEX IF NOT EXISTS idx_melakukan_nik ON melakukan(nik);
CREATE INDEX IF NOT EXISTS idx_leaves_nik ON leaves(nik);
