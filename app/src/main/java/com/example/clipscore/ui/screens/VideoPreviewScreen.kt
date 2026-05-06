package com.example.clipscore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clipscore.ui.components.ClipScoreButton
import com.example.clipscore.ui.components.MetadataCard
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito

private data class Meta(val icon: String, val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPreviewScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        Meta("⏱", "Süre", "0:47"),
        Meta("📐", "Çözünürlük", "1080p"),
        Meta("💾", "Boyut", "84 MB"),
        Meta("🎞", "Kare Hızı", "60 FPS"),
        Meta("📁", "Format", "MP4"),
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Video Önizleme",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandText,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = BrandText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBg),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(BrandSurface, shape = MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.height(64.dp),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Video Seçildi",
                        fontFamily = Nunito,
                        color = BrandText.copy(alpha = 0.75f),
                    )
                }
            }

            TextButton(onClick = { /* mock */ }) {
                Text(
                    text = "Video Değiştir",
                    fontFamily = Nunito,
                    color = BrandPrimary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Video Detayları",
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                color = BrandText,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = true),
            ) {
                items(items) { meta ->
                    MetadataCard(
                        icon = meta.icon,
                        label = meta.label,
                        value = meta.value,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            ClipScoreButton(
                text = "Devam Et →",
                onClick = onContinue,
            )
        }
    }
}

