# TECH.md — Spesifikasi Teknis Kopkust

**Versi:** 1.0.0  
**Platform:** Android (Kotlin + Jetpack Compose)  
**Backend:** Supabase (PostgreSQL + Auth + Storage + Realtime)  
**Tanggal:** Juni 2026  

---

## 1. Technology Stack Overview

```
┌─────────────────────────────────────────────────────────────────┐
│  ANDROID APP (Kotlin)                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐ │
│  │  Jetpack     │  │   Hilt DI    │  │  Jetpack Navigation   │ │
│  │  Compose UI  │  │  (Injection) │  │  (Single Activity)    │ │
│  └──────────────┘  └──────────────┘  └───────────────────────┘ │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐ │
│  │  Room DB     │  │  WorkManager │  │  ML Kit (Barcode)     │ │
│  │  (Offline)   │  │  (Sync Jobs) │  │  (Scanner POS)        │ │
│  └──────────────┘  └──────────────┘  └───────────────────────┘ │
└───────────────────────────┬─────────────────────────────────────┘
                            │ Supabase Kotlin SDK
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  SUPABASE (Backend-as-a-Service)                                │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────────┐ │
│  │  PostgreSQL  │  │  Supabase    │  │  Supabase Storage     │ │
│  │  (Database)  │  │  Auth (JWT)  │  │  (Foto produk/KTP)    │ │
│  └──────────────┘  └──────────────┘  └───────────────────────┘ │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │  Row Level   │  │  Realtime    │                            │
│  │  Security    │  │  (Notifikasi)│                            │
│  └──────────────┘  └──────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Library & Dependencies Lengkap

### 2.1 `build.gradle.kts` — Project Level

```kotlin
// build.gradle.kts (project level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

### 2.2 `libs.versions.toml` — Version Catalog

