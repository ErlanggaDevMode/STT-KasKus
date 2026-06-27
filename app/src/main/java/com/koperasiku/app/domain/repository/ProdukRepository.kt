package com.koperasiku.app.domain.repository

import com.koperasiku.app.core.utils.Resource
import com.koperasiku.app.domain.model.MutasiStok
import com.koperasiku.app.domain.model.Produk
import kotlinx.coroutines.flow.Flow

interface ProdukRepository {
    fun getProdukList(): Flow<List<Produk>>
    fun getStokMenipis(): Flow<List<Produk>>
    fun searchProduk(query: String): Flow<List<Produk>>
    fun getProdukDetail(id: String): Flow<Resource<Produk>>
    fun createProduk(produk: Produk): Flow<Resource<Unit>>
    fun updateProduk(produk: Produk): Flow<Resource<Unit>>
    fun updateStok(produkId: String, jumlah: Int, jenis: String, keterangan: String?): Flow<Resource<Unit>>
    fun executeStokOpname(opnameItems: Map<String, Int>, catatan: String?): Flow<Resource<Unit>>
    fun getMutasiStok(produkId: String): Flow<List<MutasiStok>>
}
