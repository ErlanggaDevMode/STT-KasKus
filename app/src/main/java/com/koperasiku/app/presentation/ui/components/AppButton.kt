package com.koperasiku.app.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.koperasiku.app.presentation.ui.theme.KopkustTheme
import com.koperasiku.app.presentation.ui.theme.KoperasiGold
import com.koperasiku.app.presentation.ui.theme.KoperasiGreen
import com.koperasiku.app.presentation.ui.theme.KoperasiRed

enum class ButtonVariant {
    Primary,
    Secondary,
    Danger,
    Outlined
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val buttonColors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = KoperasiGreen,
            contentColor = Color.White
        )
        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = KoperasiGold,
            contentColor = Color.Black
        )
        ButtonVariant.Danger -> ButtonDefaults.buttonColors(
            containerColor = KoperasiRed,
            contentColor = Color.White
        )
        ButtonVariant.Outlined -> ButtonDefaults.outlinedButtonColors(
            contentColor = KoperasiGreen
        )
    }

    val finalModifier = modifier.height(48.dp) // Minimum touch target constraint

    if (variant == ButtonVariant.Outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = finalModifier,
            enabled = enabled && !isLoading,
            colors = buttonColors,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            )
        ) {
            ButtonContent(text = text, isLoading = isLoading, contentColor = KoperasiGreen)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = finalModifier,
            enabled = enabled && !isLoading,
            colors = buttonColors
        ) {
            val contentColor = when (variant) {
                ButtonVariant.Secondary -> Color.Black
                else -> Color.White
            }
            ButtonContent(text = text, isLoading = isLoading, contentColor = contentColor)
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    contentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreview() {
    KopkustTheme {
        Row {
            AppButton(text = "Kirim", onClick = {})
            Spacer(modifier = Modifier.width(8.dp))
            AppButton(text = "Proses", onClick = {}, variant = ButtonVariant.Secondary)
            Spacer(modifier = Modifier.width(8.dp))
            AppButton(text = "Hapus", onClick = {}, variant = ButtonVariant.Danger)
            Spacer(modifier = Modifier.width(8.dp))
            AppButton(text = "Batal", onClick = {}, variant = ButtonVariant.Outlined)
        }
    }
}
