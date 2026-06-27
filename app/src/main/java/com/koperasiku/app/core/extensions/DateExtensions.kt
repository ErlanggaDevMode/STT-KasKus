package com.koperasiku.app.core.extensions

import kotlinx.datetime.LocalDate

fun LocalDate.toIndonesian(): String {
    val namaBulan = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    return "${this.dayOfMonth} ${namaBulan[this.monthNumber - 1]} ${this.year}"
}

fun String.toIndonesianDateTime(): String {
    try {
        val parts = this.split("T")
        if (parts.size < 2) return this
        val dateParts = parts[0].split("-")
        if (dateParts.size < 3) return this
        val year = dateParts[0]
        val monthNum = dateParts[1].toInt()
        val day = dateParts[2].toInt()
        val timeParts = parts[1].split(":")
        val hour = timeParts[0]
        val minute = timeParts[1]
        
        val namaBulan = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agt", "Sep", "Okt", "Nov", "Des"
        )
        return "$day ${namaBulan[monthNum - 1]} $year, $hour:$minute"
    } catch (e: Exception) {
        return this
    }
}
