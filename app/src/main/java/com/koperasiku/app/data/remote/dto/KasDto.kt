package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KasDto(
    val id: String,
    val jenis: String, // MASUK, KELUAR
    @SerialName("kategori_id") val kategoriId: String?,
    val jumlah: Long,
    val keterangan: String?,
    @SerialName("referensi_id") val referensiId: String?,
    @SerialName("referensi_tipe") val referensiTipe: String?,
    @SerialName("user_id") val userId: String?,
    val tanggal: String,
    @SerialName("created_at") val createdAt: String
)
