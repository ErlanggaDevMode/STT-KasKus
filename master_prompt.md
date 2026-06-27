# MASTER_PROMPT.md — AI Coding Agent Brief: Kopkust

**Versi:** 1.0.0  
**Tanggal:** Juni 2026  
**Untuk:** AI Coding Agent (Cursor AI / Windsurf / Antigravity IDE / Claude Code)  
**Proyek:** Kopkust — Aplikasi Manajemen Koperasi Android  
**Institusi:** STT Raden Wijaya Mojokerto  

---

## ⚡ CARA PENGGUNAAN PROMPT INI

Dokumen ini adalah **brief lengkap** untuk AI coding agent. Gunakan dengan cara:

1. **Buka proyek Android baru** di IDE (Android Studio / Cursor / Windsurf)
2. **Paste seluruh konten** file ini ke sistem prompt / context agent
3. **Berikan perintah spesifik** per sesi (contoh ada di Bagian 9)
4. Agent akan membangun kode mengikuti semua spesifikasi di dokumen ini

> PENTING: Setiap sesi baru, selalu ingatkan agent untuk membaca ulang bagian yang relevan dari dokumen ini sebelum mulai menulis kode.

---

## 1. IDENTITAS PROYEK

```
Nama Aplikasi  : Kopkust
Package Name   : com.koperasiku.app
Platform       : Android (Kotlin + Jetpack Compose)
Min SDK        : 26 (Android 8.0)
Target SDK     : 35
Backend        : Supabase (PostgreSQL + Auth + Storage + Realtime)
Arsitektur     : Clean Architecture + MVVM
DI Framework   : Hilt
Database Lokal : Room (SQLite) — offline cache
Institusi      : STT Raden Wijaya Mojokerto
Bahasa UI      : Bahasa Indonesia
```

---

## 2. KONTEKS BISNIS

Kopkust adalah aplikasi Android untuk manajemen koperasi lengkap. Koperasi adalah organisasi ekonomi berbasis anggota (mirip credit union) yang umum di Indonesia. Aplikasi ini menangani:

- **Stok & Inventori** — barang dagangan koperasi, alert stok menipis
- **Point of Sale (Kasir)** — transaksi penjualan dengan struk digital
- **Keuangan** — kas masuk/keluar, saldo real-time, laporan arus kas
- **Simpan Pinjam** — simpanan anggota (pokok/wajib/sukarela), pinjaman + cicilan
- **Manajemen Anggota** — data anggota, riwayat transaksi
- **Laporan** — dashboard, export PDF, share WhatsApp

**Tiga tipe pengguna (role):**
- `KETUA` — Ketua koperasi, bisa approve pinjaman, lihat semua laporan
- `ADMIN` — Pengurus harian, operasional kasir, kelola stok & keuangan
- `ANGGOTA` — Anggota koperasi, lihat saldo, ajukan pinjaman, cek cicilan

---

## 3. ATURAN ARSITEKTUR YANG WAJIB DIIKUTI

### 3.1 Layer Structure
```
presentation/  ← Compose UI + ViewModel + UiState
domain/        ← Use Cases + Repository Interfaces + Domain Models
data/          ← Repository Impl + Room DAO + Supabase Remote Source + Mapper + DTO
core/          ← Utils, DI Modules, Extensions, Constants
```

### 3.2 Aturan Per Layer

**DOMAIN LAYER — Pure Kotlin, zero Android imports:**
- Semua domain model adalah `data class` sederhana
- Repository = interface only, tidak ada implementasi
- Use case = satu file per aksi bisnis, inject repository lewat constructor
- Use case mengembalikan `Flow<Resource<T>>` atau `suspend fun` yang mengembalikan `Resource<T>`

**DATA LAYER:**
- Setiap repository impl mengimplementasikan interface dari domain
- Data mengalir: Remote Source → Mapper → Room Entity (cache) → Domain Model
- Room adalah Single Source of Truth — UI selalu observe Room, bukan Supabase langsung
- Semua Supabase call ada di `RemoteSource` class masing-masing
- DTO (untuk JSON Supabase) dan Entity (untuk Room) adalah kelas terpisah dari Domain Model
- Mapper harus punya fungsi eksplisit: `toDomain()`, `toEntity()`, `toDto()`

**PRESENTATION LAYER:**
- Satu ViewModel per Screen (kecuali shared ViewModel untuk tab)
- ViewModel expose: `StateFlow<XxxUiState>` dan `Flow<XxxEvent>` via `Channel`
- Tidak ada business logic di Composable — semua lewat ViewModel
- Composable hanya boleh: observe state, trigger event, render UI
- Gunakan `collectAsStateWithLifecycle()` bukan `collectAsState()`

### 3.3 Pola UiState Wajib

