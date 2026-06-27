package com.koperasiku.app.data.mapper

import com.koperasiku.app.data.local.entity.KasEntity
import com.koperasiku.app.data.remote.dto.KasDto
import com.koperasiku.app.domain.model.Kas

fun KasDto.toDomain() = Kas(
    id = id,
    jenis = jenis,
    kategoriId = kategoriId,
    jumlah = jumlah,
    keterangan = keterangan,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    userId = userId,
    tanggal = tanggal,
    createdAt = createdAt
)

fun KasEntity.toDomain() = Kas(
    id = id,
    jenis = jenis,
    kategoriId = kategoriId,
    jumlah = jumlah,
    keterangan = keterangan,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    userId = userId,
    tanggal = tanggal,
    createdAt = createdAt
)

fun Kas.toEntity(isSynced: Boolean = true) = KasEntity(
    id = id,
    jenis = jenis,
    kategoriId = kategoriId,
    jumlah = jumlah,
    keterangan = keterangan,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    userId = userId,
    tanggal = tanggal,
    createdAt = createdAt,
    isSynced = isSynced
)

fun Kas.toDto() = KasDto(
    id = id,
    jenis = jenis,
    kategoriId = kategoriId,
    jumlah = jumlah,
    keterangan = keterangan,
    referensiId = referensiId,
    referensiTipe = referensiTipe,
    userId = userId,
    tanggal = tanggal,
    createdAt = createdAt
)
