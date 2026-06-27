package com.koperasiku.app.data.mapper

import com.koperasiku.app.data.local.entity.SimpananEntity
import com.koperasiku.app.data.local.entity.MutasiSimpananEntity
import com.koperasiku.app.data.remote.dto.SimpananDto
import com.koperasiku.app.data.remote.dto.MutasiSimpananDto
import com.koperasiku.app.domain.model.Simpanan
import com.koperasiku.app.domain.model.MutasiSimpanan

fun SimpananDto.toDomain(anggotaNama: String? = null) = Simpanan(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    saldo = saldo,
    anggotaNama = anggotaNama
)

fun SimpananEntity.toDomain(anggotaNama: String? = null) = Simpanan(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    saldo = saldo,
    anggotaNama = anggotaNama
)

fun Simpanan.toEntity(isSynced: Boolean = true) = SimpananEntity(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    saldo = saldo,
    isSynced = isSynced
)

fun Simpanan.toDto() = SimpananDto(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    saldo = saldo
)

// MutasiSimpanan Mappers
fun MutasiSimpananDto.toDomain() = MutasiSimpanan(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    jenisSimpanan = jenisSimpanan,
    jumlah = jumlah,
    saldoSebelum = saldoSebelum,
    saldoSesudah = saldoSesudah,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)

fun MutasiSimpananEntity.toDomain() = MutasiSimpanan(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    jenisSimpanan = jenisSimpanan,
    jumlah = jumlah,
    saldoSebelum = saldoSebelum,
    saldoSesudah = saldoSesudah,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)

fun MutasiSimpanan.toEntity() = MutasiSimpananEntity(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    jenisSimpanan = jenisSimpanan,
    jumlah = jumlah,
    saldoSebelum = saldoSebelum,
    saldoSesudah = saldoSesudah,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)

fun MutasiSimpanan.toDto() = MutasiSimpananDto(
    id = id,
    anggotaId = anggotaId,
    jenis = jenis,
    jenisSimpanan = jenisSimpanan,
    jumlah = jumlah,
    saldoSebelum = saldoSebelum,
    saldoSesudah = saldoSesudah,
    keterangan = keterangan,
    userId = userId,
    createdAt = createdAt
)