```kotlin
// Setiap ViewModel HARUS menggunakan pola ini:
data class XxxUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // ... state spesifik screen
)

sealed class XxxEvent {
    data class ShowSnackbar(val message: String) : XxxEvent()
    object NavigateBack : XxxEvent()
    // ... event spesifik
}

@HiltViewModel
class XxxViewModel @Inject constructor(
    private val useCase: XxxUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(XxxUiState())
    val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()

    private val _events = Channel<XxxEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
}
```

### 3.4 Resource Wrapper Wajib

```kotlin
// Gunakan ini di semua use case dan repository:
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
```

### 3.5 Offline-First Strategy

```kotlin
// Pola yang HARUS digunakan di semua repository yang butuh offline support:
fun getXxxList(): Flow<List<Xxx>> = flow {
    // 1. Emit dari Room dulu (data lokal/cache)
    emitAll(localDao.getAll().map { entities -> entities.map { it.toDomain() } })
}.onStart {
    // 2. Sync dari Supabase di background
    if (networkMonitor.isOnline()) {
        try {
            val remoteData = remoteSource.fetchAll()
            localDao.upsertAll(remoteData.map { it.toEntity() })
        } catch (e: Exception) {
            // Gagal sync tidak crash app — data lokal tetap ditampilkan
        }
    }
}
```

---

## 4. SUPABASE CONFIGURATION

### 4.1 Setup Client

```kotlin
// AppModule.kt
@Provides @Singleton
fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Storage)
    install(Realtime)
}
```

### 4.2 Nama Tabel Supabase

```
profiles              ← extend auth.users
tbl_anggota           ← data anggota koperasi
tbl_kategori_produk   ← kategori barang
tbl_produk            ← produk / barang dagangan
tbl_mutasi_stok       ← log perubahan stok
tbl_stok_opname       ← header stok opname
tbl_stok_opname_item  ← detail stok opname
tbl_transaksi         ← header transaksi POS
tbl_transaksi_item    ← detail item transaksi POS
tbl_kategori_kas      ← kategori kas masuk/keluar
tbl_kas               ← catatan kas masuk & keluar
tbl_simpanan          ← rekening simpanan per anggota
tbl_mutasi_simpanan   ← riwayat setoran & penarikan
tbl_pinjaman          ← data pinjaman anggota
tbl_angsuran          ← jadwal & realisasi cicilan
```

### 4.3 Pola Supabase Query

```kotlin
// SELECT
val result = supabase.from("tbl_anggota")
    .select { filter { eq("is_aktif", true) } }
    .decodeList<AnggotaDto>()

// INSERT
supabase.from("tbl_kas").insert(kasDto)

// UPDATE
supabase.from("tbl_pinjaman")
    .update({ set("status", "AKTIF") }) { filter { eq("id", pinjamanId) } }

// DELETE (soft delete — gunakan is_aktif = false, bukan DELETE)
supabase.from("tbl_anggota")
    .update({ set("is_aktif", false) }) { filter { eq("id", anggotaId) } }
```

### 4.4 Auth Pattern

```kotlin
// Login
supabase.auth.signInWith(Email) {
    email = inputEmail
    password = inputPassword
}

// Get current user
val user = supabase.auth.currentUserOrNull()

// Logout
supabase.auth.signOut()

// Check session
val session = supabase.auth.currentSessionOrNull()
```

---

## 5. DATABASE ROOM — PANDUAN

### 5.1 Entity Pattern

```kotlin
@Entity(tableName = "anggota")
data class AnggotaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "nomor_anggota") val nomorAnggota: String,
    val nama: String,
    val nik: String,
    val alamat: String?,
    @ColumnInfo(name = "no_hp") val noHp: String?,
    @ColumnInfo(name = "foto_ktp_url") val fotoKtpUrl: String?,
    @ColumnInfo(name = "is_aktif") val isAktif: Boolean = true,
    @ColumnInfo(name = "tanggal_gabung") val tanggalGabung: String, // ISO format
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true    // untuk offline
)
```

### 5.2 DAO Pattern

```kotlin
@Dao
interface AnggotaDao {
    @Query("SELECT * FROM anggota WHERE is_aktif = 1 ORDER BY nama ASC")
    fun getAll(): Flow<List<AnggotaEntity>>

    @Query("SELECT * FROM anggota WHERE id = :id")
    suspend fun getById(id: String): AnggotaEntity?

    @Query("SELECT * FROM anggota WHERE nama LIKE '%' || :query || '%' OR nomor_anggota LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<AnggotaEntity>>

    @Upsert  // INSERT OR REPLACE
    suspend fun upsertAll(anggota: List<AnggotaEntity>)

    @Upsert
    suspend fun upsert(anggota: AnggotaEntity)

    @Query("UPDATE anggota SET is_aktif = 0 WHERE id = :id")
    suspend fun deactivate(id: String)
}
```

