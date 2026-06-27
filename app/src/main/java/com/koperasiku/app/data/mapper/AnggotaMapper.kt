package com.koperasiku.app.data.mapper

import com.koperasiku.app.data.local.entity.AnggotaEntity
import com.koperasiku.app.data.remote.dto.AnggotaDto
import com.koperasiku.app.domain.model.Anggota
import kotlinx.datetime.LocalDate

fun AnggotaDto.toDomain() = Anggota(
    id = id,
    nomorAnggota = nomorAnggota,
    nama = nama,
    nik = nik,
    alamat = alamat,
    noHp = noHp,
    fotoKtpUrl = fotoKtpUrl,
    isAktif = isAktif,
    tanggalGabung = LocalDate.parse(tanggalGabung)
)

fun AnggotaEntity.toDomain() = Anggota(
    id = id,
    nomorAnggota = nomorAnggota,
    nama = nama,
    nik = nik,
    alamat = alamat,
    noHp = noHp,
    fotoKtpUrl = fotoKtpUrl,
    isAktif = isAktif,
    tanggalGabung = LocalDate.parse(tanggalGabung)
)

fun Anggota.toEntity(isSynced: Boolean = true) = AnggotaEntity(
    id = id,
    nomorAnggota = nomorAnggota,
    nama = nama,
    nik = nik,
    alamat = alamat,
    noHp = noHp,
    fotoKtpUrl = fotoKtpUrl,
    isAktif = isAktif,
    tanggalGabung = tanggalGabung.toString(),
    isSynced = isSynced
)

fun Anggota.toDto(userId: String? = null) = AnggotaDto(
    id = id,
    userId = userId,
    nomorAnggota = nomorAnggota,
    nama = nama,
    nik = nik,
    alamat = alamat,
    noHp = noHp,
    fotoKtpUrl = fotoKtpUrl,
    isAktif = isAktif,
    tanggalGabung = tanggalGabung.toString()
)
