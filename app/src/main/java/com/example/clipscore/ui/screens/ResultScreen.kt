package com.example.clipscore.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clipscore.data.model.AnalyzeResponse
import com.example.clipscore.ui.components.ScoreCard
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandBorder
import com.example.clipscore.ui.theme.BrandError
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandSuccess
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.BrandWarning
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito
import com.example.clipscore.ui.viewmodel.AnalyzeUiState
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: AnalyzeViewModel,
    onCloseToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val response = (uiState as? AnalyzeUiState.Success)?.result

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("clipscore", text))
        scope.launch { snackbarHostState.showSnackbar("Kopyalandı!") }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sonuçlar ⚡",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandText,
                    )
                },
                actions = {
                    IconButton(onClick = onCloseToHome) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = BrandText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBg),
            )
        },
    ) { innerPadding ->
        if (response == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Henüz analiz sonucu yok",
                    fontFamily = Nunito,
                    color = BrandText.copy(alpha = 0.7f),
                )
            }
            return@Scaffold
        }

        val data = response

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                VibeHero(score = data.vibeScore)
            }

            item {
                SectionTitle(title = "Detaylı Analiz")
                Spacer(modifier = Modifier.height(10.dp))
                val hookProgress by animateFloatAsState(targetValue = data.hookScore / 100f, label = "hookProgress")
                val keywordProgress by animateFloatAsState(targetValue = data.keywordScore / 100f, label = "keywordProgress")
                val emotionProgress by animateFloatAsState(targetValue = data.emotionScore / 100f, label = "emotionProgress")
                val ctaProgress by animateFloatAsState(targetValue = data.ctaScore / 100f, label = "ctaProgress")

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ScoreCard(label = "Hook Gücü", score = data.hookScore, icon = "🎣", progress = hookProgress)
                    ScoreCard(label = "Anahtar Kelime", score = data.keywordScore, icon = "🔍", progress = keywordProgress)
                    ScoreCard(label = "Duygusal Etki", score = data.emotionScore, icon = "❤️", progress = emotionProgress)
                    ScoreCard(label = "CTA Kalitesi", score = data.ctaScore, icon = "📢", progress = ctaProgress)
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle(title = "Önerilen Hook'lar")
                Spacer(modifier = Modifier.height(10.dp))
            }

            itemsIndexed(data.hooks) { index, hook ->
                HookCard(
                    number = index + 1,
                    text = hook,
                    onCopy = { copyToClipboard(hook) },
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle(title = "SEO Açıklaması")
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    border = BorderStroke(1.dp, BrandBorder),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = data.description,
                            modifier = Modifier.weight(1f).padding(4.dp),
                            fontFamily = Nunito,
                            fontSize = 14.sp,
                            color = BrandText,
                        )
                        IconButton(onClick = { copyToClipboard(data.description) }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Kopyala",
                                tint = BrandText.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.resetState()
                        navController.navigate("title_input") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Yeni Analiz", fontFamily = Montserrat, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VibeHero(score: Int, modifier: Modifier = Modifier) {
    val animatedScore by animateIntAsState(targetValue = score, label = "vibeScore")
    val labelColor = when {
        score < 41 -> Color(0xFFEF4444) // Kırmızı
        score < 71 -> Color(0xFFFACC15) // Sarı
        else -> Color(0xFF22C55E) // Yeşil
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BrandSurface),
        border = BorderStroke(1.dp, BrandBorder),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VibeScore",
                fontFamily = Nunito,
                fontSize = 16.sp,
                color = BrandText.copy(alpha = 0.7f),
            )
            Text(
                text = animatedScore.toString(),
                fontFamily = Montserrat,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 84.sp,
                color = labelColor,
            )
            Text(
                text = when {
                    score < 41 -> "Geliştirilmeli"
                    score < 71 -> "Potansiyel Var"
                    else -> "Mükemmel"
                },
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = labelColor,
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        color = BrandText,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun HookCard(
    number: Int,
    text: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BrandSurface),
        border = BorderStroke(1.dp, BrandBorder),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontFamily = Nunito,
                fontSize = 15.sp,
                color = BrandText,
            )
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Kopyala",
                    tint = BrandText.copy(alpha = 0.8f),
                )
            }
        }
    }
}
