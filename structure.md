# STRUCTURE.md — Arsitektur & Struktur Proyek KoperasiKu

**Versi:** 1.0.0  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Arsitektur:** Clean Architecture + MVVM  
**Tanggal:** Juni 2026  

---

## 1. Gambaran Arsitektur

KoperasiKu menggunakan **Clean Architecture** dengan 3 layer utama yang dipadukan dengan pola **MVVM (Model-View-ViewModel)**. Pemisahan layer ini memastikan kode mudah ditest, dikembangkan, dan dimaintain oleh tim kecil sekalipun.

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│         (Jetpack Compose UI + ViewModel + State)        │
├─────────────────────────────────────────────────────────┤
│                      DOMAIN LAYER                        │
│           (Use Cases + Domain Models + Repository        │
│                       Interfaces)                        │
├─────────────────────────────────────────────────────────┤
│                       DATA LAYER                         │
│    (Repository Impl + Supabase Remote + Room Local)     │
└─────────────────────────────────────────────────────────┘
```

### Aliran Data (Data Flow)

```
UI (Composable)
    ↕  observe StateFlow / events
ViewModel
    ↕  call use case
Use Case (Domain)
    ↕  call repository interface
Repository Interface (Domain)
    ↕  implemented by
Repository Impl (Data)
    ├── Supabase Client (Remote / Online)
    └── Room DAO (Local / Offline Cache)
