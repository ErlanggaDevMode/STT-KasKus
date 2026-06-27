package com.koperasiku.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mutasi_stok")
data class MutasiStokEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "produk_id") val produkId: String,
    val jenis: String, // MASUK, KELUAR, OPNAME, RETUR
    val jumlah: Int,
    @ColumnInfo(name = "stok_sebelum") val stokSebelum: Int,
    @ColumnInfo(name = "stok_sesudah") val stokSesudah: Int,
    @ColumnInfo(name = "referensi_id") val referensiId: String?,
    @ColumnInfo(name = "referensi_tipe") val referensiTipe: String?,
    val keterangan: String?,
    @ColumnInfo(name = "user_id") val userId: String?,
    @ColumnInfo(name = "created_at") val createdAt: String
)
