package com.koperasiku.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MutasiSimpananDto(
    val id: String,
    @SerialName("anggota_id") val anggotaId: String,
    val jenis: String, // SETORAN, PENARIKAN, BUNGA
    @SerialName("jenis_simpanan") val jenisSimpanan: String, // POKOK, WAJIB, SUKARELA
    val jumlah: Long,
    @SerialName("saldo_sebelum") val saldoSebelum: Long,
    @SerialName("saldo_sesudah") val saldoSesudah: Long,
    val keterangan: String?,
    @SerialName("user_id") val userId: String?,
    @SerialName("created_at") val createdAt: String
)