```

---

## 2. Struktur Folder Lengkap

```
KoperasiKu/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/koperasiku/app/
│   │   │   │   │
│   │   │   │   ├── KoperasiKuApp.kt              ← Application class (Hilt entry point)
│   │   │   │   ├── MainActivity.kt               ← Single Activity host
│   │   │   │   │
│   │   │   │   ├── core/                         ← Shared utilities (tidak domain-spesifik)
│   │   │   │   │   ├── constants/
│   │   │   │   │   │   ├── AppConstants.kt       ← String keys, config values
│   │   │   │   │   │   └── SupabaseConstants.kt  ← URL, anon key (dari BuildConfig)
│   │   │   │   │   ├── di/                       ← Dependency Injection (Hilt modules)
│   │   │   │   │   │   ├── AppModule.kt          ← Supabase client, Retrofit, dsb
│   │   │   │   │   │   ├── DatabaseModule.kt     ← Room database instance
│   │   │   │   │   │   └── RepositoryModule.kt   ← Bind interface → implementation
│   │   │   │   │   ├── extensions/
│   │   │   │   │   │   ├── DateExtensions.kt     ← Format tanggal Indonesia
│   │   │   │   │   │   ├── CurrencyExtensions.kt ← Format Rupiah
│   │   │   │   │   │   └── FlowExtensions.kt     ← Flow helpers
│   │   │   │   │   ├── network/
│   │   │   │   │   │   └── NetworkMonitor.kt     ← Deteksi koneksi online/offline
│   │   │   │   │   ├── session/
│   │   │   │   │   │   └── SessionManager.kt     ← Kelola sesi login & role user
│   │   │   │   │   └── utils/
│   │   │   │   │       ├── BarcodeScanner.kt     ← ML Kit barcode scanner wrapper
│   │   │   │   │       ├── PdfGenerator.kt       ← Generate PDF laporan & struk
│   │   │   │   │       └── PrintHelper.kt        ← Share struk ke WhatsApp / print
│   │   │   │   │
│   │   │   │   ├── data/                         ← DATA LAYER
│   │   │   │   │   ├── local/                    ← Room (SQLite) — offline cache
│   │   │   │   │   │   ├── AppDatabase.kt        ← Room database definition
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── AnggotaDao.kt
│   │   │   │   │   │   │   ├── ProdukDao.kt
│   │   │   │   │   │   │   ├── TransaksiDao.kt
│   │   │   │   │   │   │   ├── KasDao.kt
│   │   │   │   │   │   │   ├── PinjamanDao.kt
│   │   │   │   │   │   │   └── SimpananDao.kt
│   │   │   │   │   │   ├── entity/               ← Room entities (tabel SQLite)
│   │   │   │   │   │   │   ├── AnggotaEntity.kt
│   │   │   │   │   │   │   ├── ProdukEntity.kt
│   │   │   │   │   │   │   ├── TransaksiEntity.kt
│   │   │   │   │   │   │   ├── TransaksiItemEntity.kt
│   │   │   │   │   │   │   ├── KasEntity.kt
│   │   │   │   │   │   │   ├── PinjamanEntity.kt
│   │   │   │   │   │   │   ├── AngsuranEntity.kt
│   │   │   │   │   │   │   └── SimpananEntity.kt
│   │   │   │   │   │   └── converter/
│   │   │   │   │   │       └── DateConverter.kt  ← Room type converter untuk Date
│   │   │   │   │   │
│   │   │   │   │   ├── remote/                   ← Supabase (cloud)
│   │   │   │   │   │   ├── dto/                  ← Data Transfer Objects (JSON shape)
│   │   │   │   │   │   │   ├── AnggotaDto.kt
│   │   │   │   │   │   │   ├── ProdukDto.kt
│   │   │   │   │   │   │   ├── TransaksiDto.kt
│   │   │   │   │   │   │   ├── TransaksiItemDto.kt
│   │   │   │   │   │   │   ├── KasDto.kt
│   │   │   │   │   │   │   ├── PinjamanDto.kt
│   │   │   │   │   │   │   ├── AngsuranDto.kt
│   │   │   │   │   │   │   └── SimpananDto.kt
│   │   │   │   │   │   └── source/               ← Supabase calls
│   │   │   │   │   │       ├── AuthRemoteSource.kt
│   │   │   │   │   │       ├── AnggotaRemoteSource.kt
│   │   │   │   │   │       ├── ProdukRemoteSource.kt
│   │   │   │   │   │       ├── TransaksiRemoteSource.kt
│   │   │   │   │   │       ├── KasRemoteSource.kt
│   │   │   │   │   │       ├── PinjamanRemoteSource.kt
│   │   │   │   │   │       └── SimpananRemoteSource.kt
│   │   │   │   │   │
│   │   │   │   │   ├── mapper/                   ← Konversi DTO ↔ Entity ↔ Domain Model
│   │   │   │   │   │   ├── AnggotaMapper.kt
│   │   │   │   │   │   ├── ProdukMapper.kt
│   │   │   │   │   │   ├── TransaksiMapper.kt
│   │   │   │   │   │   ├── KasMapper.kt
│   │   │   │   │   │   ├── PinjamanMapper.kt
│   │   │   │   │   │   └── SimpananMapper.kt
│   │   │   │   │   │
│   │   │   │   │   └── repository/               ← Implementasi repository (Data layer)
│   │   │   │   │       ├── AuthRepositoryImpl.kt
│   │   │   │   │       ├── AnggotaRepositoryImpl.kt
│   │   │   │   │       ├── ProdukRepositoryImpl.kt
│   │   │   │   │       ├── TransaksiRepositoryImpl.kt
│   │   │   │   │       ├── KasRepositoryImpl.kt
│   │   │   │   │       ├── PinjamanRepositoryImpl.kt
│   │   │   │   │       └── SimpananRepositoryImpl.kt
│   │   │   │   │
│   │   │   │   ├── domain/                       ← DOMAIN LAYER (pure Kotlin, no Android deps)
│   │   │   │   │   ├── model/                    ← Domain models (business objects)
│   │   │   │   │   │   ├── User.kt
│   │   │   │   │   │   ├── Anggota.kt
│   │   │   │   │   │   ├── Produk.kt
│   │   │   │   │   │   ├── Transaksi.kt
│   │   │   │   │   │   ├── TransaksiItem.kt
│   │   │   │   │   │   ├── Kas.kt
│   │   │   │   │   │   ├── Pinjaman.kt
│   │   │   │   │   │   ├── Angsuran.kt
│   │   │   │   │   │   ├── Simpanan.kt
│   │   │   │   │   │   └── enums/
│   │   │   │   │   │       ├── UserRole.kt       ← KETUA, ADMIN, ANGGOTA
│   │   │   │   │   │       ├── StatusPinjaman.kt ← DIAJUKAN, DISETUJUI, AKTIF, LUNAS, DITOLAK
│   │   │   │   │   │       ├── MetodeBayar.kt    ← TUNAI, TRANSFER, QRIS
│   │   │   │   │   │       └── JenisKas.kt       ← MASUK, KELUAR
│   │   │   │   │   │
│   │   │   │   │   ├── repository/               ← Interface repository (kontrak)
│   │   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   │   ├── AnggotaRepository.kt
│   │   │   │   │   │   ├── ProdukRepository.kt
│   │   │   │   │   │   ├── TransaksiRepository.kt
│   │   │   │   │   │   ├── KasRepository.kt
│   │   │   │   │   │   ├── PinjamanRepository.kt
│   │   │   │   │   │   └── SimpananRepository.kt
│   │   │   │   │   │
│   │   │   │   │   └── usecase/                  ← Use cases (1 aksi = 1 file)
│   │   │   │   │       ├── auth/
│   │   │   │   │       │   ├── LoginUseCase.kt
│   │   │   │   │       │   ├── LogoutUseCase.kt
│   │   │   │   │       │   └── ResetPasswordUseCase.kt
│   │   │   │   │       ├── anggota/
│   │   │   │   │       │   ├── GetAnggotaListUseCase.kt
│   │   │   │   │       │   ├── GetAnggotaDetailUseCase.kt
│   │   │   │   │       │   ├── CreateAnggotaUseCase.kt
│   │   │   │   │       │   ├── UpdateAnggotaUseCase.kt
│   │   │   │   │       │   └── DeactivateAnggotaUseCase.kt
│   │   │   │   │       ├── produk/
│   │   │   │   │       │   ├── GetProdukListUseCase.kt
│   │   │   │   │       │   ├── GetProdukStokMenipisUseCase.kt
│   │   │   │   │       │   ├── CreateProdukUseCase.kt
│   │   │   │   │       │   ├── UpdateProdukUseCase.kt
│   │   │   │   │       │   ├── UpdateStokUseCase.kt
│   │   │   │   │       │   └── StokOpnameUseCase.kt
│   │   │   │   │       ├── transaksi/
│   │   │   │   │       │   ├── GetTransaksiListUseCase.kt
│   │   │   │   │       │   ├── CreateTransaksiUseCase.kt  ← POS checkout
│   │   │   │   │       │   └── GetLaporanPenjualanUseCase.kt
│   │   │   │   │       ├── kas/
│   │   │   │   │       │   ├── GetSaldoKasUseCase.kt
│   │   │   │   │       │   ├── CatatKasUseCase.kt
│   │   │   │   │       │   └── GetLaporanKasUseCase.kt
│   │   │   │   │       ├── pinjaman/
│   │   │   │   │       │   ├── AjukanPinjamanUseCase.kt
│   │   │   │   │       │   ├── ApprovePinjamanUseCase.kt
│   │   │   │   │       │   ├── RejectPinjamanUseCase.kt
│   │   │   │   │       │   ├── BayarAngsuranUseCase.kt
│   │   │   │   │       │   ├── HitungAngsuranUseCase.kt  ← Kalkulasi cicilan
│   │   │   │   │       │   └── GetPinjamanAktifUseCase.kt
│   │   │   │   │       └── simpanan/
│   │   │   │   │           ├── GetSaldoSimpananUseCase.kt
│   │   │   │   │           ├── SetoranSimpananUseCase.kt
│   │   │   │   │           └── PenarikanSimpananUseCase.kt
│   │   │   │   │
│   │   │   │   └── presentation/                 ← PRESENTATION LAYER
│   │   │   │       ├── navigation/
│   │   │   │       │   ├── AppNavGraph.kt        ← Navigasi utama (NavHost)
│   │   │   │       │   ├── Screen.kt             ← Sealed class semua route
│   │   │   │       │   └── BottomNavBar.kt       ← Bottom navigation component
│   │   │   │       │
│   │   │   │       ├── ui/
│   │   │   │       │   ├── theme/
│   │   │   │       │   │   ├── Color.kt          ← Palet warna KoperasiKu
│   │   │   │       │   │   ├── Typography.kt     ← Font scales
│   │   │   │       │   │   ├── Shape.kt          ← Corner radius, dll
│   │   │   │       │   │   └── Theme.kt          ← MaterialTheme wrapper (light/dark)
│   │   │   │       │   │
│   │   │   │       │   └── components/           ← Reusable Compose components
│   │   │   │       │       ├── AppTopBar.kt
│   │   │   │       │       ├── AppButton.kt      ← Primary, Secondary, Danger variants
│   │   │   │       │       ├── AppTextField.kt   ← Input dengan validasi
│   │   │   │       │       ├── AppCard.kt
│   │   │   │       │       ├── AppDialog.kt      ← Konfirmasi, error, loading
│   │   │   │       │       ├── AppBadge.kt       ← Status badge (warna per status)
│   │   │   │       │       ├── LoadingOverlay.kt
│   │   │   │       │       ├── EmptyState.kt     ← Tampilan jika data kosong
│   │   │   │       │       ├── ErrorState.kt     ← Tampilan jika error
│   │   │   │       │       ├── CurrencyText.kt   ← Text format Rupiah
│   │   │   │       │       ├── SearchBar.kt
│   │   │   │       │       └── ChartComponents.kt ← Bar/Line chart wrappers
│   │   │   │       │
│   │   │   │       ├── auth/
│   │   │   │       │   ├── LoginScreen.kt
│   │   │   │       │   ├── LoginViewModel.kt
│   │   │   │       │   ├── ForgotPasswordScreen.kt
│   │   │   │       │   └── ForgotPasswordViewModel.kt
│   │   │   │       │
│   │   │   │       ├── dashboard/
│   │   │   │       │   ├── DashboardScreen.kt    ← Berbeda tampilan per role
│   │   │   │       │   ├── DashboardViewModel.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── SummaryCard.kt    ← Kartu ringkasan (kas, stok, dll)
│   │   │   │       │       ├── QuickActionGrid.kt
│   │   │   │       │       └── RecentActivityList.kt
│   │   │   │       │
│   │   │   │       ├── anggota/
│   │   │   │       │   ├── AnggotaListScreen.kt
│   │   │   │       │   ├── AnggotaDetailScreen.kt
│   │   │   │       │   ├── AnggotaFormScreen.kt  ← Create & Edit (shared screen)
│   │   │   │       │   ├── AnggotaViewModel.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── AnggotaCard.kt
│   │   │   │       │       └── AnggotaRiwayatList.kt
│   │   │   │       │
│   │   │   │       ├── stok/
│   │   │   │       │   ├── StokListScreen.kt
│   │   │   │       │   ├── StokDetailScreen.kt
│   │   │   │       │   ├── ProdukFormScreen.kt
│   │   │   │       │   ├── StokMasukScreen.kt    ← Input pembelian dari supplier
│   │   │   │       │   ├── StokOpnameScreen.kt
│   │   │   │       │   ├── StokViewModel.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── ProdukCard.kt
│   │   │   │       │       ├── StokBadge.kt      ← Indikator stok (aman/menipis/habis)
│   │   │   │       │       └── MutasiStokList.kt
│   │   │   │       │
│   │   │   │       ├── pos/
│   │   │   │       │   ├── PosScreen.kt          ← Layar kasir utama
│   │   │   │       │   ├── PosViewModel.kt
│   │   │   │       │   ├── StrukScreen.kt        ← Preview & share struk
│   │   │   │       │   ├── RiwayatTransaksiScreen.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── ProductSearchPanel.kt
│   │   │   │       │       ├── KeranjangPanel.kt
│   │   │   │       │       ├── KeranjangItem.kt
│   │   │   │       │       ├── PaymentBottomSheet.kt
│   │   │   │       │       └── StrukContent.kt
│   │   │   │       │
│   │   │   │       ├── keuangan/
│   │   │   │       │   ├── KeuanganScreen.kt     ← Tab: Kas Masuk | Kas Keluar | Saldo
│   │   │   │       │   ├── KeuanganViewModel.kt
│   │   │   │       │   ├── CatatKasScreen.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── KasItem.kt
│   │   │   │       │       ├── SaldoCard.kt
│   │   │   │       │       └── KasFilterBar.kt
│   │   │   │       │
│   │   │   │       ├── simpanpinjam/
│   │   │   │       │   ├── SimpanPinjamScreen.kt ← Tab: Simpanan | Pinjaman
│   │   │   │       │   ├── SimpananScreen.kt
│   │   │   │       │   ├── SetoranScreen.kt
│   │   │   │       │   ├── PinjamanListScreen.kt
│   │   │   │       │   ├── PinjamanDetailScreen.kt
│   │   │   │       │   ├── AjukanPinjamanScreen.kt
│   │   │   │       │   ├── ApprovalPinjamanScreen.kt ← Ketua only
│   │   │   │       │   ├── BayarAngsuranScreen.kt
│   │   │   │       │   ├── SimpanPinjamViewModel.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── SimpananCard.kt
│   │   │   │       │       ├── PinjamanCard.kt
│   │   │   │       │       ├── AngsuranList.kt
│   │   │   │       │       ├── StatusPinjamanBadge.kt
│   │   │   │       │       └── KalkulasiCicilanCard.kt
│   │   │   │       │
│   │   │   │       ├── laporan/
│   │   │   │       │   ├── LaporanScreen.kt      ← Tab: Penjualan | Keuangan | Stok | Simpinjam
│   │   │   │       │   ├── LaporanViewModel.kt
│   │   │   │       │   └── components/
│   │   │   │       │       ├── LaporanPenjualanTab.kt
│   │   │   │       │       ├── LaporanKeuanganTab.kt
│   │   │   │       │       ├── LaporanStokTab.kt
│   │   │   │       │       ├── LaporanSimpanPinjamTab.kt
│   │   │   │       │       ├── PeriodFilterBar.kt ← Harian/Mingguan/Bulanan/Custom
│   │   │   │       │       └── ExportFabButton.kt
│   │   │   │       │
│   │   │   │       └── profil/
│   │   │   │           ├── ProfilScreen.kt
│   │   │   │           └── ProfilViewModel.kt
│   │   │   │
│   │   │   └── res/
│   │   │       ├── drawable/                     ← Icon, logo, ilustrasi
│   │   │       ├── font/                         ← Custom fonts (jika ada)
│   │   │       ├── mipmap-*/                     ← App icon (berbagai ukuran)
│   │   │       ├── values/
│   │   │       │   ├── strings.xml               ← Semua teks UI (Bahasa Indonesia)
│   │   │       │   ├── colors.xml                ← Resource colors
│   │   │       │   └── dimens.xml                ← Spacing & size constants
│   │   │       └── xml/
│   │   │           ├── network_security_config.xml
│   │   │           └── backup_rules.xml
│   │   │
│   │   └── test/                                 ← Unit tests
│   │       └── java/com/koperasiku/app/
│   │           ├── usecase/
│   │           │   ├── HitungAngsuranUseCaseTest.kt
│   │           │   ├── CreateTransaksiUseCaseTest.kt
│   │           │   └── BayarAngsuranUseCaseTest.kt
│   │           └── repository/
│   │               └── PinjamanRepositoryTest.kt
│   │
│   ├── build.gradle.kts                          ← App-level dependencies
│   └── proguard-rules.pro
│
├── build.gradle.kts                              ← Project-level
├── settings.gradle.kts
├── gradle.properties
└── local.properties                              ← JANGAN di-commit (berisi API keys)
```

---

## 3. Navigation Graph

Aplikasi menggunakan **single-activity pattern** dengan Jetpack Compose Navigation. Semua route didefinisikan dalam `Screen.kt`.

```kotlin
// Screen.kt
sealed class Screen(val route: String) {
    // Auth
    object Login           : Screen("login")
    object ForgotPassword  : Screen("forgot_password")

