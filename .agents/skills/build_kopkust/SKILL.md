---
name: build-kopkust
description: Step-by-step guideline to implement and test Kopkust Android App modules
---

# Skill: Building Kopkust App Modules

Use this skill to guide the construction of Kopkust's modules sequentially.

## Workflow Sessions

### Sesi 1: Setup Proyek & Fondasi
1. Initialize the Android project with package `com.koperasiku.app`.
2. Configure `libs.versions.toml` and `build.gradle.kts` files.
3. Define the database schemas in Room (`AppDatabase.kt`, Entities, and DAOs).
4. Implement Hilt modules (`AppModule.kt`, `DatabaseModule.kt`, `RepositoryModule.kt`).
5. Create `KoperasiKuApp.kt` and `MainActivity.kt`.
6. Setup global design tokens (Colors, Typography, Theme) and navigation routes (`Screen.kt`).

### Sesi 2: Reusable UI Components
1. Create core components in `presentation/ui/components/`:
   - `AppButton.kt`, `AppTextField.kt`, `CurrencyTextField.kt`, `AppCard.kt`.
   - `StatusBadge.kt` (for status pinjaman and stock indicator).
   - `ConfirmDialog.kt`, `EmptyState.kt`, `ErrorState.kt`, `LoadingOverlay.kt`.

### Sesi 3: Authentication Module
1. Build Auth remote sources and repositories using Supabase Auth.
2. Implement `SessionManager` via DataStore to cache roles (`KETUA`, `ADMIN`, `ANGGOTA`).
3. Build `LoginScreen`, `ForgotPasswordScreen` and their corresponding ViewModels.

### Sesi 4: Anggota Module (Members)
1. Build `AnggotaEntity`, DTO, DAO, mapper, and repository.
2. Implement offline-first strategy in repository.
3. Build screen components: `AnggotaListScreen`, `AnggotaDetailScreen`, and `AnggotaFormScreen`.

### Sesi 5: Stok & Inventori Module
1. Build product entities and mutasi stok records.
2. Build list view, detail page, add product form, stok masuk, and stok opname screens.

### Sesi 6: Point of Sale (Kasir)
1. Implement split-panel kasir flow (landscape-friendly).
2. Auto-decrement stock upon successful checkout (local Room update, sync queue to Supabase).
3. Setup `SyncWorker` using WorkManager to sync offline sales transactions.
4. Implement digital receipt generation and sharing options.

### Sesi 7: Keuangan Module (Cash Log)
1. Implement cash inflow and outflow tracking.
2. Automate cash entry for POS checkouts under "Penjualan".
3. Build cash statistics dashboard using charts (Vico).

### Sesi 8: Simpan Pinjam Module
1. Implement savings calculation (Pokok, Wajib, Sukarela) and savings ledger.
2. Implement Flat Rate loan installment calculations and automatic schedule creation.
3. Add approval dashboard for Ketua.

### Sesi 9: Laporan & Dashboard
1. Implement role-based landing views.
2. Generate PDF summary reports using iTextPDF and trigger standard sharing sheets.

### Sesi 10: Finalization & Polish
1. Realtime notification updates (Supabase Realtime).
2. Network connectivity banners (online/offline indicator).
3. Obfuscation setup (ProGuard rules).

### Sesi 11: Business Logic Testing
1. Unit tests for:
   - `HitungAngsuranUseCaseTest`
   - `BayarAngsuranUseCaseTest`
   - `CreateTransaksiUseCaseTest`
   - `KasRepositoryTest`
