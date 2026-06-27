package com.koperasiku.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.koperasiku.app.data.local.dao.*
import com.koperasiku.app.data.local.entity.*

@Database(
    entities = [
        AnggotaEntity::class,
        ProdukEntity::class,
        TransaksiEntity::class,
        TransaksiItemEntity::class,
        KasEntity::class,
        SimpananEntity::class,
        PinjamanEntity::class,
        AngsuranEntity::class,
        MutasiStokEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun anggotaDao(): AnggotaDao
    abstract fun produkDao(): ProdukDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun kasDao(): KasDao
    abstract fun simpananDao(): SimpananDao
    abstract fun pinjamanDao(): PinjamanDao
    abstract fun mutasiStokDao(): MutasiStokDao
}
