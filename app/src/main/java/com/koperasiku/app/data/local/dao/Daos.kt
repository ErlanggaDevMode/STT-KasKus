package com.koperasiku.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.koperasiku.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnggotaDao {
    @Query("SELECT * FROM anggota WHERE is_aktif = 1 ORDER BY nama ASC")
    fun getAll(): Flow<List<AnggotaEntity>>

    @Query("SELECT * FROM anggota WHERE id = :id")
    suspend fun getById(id: String): AnggotaEntity?

    @Query("SELECT * FROM anggota WHERE nama LIKE '%' || :query || '%' OR nomor_anggota LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<AnggotaEntity>>

    @Upsert
    suspend fun upsertAll(anggota: List<AnggotaEntity>)

    @Upsert
    suspend fun upsert(anggota: AnggotaEntity)

    @Query("UPDATE anggota SET is_aktif = 0 WHERE id = :id")
    suspend fun deactivate(id: String)
}

@Dao
interface ProdukDao {
    @Query("SELECT * FROM produk WHERE is_aktif = 1 ORDER BY nama ASC")
    fun getAll(): Flow<List<ProdukEntity>>

    @Query("SELECT * FROM produk WHERE id = :id")
    suspend fun getById(id: String): ProdukEntity?

    @Query("SELECT * FROM produk WHERE kode = :kode")
    suspend fun getByKode(kode: String): ProdukEntity?

    @Query("SELECT * FROM produk WHERE nama LIKE '%' || :query || '%' OR kode LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ProdukEntity>>

    @Query("SELECT * FROM produk WHERE stok_saat_ini <= minimum_stok AND is_aktif = 1")
    fun getStokMenipis(): Flow<List<ProdukEntity>>

    @Upsert
    suspend fun upsertAll(produk: List<ProdukEntity>)

    @Upsert
    suspend fun upsert(produk: ProdukEntity)

    @Query("UPDATE produk SET stok_saat_ini = :newStok WHERE id = :id")
    suspend fun updateStok(id: String, newStok: Int)

    @Query("UPDATE produk SET is_aktif = 0 WHERE id = :id")
    suspend fun deactivate(id: String)
}

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC")
    fun getAll(): Flow<List<TransaksiEntity>>

    @Query("SELECT * FROM transaksi WHERE id = :id")
    suspend fun getById(id: String): TransaksiEntity?

    @Query("SELECT * FROM transaksi WHERE is_synced = 0")
    suspend fun getPendingTransactions(): List<TransaksiEntity>

    @Upsert
    suspend fun upsert(transaksi: TransaksiEntity)

    @Upsert
    suspend fun upsertAll(transaksi: List<TransaksiEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<TransaksiItemEntity>)

    @Query("SELECT * FROM transaksi_item WHERE transaksi_id = :transaksiId")
    suspend fun getItemsForTransaction(transaksiId: String): List<TransaksiItemEntity>

    @Query("SELECT * FROM transaksi_item WHERE transaksi_id = :transaksiId")
    fun getItemsFlow(transaksiId: String): Flow<List<TransaksiItemEntity>>
}

@Dao
interface KasDao {
    @Query("SELECT * FROM kas ORDER BY tanggal DESC, created_at DESC")
    fun getAll(): Flow<List<KasEntity>>

    @Query("SELECT * FROM kas WHERE id = :id")
    suspend fun getById(id: String): KasEntity?

    @Query("SELECT COALESCE(SUM(CASE WHEN jenis = 'MASUK' THEN jumlah ELSE 0 END), 0) FROM kas")
    fun getTotalMasuk(): Flow<Long>

    @Query("SELECT COALESCE(SUM(CASE WHEN jenis = 'KELUAR' THEN jumlah ELSE 0 END), 0) FROM kas")
    fun getTotalKeluar(): Flow<Long>

    @Upsert
    suspend fun upsert(kas: KasEntity)

    @Upsert
    suspend fun upsertAll(kas: List<KasEntity>)
}

@Dao
interface SimpananDao {
    @Query("SELECT * FROM simpanan ORDER BY anggota_id ASC")
    fun getAll(): Flow<List<SimpananEntity>>

    @Query("SELECT * FROM simpanan WHERE anggota_id = :anggotaId")
    fun getSimpananForAnggota(anggotaId: String): Flow<List<SimpananEntity>>

    @Query("SELECT * FROM simpanan WHERE anggota_id = :anggotaId AND jenis = :jenis")
    suspend fun getSimpananByJenis(anggotaId: String, jenis: String): SimpananEntity?

    @Upsert
    suspend fun upsert(simpanan: SimpananEntity)

    @Upsert
    suspend fun upsertAll(simpanan: List<SimpananEntity>)
}

@Dao
interface PinjamanDao {
    @Query("SELECT * FROM pinjaman ORDER BY tanggal_pengajuan DESC")
    fun getAll(): Flow<List<PinjamanEntity>>

    @Query("SELECT * FROM pinjaman WHERE id = :id")
    suspend fun getById(id: String): PinjamanEntity?

    @Query("SELECT * FROM pinjaman WHERE anggota_id = :anggotaId")
    fun getPinjamanForAnggota(anggotaId: String): Flow<List<PinjamanEntity>>

    @Query("SELECT * FROM pinjaman WHERE status = :status")
    fun getPinjamanByStatus(status: String): Flow<List<PinjamanEntity>>

    @Upsert
    suspend fun upsert(pinjaman: PinjamanEntity)

    @Upsert
    suspend fun upsertAll(pinjaman: List<PinjamanEntity>)

    @Upsert
    suspend fun upsertAngsuranAll(angsuran: List<AngsuranEntity>)

    @Query("SELECT * FROM angsuran WHERE pinjaman_id = :pinjamanId ORDER BY ke ASC")
    fun getAngsuranForPinjaman(pinjamanId: String): Flow<List<AngsuranEntity>>

    @Query("SELECT * FROM angsuran WHERE id = :id")
    suspend fun getAngsuranById(id: String): AngsuranEntity?

    @Upsert
    suspend fun upsertAngsuran(angsuran: AngsuranEntity)
}
