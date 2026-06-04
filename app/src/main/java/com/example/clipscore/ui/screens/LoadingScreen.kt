package com.example.clipscore.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito
import com.example.clipscore.ui.viewmodel.AnalyzeUiState
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    viewModel: AnalyzeViewModel,
    onCancel: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val subtitles = remember {
        listOf(
            "Hook'lar analiz ediliyor...",
            "Anahtar kelimeler taranıyor...",
            "Duygusal etki ölçülüyor...",
            "Skor hesaplanıyor...",
            "Açıklama hazırlanıyor...",
        )
    }
    var idx by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            idx = (idx + 1) % subtitles.size
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AnalyzeUiState.Success -> onFinished()
            is AnalyzeUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                onCancel()
            }
            else -> Unit
        }
    }

    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandBg)
                .padding(horizontal = 24.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .background(BrandPrimary.copy(alpha = 0.3f), shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(64.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "AI Analiz Ediyor...",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrandText,
                )
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedContent(
                    targetState = subtitles[idx],
                    label = "subtitle",
                ) { text ->
                    Text(
                        text = text,
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        color = BrandText.copy(alpha = 0.75f),
                    )
                }
            }

            TextButton(
                onClick = onCancel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp),
            ) {
                Text(
                    text = "İptal",
                    fontFamily = Nunito,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }
    }
}
