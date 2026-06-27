package com.koperasiku.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mutasi_simpanan")
data class MutasiSimpananEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "anggota_id") val anggotaId: String,
    val jenis: String, // SETORAN, PENARIKAN, BUNGA
    @ColumnInfo(name = "jenis_simpanan") val jenisSimpanan: String, // POKOK, WAJIB, SUKARELA
    val jumlah: Long,
    @ColumnInfo(name = "saldo_sebelum") val saldoSebelum: Long,
    @ColumnInfo(name = "saldo_sesudah") val saldoSesudah: Long,
    val keterangan: String?,
    @ColumnInfo(name = "user_id") val userId: String?,
    @ColumnInfo(name = "created_at") val createdAt: String
)
