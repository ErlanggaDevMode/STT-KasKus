package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnggotaDto(
    val id: String,
    @SerialName("user_id") val userId: String?,
    @SerialName("nomor_anggota") val nomorAnggota: String,
    val nama: String,
    val nik: String,
    val alamat: String?,
    @SerialName("no_hp") val noHp: String?,
    @SerialName("foto_ktp_url") val fotoKtpUrl: String?,
    @SerialName("is_aktif") val isAktif: Boolean = true,
    @SerialName("tanggal_gabung") val tanggalGabung: String
)
