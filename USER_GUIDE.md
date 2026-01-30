# 📝 Panduan Penggunaan Sistem Absensi

## Login

1. Buka aplikasi di `http://localhost:5173`
2. Masukkan email dan password
3. Klik tombol "Login"

Email default untuk testing:

- Email: `admin@email.com`
- Password: `password123` (perlu disesuaikan dengan database Anda)

## Dashboard

Dashboard menampilkan:

- Status check-in hari ini
- Tombol Check-in dan Check-out
- Quick links ke fitur lainnya

### Check-in

1. Klik tombol "Check-In" di halaman Dashboard
2. Sistem akan mencatat waktu check-in Anda
3. Status akan berubah menjadi "Anda sudah check-in"

### Check-out

1. Setelah check-in, klik tombol "Check-Out"
2. Sistem akan mencatat waktu check-out Anda

## Riwayat Absensi

1. Klik menu "Riwayat Absensi" atau link di Dashboard
2. Pilih rentang tanggal
3. Klik "Filter"
4. Lihat tabel riwayat absensi Anda

Status absensi:

- 🟢 **PRESENT** - Hadir
- 🟠 **LATE** - Terlambat
- 🔴 **ABSENT** - Tidak hadir
- 🔵 **SICK** - Sakit
- 🟣 **LEAVE** - Cuti

## Pengajuan Cuti

### Mengajukan Cuti

1. Klik menu "Pengajuan Cuti"
2. Klik tombol "Ajukan Cuti Baru"
3. Isi form:
   - Tanggal Mulai
   - Tanggal Akhir
   - Jenis Cuti (Tahunan, Sakit, Pribadi, Duka Cita, Lainnya)
   - Alasan
4. Klik "Ajukan"

### Melihat Status Cuti

- Halaman Pengajuan Cuti menampilkan status semua pengajuan Anda
- Status bisa: PENDING, APPROVED, atau REJECTED

## Daftar Karyawan

1. Klik menu "Data Karyawan"
2. Lihat tabel semua karyawan dalam sistem
3. Informasi yang ditampilkan: NIP, Nama, Email, Departemen, Posisi, Telepon, Role

## Logout

Klik tombol "Logout" di kanan atas untuk keluar dari aplikasi.

---

Untuk masalah teknis, lihat SETUP_GUIDE.md
