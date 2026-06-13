package com.example.clipscore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.clipscore.R
import com.example.clipscore.data.model.Platform
import com.example.clipscore.ui.theme.*
import com.example.clipscore.ui.viewmodel.AnalyzeUiState
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel
import com.example.clipscore.util.SnackbarManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleInputScreen(
    viewModel: AnalyzeViewModel,
    navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var titleText by rememberSaveable { mutableStateOf("") }
    var descriptionText by rememberSaveable { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf(Platform.TIKTOK) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is AnalyzeUiState.Success -> {
                navController.navigate("result") {
                    launchSingleTop = true
                }
            }
            is AnalyzeUiState.Error -> {
                SnackbarManager.showError((uiState as AnalyzeUiState.Error).message)
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        containerColor = BrandBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Başlık & Açıklama",
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
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LinearProgressIndicator(
                progress = { 2f / 3f },
                color = BrandPrimary,
                trackColor = BrandText.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
            )

            if (viewModel.hasVideoContext()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandSurface, shape = MaterialTheme.shapes.large)
                        .padding(12.dp),
                ) {
                    Text(
                        text = "🎬 ${stringResource(R.string.title_input_video_attached)}",
                        fontFamily = Nunito,
                        color = BrandPrimary,
                        fontSize = 13.sp,
                    )
                }
            }

            Text(
                text = "Platform Seç",
                color = Color(0xFFF8FAFC),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(Platform.entries.toTypedArray()) { platform ->
                    val isSelected = selectedPlatform == platform
                    Card(
                        modifier = Modifier
                            .clickable { selectedPlatform = platform },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                Color(0xFF7C3AED)
                            else
                                Color(0xFF1A1A1A)
                        ),
                        border = if (isSelected)
                            BorderStroke(2.dp, Color(0xFF7C3AED))
                        else
                            BorderStroke(1.dp, Color(0xFF333333)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = platform.emoji,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = platform.displayName,
                                color = Color(0xFFF8FAFC),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x337C3AED)
                ),
                border = BorderStroke(1.dp, Color(0x667C3AED)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = selectedPlatform.tips,
                        color = Color(0xFFCCCCCC),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LabeledField(
                label = "Video Başlığı",
                value = titleText,
                onValueChange = { if (it.length <= selectedPlatform.maxTitleLength) titleText = it },
                placeholder = "örn: Bir günde 10kg verdim!",
                maxLines = 2,
                counter = "${titleText.length}/${selectedPlatform.maxTitleLength}",
                imeAction = ImeAction.Next,
            )

            LabeledField(
                label = "Açıklama",
                value = descriptionText,
                onValueChange = { if (it.length <= selectedPlatform.maxDescLength) descriptionText = it },
                placeholder = "Videonuzun içeriğini kısaca anlatın...",
                maxLines = 5,
                counter = "${descriptionText.length}/${selectedPlatform.maxDescLength}",
                imeAction = ImeAction.Default,
            )

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    val language = if (Locale.getDefault().language == "tr") "tr" else "en"
                    viewModel.analyze(
                        title = titleText,
                        description = descriptionText,
                        platform = selectedPlatform,
                        language = language
                    )
                },
                enabled = uiState !is AnalyzeUiState.Loading && titleText.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                if (uiState is AnalyzeUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Analiz ediliyor... (ilk açılışta 30-60 sn sürebilir ⏳)")
                } else {
                    Text("Skoru Hesapla")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLines: Int,
    counter: String,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                fontFamily = Nunito,
                fontSize = 13.sp,
                color = BrandText.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = counter,
                fontFamily = Nunito,
                fontSize = 12.sp,
                color = BrandText.copy(alpha = 0.55f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = placeholder, fontFamily = Nunito, color = BrandText.copy(alpha = 0.4f))
            },
            maxLines = maxLines,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction,
            ),
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BrandSurface,
                unfocusedContainerColor = BrandSurface,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder,
                cursorColor = BrandPrimary,
                focusedTextColor = BrandText,
                unfocusedTextColor = BrandText,
            ),
        )
    }
}
