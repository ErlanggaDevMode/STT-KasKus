package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimpananDto(
    val id: String,
    @SerialName("anggota_id") val anggotaId: String,
    val jenis: String, // POKOK, WAJIB, SUKARELA
    val saldo: Long
)