    // Main (setelah login)
    object Dashboard       : Screen("dashboard")
    object Anggota         : Screen("anggota")
    object AnggotaDetail   : Screen("anggota/{anggotaId}") {
        fun createRoute(id: String) = "anggota/$id"
    }
    object AnggotaForm     : Screen("anggota/form?id={id}") // null id = create
    object Stok            : Screen("stok")
    object StokDetail      : Screen("stok/{produkId}")
    object ProdukForm      : Screen("produk/form?id={id}")
    object StokMasuk       : Screen("stok/masuk/{produkId}")
    object StokOpname      : Screen("stok/opname")
    object Pos             : Screen("pos")
    object Struk           : Screen("pos/struk/{transaksiId}")
    object RiwayatTransaksi: Screen("pos/riwayat")
    object Keuangan        : Screen("keuangan")
    object CatatKas        : Screen("keuangan/catat")
    object SimpanPinjam    : Screen("simpanpinjam")
    object Setoran         : Screen("simpanpinjam/setoran/{anggotaId}")
    object PinjamanDetail  : Screen("pinjaman/{pinjamanId}")
    object AjukanPinjaman  : Screen("pinjaman/ajukan")
    object ApprovalPinjaman: Screen("pinjaman/approval/{pinjamanId}")
    object BayarAngsuran   : Screen("pinjaman/bayar/{pinjamanId}")
    object Laporan         : Screen("laporan")
    object Profil          : Screen("profil")
}
```

### Bottom Navigation (Role-Based)

```
KETUA:    Dashboard | Laporan | Pinjaman | Profil
ADMIN:    Dashboard | Stok | Kasir | Keuangan | Anggota | Profil  
ANGGOTA:  Dashboard | Simpanan | Pinjaman | Profil
```

---

## 4. State Management Pattern

Setiap ViewModel menggunakan `UiState` sealed class + `StateFlow`:

```kotlin
// Contoh pattern standar di semua ViewModel
data class AnggotaUiState(
    val isLoading: Boolean = false,
    val anggotaList: List<Anggota> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)

