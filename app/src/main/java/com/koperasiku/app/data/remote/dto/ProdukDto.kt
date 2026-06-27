package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProdukDto(
    val id: String,
    val kode: String,
    val nama: String,
    @SerialName("kategori_id") val kategoriId: String?,
    @SerialName("harga_beli") val hargaBeli: Long,
    @SerialName("harga_jual") val hargaJual: Long,
    val satuan: String = "pcs",
    @SerialName("stok_saat_ini") val stokSaatIni: Int,
    @SerialName("minimum_stok") val minimumStok: Int = 5,
    @SerialName("foto_url") val fotoUrl: String?,
    @SerialName("is_aktif") val isAktif: Boolean = true
)
