package com.example.clipscore.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onCloseToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val gson = remember { Gson() }
    val response = remember {
        val prefs = context.getSharedPreferences(AnalyzeViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(AnalyzeViewModel.KEY_LAST_RESULT, null)
        json?.let { runCatching { gson.fromJson(it, AnalyzeResponse::class.java) }.getOrNull() }
    }

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
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle(title = "Hashtagler")
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(data.hashtags) { tag ->
                        AssistChip(
                            onClick = { copyToClipboard(tag) },
                            label = {
                                Text(
                                    text = tag,
                                    fontFamily = Nunito,
                                    fontSize = 13.sp,
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = BrandSurface,
                                labelColor = BrandText,
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                enabled = true,
                                borderColor = BrandPrimary,
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        val shareText =
                            "ClipScore Analizim: VibeScore ${data.vibeScore}/100 🚀 #ClipScore"
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                ) {
                    Text(text = "📤 Sonuçları Paylaş", fontFamily = Nunito, color = BrandText)
                }
            }
        }
    }
}

@Composable
private fun VibeHero(score: Int, modifier: Modifier = Modifier) {
    val animatedScore by animateIntAsState(targetValue = score, label = "vibeScore")
    val labelColor = when {
        score < 41 -> BrandError
        score < 71 -> BrandWarning
        else -> BrandSuccess
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(BrandPrimary, Color(0xFF4C1D95))),
                    shape = MaterialTheme.shapes.extraLarge,
                )
                .padding(vertical = 26.dp, horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = animatedScore.toString(),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 72.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "VibeScore",
                    fontFamily = Nunito,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.16f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                    shape = CircleShape,
                ) {
                    Text(
                        text = when {
                            score < 41 -> "KÖTÜ"
                            score < 71 -> "ORTA"
                            else -> "İYİ"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = labelColor,
                    )
                }
            }
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
