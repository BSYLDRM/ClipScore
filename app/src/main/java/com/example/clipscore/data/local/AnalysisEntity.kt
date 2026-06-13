package com.example.clipscore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyses")
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String = "",
    val platform: String = "TikTok",
    val title: String,
    val vibeScore: Int,
    val hookScore: Int,
    val keywordScore: Int,
    val emotionScore: Int,
    val ctaScore: Int,
    val hooks: String,        // JSON string olarak sakla
    val description: String,
    val hashtags: String,     // JSON string olarak sakla
    val contentMatchScore: Int = 0,
    val videoContentDescription: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