---

## 6. UI / DESAIN SYSTEM

### 6.1 Palet Warna

```kotlin
// Color.kt — Tema Kopkust
val KoperasiGreen = Color(0xFF1B6B3A)        // Primary — hijau koperasi
val KoperasiGreenLight = Color(0xFF4CAF50)   // Primary variant
val KoperasiGold = Color(0xFFF9A825)         // Secondary — emas/kuning
val KoperasiRed = Color(0xFFD32F2F)          // Error / danger / stok habis
val KoperasiOrange = Color(0xFFFF8F00)       // Warning / stok menipis
val KoperasiBlue = Color(0xFF1565C0)         // Info / transfer
val SurfaceLight = Color(0xFFF5F7F5)         // Background screen
val SurfaceCard = Color(0xFFFFFFFF)          // Card background
val TextPrimary = Color(0xFF1A1A1A)          // Teks utama
val TextSecondary = Color(0xFF666666)        // Teks sekunder / label
val TextHint = Color(0xFF9E9E9E)             // Placeholder
val DividerColor = Color(0xFFE0E0E0)         // Garis pemisah
```

### 6.2 Typography

```kotlin
// Typography.kt
val KoperasiTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)
)
```

### 6.3 Komponen Wajib yang Harus Dibuat

Buat semua komponen ini di `presentation/ui/components/` sebelum membangun screen:

```kotlin
// AppButton — primary, secondary, danger, outlined variants
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true
)

// AppTextField — dengan validasi dan error state
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null
)

// CurrencyTextField — input khusus Rupiah
@Composable
fun CurrencyTextField(
    value: Long,
    onValueChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier
)

// AppCard — kartu dengan shadow
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
)

// StatusBadge — chip warna sesuai status
@Composable
fun StatusBadge(status: StatusPinjaman)  // warna per status
@Composable
fun StokBadge(stok: Int, minimum: Int)   // Aman/Menipis/Habis

// AppDialog — konfirmasi, error
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Ya",
    dismissText: String = "Batal"
)

// EmptyState dan ErrorState
@Composable
fun EmptyState(message: String, action: (@Composable () -> Unit)? = null)

@Composable
fun ErrorState(message: String, onRetry: () -> Unit)

// LoadingOverlay
@Composable
fun LoadingOverlay(isVisible: Boolean)
```

### 6.4 Aturan UI

- Minimum touch target: `48.dp` untuk semua elemen interaktif
- Padding screen: `horizontal = 16.dp`, `vertical = 16.dp`
- Gap antar card/item: `8.dp`
- Corner radius card: `12.dp`
- Elevation card: `2.dp`
- Semua teks error dalam Bahasa Indonesia yang jelas
- Angka Rupiah SELALU format: `Rp 1.500.000` (titik sebagai pemisah ribuan)
- Tanggal SELALU format: `1 Juni 2026`
- Konfirmasi dialog WAJIB untuk: hapus data, logout, approve/reject pinjaman, stok opname

---

## 7. NAVIGATION

```kotlin
// AppNavGraph.kt — struktur navigasi

// Auth flow (sebelum login)
composable(Screen.Login.route) { LoginScreen(navController) }
composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }

// Main flow (setelah login — pakai Scaffold + BottomBar)
composable(Screen.Dashboard.route) { DashboardScreen(navController) }

// Anggota
composable(Screen.Anggota.route) { AnggotaListScreen(navController) }
composable("anggota/{anggotaId}") { AnggotaDetailScreen(navController, it.arguments?.getString("anggotaId")!!) }
composable("anggota/form?id={id}") { AnggotaFormScreen(navController, it.arguments?.getString("id")) }

// Stok
composable(Screen.Stok.route) { StokListScreen(navController) }
composable("stok/{produkId}") { StokDetailScreen(navController, it.arguments?.getString("produkId")!!) }

// POS
composable(Screen.Pos.route) { PosScreen(navController) }
composable("pos/struk/{transaksiId}") { StrukScreen(navController, it.arguments?.getString("transaksiId")!!) }

// Keuangan, SimpanPinjam, Laporan — sesuai pola di atas
```

