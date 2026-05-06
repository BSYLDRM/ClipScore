package com.example.clipscore.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.theme.BrandPrimary
import com.example.clipscore.ui.theme.BrandText
import com.example.clipscore.ui.theme.Montserrat
import com.example.clipscore.ui.theme.Nunito

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(0.92f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
        alpha.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        delay(2000)
        onFinished()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "⚡",
            modifier = Modifier
                .size(44.dp)
                .alpha(alpha.value),
            color = BrandPrimary,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "ClipScore",
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                )
                .alpha(alpha.value),
            fontFamily = Montserrat,
            fontWeight = FontWeight.ExtraBold,
            color = BrandPrimary,
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Paylaşmadan önce skorunu öğren",
            modifier = Modifier.alpha(alpha.value),
            fontFamily = Nunito,
            color = BrandText.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

