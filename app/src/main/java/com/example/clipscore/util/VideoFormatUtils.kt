package com.example.clipscore.util

import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object VideoFormatUtils {

    fun formatDuration(durationMs: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMs)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0L) return "—"
        val megabytes = bytes / (1024f * 1024f)
        return if (megabytes >= 1024f) {
            String.format(Locale.getDefault(), "%.1f GB", megabytes / 1024f)
        } else {
            String.format(Locale.getDefault(), "%.0f MB", megabytes)
        }
    }

    fun formatResolution(width: Int, height: Int): String {
        if (width <= 0 || height <= 0) return "—"
        val label = when {
            height >= 2160 -> "4K"
            height >= 1440 -> "1440p"
            height >= 1080 -> "1080p"
            height >= 720 -> "720p"
            height >= 480 -> "480p"
            else -> "${height}p"
        }
        return "$label (${width}x$height)"
    }

    fun formatFrameRate(frameRate: Float): String {
        if (frameRate <= 0f) return "—"
        return "${frameRate.roundToInt()} FPS"
    }

    fun formatMimeType(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "video/mp4" -> "MP4"
            "video/quicktime" -> "MOV"
            else -> mimeType.substringAfter("video/", mimeType).uppercase(Locale.getDefault())
        }
    }
}
