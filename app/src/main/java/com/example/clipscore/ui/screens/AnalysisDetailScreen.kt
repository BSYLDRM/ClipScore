package com.example.clipscore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clipscore.data.model.Platform
import com.example.clipscore.data.local.AnalysisEntity
import com.example.clipscore.data.local.ClipScoreDatabase
import com.example.clipscore.util.SnackbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnalysisDetailScreen(
    navController: NavController,
    analysisId: Int
) {
    val context = LocalContext.current
    val database = ClipScoreDatabase.getDatabase(context)
    
    var analysis by remember { mutableStateOf<AnalysisEntity?>(null) }
    
    LaunchedEffect(analysisId) {
        withContext(Dispatchers.IO) {
            analysis = database.analysisDao().getAnalysisById(analysisId)
        }
    }
    
    analysis?.let { data ->
        val hooks = data.hooks.split("|||").filter { it.isNotBlank() }
        val hashtags = data.hashtags.split("|||").filter { it.isNotBlank() }
        val scoreColor = when {
            data.vibeScore >= 71 -> Color(0xFF22C55E)
            data.vibeScore >= 41 -> Color(0xFFF59E0B)
            else -> Color(0xFFEF4444)
        }
        val clipboardManager = LocalClipboardManager.current
        val scrollState = rememberScrollState()
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Analiz Detayı", color = Color(0xFFF8FAFC)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFFF8FAFC)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0A0A0A)
                    )
                )
            },
            containerColor = Color(0xFF0A0A0A)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Başlık
                Text(
                    text = data.title,
                    color = Color(0xFFF8FAFC),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    val platformEmoji = Platform.entries.toTypedArray()
                        .find { it.displayName == data.platform }?.emoji ?: "📱"
                    Text(
                        text = "$platformEmoji ${data.platform}",
                        color = Color(0xFF888888),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Vibe Score
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${data.vibeScore}",
                            color = scoreColor,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Viral Skor",
                            color = Color(0xFF888888),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                // Skor kartları 2x2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScoreCardDetail("🎣 Hook", data.hookScore, Modifier.weight(1f))
                    ScoreCardDetail("🔍 Anahtar Kelime", data.keywordScore, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScoreCardDetail("❤️ Duygu", data.emotionScore, Modifier.weight(1f))
                    ScoreCardDetail("📢 CTA", data.ctaScore, Modifier.weight(1f))
                }

                if (data.contentMatchScore > 0) {
                    Spacer(Modifier.height(8.dp))
                    ScoreCardDetail("🎬 İçerik Uyumu", data.contentMatchScore, Modifier.fillMaxWidth())
                }

                if (data.videoContentDescription.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2A)),
                        border = BorderStroke(1.dp, Color(0xFF7C3AED))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🤖 AI Video Analizi",
                                color = Color(0xFF7C3AED),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = data.videoContentDescription,
                                color = Color(0xFFCCCCCC),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Hook'lar
                Text(
                    text = "Önerilen Hook'lar",
                    color = Color(0xFFF8FAFC),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                hooks.forEachIndexed { index, hook ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFF7C3AED), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = hook,
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(hook))
                                    SnackbarManager.showSuccess("Kopyalandı!")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Kopyala",
                                    tint = Color(0xFF7C3AED)
                                )
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // SEO Açıklama
                Text(
                    text = "SEO Açıklaması",
                    color = Color(0xFFF8FAFC),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = data.description,
                            color = Color(0xFFCCCCCC)
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(data.description))
                                SnackbarManager.showSuccess("Kopyalandı!")
                            }
                        ) {
                            Text("Kopyala", color = Color(0xFF7C3AED))
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Hashtag'ler
                Text(
                    text = "Hashtag'ler",
                    color = Color(0xFFF8FAFC),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hashtags.forEach { tag ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0x337C3AED)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Color(0xFF7C3AED),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Geri dön butonu
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C3AED)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Geri Dön", color = Color.White, fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(16.dp))
            }
        }
    } ?: run {
        // Yükleniyor
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF7C3AED))
        }
    }
}

@Composable
private fun ScoreCardDetail(label: String, score: Int, modifier: Modifier = Modifier) {
    val color = when {
        score >= 71 -> Color(0xFF22C55E)
        score >= 41 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score",
                color = color,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color(0xFF888888),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
