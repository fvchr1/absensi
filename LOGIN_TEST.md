# Login Test Checklist

## Perubahan yang sudah dilakukan

1. **Backend – SecurityConfig**  
   - Semua request diizinkan (`permitAll`) sehingga `/api/auth/login` tidak diblokir.

2. **Backend – application.yml**  
   - Dialect diubah ke `H2Dialect` (bukan MySQL) agar cocok dengan H2.

3. **Backend – TestDataLoader**  
   - Admin dengan nama `"admin"` selalu dibuat atau password-nya di-update ke `password123` saat startup.  
   - Karyawan EMP001, EMP002, EMP003 juga selalu di-update password-nya ke `password123`.

4. **Backend – AuthService**  
   - Username di-trim, validasi null/kosong.

5. **Frontend – Login.jsx**  
   - Username di-trim sebelum dikirim, pesan error dari backend ditampilkan.

## Cara uji login di lokal

### 1. Jalankan backend

```bash
cd attendance-backend
mvn spring-boot:run
```

Tunggu sampai ada log: `Started AttendanceApplication in ...`

### 2. Jalankan frontend

```bash
cd ..   # ke root project
npm run dev
```

Buka http://localhost:5173 di browser.

### 3. Uji login

**Admin**

- Username: `admin` atau `1`  
- Password: `password123`  
- Harus redirect ke dashboard admin.

**Karyawan**

- Username: `EMP001`  
- Password: `password123`  
- Harus redirect ke dashboard user.

### 4. Jika masih gagal – cek di browser

1. Buka DevTools (F12) → tab **Network**.
2. Submit form login.
3. Klik request **login** (method POST).
4. Lihat:
   - **Status**: 200 = sukses, 401 = salah user/password atau backend menolak.
   - **Response**: isi JSON (token, message, dll.).

### 5. Uji API login dengan curl (backend harus jalan)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

- Jika sukses: response JSON berisi `token`, `role`, `user`.
- Jika gagal: response JSON dengan `message` (misalnya "Invalid username or password") dan status 401.
