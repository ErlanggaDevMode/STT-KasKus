package com.koperasiku.app.domain.model

data class MutasiSimpanan(
    val id: String,
    val anggotaId: String,
    val jenis: String, // SETORAN, PENARIKAN, BUNGA
    val jenisSimpanan: String, // POKOK, WAJIB, SUKARELA
    val jumlah: Long,
    val saldoSebelum: Long,
    val saldoSesudah: Long,
    val keterangan: String?,
    val userId: String?,
    val createdAt: String
)
