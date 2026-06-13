package com.example.clipscore.data.model

import android.graphics.Bitmap

data class VideoMetadata(
    val duration: Long,
    val width: Int,
    val height: Int,
    val fileSizeBytes: Long,
    val frameRate: Float,
    val mimeType: String,
    val thumbnailBitmap: Bitmap?,
)

data class VideoContext(
    val uri: String,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val fileSizeBytes: Long,
    val frameRate: Float,
    val mimeType: String,
)
