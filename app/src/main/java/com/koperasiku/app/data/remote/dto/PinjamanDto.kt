package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinjamanDto(
    val id: String,
    @SerialName("nomor_pinjaman") val nomorPinjaman: String,
    @SerialName("anggota_id") val anggotaId: String,
    @SerialName("jumlah_pinjaman") val jumlahPinjaman: Long,
    @SerialName("tenor_bulan") val tenorBulan: Int,
    @SerialName("bunga_persen_per_bulan") val bungaPersenPerBulan: Double,
    val keperluan: String?,
    val status: String, // DIAJUKAN, AKTIF, LUNAS, DITOLAK
    @SerialName("tanggal_pengajuan") val tanggalPengajuan: String,
    @SerialName("tanggal_disetujui") val tanggalDisetujui: String?,
    @SerialName("tanggal_lunas_target") val tanggalLunasTarget: String?,
    @SerialName("approved_by_user_id") val approvedByUserId: String?,
    @SerialName("created_at") val createdAt: String
)
