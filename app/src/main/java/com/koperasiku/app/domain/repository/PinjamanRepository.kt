package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Angsuran
import com.koperasiku.app.domain.model.Pinjaman
import kotlinx.coroutines.flow.Flow

interface PinjamanRepository {
    fun getPinjamanList(): Flow<List<Pinjaman>>
    fun getPinjamanDetail(pinjamanId: String): Flow<Pinjaman?>
    fun getAngsuranList(pinjamanId: String): Flow<List<Angsuran>>
    fun applyPinjaman(pinjaman: Pinjaman): Flow<Resource<Unit>>
    fun approvePinjaman(pinjamanId: String, approverId: String): Flow<Resource<Unit>>
    fun rejectPinjaman(pinjamanId: String): Flow<Resource<Unit>>
    fun payAngsuran(angsuranId: String, jumlahDenda: Long): Flow<Resource<Unit>>
}
