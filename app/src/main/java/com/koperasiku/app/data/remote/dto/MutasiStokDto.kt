package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MutasiStokDto(
    val id: String,
    @SerialName("produk_id") val produkId: String,
    val jenis: String, // MASUK, KELUAR, OPNAME, RETUR
    val jumlah: Int,
    @SerialName("stok_sebelum") val stokSebelum: Int,
    @SerialName("stok_sesudah") val stokSesudah: Int,
    @SerialName("referensi_id") val referensiId: String?,
    @SerialName("referensi_tipe") val referensiTipe: String?,
    val keterangan: String?,
    @SerialName("user_id") val userId: String?,
    @SerialName("created_at") val createdAt: String
)
