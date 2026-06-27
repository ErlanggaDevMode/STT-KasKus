package com.koperasiku.app.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.koperasiku.app.presentation.ui.theme.KopkustTheme

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Cari"
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Bersihkan"
                    )
                }
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(24.dp), // Fully rounded modern styling
        colors = OutlinedTextFieldDefaults.colors(),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    KopkustTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SearchBar(
                query = "",
                onQueryChange = {},
                placeholder = "Cari produk..."
            )
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(
                query = "Kopi Susu",
                onQueryChange = {},
                placeholder = "Cari produk..."
            )
        }
    }
}
