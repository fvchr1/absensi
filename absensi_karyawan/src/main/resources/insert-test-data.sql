-- Test accounts (admin & karyawan) are inserted by TestDataLoader.java
-- using the same PasswordEncoder as login, so password always matches.
--
-- Test password: password123
-- Admin: username "1" (or "admin")
-- Karyawan: username "EMP001", "EMP002", "EMP003"
--
-- This file is kept for schema-only init; data is loaded by TestDataLoader.
-- (One no-op statement so Spring does not treat the script as empty.)
SELECT 1;
