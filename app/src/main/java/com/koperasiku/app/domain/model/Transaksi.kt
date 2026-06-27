package com.koperasiku.app.domain.model

data class Transaksi(
    val id: String,
    val nomorTransaksi: String,
    val anggotaId: String?,
    val userId: String, // kasir
    val totalBelanja: Long,
    val nominalBayar: Long,
    val nominalKembali: Long,
    val jenisPembayaran: String, // CASH, DEPOSIT
    val keterangan: String?,
    val createdAt: String,
    val items: List<TransaksiItem> = emptyList()
)

data class TransaksiItem(
    val id: String,
    val transaksiId: String,
    val produkId: String,
    val kuantitas: Int,
    val hargaSatuan: Long,
    val subtotal: Long,
    val produkNama: String? = null
)
