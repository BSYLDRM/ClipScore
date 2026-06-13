package com.example.clipscore.data.model

enum class Platform(
    val displayName: String,
    val emoji: String,
    val maxTitleLength: Int,
    val maxDescLength: Int,
    val tips: String
) {
    TIKTOK(
        displayName = "TikTok",
        emoji = "🎵",
        maxTitleLength = 150,
        maxDescLength = 500,
        tips = "Kısa, enerjik ve trend odaklı içerik"
    ),
    INSTAGRAM_REELS(
        displayName = "Instagram Reels",
        emoji = "📸",
        maxTitleLength = 150,
        maxDescLength = 2200,
        tips = "Estetik, hikaye odaklı içerik"
    ),
    YOUTUBE_SHORTS(
        displayName = "YouTube Shorts",
        emoji = "▶️",
        maxTitleLength = 100,
        maxDescLength = 5000,
        tips = "SEO odaklı, anahtar kelime zengin içerik"
    ),
    YOUTUBE(
        displayName = "YouTube",
        emoji = "🎬",
        maxTitleLength = 100,
        maxDescLength = 5000,
        tips = "Detaylı, SEO optimizasyonlu içerik"
    ),
    TWITTER(
        displayName = "X (Twitter)",
        emoji = "🐦",
        maxTitleLength = 280,
        maxDescLength = 280,
        tips = "Kısa, dikkat çekici ve viral içerik"
    )
}
