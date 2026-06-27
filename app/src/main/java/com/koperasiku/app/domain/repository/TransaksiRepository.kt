package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.Transaksi
import kotlinx.coroutines.flow.Flow

interface TransaksiRepository {
    fun getTransaksiHistory(): Flow<List<Transaksi>>
    fun processCheckout(transaksi: Transaksi): Flow<Resource<Unit>>
    fun getTransaksiDetail(id: String): Flow<Resource<Transaksi>>
}
