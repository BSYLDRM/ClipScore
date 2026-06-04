package com.example.clipscore.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clipscore.ui.components.ClipScoreButton
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandBorder
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito
import com.example.clipscore.ui.viewmodel.AnalyzeUiState
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleInputScreen(
    viewModel: AnalyzeViewModel,
    onBack: () -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedLanguage by rememberSaveable { mutableStateOf("tr") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AnalyzeUiState.Success -> onCalculate()
            is AnalyzeUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    val isLoading = uiState is AnalyzeUiState.Loading

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                .verticalScroll(rememberScrollState()),
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

            LabeledField(
                label = "Video Başlığı",
                value = title,
                onValueChange = { if (it.length <= 200) title = it },
                placeholder = "örn: Bir günde 10kg verdim!",
                maxLines = 2,
                counter = "${title.length}/200",
                imeAction = ImeAction.Next,
            )

            LabeledField(
                label = "Açıklama",
                value = description,
                onValueChange = { if (it.length <= 1000) description = it },
                placeholder = "Videonuzun içeriğini kısaca anlatın...",
                maxLines = 5,
                counter = "${description.length}/1000",
                imeAction = ImeAction.Default,
            )

            Text(
                text = "Dil",
                fontFamily = Nunito,
                fontSize = 13.sp,
                color = BrandText.copy(alpha = 0.7f),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                LanguageToggle(
                    text = "\uD83C\uDDF9\uD83C\uDDF7 Türkçe",
                    selected = selectedLanguage == "tr",
                    onClick = { selectedLanguage = "tr" },
                    modifier = Modifier.weight(1f),
                )
                LanguageToggle(
                    text = "\uD83C\uDDEC\uD83C\uDDE7 English",
                    selected = selectedLanguage == "en",
                    onClick = { selectedLanguage = "en" },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            ClipScoreButton(
                text = "\uD83D\uDE80 Skoru Hesapla",
                enabled = title.isNotBlank() && description.isNotBlank(),
                isLoading = isLoading,
                onClick = {
                    viewModel.analyze(title, description, selectedLanguage)
                },
            )
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

@Composable
private fun LanguageToggle(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = BrandText,
            ),
        ) {
            Text(text = text, fontFamily = Nunito, fontWeight = FontWeight.SemiBold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            border = BorderStroke(1.dp, BrandBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandText),
        ) {
            Text(text = text, fontFamily = Nunito)
        }
    }
}
