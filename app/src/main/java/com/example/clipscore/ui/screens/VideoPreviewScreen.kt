package com.example.clipscore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clipscore.R
import com.example.clipscore.data.media.VideoMetadataExtractor
import com.example.clipscore.ui.components.ClipScoreButton
import com.example.clipscore.ui.components.MetadataCard
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandBorder
import com.example.clipscore.ui.theme.BrandError
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito
import com.example.clipscore.ui.viewmodel.VideoPickerViewModel
import com.example.clipscore.util.VideoFormatUtils

private data class Meta(val icon: String, val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPreviewScreen(
    viewModel: VideoPickerViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onPickDifferent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val metadata by viewModel.videoMetadata.collectAsStateWithLifecycle()
    val isTooLarge = metadata?.fileSizeBytes?.let {
        it > VideoMetadataExtractor.MAX_FILE_SIZE_BYTES
    } ?: false

    val items = metadata?.let {
        listOf(
            Meta("⏱", stringResource(R.string.metadata_duration), VideoFormatUtils.formatDuration(it.duration)),
            Meta("📐", stringResource(R.string.metadata_resolution), VideoFormatUtils.formatResolution(it.width, it.height)),
            Meta("💾", stringResource(R.string.metadata_file_size), VideoFormatUtils.formatFileSize(it.fileSizeBytes)),
            Meta("🎞", stringResource(R.string.metadata_frame_rate), VideoFormatUtils.formatFrameRate(it.frameRate)),
        )
    }.orEmpty()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.video_preview_title),
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandText,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = BrandText,
                        )
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
                    .clip(MaterialTheme.shapes.large)
                    .background(BrandSurface),
                contentAlignment = Alignment.Center,
            ) {
                val thumbnail = metadata?.thumbnailBitmap
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = BrandPrimary.copy(alpha = 0.85f),
                        modifier = Modifier.height(64.dp),
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.height(64.dp),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.video_preview_title),
                            fontFamily = Nunito,
                            color = BrandText.copy(alpha = 0.75f),
                        )
                    }
                }
            }

            if (isTooLarge) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandError.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, BrandError),
                ) {
                    Text(
                        text = stringResource(R.string.video_preview_size_warning),
                        modifier = Modifier.padding(14.dp),
                        fontFamily = Nunito,
                        color = BrandError,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onPickDifferent,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, BrandBorder),
            ) {
                Text(
                    text = stringResource(R.string.video_preview_pick_different),
                    fontFamily = Nunito,
                    color = BrandPrimary,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.video_preview_details),
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
                text = stringResource(R.string.video_preview_start_analysis),
                enabled = metadata != null && !isTooLarge,
                onClick = onContinue,
            )
        }
    }
}
