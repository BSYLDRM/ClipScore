package com.example.clipscore.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.clipscore.ui.components.ClipScoreButton
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandBorder
import com.example.clipscore.ui.theme.BrandError
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandSurface
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito
import com.example.clipscore.ui.viewmodel.AuthUiState
import com.example.clipscore.ui.viewmodel.AuthViewModel
import com.example.clipscore.util.SnackbarManager
import com.example.clipscore.util.SnackbarType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthed: (String) -> Unit,
    viewModel: AuthViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.resetState()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                val email = viewModel.googleAuthHelper.getCurrentUserEmail()
                onAuthed(email)
                SnackbarManager.showSuccess("Giriş başarılı! Hoş geldin 👋")
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthUiState.Error -> {
                SnackbarManager.showError((uiState as AuthUiState.Error).message)
            }
            else -> {}
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { viewModel.handleGoogleSignInResult(it) }
        } catch (e: ApiException) {
            // sessiz hata
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBg)
            .imePadding(),
        containerColor = BrandBg,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 28.dp),
        ) {
            LogoHeader()
            Spacer(modifier = Modifier.height(24.dp))

            AuthTabs(
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
            )
            Spacer(modifier = Modifier.height(18.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                when (page) {
                    0 -> LoginTab(
                        viewModel = viewModel,
                        onGoogleSignInClick = {
                            val signInIntent = viewModel.googleAuthHelper
                                .getGoogleSignInClient()
                                .signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    )
                    else -> SignupTab(
                        viewModel = viewModel,
                        onGoogleSignInClick = {
                            val signInIntent = viewModel.googleAuthHelper
                                .getGoogleSignInClient()
                                .signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "⚡",
                color = BrandPrimary,
                fontSize = 36.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "ClipScore",
                fontFamily = Montserrat,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = BrandText,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Paylaşmadan önce skorunu öğren",
            fontFamily = Nunito,
            fontSize = 13.sp,
            color = BrandText.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun AuthTabs(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labels = listOf("Giriş Yap", "Kayıt Ol")
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = BrandSurface,
        contentColor = BrandText,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = tabPositions[selectedIndex].left)
            )
        },
        divider = {},
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Tab(
                selected = selected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = label,
                        fontFamily = if (selected) Montserrat else Nunito,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 14.sp,
                        color = if (selected) BrandText else BrandText.copy(alpha = 0.5f),
                    )
                },
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(BrandBorder),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(BrandPrimary)
                .align(if (selectedIndex == 0) Alignment.CenterStart else Alignment.CenterEnd),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginTab(
    viewModel: AuthViewModel,
    onGoogleSignInClick: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is AuthUiState.Loading

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BrandOutlinedField(
            value = email,
            onValueChange = { email = it },
            label = "E-posta",
            placeholder = "ornek@email.com",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            enabled = !isLoading
        )

        BrandOutlinedField(
            value = password,
            onValueChange = { password = it },
            label = "Şifre",
            placeholder = "••••••••",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { }),
            enabled = !isLoading
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
                onClick = { },
                enabled = !isLoading
            ) {
                Text(
                    text = "Şifremi Unuttum",
                    fontFamily = Nunito,
                    fontSize = 13.sp,
                    color = BrandPrimary,
                )
            }
        }

        ClipScoreButton(
            text = if (isLoading) "Lütfen bekleyin..." else "Giriş Yap",
            isLoading = isLoading,
            enabled = !isLoading,
            onClick = {
                viewModel.loginWithEmail(email, password)
            },
        )

        OrDivider()

        SocialButtons(
            onGoogleSignInClick = onGoogleSignInClick,
            enabled = !isLoading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignupTab(
    viewModel: AuthViewModel,
    onGoogleSignInClick: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is AuthUiState.Loading

    val passwordsMismatch = password2.isNotBlank() && password != password2

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BrandOutlinedField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Ad Soyad",
            placeholder = "Ahmet Yılmaz",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            enabled = !isLoading
        )

        BrandOutlinedField(
            value = email,
            onValueChange = { email = it },
            label = "E-posta",
            placeholder = "ornek@email.com",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            enabled = !isLoading
        )

        BrandOutlinedField(
            value = password,
            onValueChange = { password = it },
            label = "Şifre",
            placeholder = "••••••••",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            enabled = !isLoading
        )

        BrandOutlinedField(
            value = password2,
            onValueChange = { password2 = it },
            label = "Şifre Tekrar",
            placeholder = "••••••••",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordsMismatch,
            supportingText = if (passwordsMismatch) "Şifreler eşleşmiyor" else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            enabled = !isLoading
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(10.dp))
            TermsText(
                onTermsClick = { },
            )
        }

        ClipScoreButton(
            text = if (isLoading) "Lütfen bekleyin..." else "Hesap Oluştur",
            enabled = termsAccepted && !isLoading,
            isLoading = isLoading,
            onClick = {
                viewModel.registerWithEmail(email, password, password2)
            },
        )

        OrDivider()

        SocialButtons(
            onGoogleSignInClick = onGoogleSignInClick,
            enabled = !isLoading
        )
    }
}

@Composable
private fun TermsText(
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val annotated = remember {
        buildAnnotatedString {
            val prefix = "Kullanım "
            val link = "koşullarını"
            val suffix = " kabul ediyorum"

            append(prefix)
            val start = length
            append(link)
            val end = length
            append(suffix)

            addStyle(
                style = SpanStyle(color = BrandPrimary, textDecoration = TextDecoration.Underline),
                start = start,
                end = end,
            )
            addStringAnnotation(tag = "terms", annotation = "terms", start = start, end = end)
        }
    }

    ClickableText(
        text = annotated,
        modifier = modifier,
        style = androidx.compose.ui.text.TextStyle(
            fontFamily = Nunito,
            fontSize = 13.sp,
            color = BrandText,
        ),
    ) { offset ->
        val hit = annotated.getStringAnnotations(tag = "terms", start = offset, end = offset).firstOrNull()
        if (hit != null) onTermsClick()
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = BrandBorder)
        Text(
            text = "veya",
            modifier = Modifier.padding(horizontal = 12.dp),
            fontFamily = Nunito,
            fontSize = 12.sp,
            color = BrandText.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = BrandBorder)
    }
}