**Bottom Navigation per role:**
```kotlin
val bottomNavItems = when (userRole) {
    UserRole.KETUA -> listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Dashboard, "Beranda"),
        BottomNavItem(Screen.Laporan, Icons.Default.BarChart, "Laporan"),
        BottomNavItem(Screen.SimpanPinjam, Icons.Default.AccountBalance, "Simpinjam"),
        BottomNavItem(Screen.Profil, Icons.Default.Person, "Profil")
    )
    UserRole.ADMIN -> listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Dashboard, "Beranda"),
        BottomNavItem(Screen.Stok, Icons.Default.Inventory, "Stok"),
        BottomNavItem(Screen.Pos, Icons.Default.PointOfSale, "Kasir"),
        BottomNavItem(Screen.Keuangan, Icons.Default.Wallet, "Keuangan"),
        BottomNavItem(Screen.Anggota, Icons.Default.Group, "Anggota")
    )
    UserRole.ANGGOTA -> listOf(
        BottomNavItem(Screen.Dashboard, Icons.Default.Home, "Beranda"),
        BottomNavItem(Screen.SimpanPinjam, Icons.Default.AccountBalance, "Simpinjam"),
        BottomNavItem(Screen.Profil, Icons.Default.Person, "Profil")
    )
}
```

---

## 8. PANDUAN PER MODUL

### Modul: POS (Kasir) — Prioritas Tinggi

**Layout POS Screen (landscape-friendly, split panel):**
```
┌─────────────────────────┬───────────────────────────┐
│   Panel Kiri (55%)      │   Panel Kanan (45%)        │
│                         │                            │
│  SearchBar produk       │  KERANJANG BELANJA         │
│  + tombol scan barcode  │                            │
│                         │  [Item 1]  Rp 5.000  [+/-]│
│  Grid produk (LazyGrid) │  [Item 2]  Rp 12.000 [+/-]│
│  Tap → masuk keranjang  │                            │
│                         │  Subtotal: Rp 17.000       │
│                         │  Diskon:   Rp 0            │
│                         │  ─────────────────         │
│                         │  TOTAL:    Rp 17.000       │
│                         │                            │
│                         │  [    BAYAR SEKARANG    ]  │
└─────────────────────────┴───────────────────────────┘
```

**Payment BottomSheet:**
```
Pilih Metode: [Tunai] [Transfer]
Nominal Bayar: [__________]
Kembalian: Rp X.XXX
[Proses Transaksi]
```

**Setelah transaksi sukses:**
- Kurangi stok otomatis (Room + queue sync ke Supabase)
- Catat kas masuk otomatis
- Tampilkan StrukScreen
- Tombol: Share WhatsApp, Cetak, Transaksi Baru

### Modul: Simpan Pinjam

**Kalkulasi Angsuran Flat Rate:**
```
Pokok per bulan = Total Pinjaman / Tenor
Bunga per bulan = Total Pinjaman × Bunga% per bulan
Angsuran        = Pokok + Bunga
Total Bayar     = Angsuran × Tenor
```

**Status Flow Pinjaman:**
```
DIAJUKAN → (Ketua/Admin review) → DISETUJUI / DITOLAK
DISETUJUI → (Admin cairkan) → AKTIF (jadwal angsuran terbentuk)
AKTIF → (semua angsuran lunas) → LUNAS
```

**Warna status:**
- DIAJUKAN → `KoperasiOrange`
- DISETUJUI → `KoperasiBlue`
- AKTIF → `KoperasiGreen`
- LUNAS → `TextSecondary`
- DITOLAK → `KoperasiRed`

### Modul: Stok

**StokBadge logic:**
```kotlin
when {
    stok == 0 -> Badge merah "Habis"
    stok <= minimum -> Badge oranye "Menipis"
    else -> Badge hijau "Aman"
}
```

**Mutasi stok terjadi saat:**
- Transaksi POS → `KELUAR` (otomatis via trigger DB)
- Input stok masuk → `MASUK`
- Stok opname → `OPNAME` (penyesuaian ±)
- Retur barang → `RETUR`

### Modul: Laporan

**Dashboard Summary Cards (4 kartu):**
```kotlin
SummaryCard("Saldo Kas", saldoKas.toRupiah(), KoperasiGreen, Icons.Filled.Wallet)
SummaryCard("Total Stok", "$totalProduk produk", KoperasiBlue, Icons.Filled.Inventory)
SummaryCard("Piutang Aktif", totalPiutang.toRupiah(), KoperasiOrange, Icons.Filled.Receipt)
SummaryCard("Anggota Aktif", "$totalAnggota orang", KoperasiGold, Icons.Filled.Group)
```

**Export PDF:**
- Gunakan library iTextPDF
- PDF disimpan sementara di cache dir, langsung share via Intent
- Jangan simpan permanen di storage eksternal

---

## 9. PERINTAH SESI AGENT — GUNAKAN INI

Untuk setiap sesi, berikan perintah berikut ke agent. Kamu bisa copy-paste salah satu yang sesuai kebutuhan:

---

### SESI 1 — Setup Proyek & Fondasi

