package com.koperasiku.app.domain.model

data class Simpanan(
    val id: String,
    val anggotaId: String,
    val jenis: String, // POKOK, WAJIB, SUKARELA
    val saldo: Long,
    val anggotaNama: String? = null
)
