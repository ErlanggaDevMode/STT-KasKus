package com.koperasiku.app.domain.model

data class CartItem(
    val produk: Produk,
    val kuantitas: Int
) {
    val subtotal: Long get() = produk.hargaJual * kuantitas
}
