package com.koperasiku.app.core.extensions

import java.text.NumberFormat
import java.util.Locale

fun Long.toRupiah(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(this).replace("Rp", "Rp ").replace(",00", "")
}
