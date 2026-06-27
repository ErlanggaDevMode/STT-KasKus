package com.koperasiku.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.koperasiku.app.data.local.entity.MutasiSimpananEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MutasiSimpananDao {
    @Query("SELECT * FROM mutasi_simpanan WHERE anggota_id = :anggotaId ORDER BY created_at DESC")
    fun getByAnggotaId(anggotaId: String): Flow<List<MutasiSimpananEntity>>

    @Upsert
    suspend fun upsert(mutasi: MutasiSimpananEntity)

    @Upsert
    suspend fun upsertAll(mutasiList: List<MutasiSimpananEntity>)
}
