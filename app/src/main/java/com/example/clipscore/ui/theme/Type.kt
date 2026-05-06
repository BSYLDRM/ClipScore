package com.example.clipscore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.clipscore.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
)

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        lineHeight = 48.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
    ),
)