```
Kamu adalah senior Android developer yang membangun Kopkust.
Baca konteks di atas dengan seksama, lalu:

1. Buat project Android baru dengan package com.koperasiku.app
2. Setup semua dependency di libs.versions.toml dan build.gradle.kts sesuai tech.md
3. Buat AppDatabase.kt dengan semua entity dan DAO sesuai struktur di structure.md
4. Buat AppModule.kt, DatabaseModule.kt, RepositoryModule.kt untuk Hilt
5. Buat KoperasiKuApp.kt sebagai Application class dengan @HiltAndroidApp
6. Buat MainActivity.kt sebagai single activity host
7. Buat theme/Color.kt, Typography.kt, Shape.kt, Theme.kt dengan design token yang sudah ditentukan
8. Buat Screen.kt sealed class dengan semua route

Jangan buat UI screen dulu. Fokus pada fondasi dan dependency injection.
Semua file harus compile tanpa error.
```

---

### SESI 2 — Komponen UI & Design System

```
Lanjutkan Kopkust. Fondasi sudah ada.
Sekarang buat semua reusable components di presentation/ui/components/:

1. AppButton.kt — primary, secondary, danger, outlined variants + loading state
2. AppTextField.kt — dengan validasi error dan show/hide password
3. CurrencyTextField.kt — input Rupiah otomatis format titik ribuan
4. AppCard.kt — dengan dan tanpa onClick
5. StatusBadge.kt — StatusPinjaman dan StokBadge
6. AppDialog.kt — ConfirmDialog dan ErrorDialog
7. EmptyState.kt dan ErrorState.kt
8. LoadingOverlay.kt
9. SearchBar.kt
10. AppTopBar.kt — dengan back button opsional dan action icons

Gunakan design tokens dari Color.kt dan Typography.kt yang sudah dibuat.
Semua komponen harus punya @Preview dengan data dummy.
```

---

### SESI 3 — Auth Module

```
Lanjutkan Kopkust. Setup dan UI components sudah ada.
Buat modul Auth lengkap:

Domain:
- User.kt domain model dengan field: id, nama, noHp, fotoUrl, role (UserRole enum)
- UserRole.kt enum: KETUA, ADMIN, ANGGOTA
- AuthRepository.kt interface
- LoginUseCase.kt, LogoutUseCase.kt, ResetPasswordUseCase.kt

Data:
- AuthRemoteSource.kt — Supabase Auth calls
- AuthRepositoryImpl.kt
- SessionManager.kt — simpan & ambil user session dengan DataStore

Presentation:
- LoginScreen.kt dengan validasi email + password
- LoginViewModel.kt dengan UiState pattern
- ForgotPasswordScreen.kt dan ViewModel
- Redirect otomatis ke Dashboard jika sudah login
- Redirect ke Login jika session expired

Pastikan setelah login, role user tersimpan di SessionManager dan dipakai untuk
menentukan bottom nav yang ditampilkan.
```

---

### SESI 4 — Modul Anggota

```
Lanjutkan Kopkust. Auth sudah berjalan.
Buat modul Anggota lengkap (mengikuti pola Clean Architecture):

Domain:
- Anggota.kt domain model (semua field sesuai tech.md)
- AnggotaRepository.kt interface
- GetAnggotaListUseCase, GetAnggotaDetailUseCase, CreateAnggotaUseCase,
  UpdateAnggotaUseCase, DeactivateAnggotaUseCase

Data:
- AnggotaEntity.kt (Room)
- AnggotaDto.kt (Supabase JSON)
- AnggotaDao.kt dengan getAll() Flow, search(), upsertAll(), getById()
- AnggotaRemoteSource.kt
- AnggotaMapper.kt: toDomain(), toEntity(), toDto()
- AnggotaRepositoryImpl.kt dengan offline-first pattern

Presentation:
- AnggotaListScreen: list + search bar + FAB tambah anggota
- AnggotaDetailScreen: info lengkap + riwayat transaksi anggota
- AnggotaFormScreen: form create & edit (shared, id null = create)
- AnggotaViewModel.kt

UI rules:
- Kartu anggota tampilkan: foto (Coil placeholder), nama, nomor anggota, badge aktif/nonaktif
- Soft delete (is_aktif = false), bukan hapus permanen
- Konfirmasi dialog saat nonaktifkan anggota
```

---

### SESI 5 — Modul Stok & Inventori

