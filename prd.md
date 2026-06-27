# PRD — Kopkust: Aplikasi Manajemen Koperasi Digital
**Versi:** 1.0.0  
**Tanggal:** Juni 2026  
**Developer:** Erlangga D  
**Status:** Draft Final  

---

## 1. Ringkasan Eksekutif

### 1.1 Latar Belakang

Koperasi merupakan salah satu pilar ekonomi kerakyatan Indonesia yang strategis. Namun pengelolaan koperasi saat ini masih banyak dilakukan secara manual — pencatatan stok dengan buku tulis, laporan keuangan di spreadsheet terpisah, dan rekap simpan pinjam yang rawan kesalahan manusia. Kondisi ini menyebabkan keterlambatan laporan, ketidaktransparanan data, dan potensi kerugian finansial yang tidak terdeteksi.

**Kopkust** hadir sebagai solusi manajemen koperasi berbasis Android yang terintegrasi — mencakup stok, keuangan, simpan pinjam, dan laporan — dalam satu platform yang mudah digunakan oleh pengurus maupun anggota.

### 1.2 Visi Produk

> Menjadi platform manajemen koperasi digital terpercaya yang memberdayakan koperasi-koperasi di Indonesia untuk beroperasi secara transparan, efisien, dan akuntabel.

### 1.3 Tujuan Produk

| No | Tujuan | Metrik Keberhasilan |
|----|--------|---------------------|
| 1  | Mendigitalisasi pencatatan stok | Akurasi stok ≥ 99%, 0 selisih fisik vs sistem per bulan |
| 2  | Mempercepat laporan keuangan | Laporan harian otomatis, laporan bulanan dalam < 2 menit |
| 3  | Transparansi simpan pinjam | Anggota bisa cek saldo & riwayat kapan saja |
| 4  | Kemudahan operasional kasir | Waktu transaksi POS < 30 detik per item |
| 5  | Adopsi pengurus | ≥ 80% pengurus aktif menggunakan dalam 3 bulan pertama |

---

## 2. Pengguna & Persona

### 2.1 Segmen Pengguna

**Aplikasi ini melayani tiga tipe pengguna** dengan hak akses berbeda:

---

#### 👔 Persona 1: Kepala / Ketua Koperasi
**Nama fiktif:** Pak Hendra, 48 tahun  
**Background:** Ketua koperasi simpan pinjam di lingkungan kampus. Tidak terlalu melek teknologi tapi ingin tahu kondisi koperasi setiap saat.

**Kebutuhan:**
- Dashboard ringkasan kondisi koperasi (total aset, kas, piutang, stok)
- Laporan keuangan bulanan & tahunan yang bisa dicetak / di-share via WhatsApp
- Notifikasi jika ada transaksi besar atau anomali keuangan
- Bisa approve / reject pengajuan pinjaman anggota

**Pain Points:**
- Harus menunggu bendahara selesai buat laporan sebelum bisa ambil keputusan
- Tidak tahu persis stok barang tersisa tanpa tanya ke gudang
- Laporan sebelumnya berbeda format setiap bulan, susah dibandingkan

**Goal:** Bisa memantau kesehatan koperasi dari smartphone kapanpun tanpa bergantung orang lain

---

#### 🗂️ Persona 2: Admin / Pengurus Koperasi
**Nama fiktif:** Bu Sari, 35 tahun  
**Background:** Pengurus harian koperasi, merangkap kasir dan pencatat stok. Terbiasa dengan Excel, punya smartphone Android.

**Kebutuhan:**
- Input transaksi penjualan (POS) dengan cepat dan akurat
- Kelola stok masuk / keluar, alert stok menipis
- Catat kas masuk & keluar harian dengan kategori
- Kelola data anggota dan cicilan pinjaman
- Cetak struk transaksi

**Pain Points:**
- Sering salah hitung kembalian saat ramai
- Stok habis baru ketahuan saat sudah dibutuhkan pembeli
- Rekap akhir hari memakan waktu 1–2 jam

**Goal:** Operasional sehari-hari jadi lebih cepat dan tidak ada selisih kas di akhir hari

---

#### 👤 Persona 3: Anggota Koperasi
**Nama fiktif:** Deni, 22 tahun, mahasiswa  
**Background:** Anggota koperasi kampus, rutin menabung dan pernah pinjam untuk kebutuhan kuliah. Aktif di smartphone.

