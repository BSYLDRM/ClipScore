package com.example.clipscore.data.model

data class AnalyzeResponse(
    val vibeScore: Int,
    val hookScore: Int,
    val keywordScore: Int,
    val emotionScore: Int,
    val ctaScore: Int,
    val contentMatchScore: Int = 0,
    val videoContentDescription: String = "",
    val hooks: List<String>,
    val description: String,
    val hashtags: List<String>,
)