```
Lanjutkan Kopkust. Modul Anggota sudah selesai.
Buat modul Stok lengkap:

Domain:
- Produk.kt dengan computed properties isStokMenipis, isStokHabis
- ProdukRepository.kt interface
- GetProdukListUseCase, GetProdukStokMenipisUseCase, CreateProdukUseCase,
  UpdateProdukUseCase, UpdateStokUseCase, StokOpnameUseCase

Data:
- ProdukEntity.kt, ProdukDto.kt, ProdukDao.kt
- MutasiStokEntity.kt, MutasiStokDao.kt
- ProdukRemoteSource.kt, ProdukMapper.kt, ProdukRepositoryImpl.kt

Presentation:
- StokListScreen: list produk dengan filter (semua/menipis/habis) + FAB + search
- StokDetailScreen: info produk + grafik mutasi stok 30 hari terakhir + history
- ProdukFormScreen: form create & edit produk + ambil foto (kamera/galeri dengan Coil preview)
- StokMasukScreen: form input barang masuk dari supplier
- StokOpnameScreen: list produk + input stok fisik + lihat selisih real-time
- StokViewModel.kt

Warna badge stok:
- Habis (stok=0): KoperasiRed
- Menipis (stok <= minimum): KoperasiOrange  
- Aman: KoperasiGreen
```

---

### SESI 6 — Modul POS (Kasir)

```
Lanjutkan Kopkust. Stok sudah berjalan.
Buat modul POS lengkap — ini modul paling kritis untuk penggunaan harian:

Domain:
- Transaksi.kt, TransaksiItem.kt, MetodeBayar.kt enum
- TransaksiRepository.kt interface
- CreateTransaksiUseCase.kt (termasuk kurangi stok + catat kas masuk)
- GetTransaksiListUseCase.kt

Data:
- TransaksiEntity.kt + TransaksiItemEntity.kt (dengan field is_synced untuk offline)
- TransaksiDao.kt dengan query: getByDate(), getPending (is_synced=false)
- TransaksiRemoteSource.kt, TransaksiMapper.kt, TransaksiRepositoryImpl.kt
- SyncWorker.kt — WorkManager job untuk sync transaksi offline ke Supabase

Presentation:
- PosScreen.kt: split panel (search kiri, keranjang kanan) atau stacked di mobile
- PosViewModel.kt: kelola state keranjang, hitung total, proses bayar
- PaymentBottomSheet.kt: pilih metode, input nominal, hitung kembalian
- StrukScreen.kt: tampilkan struk + tombol share WhatsApp + tombol transaksi baru
- RiwayatTransaksiScreen.kt: list transaksi hari ini dengan total

PENTING:
- Pencarian produk harus realtime saat ketik (debounce 300ms)
- Setelah transaksi sukses: stok berkurang di Room SEGERA (bukan nunggu sync)
- Kas masuk otomatis tercatat di Room bersamaan dengan transaksi
- Struk harus bisa di-share sebagai teks atau gambar (screenshot StrukContent)
- Mode offline: transaksi tersimpan lokal, WorkManager sync saat online
```

---

### SESI 7 — Modul Keuangan

```
Lanjutkan Kopkust. POS sudah berjalan.
Buat modul Keuangan (Kas) lengkap:

Domain:
- Kas.kt, JenisKas.kt enum (MASUK/KELUAR), KategoriKas.kt
- KasRepository.kt interface
- GetSaldoKasUseCase, CatatKasUseCase, GetLaporanKasUseCase

Data:
- KasEntity.kt, KasDto.kt, KasDao.kt (dengan query saldo, per tanggal, per bulan)
- KasRemoteSource.kt, KasMapper.kt, KasRepositoryImpl.kt

Presentation:
- KeuanganScreen.kt: 3 tab — Kas Masuk | Kas Keluar | Laporan
- CatatKasScreen.kt: form catat kas manual (pilih jenis, kategori, nominal, keterangan, tanggal)
- KeuanganViewModel.kt

Dashboard Keuangan menampilkan:
- SaldoCard: kas masuk total, kas keluar total, saldo bersih (hijau jika positif)
- List transaksi kas hari ini dengan kategori dan nominal
- Filter: Hari ini / Minggu ini / Bulan ini / Custom range
- Bar chart: kas masuk vs keluar per minggu (Vico library)

PERHATIAN:
- Saldo kas adalah SUM dari semua kas masuk dikurangi kas keluar (bukan field tersendiri)
- Transaksi POS yang berhasil otomatis membuat record kas masuk dengan kategori "Penjualan"
- Jangan ada tombol hapus transaksi kas — hanya bisa koreksi/reversal dengan entri baru
```

---

### SESI 8 — Modul Simpan Pinjam