**Kebutuhan:**
- Lihat saldo simpanan (wajib, sukarela, pokok)
- Cek riwayat setor dan penarikan simpanan
- Lihat saldo pinjaman aktif dan jadwal cicilan
- Daftar / ajukan permohonan pinjaman dari app
- Notifikasi jatuh tempo cicilan

**Pain Points:**
- Tidak tahu saldo simpanan tanpa datang langsung ke koperasi
- Terlupa bayar cicilan karena tidak ada pengingat
- Proses pengajuan pinjaman manual butuh banyak waktu

**Goal:** Bisa kelola urusan koperasi dari mana saja tanpa harus antre di kantor

---

## 3. Ruang Lingkup Produk (Scope)

### 3.1 Dalam Scope — Versi 1.0

#### Modul M1: Autentikasi & Manajemen Pengguna
- Login dengan email + password
- Role-based access: Ketua, Admin, Anggota
- Profil pengguna (foto, data diri, no. anggota)
- Reset password via email
- Session management & logout

#### Modul M2: Manajemen Anggota
- CRUD data anggota (nama, NIK, nomor anggota, alamat, foto KTP)
- Lihat status keanggotaan (aktif / tidak aktif)
- Riwayat transaksi per anggota (simpan, pinjam, beli)
- Cetak kartu anggota (PDF)
- Pencarian & filter anggota

#### Modul M3: Manajemen Stok & Inventori
- CRUD produk (nama, kode, harga beli, harga jual, satuan, kategori, foto)
- Input stok masuk (pembelian dari supplier)
- Input stok keluar otomatis dari transaksi POS
- Stok opname (sesuaikan stok fisik vs sistem)
- Alert stok minimum (bisa dikonfigurasi per produk)
- Riwayat mutasi stok per produk
- Ekspor data stok ke PDF/Excel

#### Modul M4: Point of Sale (Kasir)
- Pencarian produk cepat (scan barcode atau ketik nama)
- Keranjang belanja dengan tambah / kurangi / hapus item
- Hitung total otomatis + kembalian
- Pilih metode bayar: Tunai / Transfer / QRIS (placeholder)
- Cetak / share struk digital (PDF atau gambar)
- Riwayat transaksi POS harian
- Diskon per item atau per transaksi

#### Modul M5: Keuangan (Kas Masuk / Keluar)
- Catat kas masuk manual (dengan kategori: penjualan, iuran, dll.)
- Catat kas keluar manual (dengan kategori: pembelian, operasional, dll.)
- Saldo kas real-time
- Jurnal harian otomatis dari transaksi POS
- Laporan arus kas harian / mingguan / bulanan
- Rekonsiliasi kas (bandingkan catatan vs fisik)
- Neraca sederhana

#### Modul M6: Simpan Pinjam
- Manajemen simpanan anggota (pokok, wajib, sukarela)
- Catat setoran & penarikan simpanan
- Pengajuan pinjaman oleh anggota (via app)
- Review & approval pinjaman oleh Ketua / Admin
- Jadwal angsuran otomatis (flat rate / anuitas)
- Pencatatan pembayaran cicilan
- Hitung bunga otomatis
- Status pinjaman: Diajukan → Disetujui → Aktif → Lunas
- Notifikasi jatuh tempo cicilan (in-app + push)
- Denda keterlambatan otomatis (jika diaktifkan)

#### Modul M7: Laporan & Rekap
- Dashboard ringkasan: total kas, stok, piutang, simpanan
- Laporan penjualan (harian, mingguan, bulanan)
- Laporan stok (mutasi, nilai stok, barang hampir habis)
- Laporan keuangan (arus kas, laba rugi sederhana)
- Laporan simpan pinjam (saldo per anggota, daftar pinjaman aktif)
- Export laporan ke PDF
- Share laporan via WhatsApp / email

### 3.2 Luar Scope — Versi 1.0 (Roadmap Berikutnya)

- Integrasi payment gateway nyata (QRIS, GoPay, OVO)
- Multi-cabang koperasi
- Akuntansi double-entry penuh
- Integrasi dengan sistem perbankan
- Versi web admin panel
- AI analitik prediktif

---

## 4. User Stories & Acceptance Criteria

