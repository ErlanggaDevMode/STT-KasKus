package com.koperasiku.app.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.koperasiku.app.presentation.ui.theme.KopkustTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CurrencyTextField(
    value: Long,
    onValueChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    // Format numeric value for display (thousands separator only, eg: 1.500.000)
    val formattedText = remember(value) {
        if (value == 0L) "" else formatNumber(value)
    }

    OutlinedTextField(
        value = formattedText,
        onValueChange = { inputString ->
            // Filter digits only to clean out any typed thousand dots or non-numeric entries
            val cleanNumberString = inputString.filter { it.isDigit() }
            val parsedValue = cleanNumberString.toLongOrNull() ?: 0L
            onValueChange(parsedValue)
        },
        label = { Text(text = label) },
        leadingIcon = {
            Text(
                text = "Rp",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp, end = 8.dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth().height(56.dp)
    )
}

private fun formatNumber(number: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return formatter.format(number)
}

@Preview(showBackground = true)
@Composable
fun CurrencyTextFieldPreview() {
    KopkustTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CurrencyTextField(
                value = 1500000L,
                onValueChange = {},
                label = "Jumlah Pinjaman"
            )
        }
    }
}
