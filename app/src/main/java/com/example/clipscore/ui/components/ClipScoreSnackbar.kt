package com.example.clipscore.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clipscore.util.SnackbarType

@Composable
fun ClipScoreSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState) { data ->
        val type = data.visuals.message.let {
            when {
                it.startsWith("SUCCESS:") -> SnackbarType.SUCCESS
                it.startsWith("ERROR:") -> SnackbarType.ERROR
                it.startsWith("WARNING:") -> SnackbarType.WARNING
                else -> SnackbarType.INFO
            }
        }
        val cleanMessage = data.visuals.message
            .removePrefix("SUCCESS:")
            .removePrefix("ERROR:")
            .removePrefix("WARNING:")
            .trim()

        val backgroundColor = when (type) {
            SnackbarType.SUCCESS -> Color(0xFF1A3A2A)
            SnackbarType.ERROR -> Color(0xFF3A1A1A)
            SnackbarType.WARNING -> Color(0xFF3A2A0A)
            SnackbarType.INFO -> Color(0xFF1A1A3A)
        }
        val borderColor = when (type) {
            SnackbarType.SUCCESS -> Color(0xFF22C55E)
            SnackbarType.ERROR -> Color(0xFFEF4444)
            SnackbarType.WARNING -> Color(0xFFF59E0B)
            SnackbarType.INFO -> Color(0xFF7C3AED)
        }
        val icon = when (type) {
            SnackbarType.SUCCESS -> "✅"
            SnackbarType.ERROR -> "❌"
            SnackbarType.WARNING -> "⚠️"
            SnackbarType.INFO -> "ℹ️"
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            border = BorderStroke(1.dp, borderColor),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = cleanMessage,
                    color = Color(0xFFF8FAFC),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                data.visuals.actionLabel?.let { label ->
                    TextButton(onClick = { data.performAction() }) {
                        Text(
                            text = label,
                            color = borderColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
