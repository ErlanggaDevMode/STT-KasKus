# Kopkust Project Guidelines

## 1. Project Specifications
- **Package Name**: `com.koperasiku.app`
- **Platform**: Android (Kotlin + Jetpack Compose)
- **Min SDK**: 26 (Android 8.0), **Target SDK**: 35
- **Backend**: Supabase (PostgreSQL + Auth + Storage + Realtime)
- **DI**: Hilt
- **Local DB**: Room (SQLite)
- **UI Language**: Bahasa Indonesia

## 2. Architectural Layer Constraints
- **Domain Layer**: Must be pure Kotlin (zero Android imports).
  - Models must be simple data classes.
  - Repositories must be interfaces only.
  - Use cases must return `Flow<Resource<T>>` or `Resource<T>`.
- **Data Layer**:
  - Room is the **Single Source of Truth** (UI observes Room, not Supabase directly).
  - Separate classes for Domain model, Room Entity, and Supabase DTO.
  - Mappers must have explicit functions: `toDomain()`, `toEntity()`, `toDto()`.
- **Presentation Layer**:
  - One ViewModel per Screen (or shared VM for tabs).
  - ViewModel must expose: `StateFlow<XxxUiState>` and `Flow<XxxEvent>`.
  - Use `collectAsStateWithLifecycle()` in composables.
  - Zero business logic in Composable files.

## 3. Strict Coding Guardrails (DO NOT VIOLATE)
- ❌ **No Hardcoded Keys**: Supabase URL and Anon Key must come from `BuildConfig`, initialized in `local.properties`.
- ❌ **No Double/Float for Money**: Always represent currency as `Long` (Rupiah cents or whole Rupiah without decimals).
- ❌ **No Hard Deletes**: Soft delete only (`is_aktif = false`) for financial data (transaksi, kas, pinjaman, simpanan).
- ❌ **No Direct Calls**: Never access Supabase directly from ViewModels or Composables.
- ✅ **Rupiah Formatting**: Always format money as `Rp X.XXX.XXX` (with dots as thousands separator).
- ✅ **Date Formatting**: Always format dates in Indonesian style: `1 Juni 2026`.
- ✅ **Touch Targets**: Minimum `48.dp` touch target size for all interactive elements.
- ✅ **Error Handling**: Friendly error messages in Bahasa Indonesia.
- ✅ **Previews**: Every Composable must have a `@Preview` with dummy data.
