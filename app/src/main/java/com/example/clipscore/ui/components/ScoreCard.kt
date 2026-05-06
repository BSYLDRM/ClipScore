package com.example.clipscore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clipscore.ui.theme.BrandError
import com.example.clipscore.ui.theme.BrandSuccess
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.BrandWarning
import com.example.clipscore.ui.theme.Nunito

@Composable
fun ScoreCard(
    label: String,
    score: Int,
    icon: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val color = when {
        score < 41 -> BrandError
        score < 71 -> BrandWarning
        else -> BrandSuccess
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                fontFamily = Nunito,
                color = BrandText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = score.toString(),
                color = BrandText,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                color = color,
                trackColor = BrandText.copy(alpha = 0.12f),
                modifier = Modifier.width(160.dp),
            )
        }
    }
}

