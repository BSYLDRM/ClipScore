package com.example.clipscore.ui.screens

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.example.clipscore.ui.components.ClipScoreButton
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
import kotlinx.coroutines.launch

val mockVibeScore = 73
val mockHookScore = 81
val mockKeywordScore = 68
val mockEmotionScore = 79
val mockCtaScore = 64
val mockHooks = listOf(
    "Doktorum bile inanamadı — 1 günde 10 kilo nasıl verdim?",
    "Bu diyeti kimse size söylemez... ben söylüyorum",
    "Sabah 60kg, akşam 50kg — imkansız mı? İzle de gör",
)
val mockDescription =
    "Bu videoda sizinle 1 günde 10 kilo vermenin sırlarını paylaşıyorum. Yıllarca denediğim ve işe yarayan bu yöntemi artık herkesle paylaşma zamanı geldi. Sağlıklı beslenme ve doğru egzersiz kombinasyonu ile inanılmaz sonuçlar elde ettim..."
val mockHashtags = listOf("#diyet", "#kilo", "#sağlık", "#fitness", "#youtube", "#shorts")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(
    onCloseToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analiz Sonucu ⚡",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandText,
                    )
                },
                actions = {
                    IconButton(onClick = onCloseToHome) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = BrandText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBg),
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BrandBg,
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                    ClipScoreButton(
                        text = "\uD83D\uDCE4 Sonuçları Paylaş",
                        onClick = {
                            val shareText = "ClipScore Analizim: VibeScore $mockVibeScore/100 \uD83D\uDE80 #ClipScore"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                VibeHero(score = mockVibeScore)
            }

            item {
                SectionTitle(title = "Detaylı Analiz")
                Spacer(modifier = Modifier.height(10.dp))
                val hookProgress by animateFloatAsState(targetValue = mockHookScore / 100f, label = "hookProgress")
                val keywordProgress by animateFloatAsState(targetValue = mockKeywordScore / 100f, label = "keywordProgress")
                val emotionProgress by animateFloatAsState(targetValue = mockEmotionScore / 100f, label = "emotionProgress")
                val ctaProgress by animateFloatAsState(targetValue = mockCtaScore / 100f, label = "ctaProgress")

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ScoreCard(label = "Hook Gücü", score = mockHookScore, icon = "\uD83C\uDFA3", progress = hookProgress)
                    ScoreCard(label = "Anahtar Kelime", score = mockKeywordScore, icon = "\uD83D\uDD0D", progress = keywordProgress)
                    ScoreCard(label = "Duygusal Etki", score = mockEmotionScore, icon = "❤️", progress = emotionProgress)
                    ScoreCard(label = "CTA Kalitesi", score = mockCtaScore, icon = "\uD83D\uDCE2", progress = ctaProgress)
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle(
                    title = "Hook Önerileri",
                    subtitle = "En dikkat çekici başlangıcı seç",
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            itemsIndexed(mockHooks) { index, hook ->
                HookCard(
                    number = index + 1,
                    text = hook,
                    onCopy = {
                        clipboard.setText(AnnotatedString(hook))
                        scope.launch { snackbarHostState.showSnackbar("Kopyalandı!") }
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Backend bağlandığında aktif olacak") } },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandText.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "\uD83D\uDD04 Yeniden Üret",
                        fontFamily = Nunito,
                        color = BrandText,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle(title = "SEO Açıklaması")
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    border = BorderStroke(1.dp, BrandBorder),
                ) {
                    Text(
                        text = mockDescription,
                        modifier = Modifier.padding(16.dp),
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        color = BrandText,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(mockDescription))
                        scope.launch { snackbarHostState.showSnackbar("Kopyalandı!") }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                ) {
                    Text(text = "Kopyala", fontFamily = Nunito, color = BrandText)
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionTitle(title = "Hashtagler")
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    mockHashtags.forEach { tag ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BrandBg),
                            border = BorderStroke(1.dp, BrandPrimary),
                            shape = CircleShape,
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontFamily = Nunito,
                                color = BrandText,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun VibeHero(score: Int, modifier: Modifier = Modifier) {
    val animatedScore by animateIntAsState(targetValue = score, label = "vibeScore")
    val labelColor = when {
        score < 40 -> BrandError
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
                            score < 40 -> "KÖTÜ"
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
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Bold,
            color = BrandText,
            style = MaterialTheme.typography.titleLarge,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontFamily = Nunito,
                color = BrandText.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
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
                    contentDescription = null,
                    tint = BrandText.copy(alpha = 0.8f),
                )
            }
        }
    }
}

