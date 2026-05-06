package com.example.clipscore.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.clipscore.ui.theme.BrandBorder
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Nunito

@Composable
fun MetadataCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clip(MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = BrandSurface),
        border = BorderStroke(1.dp, BrandBorder),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = icon, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontFamily = Nunito,
                color = BrandText.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = Nunito,
                color = BrandText,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

