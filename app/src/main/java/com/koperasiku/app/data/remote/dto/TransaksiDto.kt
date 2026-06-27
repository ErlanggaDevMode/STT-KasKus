package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransaksiDto(
    val id: String,
    @SerialName("nomor_transaksi") val nomorTransaksi: String,
    @SerialName("anggota_id") val anggotaId: String?,
    @SerialName("user_id") val userId: String,
    @SerialName("total_belanja") val totalBelanja: Long,
    @SerialName("nominal_bayar") val nominalBayar: Long,
    @SerialName("nominal_kembali") val nominalKembali: Long,
    @SerialName("jenis_pembayaran") val jenisPembayaran: String,
    val keterangan: String?,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class TransaksiItemDto(
    val id: String,
    @SerialName("transaksi_id") val transaksiId: String,
    @SerialName("produk_id") val produkId: String,
    val kuantitas: Int,
    @SerialName("harga_satuan") val hargaSatuan: Long,
    val subtotal: Long
)
