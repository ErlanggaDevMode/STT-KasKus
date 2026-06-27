package com.koperasiku.app.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.koperasiku.app.presentation.ui.theme.KopkustTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardDefaults.cardColors().containerColor
            ),
            content = content
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppCardPreview() {
    KopkustTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Kartu Statis")
                    Text(text = "Detail informasi tambahan di sini.")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AppCard(onClick = {}) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Kartu Klik-able")
                    Text(text = "Dapat ditekan untuk navigasi.")
                }
            }
        }
    }
}


