package com.koperasiku.app.data.mapper

import com.koperasiku.app.data.local.entity.AngsuranEntity
import com.koperasiku.app.data.local.entity.PinjamanEntity
import com.koperasiku.app.data.remote.dto.AngsuranDto
import com.koperasiku.app.data.remote.dto.PinjamanDto
import com.koperasiku.app.domain.model.Angsuran
import com.koperasiku.app.domain.model.Pinjaman

// ---------- Pinjaman Mappers ----------

fun PinjamanDto.toDomain(anggotaNama: String? = null) = Pinjaman(
    id = id,
    nomorPinjaman = nomorPinjaman,
    anggotaId = anggotaId,
    anggotaNama = anggotaNama,
    jumlahPinjaman = jumlahPinjaman,
    tenorBulan = tenorBulan,
    bungaPersenPerBulan = bungaPersenPerBulan,
    keperluan = keperluan,
    status = status,
    tanggalPengajuan = tanggalPengajuan,
    tanggalDisetujui = tanggalDisetujui,
    tanggalLunasTarget = tanggalLunasTarget,
    approvedByUserId = approvedByUserId,
    createdAt = createdAt
)

fun PinjamanEntity.toDomain(anggotaNama: String? = null) = Pinjaman(
    id = id,
    nomorPinjaman = nomorPinjaman,
    anggotaId = anggotaId,
    anggotaNama = anggotaNama,
    jumlahPinjaman = jumlahPinjaman,
    tenorBulan = tenorBulan,
    bungaPersenPerBulan = bungaPersenPerBulan,
    keperluan = keperluan,
    status = status,
    tanggalPengajuan = tanggalPengajuan,
    tanggalDisetujui = tanggalDisetujui,
    tanggalLunasTarget = tanggalLunasTarget,
    approvedByUserId = approvedByUserId,
    createdAt = createdAt
)

fun Pinjaman.toEntity(isSynced: Boolean = true) = PinjamanEntity(
    id = id,
    nomorPinjaman = nomorPinjaman,
    anggotaId = anggotaId,
    jumlahPinjaman = jumlahPinjaman,
    tenorBulan = tenorBulan,
    bungaPersenPerBulan = bungaPersenPerBulan,
    keperluan = keperluan,
    status = status,
    tanggalPengajuan = tanggalPengajuan,
    tanggalDisetujui = tanggalDisetujui,
    tanggalLunasTarget = tanggalLunasTarget,
    approvedByUserId = approvedByUserId,
    createdAt = createdAt,
    isSynced = isSynced
)

fun Pinjaman.toDto() = PinjamanDto(
    id = id,
    nomorPinjaman = nomorPinjaman,
    anggotaId = anggotaId,
    jumlahPinjaman = jumlahPinjaman,
    tenorBulan = tenorBulan,
    bungaPersenPerBulan = bungaPersenPerBulan,
    keperluan = keperluan,
    status = status,
    tanggalPengajuan = tanggalPengajuan,
    tanggalDisetujui = tanggalDisetujui,
    tanggalLunasTarget = tanggalLunasTarget,
    approvedByUserId = approvedByUserId,
    createdAt = createdAt
)

// ---------- Angsuran Mappers ----------

fun AngsuranDto.toDomain() = Angsuran(
    id = id,
    pinjamanId = pinjamanId,
    anggotaId = anggotaId,
    ke = ke,
    jumlahPokok = jumlahPokok,
    jumlahBunga = jumlahBunga,
    jumlahDenda = jumlahDenda,
    jumlahTotal = jumlahTotal,
    status = status,
    tanggalJatuhTempo = tanggalJatuhTempo,
    tanggalBayar = tanggalBayar,
    createdAt = createdAt
)

fun AngsuranEntity.toDomain() = Angsuran(
    id = id,
    pinjamanId = pinjamanId,
    anggotaId = anggotaId,
    ke = ke,
    jumlahPokok = jumlahPokok,
    jumlahBunga = jumlahBunga,
    jumlahDenda = jumlahDenda,
    jumlahTotal = jumlahTotal,
    status = status,
    tanggalJatuhTempo = tanggalJatuhTempo,
    tanggalBayar = tanggalBayar,
    createdAt = createdAt
)

fun Angsuran.toEntity() = AngsuranEntity(
    id = id,
    pinjamanId = pinjamanId,
    anggotaId = anggotaId,
    ke = ke,
    jumlahPokok = jumlahPokok,
    jumlahBunga = jumlahBunga,
    jumlahDenda = jumlahDenda,
    jumlahTotal = jumlahTotal,
    status = status,
    tanggalJatuhTempo = tanggalJatuhTempo,
    tanggalBayar = tanggalBayar,
    createdAt = createdAt
)

fun Angsuran.toDto() = AngsuranDto(
    id = id,
    pinjamanId = pinjamanId,
    anggotaId = anggotaId,
    ke = ke,
    jumlahPokok = jumlahPokok,
    jumlahBunga = jumlahBunga,
    jumlahDenda = jumlahDenda,
    jumlahTotal = jumlahTotal,
    status = status,
    tanggalJatuhTempo = tanggalJatuhTempo,
    tanggalBayar = tanggalBayar,
    createdAt = createdAt
)
