package com.koperasiku.app.data.mapper

import com.koperasiku.app.data.local.entity.TransaksiEntity
import com.koperasiku.app.data.local.entity.TransaksiItemEntity
import com.koperasiku.app.data.remote.dto.TransaksiDto
import com.koperasiku.app.data.remote.dto.TransaksiItemDto
import com.koperasiku.app.domain.model.Transaksi
import com.koperasiku.app.domain.model.TransaksiItem

fun TransaksiEntity.toDomain(items: List<TransaksiItem> = emptyList()) = Transaksi(
    id = id,
    nomorTransaksi = nomorTransaksi,
    anggotaId = anggotaId,
    userId = kasirId ?: "",
    totalBelanja = total,
    nominalBayar = bayar,
    nominalKembali = kembalian,
    jenisPembayaran = metodeBayar,
    keterangan = catatan,
    createdAt = tanggal,
    items = items
)

fun TransaksiItemEntity.toDomain() = TransaksiItem(
    id = id,
    transaksiId = transaksiId,
    produkId = produkId,
    kuantitas = jumlah,
    hargaSatuan = hargaSatuan,
    subtotal = subtotal,
    produkNama = namaProduk
)

fun Transaksi.toEntity(isSynced: Boolean = true) = TransaksiEntity(
    id = id,
    nomorTransaksi = nomorTransaksi,
    anggotaId = anggotaId,
    kasirId = userId,
    subtotal = totalBelanja,
    diskon = 0L,
    total = totalBelanja,
    metodeBayar = jenisPembayaran,
    bayar = nominalBayar,
    kembalian = nominalKembali,
    catatan = keterangan,
    tanggal = createdAt,
    isSynced = isSynced
)

fun TransaksiItem.toEntity(namaProduk: String) = TransaksiItemEntity(
    id = id,
    transaksiId = transaksiId,
    produkId = produkId,
    namaProduk = namaProduk,
    hargaSatuan = hargaSatuan,
    jumlah = kuantitas,
    diskonItem = 0L,
    subtotal = subtotal
)

fun Transaksi.toDto() = TransaksiDto(
    id = id,
    nomorTransaksi = nomorTransaksi,
    anggotaId = anggotaId,
    userId = userId,
    totalBelanja = totalBelanja,
    nominalBayar = nominalBayar,
    nominalKembali = nominalKembali,
    jenisPembayaran = jenisPembayaran,
    keterangan = keterangan,
    createdAt = createdAt
)

fun TransaksiItem.toDto() = TransaksiItemDto(
    id = id,
    transaksiId = transaksiId,
    produkId = produkId,
    kuantitas = kuantitas,
    hargaSatuan = hargaSatuan,
    subtotal = subtotal
)
