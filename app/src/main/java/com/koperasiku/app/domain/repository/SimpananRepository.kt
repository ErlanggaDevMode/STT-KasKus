package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.MutasiSimpanan
import com.koperasiku.app.domain.model.Simpanan
import kotlinx.coroutines.flow.Flow

interface SimpananRepository {
    fun getSimpananList(): Flow<List<Simpanan>>
    fun getSimpananForAnggota(anggotaId: String): Flow<List<Simpanan>>
    fun getMutasiSimpanan(anggotaId: String): Flow<List<MutasiSimpanan>>
    fun executeSimpananTransaction(
        anggotaId: String,
        jenisTransaction: String, // SETORAN, PENARIKAN
        jenisSimpanan: String, // POKOK, WAJIB, SUKARELA
        jumlah: Long,
        keterangan: String?
    ): Flow<Resource<Unit>>
}