```toml
[versions]
# Core
agp = "8.5.0"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.25"
coreKtx = "1.13.1"

# Compose
composeBom = "2024.09.00"
activityCompose = "1.9.2"
navigationCompose = "2.8.2"
lifecycleRuntimeKtx = "2.8.5"

# Dependency Injection
hilt = "2.52"
hiltNavigationCompose = "1.2.0"

# Supabase
supabase = "3.0.0"
ktor = "3.0.0"

# Database (Room)
room = "2.6.1"

# Background Work
workManager = "2.9.1"
hiltWork = "1.2.0"

# Image Loading
coil = "2.7.0"

# Charts
vico = "2.0.0-alpha.28"

# PDF
itextpdf = "5.5.13.3"

# Barcode Scanner
mlkitBarcode = "17.3.0"

# Serialization
kotlinxSerialization = "1.7.3"

# Coroutines
coroutines = "1.9.0"

# Date/Time
kotlinxDatetime = "0.6.1"

# Testing
junit = "4.13.2"
junitExt = "1.2.1"
espresso = "3.6.1"
mockk = "1.13.12"
coroutinesTest = "1.9.0"

[libraries]
# AndroidX Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

# Compose BOM
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
hilt-work = { group = "androidx.hilt", name = "hilt-work", version.ref = "hiltWork" }
hilt-work-compiler = { group = "androidx.hilt", name = "hilt-compiler", version.ref = "hiltWork" }

# Supabase
supabase-bom = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabase" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt" }
supabase-auth = { group = "io.github.jan-tennert.supabase", name = "auth-kt" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# WorkManager
work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }

# Coil (image loading)
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

# Vico (Charts)
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }

# iText PDF
itext-pdf = { group = "com.itextpdf", name = "itextpdf", version.ref = "itextpdf" }

# ML Kit Barcode
mlkit-barcode = { group = "com.google.mlkit", name = "barcode-scanning", version.ref = "mlkitBarcode" }

# CameraX (untuk barcode scanner)
camerax-core = { group = "androidx.camera", name = "camera-core", version = "1.3.4" }
camerax-camera2 = { group = "androidx.camera", name = "camera-camera2", version = "1.3.4" }
camerax-lifecycle = { group = "androidx.camera", name = "camera-lifecycle", version = "1.3.4" }
camerax-view = { group = "androidx.camera", name = "camera-view", version = "1.3.4" }

# Serialization
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# Date/Time
kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlinxDatetime" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitExt" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutinesTest" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

### 2.3 `build.gradle.kts` — App Level

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.Kopkust.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.Kopkust.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Ambil dari local.properties — JANGAN hardcode!
        val supabaseUrl = project.findProperty("SUPABASE_URL") as String? ?: ""
        val supabaseKey = project.findProperty("SUPABASE_ANON_KEY") as String? ?: ""
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"  // diperlukan untuk iTextPDF
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Image
    implementation(libs.coil.compose)

    // Charts
    implementation(libs.vico.compose.m3)

    // PDF
    implementation(libs.itext.pdf)

    // Barcode + Camera
    implementation(libs.mlkit.barcode)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // DateTime
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

---

## 3. Supabase Database Schema (PostgreSQL)

### 3.1 Setup Awal — Eksekusi di Supabase SQL Editor

```sql
-- ============================================================
-- Kopkust DATABASE SCHEMA
-- Eksekusi di Supabase SQL Editor (urut dari atas ke bawah)
-- ============================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE user_role AS ENUM ('KETUA', 'ADMIN', 'ANGGOTA');
CREATE TYPE status_pinjaman AS ENUM ('DIAJUKAN', 'DISETUJUI', 'AKTIF', 'LUNAS', 'DITOLAK');
CREATE TYPE metode_bayar AS ENUM ('TUNAI', 'TRANSFER', 'QRIS');
CREATE TYPE jenis_kas AS ENUM ('MASUK', 'KELUAR');
CREATE TYPE jenis_simpanan AS ENUM ('POKOK', 'WAJIB', 'SUKARELA');
CREATE TYPE jenis_mutasi_simpanan AS ENUM ('SETORAN', 'PENARIKAN');
```

### 3.2 Tabel Pengguna & Anggota

```sql
-- ============================================================
-- PROFILES (extend Supabase Auth users)
-- ============================================================
CREATE TABLE profiles (
    id          UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    nama        TEXT NOT NULL,
    no_hp       TEXT,
    foto_url    TEXT,
    role        user_role NOT NULL DEFAULT 'ANGGOTA',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- ANGGOTA
-- ============================================================
CREATE TABLE tbl_anggota (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID REFERENCES profiles(id) ON DELETE SET NULL,
    nomor_anggota   TEXT NOT NULL UNIQUE,
    nama            TEXT NOT NULL,
    nik             TEXT NOT NULL UNIQUE,
    alamat          TEXT,
    no_hp           TEXT,
    foto_ktp_url    TEXT,
    is_aktif        BOOLEAN NOT NULL DEFAULT TRUE,
    tanggal_gabung  DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Auto-generate nomor anggota: KOP-2026-001
CREATE SEQUENCE nomor_anggota_seq START 1;
CREATE OR REPLACE FUNCTION generate_nomor_anggota()
RETURNS TEXT AS $$
BEGIN
    RETURN 'KOP-' || EXTRACT(YEAR FROM NOW())::TEXT || '-' || 
           LPAD(nextval('nomor_anggota_seq')::TEXT, 3, '0');
END;
$$ LANGUAGE plpgsql;
```

### 3.3 Tabel Produk & Stok

```sql
-- ============================================================
-- KATEGORI PRODUK
-- ============================================================
CREATE TABLE tbl_kategori_produk (
    id      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nama    TEXT NOT NULL UNIQUE
);

-- ============================================================
-- PRODUK
-- ============================================================
CREATE TABLE tbl_produk (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    kode            TEXT NOT NULL UNIQUE,
    nama            TEXT NOT NULL,
    kategori_id     UUID REFERENCES tbl_kategori_produk(id),
    harga_beli      BIGINT NOT NULL DEFAULT 0,    -- dalam Rupiah (tidak pakai desimal)
    harga_jual      BIGINT NOT NULL DEFAULT 0,
    satuan          TEXT NOT NULL DEFAULT 'pcs',
    stok_saat_ini   INTEGER NOT NULL DEFAULT 0,
    minimum_stok    INTEGER NOT NULL DEFAULT 5,
    foto_url        TEXT,
    is_aktif        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- MUTASI STOK (log semua perubahan stok)
-- ============================================================
CREATE TABLE tbl_mutasi_stok (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    produk_id       UUID NOT NULL REFERENCES tbl_produk(id),
    jenis           TEXT NOT NULL CHECK (jenis IN ('MASUK', 'KELUAR', 'OPNAME', 'RETUR')),
    jumlah          INTEGER NOT NULL,             -- positif = masuk, negatif = keluar
    stok_sebelum    INTEGER NOT NULL,
    stok_sesudah    INTEGER NOT NULL,
    referensi_id    UUID,                         -- id transaksi / stok opname yg memicu
    referensi_tipe  TEXT,                         -- 'TRANSAKSI', 'OPNAME', 'MANUAL'
    keterangan      TEXT,
    user_id         UUID REFERENCES profiles(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- STOK OPNAME
-- ============================================================
CREATE TABLE tbl_stok_opname (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tanggal         DATE NOT NULL DEFAULT CURRENT_DATE,
    petugas_id      UUID REFERENCES profiles(id),
    catatan         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE tbl_stok_opname_item (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    opname_id       UUID NOT NULL REFERENCES tbl_stok_opname(id) ON DELETE CASCADE,
    produk_id       UUID NOT NULL REFERENCES tbl_produk(id),
    stok_sistem     INTEGER NOT NULL,
    stok_fisik      INTEGER NOT NULL,
    selisih         INTEGER GENERATED ALWAYS AS (stok_fisik - stok_sistem) STORED
);
```

### 3.4 Tabel Transaksi POS

```sql
-- ============================================================
-- TRANSAKSI (POS Header)
-- ============================================================
CREATE TABLE tbl_transaksi (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nomor_transaksi TEXT NOT NULL UNIQUE,         -- TRX-20260601-001
    kasir_id        UUID REFERENCES profiles(id),
    subtotal        BIGINT NOT NULL DEFAULT 0,
    diskon          BIGINT NOT NULL DEFAULT 0,
    total           BIGINT NOT NULL DEFAULT 0,
    metode_bayar    metode_bayar NOT NULL DEFAULT 'TUNAI',
    bayar           BIGINT NOT NULL DEFAULT 0,
    kembalian       BIGINT NOT NULL DEFAULT 0,
    catatan         TEXT,
    tanggal         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- TRANSAKSI ITEM (POS Detail)
-- ============================================================
CREATE TABLE tbl_transaksi_item (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaksi_id    UUID NOT NULL REFERENCES tbl_transaksi(id) ON DELETE CASCADE,
    produk_id       UUID NOT NULL REFERENCES tbl_produk(id),
    nama_produk     TEXT NOT NULL,                -- snapshot nama saat transaksi
    harga_satuan    BIGINT NOT NULL,              -- snapshot harga saat transaksi
    jumlah          INTEGER NOT NULL,
    diskon_item     BIGINT NOT NULL DEFAULT 0,
    subtotal        BIGINT NOT NULL
);

-- Auto-generate nomor transaksi
CREATE SEQUENCE transaksi_seq START 1;
CREATE OR REPLACE FUNCTION generate_nomor_transaksi()
RETURNS TRIGGER AS $$
BEGIN
    NEW.nomor_transaksi := 'TRX-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' ||
                           LPAD(nextval('transaksi_seq')::TEXT, 4, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_nomor_transaksi
BEFORE INSERT ON tbl_transaksi
FOR EACH ROW EXECUTE FUNCTION generate_nomor_transaksi();

-- Trigger: kurangi stok otomatis saat transaksi item dibuat
CREATE OR REPLACE FUNCTION kurangi_stok_transaksi()
RETURNS TRIGGER AS $$
DECLARE
    stok_lama INTEGER;
BEGIN
    SELECT stok_saat_ini INTO stok_lama FROM tbl_produk WHERE id = NEW.produk_id;

    UPDATE tbl_produk
    SET stok_saat_ini = stok_saat_ini - NEW.jumlah,
        updated_at = NOW()
    WHERE id = NEW.produk_id;

    INSERT INTO tbl_mutasi_stok (produk_id, jenis, jumlah, stok_sebelum, stok_sesudah,
                                  referensi_id, referensi_tipe)
    VALUES (NEW.produk_id, 'KELUAR', -NEW.jumlah, stok_lama,
            stok_lama - NEW.jumlah, NEW.transaksi_id, 'TRANSAKSI');

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_kurangi_stok
AFTER INSERT ON tbl_transaksi_item
FOR EACH ROW EXECUTE FUNCTION kurangi_stok_transaksi();
```

### 3.5 Tabel Keuangan (Kas)

```sql
-- ============================================================
-- KATEGORI KAS
-- ============================================================
CREATE TABLE tbl_kategori_kas (
    id      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nama    TEXT NOT NULL,
    jenis   jenis_kas NOT NULL
);

-- Seed data kategori kas
INSERT INTO tbl_kategori_kas (nama, jenis) VALUES
    ('Penjualan', 'MASUK'),
    ('Setoran Simpanan', 'MASUK'),
    ('Pembayaran Cicilan', 'MASUK'),
    ('Iuran Anggota', 'MASUK'),
    ('Penerimaan Lainnya', 'MASUK'),
    ('Pembelian Barang', 'KELUAR'),
    ('Biaya Operasional', 'KELUAR'),
    ('Pencairan Pinjaman', 'KELUAR'),
    ('Penarikan Simpanan', 'KELUAR'),
    ('Biaya Lainnya', 'KELUAR');

-- ============================================================
-- KAS
-- ============================================================
CREATE TABLE tbl_kas (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    jenis           jenis_kas NOT NULL,
    kategori_id     UUID REFERENCES tbl_kategori_kas(id),
    jumlah          BIGINT NOT NULL,
    keterangan      TEXT,
    referensi_id    UUID,                         -- link ke transaksi / pinjaman / simpanan
    referensi_tipe  TEXT,
    user_id         UUID REFERENCES profiles(id),
    tanggal         DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- View: Saldo kas real-time
CREATE OR REPLACE VIEW vw_saldo_kas AS
SELECT
    COALESCE(SUM(CASE WHEN jenis = 'MASUK' THEN jumlah ELSE 0 END), 0) AS total_masuk,
    COALESCE(SUM(CASE WHEN jenis = 'KELUAR' THEN jumlah ELSE 0 END), 0) AS total_keluar,
    COALESCE(SUM(CASE WHEN jenis = 'MASUK' THEN jumlah ELSE -jumlah END), 0) AS saldo
FROM tbl_kas;

-- View: Kas per bulan (untuk laporan)
CREATE OR REPLACE VIEW vw_kas_bulanan AS
SELECT
    DATE_TRUNC('month', tanggal) AS bulan,
    jenis,
    SUM(jumlah) AS total
FROM tbl_kas
GROUP BY DATE_TRUNC('month', tanggal), jenis
ORDER BY bulan DESC;
```

### 3.6 Tabel Simpan Pinjam

```sql
-- ============================================================
-- SIMPANAN
-- ============================================================
CREATE TABLE tbl_simpanan (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    anggota_id      UUID NOT NULL REFERENCES tbl_anggota(id),
    jenis           jenis_simpanan NOT NULL,
    saldo           BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (anggota_id, jenis)                    -- 1 anggota max 1 rekening per jenis
);

-- ============================================================
-- MUTASI SIMPANAN (riwayat setoran & penarikan)
-- ============================================================
CREATE TABLE tbl_mutasi_simpanan (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    simpanan_id     UUID NOT NULL REFERENCES tbl_simpanan(id),
    anggota_id      UUID NOT NULL REFERENCES tbl_anggota(id),
    jenis_mutasi    jenis_mutasi_simpanan NOT NULL,
    jumlah          BIGINT NOT NULL,
    saldo_sebelum   BIGINT NOT NULL,
    saldo_sesudah   BIGINT NOT NULL,
    keterangan      TEXT,
    user_id         UUID REFERENCES profiles(id), -- petugas yang mencatat
    tanggal         DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Trigger: update saldo simpanan otomatis
CREATE OR REPLACE FUNCTION update_saldo_simpanan()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.jenis_mutasi = 'SETORAN' THEN
        UPDATE tbl_simpanan SET saldo = saldo + NEW.jumlah, updated_at = NOW()
        WHERE id = NEW.simpanan_id;
    ELSE
        UPDATE tbl_simpanan SET saldo = saldo - NEW.jumlah, updated_at = NOW()
        WHERE id = NEW.simpanan_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_saldo_simpanan
AFTER INSERT ON tbl_mutasi_simpanan
FOR EACH ROW EXECUTE FUNCTION update_saldo_simpanan();

-- ============================================================
-- PINJAMAN
-- ============================================================
CREATE TABLE tbl_pinjaman (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nomor_pinjaman      TEXT NOT NULL UNIQUE,     -- PIN-2026-001
    anggota_id          UUID NOT NULL REFERENCES tbl_anggota(id),
    jumlah              BIGINT NOT NULL,
    bunga_persen        NUMERIC(5,2) NOT NULL DEFAULT 1.5,  -- per bulan
    tenor_bulan         INTEGER NOT NULL,
    total_bunga         BIGINT NOT NULL DEFAULT 0,
    total_bayar         BIGINT NOT NULL DEFAULT 0, -- jumlah + total_bunga
    status              status_pinjaman NOT NULL DEFAULT 'DIAJUKAN',
    keperluan           TEXT,
    reviewer_id         UUID REFERENCES profiles(id),
    tanggal_pengajuan   DATE NOT NULL DEFAULT CURRENT_DATE,
    tanggal_disetujui   DATE,
    tanggal_cair        DATE,
    catatan_approval    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- ANGSURAN (jadwal cicilan)
-- ============================================================
CREATE TABLE tbl_angsuran (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    pinjaman_id     UUID NOT NULL REFERENCES tbl_pinjaman(id) ON DELETE CASCADE,
    ke              INTEGER NOT NULL,             -- angsuran ke-1, ke-2, dst
    jatuh_tempo     DATE NOT NULL,
    pokok           BIGINT NOT NULL,
    bunga           BIGINT NOT NULL,
    denda           BIGINT NOT NULL DEFAULT 0,
    total_bayar     BIGINT NOT NULL,              -- pokok + bunga + denda
    is_bayar        BOOLEAN NOT NULL DEFAULT FALSE,
    tanggal_bayar   DATE,
    user_pencatat   UUID REFERENCES profiles(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Auto-generate nomor pinjaman
CREATE SEQUENCE pinjaman_seq START 1;
CREATE OR REPLACE FUNCTION generate_nomor_pinjaman()
RETURNS TRIGGER AS $$
BEGIN
    NEW.nomor_pinjaman := 'PIN-' || EXTRACT(YEAR FROM NOW())::TEXT || '-' ||
                          LPAD(nextval('pinjaman_seq')::TEXT, 3, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_nomor_pinjaman
BEFORE INSERT ON tbl_pinjaman
FOR EACH ROW EXECUTE FUNCTION generate_nomor_pinjaman();

-- Function: hitung dan generate jadwal angsuran (flat rate)
CREATE OR REPLACE FUNCTION generate_jadwal_angsuran(p_pinjaman_id UUID)
RETURNS VOID AS $$
DECLARE
    v_pinjaman      tbl_pinjaman%ROWTYPE;
    v_pokok         BIGINT;
    v_bunga         BIGINT;
    v_jatuh_tempo   DATE;
    i               INTEGER;
BEGIN
    SELECT * INTO v_pinjaman FROM tbl_pinjaman WHERE id = p_pinjaman_id;

    v_pokok := ROUND(v_pinjaman.jumlah::NUMERIC / v_pinjaman.tenor_bulan);
    v_bunga := ROUND(v_pinjaman.jumlah::NUMERIC * (v_pinjaman.bunga_persen / 100));

    FOR i IN 1..v_pinjaman.tenor_bulan LOOP
        v_jatuh_tempo := v_pinjaman.tanggal_cair + (i * INTERVAL '1 month');

        INSERT INTO tbl_angsuran (pinjaman_id, ke, jatuh_tempo, pokok, bunga, total_bayar)
        VALUES (p_pinjaman_id, i, v_jatuh_tempo, v_pokok, v_bunga, v_pokok + v_bunga);
    END LOOP;
END;
$$ LANGUAGE plpgsql;
```

### 3.7 Row Level Security (RLS)

```sql
-- ============================================================
-- ROW LEVEL SECURITY — Aktifkan semua tabel
-- ============================================================

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_anggota ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_produk ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_transaksi ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_kas ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_simpanan ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_pinjaman ENABLE ROW LEVEL SECURITY;
ALTER TABLE tbl_angsuran ENABLE ROW LEVEL SECURITY;

-- Helper: cek role user yang sedang login
CREATE OR REPLACE FUNCTION current_user_role()
RETURNS user_role AS $$
    SELECT role FROM profiles WHERE id = auth.uid()
$$ LANGUAGE sql STABLE SECURITY DEFINER;

-- Helper: cek apakah user adalah KETUA atau ADMIN
CREATE OR REPLACE FUNCTION is_admin_or_ketua()
RETURNS BOOLEAN AS $$
    SELECT current_user_role() IN ('KETUA', 'ADMIN')
$$ LANGUAGE sql STABLE;

-- ─── Profiles ───
CREATE POLICY "User bisa lihat profil sendiri"
    ON profiles FOR SELECT USING (id = auth.uid());
CREATE POLICY "Admin bisa lihat semua profil"
    ON profiles FOR SELECT USING (is_admin_or_ketua());
CREATE POLICY "User bisa update profil sendiri"
    ON profiles FOR UPDATE USING (id = auth.uid());

-- ─── Anggota ───
CREATE POLICY "Admin & Ketua bisa kelola anggota"
    ON tbl_anggota FOR ALL USING (is_admin_or_ketua());
CREATE POLICY "Anggota bisa lihat data diri sendiri"
    ON tbl_anggota FOR SELECT USING (user_id = auth.uid());

-- ─── Produk ───
CREATE POLICY "Semua user bisa lihat produk aktif"
    ON tbl_produk FOR SELECT USING (is_aktif = TRUE);
CREATE POLICY "Admin bisa kelola produk"
    ON tbl_produk FOR ALL USING (is_admin_or_ketua());

-- ─── Transaksi ───
CREATE POLICY "Admin bisa kelola transaksi"
    ON tbl_transaksi FOR ALL USING (is_admin_or_ketua());

-- ─── Kas ───
CREATE POLICY "Admin & Ketua bisa kelola kas"
    ON tbl_kas FOR ALL USING (is_admin_or_ketua());

-- ─── Simpanan ───
CREATE POLICY "Admin & Ketua bisa kelola simpanan"
    ON tbl_simpanan FOR ALL USING (is_admin_or_ketua());
CREATE POLICY "Anggota bisa lihat simpanan sendiri"
    ON tbl_simpanan FOR SELECT
    USING (anggota_id IN (SELECT id FROM tbl_anggota WHERE user_id = auth.uid()));

-- ─── Pinjaman ───
CREATE POLICY "Admin & Ketua bisa kelola pinjaman"
    ON tbl_pinjaman FOR ALL USING (is_admin_or_ketua());
CREATE POLICY "Anggota bisa lihat pinjaman sendiri"
    ON tbl_pinjaman FOR SELECT
    USING (anggota_id IN (SELECT id FROM tbl_anggota WHERE user_id = auth.uid()));
CREATE POLICY "Anggota bisa mengajukan pinjaman"
    ON tbl_pinjaman FOR INSERT
    WITH CHECK (anggota_id IN (SELECT id FROM tbl_anggota WHERE user_id = auth.uid()));

-- ─── Angsuran ───
CREATE POLICY "Admin & Ketua bisa kelola angsuran"
    ON tbl_angsuran FOR ALL USING (is_admin_or_ketua());
CREATE POLICY "Anggota bisa lihat angsuran sendiri"
    ON tbl_angsuran FOR SELECT
    USING (pinjaman_id IN (
        SELECT id FROM tbl_pinjaman WHERE anggota_id IN (
            SELECT id FROM tbl_anggota WHERE user_id = auth.uid()
        )
    ));
```

### 3.8 Indexes untuk Performa

```sql
-- ============================================================
-- INDEXES
-- ============================================================

-- Produk
CREATE INDEX idx_produk_nama ON tbl_produk USING gin(to_tsvector('simple', nama));
CREATE INDEX idx_produk_kode ON tbl_produk (kode);
CREATE INDEX idx_produk_stok ON tbl_produk (stok_saat_ini) WHERE is_aktif = TRUE;

-- Transaksi
CREATE INDEX idx_transaksi_tanggal ON tbl_transaksi (tanggal DESC);
CREATE INDEX idx_transaksi_kasir ON tbl_transaksi (kasir_id);

-- Kas
CREATE INDEX idx_kas_tanggal ON tbl_kas (tanggal DESC);
CREATE INDEX idx_kas_jenis ON tbl_kas (jenis);

-- Pinjaman
CREATE INDEX idx_pinjaman_anggota ON tbl_pinjaman (anggota_id);
CREATE INDEX idx_pinjaman_status ON tbl_pinjaman (status);

-- Angsuran
CREATE INDEX idx_angsuran_pinjaman ON tbl_angsuran (pinjaman_id);
CREATE INDEX idx_angsuran_jatuh_tempo ON tbl_angsuran (jatuh_tempo) WHERE is_bayar = FALSE;

-- Simpanan
CREATE INDEX idx_simpanan_anggota ON tbl_simpanan (anggota_id);

-- Mutasi Stok
CREATE INDEX idx_mutasi_stok_produk ON tbl_mutasi_stok (produk_id, created_at DESC);
```

---

## 4. Supabase Storage Buckets

```sql
-- Buat di Supabase Dashboard > Storage, atau via SQL:

-- Bucket untuk foto produk (public)
INSERT INTO storage.buckets (id, name, public)
VALUES ('foto-produk', 'foto-produk', TRUE);

-- Bucket untuk foto KTP anggota (private)
INSERT INTO storage.buckets (id, name, public)
VALUES ('foto-ktp', 'foto-ktp', FALSE);

-- Bucket untuk laporan PDF (private)
INSERT INTO storage.buckets (id, name, public)
VALUES ('laporan', 'laporan', FALSE);
```

**Storage RLS Policies:**
```sql
-- Foto produk: semua user authenticated bisa lihat, admin bisa upload
CREATE POLICY "Public can view foto produk"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'foto-produk');

CREATE POLICY "Admin bisa upload foto produk"
    ON storage.objects FOR INSERT
    WITH CHECK (bucket_id = 'foto-produk' AND is_admin_or_ketua());

-- Foto KTP: hanya admin dan anggota bersangkutan
CREATE POLICY "Admin bisa akses semua foto KTP"
    ON storage.objects FOR ALL
    USING (bucket_id = 'foto-ktp' AND is_admin_or_ketua());
```

---

## 5. Kalkulasi Keuangan

### 5.1 Hitung Angsuran (Flat Rate)

```kotlin
// HitungAngsuranUseCase.kt
data class HasilHitungAngsuran(
    val angsuranPerBulan: Long,
    val pokokPerBulan: Long,
    val bungaPerBulan: Long,
    val totalBunga: Long,
    val totalBayar: Long,
    val jadwal: List<JadwalAngsuran>
)

data class JadwalAngsuran(
    val ke: Int,
    val jatuhTempo: LocalDate,
    val pokok: Long,
    val bunga: Long,
    val total: Long
)

class HitungAngsuranUseCase @Inject constructor() {
    operator fun invoke(
        jumlahPinjaman: Long,
        bungaPersenPerBulan: Double,
        tenorBulan: Int,
        tanggalMulai: LocalDate = LocalDate.today()
    ): HasilHitungAngsuran {

        val pokokPerBulan = jumlahPinjaman / tenorBulan
        val bungaPerBulan = (jumlahPinjaman * bungaPersenPerBulan / 100).toLong()
        val angsuranPerBulan = pokokPerBulan + bungaPerBulan
        val totalBunga = bungaPerBulan * tenorBulan
        val totalBayar = jumlahPinjaman + totalBunga

        val jadwal = (1..tenorBulan).map { ke ->
            JadwalAngsuran(
                ke = ke,
                jatuhTempo = tanggalMulai.plus(ke, DateTimeUnit.MONTH),
                pokok = pokokPerBulan,
                bunga = bungaPerBulan,
                total = angsuranPerBulan
            )
        }

        return HasilHitungAngsuran(
            angsuranPerBulan = angsuranPerBulan,
            pokokPerBulan = pokokPerBulan,
            bungaPerBulan = bungaPerBulan,
            totalBunga = totalBunga,
            totalBayar = totalBayar,
            jadwal = jadwal
        )
    }
}
```

### 5.2 Hitung Denda Keterlambatan

```kotlin
// Denda default: 0.5% per hari dari pokok angsuran
fun hitungDenda(
    pokokAngsuran: Long,
    jatuhTempo: LocalDate,
    tanggalBayar: LocalDate,
    dendaPersenPerHari: Double = 0.5
): Long {
    val hariTerlambat = jatuhTempo.daysUntil(tanggalBayar)
    if (hariTerlambat <= 0) return 0L
    return (pokokAngsuran * (dendaPersenPerHari / 100) * hariTerlambat).toLong()
}
```

---

## 6. Format Utility

```kotlin
// CurrencyExtensions.kt
fun Long.toRupiah(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(this).replace("Rp", "Rp ").replace(",00", "")
}

// Contoh: 150000L.toRupiah() → "Rp 150.000"

// DateExtensions.kt
fun LocalDate.toIndonesian(): String {
    val namaBulan = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    return "${this.dayOfMonth} ${namaBulan[this.monthNumber - 1]} ${this.year}"
}

// Contoh: LocalDate(2026, 6, 1).toIndonesian() → "1 Juni 2026"
```

---

## 7. WorkManager — Sync Jobs

```kotlin
// SyncWorker.kt — background sync saat online kembali
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val transaksiRepository: TransaksiRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            transaksiRepository.syncPendingTransactions()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "Kopkust_sync"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}
```

---

## 8. Supabase Realtime — Notifikasi Pinjaman

```kotlin
// RealtimeNotificationService.kt
class RealtimeNotificationService @Inject constructor(
    private val supabase: SupabaseClient,
    private val sessionManager: SessionManager
) {
    suspend fun listenPinjamanUpdates(onUpdate: (PinjamanDto) -> Unit) {
        val anggotaId = sessionManager.currentAnggotaId ?: return

        supabase.realtime.createChannel("pinjaman-$anggotaId")
            .postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                table = "tbl_pinjaman"
                filter = "anggota_id=eq.$anggotaId"
            }
            .onEach { change ->
                val pinjaman = change.decodeRecord<PinjamanDto>()
                onUpdate(pinjaman)
            }
            .launchIn(CoroutineScope(Dispatchers.IO))

        supabase.realtime.connect()
    }
}
```

---

## 9. Konfigurasi Keamanan

### `local.properties` (JANGAN commit ke Git)
```properties
SUPABASE_URL=https://xxxxxxxxxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### `.gitignore` — tambahkan entri ini
```
local.properties
*.jks
*.keystore
google-services.json
```

### `network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">supabase.co</domain>
    </domain-config>
</network-security-config>
```

---

## 10. Ringkasan Keputusan Teknis

| Keputusan | Pilihan | Alasan |
|-----------|---------|--------|
| Backend | Supabase | Free tier cukup, PostgreSQL matang, Auth built-in, RLS kuat |
| Database lokal | Room (SQLite) | Offline support, tipe safe, integrasi Kotlin baik |
| UI Framework | Jetpack Compose | Modern, deklaratif, Google-recommended untuk Android baru |
| DI | Hilt | Standar Android, integrasi ViewModel & WorkManager |
| Penyimpanan uang | BIGINT (Rupiah bulat) | Hindari floating point error di kalkulasi keuangan |
| Foto produk | Supabase Storage | Gratis 1GB, CDN, integrasi mudah |
| Charts | Vico | Native Compose, ringan, Material 3 compatible |
| PDF | iTextPDF 5 | Stabil, banyak dokumentasi, bisa di Android |
| Barcode | ML Kit | On-device, cepat, tidak butuh internet |
| Sync strategy | WorkManager | Guaranteed execution, battery-friendly |
| Nomor referensi | PostgreSQL sequence + trigger | Auto-generate tanpa race condition |

---

*Dokumen ini adalah referensi teknis untuk developer. Update setiap ada perubahan dependency atau keputusan arsitektur.*