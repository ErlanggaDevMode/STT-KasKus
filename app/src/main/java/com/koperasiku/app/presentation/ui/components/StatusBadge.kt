package com.koperasiku.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.koperasiku.app.domain.model.enums.StatusPinjaman
import com.koperasiku.app.presentation.ui.theme.KopkustTheme
import com.koperasiku.app.presentation.ui.theme.KoperasiBlue
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen
import com.koperasiku.app.presentation.ui.theme.KoperasiOrange
import com.koperasiku.app.presentation.ui.theme.KoperasiRed
import com.koperasiku.app.presentation.ui.theme.TextSecondary

@Composable
fun StatusBadge(
    status: StatusPinjaman,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        StatusPinjaman.DIAJUKAN -> Triple(KoperasiOrange.copy(alpha = 0.15f), KoperasiOrange, "Diajukan")
        StatusPinjaman.DISETUJUI -> Triple(KoperasiBlue.copy(alpha = 0.15f), KoperasiBlue, "Disetujui")
        StatusPinjaman.AKTIF -> Triple(KoperasiGreen.copy(alpha = 0.15f), KoperasiGreen, "Aktif")
        StatusPinjaman.LUNAS -> Triple(TextSecondary.copy(alpha = 0.15f), TextSecondary, "Lunas")
        StatusPinjaman.DITOLAK -> Triple(KoperasiRed.copy(alpha = 0.15f), KoperasiRed, "Ditolak")
    }

    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .border(1.dp, textColor.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun StokBadge(
    stok: Int,
    minimum: Int,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when {
        stok == 0 -> Triple(KoperasiRed.copy(alpha = 0.15f), KoperasiRed, "Habis")
        stok <= minimum -> Triple(KoperasiOrange.copy(alpha = 0.15f), KoperasiOrange, "Menipis ($stok)")
        else -> Triple(KoperasiGreen.copy(alpha = 0.15f), KoperasiGreen, "Aman ($stok)")
    }

    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .border(1.dp, textColor.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgePreview() {
    KopkustTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                StatusBadge(status = StatusPinjaman.DIAJUKAN)
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = StatusPinjaman.DISETUJUI)
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = StatusPinjaman.AKTIF)
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = StatusPinjaman.LUNAS)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row {
                StokBadge(stok = 0, minimum = 5)
                Spacer(modifier = Modifier.width(8.dp))
                StokBadge(stok = 3, minimum = 5)
                Spacer(modifier = Modifier.width(8.dp))
                StokBadge(stok = 12, minimum = 5)
            }
        }
    }
}