```
Lanjutkan Kopkust. Keuangan sudah selesai.
Buat modul Simpan Pinjam — modul paling kompleks:

Domain:
- Simpanan.kt, MutasiSimpanan.kt, JenisSimpanan.kt, JenisMutasiSimpanan.kt
- Pinjaman.kt (dengan computed property sisaPokok), Angsuran.kt, StatusPinjaman.kt
- Repository interfaces: SimpananRepository, PinjamanRepository
- Use cases:
  - GetSaldoSimpananUseCase, SetoranSimpananUseCase, PenarikanSimpananUseCase
  - AjukanPinjamanUseCase, ApprovePinjamanUseCase, RejectPinjamanUseCase
  - BayarAngsuranUseCase, HitungAngsuranUseCase, GetPinjamanAktifUseCase

Data:
- Semua entity, DTO, DAO, mapper, remote source, repository impl untuk simpanan & pinjaman

Presentation:
- SimpanPinjamScreen.kt: 2 tab — Simpanan | Pinjaman
- SimpananScreen.kt: kartu saldo per jenis (Pokok/Wajib/Sukarela) + riwayat mutasi
- SetoranScreen.kt: form setoran/penarikan simpanan
- PinjamanListScreen.kt: list pinjaman anggota + filter status
- PinjamanDetailScreen.kt: detail pinjaman + tabel jadwal angsuran + status tiap cicilan
- AjukanPinjamanScreen.kt: form ajukan pinjaman + preview kalkulasi cicilan real-time
- ApprovalPinjamanScreen.kt (KETUA only): review pengajuan + approve/reject + ubah jumlah
- BayarAngsuranScreen.kt: pilih angsuran yang dibayar + hitung denda otomatis
- SimpanPinjamViewModel.kt

PENTING — Kalkulasi flat rate:
  pokok_per_bulan = jumlah / tenor
  bunga_per_bulan = jumlah * (bunga_persen / 100)
  angsuran        = pokok + bunga (sama setiap bulan)

Denda keterlambatan: 0.5% per hari dari nilai angsuran yang terlambat
Setelah approve pinjaman → kas keluar otomatis tercatat (kategori: Pencairan Pinjaman)
Setelah bayar angsuran → kas masuk otomatis tercatat (kategori: Pembayaran Cicilan)
```

---

### SESI 9 — Modul Laporan & Dashboard

```
Lanjutkan Kopkust. Semua modul operasional sudah selesai.
Buat modul Laporan dan finalisasi Dashboard:

Domain:
- GetLaporanPenjualanUseCase, GetLaporanKasUseCase (sudah ada), GetDashboardSummaryUseCase

Presentation:
DASHBOARD (berbeda per role):

[KETUA]
- 4 summary cards: Saldo Kas, Total Piutang, Total Simpanan, Anggota Aktif
- Line chart: trend penjualan 7 hari terakhir
- Daftar pinjaman menunggu approval (badge notif)
- Quick actions: Lihat Laporan, Approval Pinjaman

[ADMIN]
- 4 summary cards: Saldo Kas, Stok Menipis (jumlah produk), Transaksi Hari Ini, Piutang
- Daftar produk stok menipis/habis
- Quick actions: Buka Kasir, Catat Kas, Input Stok Masuk

[ANGGOTA]
- Kartu saldo simpanan (total semua jenis)
- Kartu pinjaman aktif (sisa pokok + angsuran berikutnya)
- Riwayat transaksi terakhir (pembelian + cicilan)
- Quick action: Ajukan Pinjaman

LAPORAN SCREEN (4 tab):
1. Penjualan: filter periode, total omset, total item terjual, grafik bar per hari, list transaksi
2. Keuangan: arus kas masuk vs keluar, saldo, grafik perbandingan per minggu
3. Stok: nilai total stok, stok menipis, mutasi stok terbanyak, produk terlaris
4. Simpan Pinjam: total simpanan, total piutang, daftar pinjaman aktif, angsuran bulan ini

Export PDF: tombol export di setiap tab laporan
- Generate PDF dengan iTextPDF
- Header PDF: nama koperasi, periode, tanggal cetak
- Footer PDF: "Kopkust — STT Raden Wijaya Mojokerto"
- Share via Intent ke WhatsApp atau aplikasi lain
```

---

### SESI 10 — Profil, Notifikasi & Polish

```
Lanjutkan Kopkust. Semua fitur utama sudah selesai.
Sekarang finalisasi dan polish:

1. ProfilScreen.kt:
   - Tampilkan foto, nama, role, nomor anggota (jika ANGGOTA)
   - Edit profil: nama, foto, no HP
   - Tombol Logout dengan konfirmasi dialog
   - Info versi app

2. Notifikasi in-app:
   - Stok produk mencapai batas minimum → tampilkan banner di Dashboard
   - Angsuran jatuh tempo dalam 3 hari → tampilkan reminder di SimpanPinjam
   - Status pinjaman berubah → badge di tab Pinjaman

3. Supabase Realtime:
   - Admin mendapat notifikasi real-time saat ada pengajuan pinjaman baru
   - Anggota mendapat notifikasi real-time saat status pinjaman berubah

4. Offline indicator:
   - Banner kuning di atas screen jika tidak ada koneksi internet
   - Pesan "Data mungkin tidak terbaru" jika sudah > 1 jam tidak sync

5. Empty state yang baik untuk semua list screen

6. Error handling konsisten:
   - Supabase timeout → retry otomatis 3x dengan exponential backoff
   - Error yang ditampilkan ke user harus dalam Bahasa Indonesia yang ramah

7. Konfigurasi Fireabse Crashlytics untuk production crash reporting

8. ProGuard rules untuk Supabase, Kotlin serialization, dan iTextPDF
```

