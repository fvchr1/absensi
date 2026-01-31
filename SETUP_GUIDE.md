# SSMAS Web

Aplikasi web untuk manajemen absensi karyawan dengan backend Spring Boot dan frontend React + Vite.

## Fitur

- ✅ Autentikasi pengguna dengan JWT
- ✅ Check-in dan Check-out absensi
- ✅ Riwayat absensi per karyawan
- ✅ Pengajuan dan persetujuan cuti
- ✅ Manajemen data karyawan
- ✅ Dashboard untuk karyawan
- ✅ Responsive design

## Teknologi

### Backend

- Java 17
- Spring Boot 3.1.5
- Spring Data JPA
- Spring Security
- JWT (JSON Web Token)
- MySQL 8.0
- Maven

### Frontend

- React 19
- Vite
- React Router v6
- Axios
- CSS3

## Requirement

- Java JDK 17 atau lebih baru
- MySQL 8.0
- Node.js 18 atau lebih baru
- npm atau yarn

## Setup Backend

### 1. Setup Database

```sql
-- Buat database
CREATE DATABASE attendance_db;
USE attendance_db;
```

### 2. Konfigurasi Database

Edit file `attendance-backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/attendance_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root # Ganti dengan username MySQL Anda
    password: root # Ganti dengan password MySQL Anda
```

### 3. Build dan Jalankan Backend

```bash
cd attendance-backend

# Build dengan Maven
mvn clean install

# Jalankan aplikasi
mvn spring-boot:run
```

Backend akan berjalan di `http://localhost:8080`

## Setup Frontend

### 1. Install Dependencies

```bash
# Di folder root project
npm install
```

### 2. Jalankan Frontend

```bash
npm run dev
```

Frontend akan berjalan di `http://localhost:5173`

## Data Testing

### Setup Data Awal

Setelah membuat database, Anda perlu insert data karyawan untuk testing.

**Ada 2 cara:**

#### Cara 1: Menggunakan SQL File (RECOMMENDED)

1. Buka MySQL Workbench atau MySQL Command Line
2. Jalankan database dulu:

```bash
# Pastikan MySQL sudah running
```

3. Execute file SQL:

```sql
-- Di MySQL terminal/GUI
source attendance-backend/src/main/resources/insert-test-data.sql;
```

#### Cara 2: Manual Insert

Jika Cara 1 tidak bisa, jalankan query ini di MySQL:

```sql
USE attendance_db;

INSERT INTO employees (nip, name, email, password, department, position, phone_number, role, active)
VALUES
('001', 'Admin User', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'IT', 'Administrator', '081234567890', 'ADMIN', TRUE),
('002', 'Manager User', 'manager@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'HR', 'HR Manager', '081234567891', 'MANAGER', TRUE),
('003', 'Budi Santoso', 'budi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Sales', 'Sales Staff', '081234567892', 'EMPLOYEE', TRUE),
('004', 'Siti Nurhaliza', 'siti@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Marketing', 'Marketing Specialist', '081234567893', 'EMPLOYEE', TRUE),
('005', 'Ahmad Wijaya', 'ahmad@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm', 'Finance', 'Accountant', '081234567894', 'EMPLOYEE', TRUE);
```

### Akun untuk Testing

Setelah insert data, Anda bisa login dengan:

| Email                 | Password      | Role     |
| --------------------- | ------------- | -------- |
| `admin@example.com`   | `password123` | Admin    |
| `manager@example.com` | `password123` | Manager  |
| `budi@example.com`    | `password123` | Employee |
| `siti@example.com`    | `password123` | Employee |
| `ahmad@example.com`   | `password123` | Employee |

**Password hash info:**

- Plain: `password123`
- BCrypt hash: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KLm`

## API Endpoints

### Authentication

- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register karyawan baru

### Employee

- `GET /api/employees` - Dapatkan semua karyawan
- `GET /api/employees/{id}` - Dapatkan detail karyawan
- `POST /api/employees` - Tambah karyawan baru
- `PUT /api/employees/{id}` - Update karyawan
- `DELETE /api/employees/{id}` - Hapus karyawan

### Attendance

- `POST /api/attendance/check-in/{employeeId}` - Check-in
- `POST /api/attendance/check-out/{employeeId}` - Check-out
- `GET /api/attendance/employee/{employeeId}` - Riwayat absensi karyawan
- `GET /api/attendance/employee/{employeeId}/range` - Absensi by date range
- `PUT /api/attendance/{id}` - Update absensi
- `DELETE /api/attendance/{id}` - Hapus absensi

### Leave

- `POST /api/leaves/request` - Ajukan cuti
- `GET /api/leaves/employee/{employeeId}` - Riwayat cuti karyawan
- `GET /api/leaves/pending` - Daftar cuti yang pending
- `POST /api/leaves/{id}/approve` - Setujui cuti
- `POST /api/leaves/{id}/reject` - Tolak cuti
- `DELETE /api/leaves/{id}` - Hapus cuti

## Struktur Project

```
vite-project/
├── attendance-backend/          # Backend Spring Boot
│   ├── src/main/
│   │   ├── java/com/attendance/app/
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── service/          # Business Logic
│   │   │   ├── model/            # Entity Models
│   │   │   ├── repository/       # Data Access
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── security/         # Security Config & JWT
│   │   │   └── AttendanceApplication.java
│   │   └── resources/
│   │       └── application.yml   # Configuration
│   └── pom.xml                   # Maven dependencies
│
├── src/                          # Frontend React
│   ├── pages/                    # Page components
│   │   ├── Login.jsx
│   │   ├── Dashboard.jsx
│   │   ├── Attendance.jsx
│   │   ├── Leaves.jsx
│   │   └── Employees.jsx
│   ├── components/               # Reusable components
│   ├── context/                  # React Context (Auth)
│   ├── services/                 # API services
│   ├── styles/                   # CSS files
│   ├── App.jsx
│   └── main.jsx
│
├── package.json
└── README.md
```

## Troubleshooting

### Backend tidak connect ke database

- Pastikan MySQL sudah berjalan
- Check username dan password di `application.yml`
- Pastikan database `attendance_db` sudah dibuat

### Frontend tidak bisa call API

- Pastikan backend sudah running di port 8080
- Check CORS configuration di `SecurityConfig.java`
- Buka browser console untuk melihat error

### Port sudah terpakai

- Backend: Ubah port di `application.yml` dengan `server.port`
- Frontend: `npm run dev -- --port 3000`

## Development

### Menambah fitur baru

1. **Backend:**

   - Buat entity di `model/`
   - Buat repository di `repository/`
   - Buat service di `service/`
   - Buat controller di `controller/`

2. **Frontend:**
   - Buat component di `pages/` atau `components/`
   - Add route di `App.jsx`
   - Add API method di `services/api.js`

## License

MIT License
