package com.koperasiku.app.domain.model

data class MutasiStok(
    val id: String,
    val produkId: String,
    val jenis: String, // MASUK, KELUAR, OPNAME, RETUR
    val jumlah: Int,
    val stokSebelum: Int,
    val stokSesudah: Int,
    val referensiId: String?,
    val referensiTipe: String?,
    val keterangan: String?,
    val userId: String?,
    val createdAt: String
)