@Composable
private fun SocialButtons(
    onGoogleSignInClick: () -> Unit,
    enabled: Boolean = true
) {
    val scope = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = onGoogleSignInClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandBorder),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "\uD83C\uDDEC", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Google ile devam et",
                    fontFamily = Nunito,
                    fontSize = 14.sp,
                    color = BrandText,
                )
            }
        }

        OutlinedButton(
            onClick = { SnackbarManager.showInfo("Apple girişi aktif değil (mock)") },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandBorder),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Apple ile devam et",
                    fontFamily = Nunito,
                    fontSize = 14.sp,
                    color = BrandText,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrandOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()

    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = Nunito,
            fontSize = 13.sp,
            color = BrandText.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (focused) 10.dp else 0.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = BrandPrimary.copy(alpha = 0.35f),
                    spotColor = BrandPrimary.copy(alpha = 0.35f),
                ),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Nunito,
                    color = BrandText.copy(alpha = 0.4f),
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides BrandText.copy(alpha = 0.5f),
                    ) {
                        it()
                    }
                }
            },
            trailingIcon = trailingIcon,
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interaction,
            isError = isError,
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        color = BrandError,
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BrandSurface,
                unfocusedContainerColor = BrandSurface,
                focusedBorderColor = if (isError) BrandError else BrandPrimary,
                unfocusedBorderColor = if (isError) BrandError else BrandBorder,
                focusedTextColor = BrandText,
                unfocusedTextColor = BrandText,
                cursorColor = BrandPrimary,
                focusedLeadingIconColor = BrandText.copy(alpha = 0.6f),
                unfocusedLeadingIconColor = BrandText.copy(alpha = 0.5f),
                focusedTrailingIconColor = BrandText.copy(alpha = 0.7f),
                unfocusedTrailingIconColor = BrandText.copy(alpha = 0.5f),
            ),
        )
    }
}
