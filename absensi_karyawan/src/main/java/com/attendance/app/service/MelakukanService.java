package com.attendance.app.service;

import com.attendance.app.dto.KaryawanAbsenTodayDTO;
import com.attendance.app.dto.MelakukanDTO;
import com.attendance.app.dto.RekapAbsenDTO;
import com.attendance.app.dto.RekapAbsenKaryawanDTO;
import com.attendance.app.model.Absen;
import com.attendance.app.model.Karyawan;
import com.attendance.app.model.Melakukan;
import com.attendance.app.repository.KaryawanRepository;
import com.attendance.app.repository.MelakukanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MelakukanService {

    @Autowired
    private MelakukanRepository melakukanRepository;

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private AbsenService absenService;

    private static final ZoneId ZONE_JAKARTA = ZoneId.of("Asia/Jakarta");

    @Transactional
    public MelakukanDTO checkIn(String nik) {
        Karyawan karyawan = karyawanRepository.findByNik(nik)
                .orElseThrow(() -> new RuntimeException("Karyawan not found"));
        LocalDate today = LocalDate.now(ZONE_JAKARTA);
        Absen absen = absenService.getOrCreateAbsenForDate(today);

        Optional<Melakukan> existing = melakukanRepository.findByAbsen_IdAbsenAndKaryawan_Nik(absen.getIdAbsen(), nik);
        if (existing.isPresent()) {
            throw new RuntimeException("Anda sudah check-in hari ini");
        }

        Melakukan m = new Melakukan();
        m.setAbsen(absen);
        m.setKaryawan(karyawan);
        m.setJamMasuk(LocalTime.now());
        m.setCreatedAt(LocalDateTime.now());
        m.setUpdatedAt(LocalDateTime.now());
        Melakukan saved = melakukanRepository.save(m);
        return toDTO(saved);
    }

    @Transactional
    public MelakukanDTO checkOut(String nik) {
        LocalDate today = LocalDate.now(ZONE_JAKARTA);
        Absen absen = absenService.findByTgl(today)
                .orElseThrow(() -> new RuntimeException("No attendance record for today"));
        Melakukan m = melakukanRepository.findByAbsen_IdAbsenAndKaryawan_Nik(absen.getIdAbsen(), nik)
                .orElseThrow(() -> new RuntimeException("No check-in record found for today"));

        m.setJamKeluar(LocalTime.now());
        m.setUpdatedAt(LocalDateTime.now());
        Melakukan saved = melakukanRepository.save(m);
        return toDTO(saved);
    }

    public List<MelakukanDTO> getByKaryawanNik(String nik) {
        return melakukanRepository.findByKaryawan_Nik(nik).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MelakukanDTO> getByKaryawanNikAndDateRange(String nik, LocalDate start, LocalDate end) {
        return melakukanRepository.findByAbsen_TglBetweenAndKaryawan_Nik(start, end, nik).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MelakukanDTO> getAllByDateRange(LocalDate start, LocalDate end) {
        return melakukanRepository.findByAbsen_TglBetween(start, end).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<MelakukanDTO> getTodayByNik(String nik) {
        LocalDate today = LocalDate.now(ZONE_JAKARTA);
        return absenService.findByTgl(today)
                .flatMap(absen -> melakukanRepository.findByAbsen_IdAbsenAndKaryawan_Nik(absen.getIdAbsen(), nik))
                .map(this::toDTO);
    }

    /** Returns true if the karyawan has an attendance record (check-in) on the given date. */
    public boolean hasAttendedOnDate(String nik, LocalDate date) {
        return absenService.findByTgl(date)
                .flatMap(absen -> melakukanRepository.findByAbsen_IdAbsenAndKaryawan_Nik(absen.getIdAbsen(), nik))
                .isPresent();
    }

    /**
     * Returns all karyawan with their attendance status for today.
     * Karyawan who have checked in have jamMasuk/jamKeluar; others have sudahAbsen=false.
     */
    public List<KaryawanAbsenTodayDTO> getTodayWithAllKaryawan() {
        LocalDate today = LocalDate.now(ZONE_JAKARTA);
        absenService.getOrCreateAbsenForDate(today);
        List<Karyawan> allKaryawan = karyawanRepository.findAll();
        List<Melakukan> todayMelakukan = absenService.findByTgl(today)
                .map(absen -> melakukanRepository.findByAbsen_IdAbsen(absen.getIdAbsen()))
                .orElse(List.of());

        java.util.Map<String, Melakukan> mapByNik = todayMelakukan.stream()
                .collect(Collectors.toMap(m -> m.getKaryawan().getNik(), m -> m));

        return allKaryawan.stream()
                .map(k -> {
                    Melakukan m = mapByNik.get(k.getNik());
                    if (m != null) {
                        return new KaryawanAbsenTodayDTO(
                                k.getNik(),
                                k.getNama(),
                                k.getDivisi() != null ? k.getDivisi() : "",
                                m.getJamMasuk(),
                                m.getJamKeluar(),
                                m.getCreatedAt(),
                                true);
                    }
                    return new KaryawanAbsenTodayDTO(
                            k.getNik(),
                            k.getNama(),
                            k.getDivisi() != null ? k.getDivisi() : "",
                            null,
                            null,
                            null,
                            false);
                })
                .collect(Collectors.toList());
    }

    /**
     * Rekap absensi bulanan: menghitung total gaji berdasarkan absensi semua karyawan dalam bulan ini.
     * Gaji pokok: 4jt, Gaji absensi full: 1jt (dibagi per hari kerja), Lembur: 100rb/hari.
     */
    public RekapAbsenDTO getMonthlyRecap() {
        LocalDate now = LocalDate.now(ZONE_JAKARTA);
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        // Hitung hari kerja (Senin-Jumat) dalam bulan ini
        int totalHariKerja = 0;
        LocalDate date = startOfMonth;
        while (!date.isAfter(endOfMonth)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                totalHariKerja++;
            }
            date = date.plusDays(1);
        }

        // Hitung total hari absen semua karyawan dalam bulan ini (distinct dates)
        List<Melakukan> bulanIni = melakukanRepository.findByAbsen_TglBetween(startOfMonth, endOfMonth);
        Set<LocalDate> distinctDates = bulanIni.stream()
                .map(m -> m.getAbsen().getTgl())
                .collect(Collectors.toSet());
        
        // Pisahkan weekday vs weekend
        long totalHariAbsen = distinctDates.stream()
                .filter(d -> {
                    DayOfWeek dayOfWeek = d.getDayOfWeek();
                    return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
                })
                .count();
        
        long hariLembur = distinctDates.stream()
                .filter(d -> {
                    DayOfWeek dayOfWeek = d.getDayOfWeek();
                    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
                })
                .count();

        // Perhitungan gaji
        double gajiPokok = 4_500_000.0;
        double gajiAbsensiFull = 1_000_000.0;
        double gajiPerHari = totalHariKerja > 0 ? gajiAbsensiFull / totalHariKerja : 0;
        double gajiAbsensi = totalHariAbsen * gajiPerHari;
        double gajiLemburPerHari = 100_000.0;
        double gajiLembur = hariLembur * gajiLemburPerHari;
        double totalGaji = gajiPokok + gajiAbsensi + gajiLembur;

        return new RekapAbsenDTO(
                totalHariKerja,
                (int) totalHariAbsen,
                gajiPokok,
                gajiPerHari,
                gajiAbsensi,
                (int) hariLembur,
                gajiLembur,
                totalGaji
        );
    }

    /**
     * Rekap absensi bulanan per karyawan: menghitung gaji setiap karyawan berdasarkan absensi bulan ini.
     */
    public List<RekapAbsenKaryawanDTO> getMonthlyRecapPerKaryawan() {
        LocalDate now = LocalDate.now(ZONE_JAKARTA);
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        // Hitung hari kerja (Senin-Jumat) dalam bulan ini
        int totalHariKerja = 0;
        LocalDate date = startOfMonth;
        while (!date.isAfter(endOfMonth)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                totalHariKerja++;
            }
            date = date.plusDays(1);
        }

        // Ambil semua karyawan
        List<Karyawan> allKaryawan = karyawanRepository.findAll();

        // Ambil semua absen bulan ini dan group by nik, hitung distinct dates per karyawan
        List<Melakukan> bulanIni = melakukanRepository.findByAbsen_TglBetween(startOfMonth, endOfMonth);
        java.util.Map<String, Set<LocalDate>> absenDatesByNik = bulanIni.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getKaryawan().getNik(),
                        Collectors.mapping(m -> m.getAbsen().getTgl(), Collectors.toSet())
                ));

        // Perhitungan gaji
        double gajiPokok = 4_500_000.0;
        double gajiAbsensiFull = 1_000_000.0;
        double gajiPerHari = totalHariKerja > 0 ? gajiAbsensiFull / totalHariKerja : 0;
        double gajiLemburPerHari = 100_000.0;

        return allKaryawan.stream()
                .map(k -> {
                    Set<LocalDate> dates = absenDatesByNik.getOrDefault(k.getNik(), Set.of());
                    
                    // Pisahkan hari absen: weekday (Senin-Jumat) vs weekend/holiday (Sabtu, Minggu)
                    long hariAbsen = dates.stream()
                            .filter(d -> {
                                DayOfWeek dayOfWeek = d.getDayOfWeek();
                                return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
                            })
                            .count();
                    
                    long hariLembur = dates.stream()
                            .filter(d -> {
                                DayOfWeek dayOfWeek = d.getDayOfWeek();
                                return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
                            })
                            .count();
                    
                    double gajiAbsensi = hariAbsen * gajiPerHari;
                    double gajiLembur = hariLembur * gajiLemburPerHari;
                    double totalGaji = gajiPokok + gajiAbsensi + gajiLembur;

                    return new RekapAbsenKaryawanDTO(
                            k.getNik(),
                            k.getNama(),
                            k.getDivisi() != null ? k.getDivisi() : "",
                            (int) hariAbsen,
                            gajiPokok,
                            gajiAbsensi,
                            (int) hariLembur,
                            gajiLembur,
                            totalGaji
                    );
                })
                .collect(Collectors.toList());
    }

    private MelakukanDTO toDTO(Melakukan m) {
        return new MelakukanDTO(
                m.getIdMelakukan(),
                m.getAbsen().getIdAbsen(),
                m.getAbsen().getTgl(),
                m.getKaryawan().getNik(),
                m.getKaryawan().getNama(),
                m.getKaryawan().getDivisi() != null ? m.getKaryawan().getDivisi() : "",
                m.getJamMasuk(),
                m.getJamKeluar(),
                m.getCreatedAt(),
                m.getUpdatedAt());
    }
}
