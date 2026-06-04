package com.example.clipscore.data.model

data class AnalyzeRequest(
    val title: String,
    val description: String,
    val language: String = "tr",
)