sealed class AnggotaEvent {
    data class ShowSnackbar(val message: String) : AnggotaEvent()
    object NavigateBack : AnggotaEvent()
}

@HiltViewModel
class AnggotaViewModel @Inject constructor(
    private val getAnggotaListUseCase: GetAnggotaListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnggotaUiState())
    val uiState: StateFlow<AnggotaUiState> = _uiState.asStateFlow()

    private val _events = Channel<AnggotaEvent>()
    val events = _events.receiveAsFlow()

    fun loadAnggota() { /* ... */ }
    fun onSearchChanged(query: String) { /* ... */ }
}
```

---

## 5. Offline-First Strategy

Menggunakan pola **"Single Source of Truth"** — Room sebagai sumber data utama, Supabase sebagai remote backup & sinkronisasi.

```
┌──────────────────────────────────────────────┐
│                  Repository Impl              │
│                                              │
│  fun getAnggotaList(): Flow<List<Anggota>> { │
│    return localDao.getAll()                  │  ← UI selalu dari Room (lokal)
│      .onStart { syncFromRemote() }           │  ← Sync dari Supabase di background
│  }                                           │
│                                              │
│  private suspend fun syncFromRemote() {      │
│    if (networkMonitor.isOnline()) {          │
│      val remote = remoteSource.fetchAll()    │
│      localDao.upsertAll(remote)              │  ← Update Room dengan data baru
│    }                                         │
│  }                                           │
└──────────────────────────────────────────────┘
```

### Transaksi Offline (POS Mode)
```
Kasir input transaksi (offline)
    ↓
