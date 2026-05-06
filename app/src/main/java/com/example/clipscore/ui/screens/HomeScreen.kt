package com.example.clipscore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clipscore.ui.components.ClipScoreButton
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandBorder
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.BrandWarning
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAnalyzeClick: () -> Unit,
    onOpenLastAnalysis: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ClipScore ⚡",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandText,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBg,
                    titleContentColor = BrandText,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(
                text = "Videounu analiz etmeye hazır mısın?",
                fontFamily = Nunito,
                color = BrandText,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(14.dp))
            ClipScoreButton(
                text = "\uD83C\uDFAC Videoyu Analiz Et",
                onClick = onAnalyzeClick,
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Son Analiz",
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                color = BrandText,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(12.dp))

            LastAnalysisCard(onClick = onOpenLastAnalysis)
        }
    }
}

@Composable
private fun LastAnalysisCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BrandSurface),
        border = BorderStroke(1.dp, BrandBorder),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bir günde 10kg verdim şok sonuçlar",
                        fontFamily = Nunito,
                        color = BrandText,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "2 saat önce",
                        fontFamily = Nunito,
                        color = BrandText.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                VibeBadge(score = 73)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(text = "YouTube Shorts")
            }
        }
    }
}

@Composable
private fun VibeBadge(score: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandWarning.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, BrandWarning.copy(alpha = 0.6f)),
    ) {
        Text(
            text = score.toString(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontFamily = Montserrat,
            fontWeight = FontWeight.ExtraBold,
            color = BrandWarning,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun Chip(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandBg),
        border = BorderStroke(1.dp, BrandBorder),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontFamily = Nunito,
            color = BrandText.copy(alpha = 0.85f),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

