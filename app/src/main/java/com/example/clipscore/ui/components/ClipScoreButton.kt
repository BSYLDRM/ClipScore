package com.example.clipscore.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat

@Composable
fun ClipScoreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val finalEnabled = enabled && !isLoading
    val alpha = if (enabled) 1f else 0.4f

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(alpha),
        enabled = finalEnabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandPrimary,
            contentColor = BrandText,
            disabledContainerColor = BrandPrimary,
            disabledContentColor = BrandText,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                color = BrandText,
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