---

### SESI 11 — Testing

```
Lanjutkan Kopkust. Semua fitur sudah selesai.
Buat unit tests untuk logika bisnis kritis:

1. HitungAngsuranUseCaseTest.kt:
   - Test flat rate dengan berbagai kombinasi jumlah/tenor/bunga
   - Test edge case: tenor 1 bulan, bunga 0%
   - Validasi total_bayar == jumlah + (bunga × tenor)

2. BayarAngsuranUseCaseTest.kt:
   - Test pembayaran tepat waktu (denda = 0)
   - Test pembayaran terlambat (denda = 0.5% × hari terlambat × angsuran)
   - Test update saldo pinjaman setelah bayar

3. CreateTransaksiUseCaseTest.kt:
   - Test stok berkurang setelah transaksi
   - Test kembalian = bayar - total
   - Test gagal jika stok < jumlah order

4. KasRepositoryTest.kt:
   - Test saldo kas = total masuk - total keluar
   - Test filter tanggal

Gunakan MockK untuk mock dependencies.
Gunakan kotlinx-coroutines-test untuk coroutine testing.
```

---

## 10. ATURAN UMUM AGENT

### Yang HARUS dilakukan:

- ✅ Selalu ikuti struktur folder di `structure.md`
- ✅ Selalu gunakan `Resource<T>` wrapper di use case
- ✅ Selalu pakai `collectAsStateWithLifecycle()` bukan `collectAsState()`
- ✅ Selalu format uang sebagai `Long` dalam Rupiah (bukan Double/Float)
- ✅ Selalu soft-delete (is_aktif = false), tidak pernah hard delete data finansial
- ✅ Selalu buat `@Preview` untuk setiap Composable
- ✅ Selalu cek null safety — Kotlin idiomatic, gunakan `?.let {}`, `?: return`
- ✅ Semua teks yang tampil di UI harus dalam Bahasa Indonesia
- ✅ Gunakan `@StringRes` untuk string yang bisa dilokalisasi (daftar ke `strings.xml`)
- ✅ Konfirmasi dialog sebelum aksi permanen (approve, nonaktifkan, dsb)
- ✅ Handle loading state di semua screen yang fetch data
- ✅ Handle error state dengan tombol retry
- ✅ Handle empty state dengan ilustrasi dan pesan yang ramah

### Yang TIDAK BOLEH dilakukan:

- ❌ Jangan hardcode Supabase URL/Key di kode — wajib dari `BuildConfig`
- ❌ Jangan gunakan `Double` atau `Float` untuk kalkulasi uang — gunakan `Long` (Rupiah bulat)
- ❌ Jangan panggil Supabase langsung dari ViewModel atau Composable
- ❌ Jangan taruh business logic di Composable
- ❌ Jangan hard delete data transaksi, kas, pinjaman, simpanan — data finansial tidak bisa dihapus
- ❌ Jangan abaikan offline state — semua operasi data harus handle kasus tidak ada internet
- ❌ Jangan gunakan `Thread.sleep()` — gunakan coroutines `delay()`
- ❌ Jangan commit `local.properties` ke Git
- ❌ Jangan buat UI dengan hardcoded string — daftarkan semua ke `strings.xml`
- ❌ Jangan gunakan `!!` (not-null assertion) kecuali benar-benar terjamin tidak null

---

## 11. CHECKLIST SEBELUM SELESAI PER SESI

Sebelum mengakhiri setiap sesi coding, pastikan:

```
[ ] Semua file baru compile tanpa error
[ ] Tidak ada import yang tidak terpakai
[ ] Tidak ada TODO yang tertinggal tanpa keterangan
[ ] Setiap Composable baru punya @Preview
[ ] Setiap ViewModel baru punya UiState dan Event
[ ] Setiap use case baru punya interface repository yang sesuai
[ ] Offline scenario sudah dihandle (atau didokumentasikan akan dihandle di sesi sync)
[ ] Tidak ada hardcoded string di Composable (semua ke strings.xml)
[ ] Error message dalam Bahasa Indonesia
```

---

*Master prompt ini adalah panduan hidup — update jika ada perubahan arsitektur, keputusan teknis baru, atau scope yang berubah.*

**Kopkust — Membangun Koperasi Digital untuk Indonesia**  
**STT Raden Wijaya Mojokerto × Tim Developer**