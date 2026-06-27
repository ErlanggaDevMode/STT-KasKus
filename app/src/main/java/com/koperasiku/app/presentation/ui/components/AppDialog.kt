package com.koperasiku.app.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.koperasiku.app.presentation.ui.theme.KopkustTheme

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Ya",
    dismissText: String = "Batal"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        }
    )
}

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    dismissText: String = "Tutup"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppDialogPreview() {
    KopkustTheme {
        ConfirmDialog(
            title = "Keluar Aplikasi",
            message = "Apakah Anda yakin ingin keluar dari aplikasi KoperasiKu?",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
