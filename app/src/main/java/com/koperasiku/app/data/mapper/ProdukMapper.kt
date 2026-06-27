package com.koperasiku.app.data.mapper

import com.koperasiku.app.data.local.entity.ProdukEntity
import com.koperasiku.app.data.local.entity.MutasiStokEntity
import com.koperasiku.app.data.remote.dto.ProdukDto
import com.koperasiku.app.data.remote.dto.MutasiStokDto
import com.koperasiku.app.domain.model.Produk
import com.koperasiku.app.domain.model.MutasiStok

fun ProdukDto.toDomain() = Produk(
    id = id,
    kode = kode,
    nama = nama,
    kategoriId = kategoriId,
    hargaBeli = hargaBeli,
    hargaJual = hargaJual,
    satuan = satuan,
    stokSaatIni = stokSaatIni,
    minimumStok = minimumStok,
    fotoUrl = fotoUrl,
    isAktif = isAktif
)

fun ProdukEntity.toDomain() = Produk(
    id = id,
    kode = kode,
    nama = nama,
    kategoriId = kategoriId,
    hargaBeli = hargaBeli,
    hargaJual = hargaJual,
    satuan = satuan,
    stokSaatIni = stokSaatIni,
    minimumStok = minimumStok,
    fotoUrl = fotoUrl,
    isAktif = isAktif
)

fun Produk.toEntity(isSynced: Boolean = true) = ProdukEntity(
    id = id,
    kode = kode,
    nama = nama,
    kategoriId = kategoriId,
    hargaBeli = hargaBeli,
    hargaJual = hargaJual,
    satuan = satuan,
    stokSaatIni = stokSaatIni,
    minimumStok = minimumStok,
    fotoUrl = fotoUrl,
    isAktif = isAktif,
    isSynced = isSynced
)

fun Produk.toDto() = ProdukDto(
    id = id,
    kode = kode,
    nama = nama,
    kategoriId = kategoriId,
    hargaBeli = hargaBeli,
    hargaJual = hargaJual,
    satuan = satuan,
    stokSaatIni = stokSaatIni,
    minimumStok = minimumStok,
    fotoUrl = fotoUrl,
    isAktif = isAktif
)

// MutasiStok Mappers
fun MutasiStokDto.toDomain() = MutasiStok(
    id = id,
    produkId = produkId,
    jenis = jenis,
    jumlah = jumlah,
    stokSebelum = stokSebelum,
    stokSesudah = stokSesudah,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)

fun MutasiStokEntity.toDomain() = MutasiStok(
    id = id,
    produkId = produkId,
    jenis = jenis,
    jumlah = jumlah,
    stokSebelum = stokSebelum,
    stokSesudah = stokSesudah,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)

fun MutasiStok.toEntity() = MutasiStokEntity(
    id = id,
    produkId = produkId,
    jenis = jenis,
    jumlah = jumlah,
    stokSebelum = stokSebelum,
    stokSesudah = stokSesudah,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)

fun MutasiStok.toDto() = MutasiStokDto(
    id = id,
    produkId = produkId,
    jenis = jenis,
    jumlah = jumlah,
    stokSebelum = stokSebelum,
    stokSesudah = stokSesudah,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)
