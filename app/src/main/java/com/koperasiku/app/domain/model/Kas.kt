package com.koperasiku.app.domain.model

data class Kas(
    val id: String,
    val jenis: String, // MASUK, KELUAR
    val kategoriId: String?,
    val jumlah: Long,
    val keterangan: String?,
    val referensiId: String?,
    val referensiTipe: String?,
    val userId: String?,
    val tanggal: String,
    val createdAt: String
)
