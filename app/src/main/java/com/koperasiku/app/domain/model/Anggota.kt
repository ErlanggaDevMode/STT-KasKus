package com.koperasiku.app.domain.model

import kotlinx.datetime.LocalDate

data class Anggota(
    val id: String,
    val nomorAnggota: String,
    val nama: String,
    val nik: String,
    val alamat: String?,
    val noHp: String?,
    val fotoKtpUrl: String?,
    val isAktif: Boolean,
    val tanggalGabung: LocalDate
)
