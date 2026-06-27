package com.koperasiku.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anggota")
data class AnggotaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "nomor_anggota") val nomorAnggota: String,
    val nama: String,
    val nik: String,
    val alamat: String?,
    @ColumnInfo(name = "no_hp") val noHp: String?,
    @ColumnInfo(name = "foto_ktp_url") val fotoKtpUrl: String?,
    @ColumnInfo(name = "is_aktif") val isAktif: Boolean = true,
    @ColumnInfo(name = "tanggal_gabung") val tanggalGabung: String, // ISO String
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true
)

@Entity(tableName = "produk")
data class ProdukEntity(
    @PrimaryKey val id: String,
    val kode: String,
    val nama: String,
    @ColumnInfo(name = "kategori_id") val kategoriId: String?,
    @ColumnInfo(name = "harga_beli") val hargaBeli: Long,
    @ColumnInfo(name = "harga_jual") val hargaJual: Long,
    val satuan: String,
    @ColumnInfo(name = "stok_saat_ini") val stokSaatIni: Int,
    @ColumnInfo(name = "minimum_stok") val minimumStok: Int,
    @ColumnInfo(name = "foto_url") val fotoUrl: String?,
    @ColumnInfo(name = "is_aktif") val isAktif: Boolean = true,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true
)

@Entity(tableName = "transaksi")
data class TransaksiEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "nomor_transaksi") val nomorTransaksi: String,
    @ColumnInfo(name = "kasir_id") val kasirId: String?,
    val subtotal: Long,
    val diskon: Long,
    val total: Long,
    @ColumnInfo(name = "metode_bayar") val metodeBayar: String, // TUNAI, TRANSFER, QRIS
    val bayar: Long,
    val kembalian: Long,
    val catatan: String?,
    val tanggal: String, // ISO String
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true
)

@Entity(tableName = "transaksi_item")
data class TransaksiItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "transaksi_id") val transaksiId: String,
    @ColumnInfo(name = "produk_id") val produkId: String,
    @ColumnInfo(name = "nama_produk") val namaProduk: String,
    @ColumnInfo(name = "harga_satuan") val hargaSatuan: Long,
    val jumlah: Int,
    @ColumnInfo(name = "diskon_item") val diskonItem: Long,
    val subtotal: Long
)

@Entity(tableName = "kas")
data class KasEntity(
    @PrimaryKey val id: String,
    val jenis: String, // MASUK, KELUAR
    @ColumnInfo(name = "kategori_id") val kategoriId: String?,
    val jumlah: Long,
    val keterangan: String?,
    @ColumnInfo(name = "referensi_id") val referensiId: String?,
    @ColumnInfo(name = "referensi_tipe") val referensiTipe: String?,
    @ColumnInfo(name = "user_id") val userId: String?,
    val tanggal: String, // ISO Date String (YYYY-MM-DD)
    @ColumnInfo(name = "created_at") val createdAt: String, // ISO String or Timestamp
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true
)

@Entity(tableName = "simpanan")
data class SimpananEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "anggota_id") val anggotaId: String,
    val jenis: String, // POKOK, WAJIB, SUKARELA
    val saldo: Long,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true
)

@Entity(tableName = "pinjaman")
data class PinjamanEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "nomor_pinjaman") val nomorPinjaman: String,
    @ColumnInfo(name = "anggota_id") val anggotaId: String,
    val jumlah: Long,
    @ColumnInfo(name = "bunga_persen") val bungaPersen: Double,
    @ColumnInfo(name = "tenor_bulan") val tenorBulan: Int,
    @ColumnInfo(name = "total_bunga") val totalBunga: Long,
    @ColumnInfo(name = "total_bayar") val totalBayar: Long,
    val status: String, // DIAJUKAN, DISETUJUI, AKTIF, LUNAS, DITOLAK
    val keperluan: String?,
    @ColumnInfo(name = "reviewer_id") val reviewerId: String?,
    @ColumnInfo(name = "tanggal_pengajuan") val tanggalPengajuan: String,
    @ColumnInfo(name = "tanggal_disetujui") val tanggalDisetujui: String?,
    @ColumnInfo(name = "tanggal_cair") val tanggalCair: String?,
    @ColumnInfo(name = "catatan_approval") val catatanApproval: String?,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = true
)

@Entity(tableName = "angsuran")
data class AngsuranEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "pinjaman_id") val pinjamanId: String,
    val ke: Int,
    @ColumnInfo(name = "jatuh_tempo") val jatuhTempo: String,
    val pokok: Long,
    val bunga: Long,
    val denda: Long,
    @ColumnInfo(name = "total_bayar") val totalBayar: Long,
    @ColumnInfo(name = "is_bayar") val isBayar: Boolean = false,
    @ColumnInfo(name = "tanggal_bayar") val tanggalBayar: String?,
    @ColumnInfo(name = "user_pencatat") val userPencatat: String?
)
