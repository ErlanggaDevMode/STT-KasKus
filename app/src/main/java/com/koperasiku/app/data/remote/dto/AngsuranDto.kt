package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AngsuranDto(
    val id: String,
    @SerialName("pinjaman_id") val pinjamanId: String,
    @SerialName("anggota_id") val anggotaId: String,
    val ke: Int,
    @SerialName("jumlah_pokok") val jumlahPokok: Long,
    @SerialName("jumlah_bunga") val jumlahBunga: Long,
    @SerialName("jumlah_denda") val jumlahDenda: Long,
    @SerialName("jumlah_total") val jumlahTotal: Long,
    val status: String, // BELUM_BAYAR, LUNAS, TERLAMBAT
    @SerialName("tanggal_jatuh_tempo") val tanggalJatuhTempo: String,
    @SerialName("tanggal_bayar") val tanggalBayar: String?,
    @SerialName("created_at") val createdAt: String
)
