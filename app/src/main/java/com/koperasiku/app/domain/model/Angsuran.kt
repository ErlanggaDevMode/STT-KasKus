package com.koperasiku.app.domain.model

data class Angsuran(
    val id: String,
    val pinjamanId: String,
    val anggotaId: String,
    val ke: Int, // cicilan ke-1, ke-2, dst.
    val jumlahPokok: Long,
    val jumlahBunga: Long,
    val jumlahDenda: Long,
    val jumlahTotal: Long,
    val status: String, // BELUM_BAYAR, LUNAS, TERLAMBAT
    val tanggalJatuhTempo: String,
    val tanggalBayar: String?,
    val createdAt: String
)
