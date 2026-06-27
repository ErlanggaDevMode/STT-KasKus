package com.koperasiku.app.domain.model

data class Produk(
    val id: String,
    val kode: String,
    val nama: String,
    val kategoriId: String?,
    val hargaBeli: Long,
    val hargaJual: Long,
    val satuan: String,
    val stokSaatIni: Int,
    val minimumStok: Int,
    val fotoUrl: String?,
    val isAktif: Boolean
) {
    val isStokMenipis: Boolean get() = stokSaatIni <= minimumStok
    val isStokHabis: Boolean get() = stokSaatIni == 0
}