Simpan ke Room dengan flag: is_synced = false
    ↓
WorkManager schedule sync job
    ↓
Saat online → kirim ke Supabase → update is_synced = true
    ↓
Konflik terdeteksi → strategi: remote wins (kecuali untuk transaksi lokal baru)
```

---

## 6. Dependency Injection (Hilt)

```kotlin
// AppModule.kt — contoh struktur
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideSupabaseClient(): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }

    @Provides @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)
}

// DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "koperasiku_db"
    ).build()

    @Provides fun provideAnggotaDao(db: AppDatabase) = db.anggotaDao()
    @Provides fun provideProdukDao(db: AppDatabase) = db.produkDao()
    // ... dst
}

// RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindAnggotaRepo(
        impl: AnggotaRepositoryImpl
    ): AnggotaRepository
    // ... dst
}
```

---

## 7. Domain Models Utama

```kotlin
// Anggota.kt
data class Anggota(
    val id: String,
    val nomorAnggota: String,
    val nama: String,
    val nik: String,
    val alamat: String,
    val noHp: String,
    val fotoUrl: String?,
    val isAktif: Boolean,
    val tanggalGabung: LocalDate
)

// Produk.kt
data class Produk(
    val id: String,
    val kode: String,
    val nama: String,
    val kategori: String,
    val hargaBeli: Long,
    val hargaJual: Long,
    val satuan: String,
    val stokSaat ini: Int,
    val minimumStok: Int,
    val fotoUrl: String?,
    val isAktif: Boolean
) {
    val isStokMenipis: Boolean get() = stokSaatIni <= minimumStok
    val isStokHabis: Boolean get() = stokSaatIni == 0
}

