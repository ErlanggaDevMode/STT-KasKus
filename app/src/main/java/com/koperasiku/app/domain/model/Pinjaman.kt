package com.koperasiku.app.domain.model

data class Pinjaman(
    val id: String,
    val nomorPinjaman: String,
    val anggotaId: String,
    val anggotaNama: String? = null,
    val jumlahPinjaman: Long,
    val tenorBulan: Int,
    val bungaPersenPerBulan: Double, // e.g. 1.0 = 1% per bulan flat
    val keperluan: String?,
    val status: String, // DIAJUKAN, AKTIF, LUNAS, DITOLAK
    val tanggalPengajuan: String,
    val tanggalDisetujui: String?,
    val tanggalLunasTarget: String?,
    val approvedByUserId: String?,
    val createdAt: String
) {
    val angsuranPokok: Long get() = jumlahPinjaman / tenorBulan
    val bungaBulanan: Long get() = (jumlahPinjaman * bungaPersenPerBulan / 100).toLong()
    val angsuranPerBulan: Long get() = angsuranPokok + bungaBulanan
    val totalBayar: Long get() = angsuranPerBulan * tenorBulan
    val totalBunga: Long get() = bungaBulanan * tenorBulan
}
