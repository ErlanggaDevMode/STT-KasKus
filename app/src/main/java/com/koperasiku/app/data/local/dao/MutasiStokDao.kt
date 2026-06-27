package com.koperasiku.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.koperasiku.app.data.local.entity.MutasiStokEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MutasiStokDao {
    @Query("SELECT * FROM mutasi_stok WHERE produk_id = :produkId ORDER BY created_at DESC")
    fun getByProdukId(produkId: String): Flow<List<MutasiStokEntity>>

    @Upsert
    suspend fun upsert(mutasi: MutasiStokEntity)

    @Upsert
    suspend fun upsertAll(mutasiList: List<MutasiStokEntity>)
}