// Pinjaman.kt
data class Pinjaman(
    val id: String,
    val anggotaId: String,
    val jumlah: Long,
    val bungaPersen: Double,
    val tenorBulan: Int,
    val status: StatusPinjaman,
    val tanggalPengajuan: LocalDate,
    val tanggalDisetujui: LocalDate?,
    val catatanApproval: String?,
    val angsuranList: List<Angsuran>
) {
    val sisaPokok: Long get() = angsuranList
        .filter { !it.isBayar }
        .sumOf { it.pokok }
}

// Transaksi.kt (POS)
data class Transaksi(
    val id: String,
    val kasirId: String,
    val items: List<TransaksiItem>,
    val subtotal: Long,
    val diskon: Long,
    val total: Long,
    val metodeBayar: MetodeBayar,
    val bayar: Long,
    val kembalian: Long,
    val tanggal: LocalDateTime,
    val isSynced: Boolean
)
```

---

## 8. Error Handling Strategy

Menggunakan `Result` wrapper di semua use case:

```kotlin
// Resource.kt (di core/utils)
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

// Contoh penggunaan di UseCase
class GetAnggotaListUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    operator fun invoke(): Flow<Resource<List<Anggota>>> = flow {
        emit(Resource.Loading)
        try {
            repository.getAnggotaList().collect { list ->
                emit(Resource.Success(list))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        }
    }
}
```

---

## 9. Naming Conventions

| Elemen | Konvensi | Contoh |
|--------|----------|--------|
| File Kotlin | PascalCase | `AnggotaViewModel.kt` |
| Composable | PascalCase + suffix Screen/Component | `AnggotaListScreen`, `AppButton` |
| ViewModel | PascalCase + ViewModel | `PosViewModel` |
| UseCase | PascalCase + UseCase | `AjukanPinjamanUseCase` |
| Repository Interface | PascalCase + Repository | `PinjamanRepository` |
| Repository Impl | PascalCase + RepositoryImpl | `PinjamanRepositoryImpl` |
| DTO | PascalCase + Dto | `PinjamanDto` |
| Entity (Room) | PascalCase + Entity | `PinjamanEntity` |
| Variabel / fungsi | camelCase | `saldoKas`, `loadAnggota()` |
| Konstanta | SCREAMING_SNAKE_CASE | `MAX_TENOR_BULAN` |
| Supabase table | snake_case | `tbl_anggota`, `tbl_pinjaman` |

---

## 10. Konfigurasi Build

```kotlin
// app/build.gradle.kts (ringkasan)
android {
    compileSdk = 35
    defaultConfig {
        applicationId = "com.koperasiku.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Supabase credentials dari local.properties (JANGAN hardcode!)
        buildConfigField("String", "SUPABASE_URL", 
            "\"${project.findProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", 
            "\"${project.findProperty("SUPABASE_ANON_KEY")}\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
```

---

*Dokumen ini adalah panduan struktural untuk developer. Setiap penambahan modul atau screen baru harus mengikuti pola yang sudah ditetapkan di sini.*