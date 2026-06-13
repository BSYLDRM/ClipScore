package com.example.clipscore.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.clipscore.util.SnackbarManager
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
    var isVideoAnalysisExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("clipscore", text))
        SnackbarManager.showSuccess("Kopyalandı!")
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
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
                val matchProgress by animateFloatAsState(targetValue = data.contentMatchScore / 100f, label = "matchProgress")

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ScoreCard(label = "Hook Gücü", score = data.hookScore, icon = "🎣", progress = hookProgress)
                    ScoreCard(label = "Anahtar Kelime", score = data.keywordScore, icon = "🔍", progress = keywordProgress)
                    ScoreCard(label = "Duygusal Etki", score = data.emotionScore, icon = "❤️", progress = emotionProgress)
                    ScoreCard(label = "CTA Kalitesi", score = data.ctaScore, icon = "📢", progress = ctaProgress)
                    if (data.contentMatchScore > 0) {
                        ScoreCard(label = "İçerik Uyumu", score = data.contentMatchScore, icon = "🎬", progress = matchProgress)
                    }
                }
            }

            if (data.videoContentDescription.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isVideoAnalysisExpanded = !isVideoAnalysisExpanded }
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isVideoAnalysisExpanded)
                                Color(0xFF1A1A3A)
                            else
                                Color(0xFF12122A)
                        ),
                        border = BorderStroke(
                            width = if (isVideoAnalysisExpanded) 2.dp else 1.dp,
                            color = Color(0xFF7C3AED)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isVideoAnalysisExpanded) 8.dp else 2.dp
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🤖",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "AI Video Analizi",
                                        color = Color(0xFF7C3AED),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                // Ok ikonu — açık/kapalı duruma göre döner
                                Icon(
                                    imageVector = if (isVideoAnalysisExpanded)
                                        Icons.Default.KeyboardArrowUp
                                    else
                                        Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Kapalıyken sadece ilk satırı göster
                            if (!isVideoAnalysisExpanded) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = data.videoContentDescription,
                                    color = Color(0xFF888888),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Devamını oku →",
                                    color = Color(0xFF7C3AED),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Açıkken tüm metni göster
                            if (isVideoAnalysisExpanded) {
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFF7C3AED).copy(alpha = 0.3f))
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = data.videoContentDescription,
                                    color = Color(0xFFCCCCCC),
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 20.sp,
                                    softWrap = true
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Kapat ↑",
                                    color = Color(0xFF7C3AED),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
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