### Modul Autentikasi

**US-01: Login sebagai Admin**
```
Sebagai Admin, saya ingin login dengan email dan password
Agar saya bisa mengakses fitur pengelolaan koperasi

Acceptance Criteria:
- [ ] Form login menampilkan field email dan password
- [ ] Validasi email format yang benar
- [ ] Password disembunyikan dengan opsi show/hide
- [ ] Error message jelas jika kredensial salah
- [ ] Berhasil login → redirect ke dashboard sesuai role
- [ ] Session disimpan sehingga tidak perlu login ulang saat buka app
```

**US-02: Reset Password**
```
Sebagai pengguna yang lupa password, saya ingin reset password via email
Agar saya bisa masuk kembali ke akun

Acceptance Criteria:
- [ ] Tersedia link "Lupa Password?" di halaman login
- [ ] Input email → sistem kirim link reset ke email tersebut
- [ ] Link reset aktif selama 1 jam
- [ ] Password baru minimal 6 karakter
```

### Modul Stok

**US-10: Tambah Produk Baru**
```
Sebagai Admin, saya ingin menambah produk baru ke sistem
Agar produk bisa dijual dan dipantau stoknya

Acceptance Criteria:
- [ ] Form produk: nama, kode, kategori, harga beli, harga jual, satuan, stok awal, minimum stok, foto (opsional)
- [ ] Kode produk unik, sistem validasi duplikat
- [ ] Foto bisa diambil dari kamera atau galeri
- [ ] Produk tersimpan dan langsung muncul di daftar produk & POS
```

**US-11: Alert Stok Menipis**
```
Sebagai Admin, saya ingin mendapat peringatan saat stok produk hampir habis
Agar saya bisa segera restok sebelum kehabisan

Acceptance Criteria:
- [ ] Notifikasi muncul saat stok ≤ nilai minimum_stok yang dikonfigurasi
- [ ] Daftar produk menipis terlihat di dashboard dan halaman stok
- [ ] Badge merah/kuning pada produk yang stoknya kritis
```

**US-12: Stok Opname**
```
Sebagai Admin, saya ingin melakukan stok opname
Agar data stok sistem sesuai dengan stok fisik di gudang

Acceptance Criteria:
- [ ] Bisa input jumlah fisik per produk
- [ ] Sistem hitung selisih (sistem vs fisik) otomatis
- [ ] Selisih positif/negatif tercatat sebagai penyesuaian stok dengan keterangan
- [ ] Riwayat stok opname tersimpan dengan tanggal dan petugas
```

### Modul POS

**US-20: Transaksi Penjualan**
```
Sebagai Kasir, saya ingin memproses penjualan dengan cepat
Agar antrean tidak panjang dan kembalian akurat

Acceptance Criteria:
- [ ] Bisa cari produk dengan ketik nama atau scan barcode
- [ ] Tambah ke keranjang dengan tap, ubah qty dengan +/-
- [ ] Total belanja, diskon, dan kembalian terhitung otomatis
- [ ] Bisa pilih metode bayar: Tunai / Transfer
- [ ] Setelah bayar → stok berkurang otomatis
- [ ] Struk bisa di-share via WhatsApp
- [ ] Seluruh proses < 30 detik untuk 1-3 item
```

### Modul Simpan Pinjam

**US-30: Pengajuan Pinjaman**
```
Sebagai Anggota, saya ingin mengajukan pinjaman dari aplikasi
Agar tidak perlu datang langsung ke koperasi

Acceptance Criteria:
- [ ] Form pengajuan: jumlah, tenor, keperluan
- [ ] Sistem kalkulasi estimasi cicilan otomatis sebelum submit
- [ ] Status pengajuan bisa dipantau real-time
- [ ] Notifikasi saat pengajuan disetujui atau ditolak
```

**US-31: Approval Pinjaman**
```
Sebagai Ketua, saya ingin melihat dan memutuskan pengajuan pinjaman
Agar proses persetujuan bisa dilakukan kapanpun

Acceptance Criteria:
- [ ] Daftar pengajuan masuk dengan data lengkap pemohon
- [ ] Lihat riwayat kredit anggota (pinjaman sebelumnya, cicilan on-time/late)
- [ ] Bisa approve dengan jumlah yang berbeda dari yang diminta
- [ ] Bisa reject dengan alasan
- [ ] Anggota mendapat notifikasi hasil keputusan
```

**US-32: Bayar Cicilan**
```
Sebagai Admin, saya ingin mencatat pembayaran cicilan anggota
Agar saldo pinjaman dan kas ter-update akurat

Acceptance Criteria:
- [ ] Cari anggota → lihat pinjaman aktif → pilih angsuran yang dibayar
- [ ] Hitung denda otomatis jika terlambat (sesuai konfigurasi)
- [ ] Pembayaran tercatat → saldo pinjaman berkurang → kas bertambah
- [ ] Cetak bukti pembayaran cicilan
```

### Modul Laporan

**US-40: Laporan Keuangan Bulanan**
```
Sebagai Ketua, saya ingin melihat laporan keuangan bulanan
Agar bisa evaluasi performa koperasi setiap bulan

Acceptance Criteria:
- [ ] Laporan mencakup: total pendapatan, pengeluaran, laba, saldo kas
- [ ] Bisa filter per periode (minggu, bulan, tahun)
- [ ] Grafik visual (bar chart, line chart)
- [ ] Bisa export ke PDF dan share via WhatsApp
- [ ] Format laporan konsisten setiap bulan
```

---

## 5. Alur Aplikasi (App Flow)

### 5.1 Alur Login & Onboarding
```
Launch App
    ↓
Splash Screen (logo + loading)
    ↓
Cek Session
    ├── Ada Session → Dashboard (sesuai role)
    └── Tidak Ada → Login Screen
                        ↓
                    Input Email + Password
                        ↓
                    Autentikasi Supabase
                        ├── Berhasil → Dashboard
                        └── Gagal → Error Message → Retry
```

### 5.2 Alur Transaksi POS
```
Dashboard Admin
    ↓
Tap "Kasir" / POS
    ↓
Layar POS (search produk + keranjang)
    ↓
Cari Produk (ketik nama atau scan barcode)
    ↓
Tap produk → masuk keranjang
    ↓
(Opsional) Atur qty, tambah diskon
    ↓
Tap "Bayar"
    ↓
Pilih Metode Bayar → Input nominal
    ↓
Konfirmasi → Proses
    ↓
Sukses: Struk ditampilkan
    ├── Share via WhatsApp
    └── Tutup → kembali ke POS
```

### 5.3 Alur Pengajuan & Approval Pinjaman
```
[ANGGOTA]                           [KETUA/ADMIN]
    ↓                                       
Buka Tab Pinjaman                           
    ↓                                       
Tap "Ajukan Pinjaman"                       
    ↓                                       
Isi Form Pengajuan                          
(jumlah, tenor, keperluan)                  
    ↓                                       
Cek estimasi cicilan                        
    ↓                                       
Submit Pengajuan                            
    ↓                                       
Status: "Menunggu Review"                   
                                    Notifikasi masuk → 
                                    Buka Daftar Pengajuan → 
                                    Review Data Anggota + Riwayat →
                                    Approve (mungkin revisi jumlah) / Reject
    ↓                                       
Notifikasi Hasil ←─────────────────────────
    ↓
(Jika Approve) Pinjaman Aktif
Jadwal Cicilan Terbentuk
```

---

## 6. Persyaratan Non-Fungsional

### 6.1 Performa
- App harus launch dalam < 3 detik pada koneksi 4G
- Pencarian produk di POS harus response < 500ms
- Laporan bulanan harus generate dalam < 5 detik
- Transaksi POS harus tersimpan dalam < 2 detik

### 6.2 Keamanan
- Semua data dienkripsi in-transit (HTTPS/TLS)
- Password di-hash dengan bcrypt (ditangani Supabase Auth)
- Row Level Security (RLS) Supabase untuk isolasi data per koperasi
- Token sesi expire setelah 7 hari inaktif
- Log audit untuk semua transaksi finansial (tidak bisa dihapus)

### 6.3 Offline Capability
- Tampilan data terakhir yang sudah di-cache bisa dilihat saat offline
- Transaksi POS bisa dilakukan offline → sinkronisasi otomatis saat online kembali
- Notifikasi warning jika transaksi dilakukan dalam mode offline

### 6.4 Aksesibilitas & UX
- Mendukung text size system Android
- Tombol aksi utama berukuran minimal 48x48dp (touch target)
- Konfirmasi dialog untuk semua aksi yang tidak bisa di-undo
- Pesan error dalam Bahasa Indonesia yang jelas dan actionable
- Dark mode opsional

### 6.5 Kompatibilitas
- Android 8.0 (API Level 26) ke atas
- Ukuran layar: 5 inch hingga 7 inch (tablet dasar)
- RAM minimal 2 GB
- Storage: App size < 50 MB

---

## 7. Persyaratan Data & Privasi

### 7.1 Data yang Dikumpulkan
- Data pribadi anggota: nama, NIK, alamat, foto KTP
- Data finansial: saldo, riwayat transaksi, cicilan
- Data operasional: stok, transaksi penjualan

### 7.2 Kebijakan Data
- Data anggota hanya bisa dilihat oleh admin koperasi yang bersangkutan dan anggota itu sendiri
- Data keuangan hanya dapat dilihat oleh Ketua dan Admin
- Tidak ada sharing data ke pihak ketiga
- Data disimpan di Supabase (server region: Asia Tenggara / Singapore)
- Backup otomatis oleh Supabase setiap hari

---

## 8. Metrik Keberhasilan (KPI)

| KPI | Target V1 | Cara Ukur |
|-----|-----------|-----------|
| Daily Active Users (Admin) | ≥ 90% hari kerja | Supabase analytics |
| Akurasi Stok | < 1% selisih per bulan | Laporan stok opname |
| Waktu Tutup Kasir | < 10 menit / hari | Survey pengguna |
| Pinjaman Disetujui | Proses < 24 jam | Timestamp status change |
| Kepuasan Pengguna | Rating ≥ 4.0/5 | Survey internal |
| Crash Rate | < 0.1% sesi | Firebase Crashlytics |

---

## 9. Timeline Rilis

| Fase | Durasi | Deliverable |
|------|--------|-------------|
| Fase 1: Fondasi | 3 minggu | Auth, Manajemen Anggota, UI Design System |
| Fase 2: Operasional | 4 minggu | Stok + POS (core flow selesai) |
| Fase 3: Keuangan | 3 minggu | Kas Masuk/Keluar, Simpan Pinjam |
| Fase 4: Laporan | 2 minggu | Dashboard, Export PDF |
| Fase 5: Testing & Polish | 2 minggu | UAT, bug fix, performance |
| **Total** | **~14 minggu** | **V1.0 Release** |

---

## 10. Risiko & Mitigasi

| Risiko | Probabilitas | Dampak | Mitigasi |
|--------|-------------|--------|----------|
| Pengurus tidak mau beralih dari Excel | Tinggi | Tinggi | UX semudah mungkin + training + migrasi data awal |
| Koneksi internet tidak stabil di lokasi | Sedang | Tinggi | Implementasi offline mode + sync |
| Data anggota sensitif bocor | Rendah | Sangat Tinggi | RLS Supabase + enkripsi + audit log |
| Salah hitung cicilan pinjaman | Rendah | Tinggi | Unit test formula keuangan + verifikasi manual |
| Supabase free tier limit | Sedang | Sedang | Monitor usage, siap upgrade ke paid plan |

---

## 11. Glossary

| Istilah | Definisi |
|---------|----------|
| Simpanan Pokok | Uang yang dibayar satu kali saat pertama jadi anggota |
| Simpanan Wajib | Iuran rutin (biasanya per bulan) yang wajib dibayar anggota |
| Simpanan Sukarela | Tabungan tambahan yang bisa disetor dan ditarik kapan saja |
| Angsuran / Cicilan | Pembayaran pinjaman per periode (mingguan/bulanan) |
| Stok Opname | Penghitungan fisik stok dan penyesuaian dengan data sistem |
| POS | Point of Sale, sistem transaksi kasir |
| RLS | Row Level Security, fitur keamanan database di Supabase |
| SHU | Sisa Hasil Usaha, keuntungan koperasi yang dibagi ke anggota |

---

*Dokumen ini akan diperbarui seiring perkembangan proyek. Perubahan signifikan dikomunikasikan ke seluruh tim.*

**Dibuat oleh:** Erlangga Dimas Suryadi  
**Disetujui oleh:** Ketua Koprasi STT Raden Wijaya Mojokerto