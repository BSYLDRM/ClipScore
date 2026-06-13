package com.example.clipscore.data.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.clipscore.data.model.VideoMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoMetadataExtractor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun extract(uri: Uri): Result<VideoMetadata> = withContext(Dispatchers.IO) {
        runCatching {
            withTimeout(METADATA_TIMEOUT_MS) {
                extractInternal(uri)
            }
        }
    }

    private fun extractInternal(uri: Uri): VideoMetadata {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)

            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?: throw IllegalStateException("Duration unavailable")

            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull()
                ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull()
                ?: 0

            val frameRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)
                ?.toFloatOrNull()
                ?: retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
                    ?.toFloatOrNull()
                    ?.let { frameCount ->
                        if (duration > 0) frameCount / (duration / 1000f) else 30f
                    }
                ?: 30f

            val mimeType = resolveMimeType(uri)
            val fileSizeBytes = resolveFileSize(uri)
            val thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

            if (width <= 0 || height <= 0 || duration <= 0) {
                throw IllegalStateException("Incomplete metadata")
            }

            return VideoMetadata(
                duration = duration,
                width = width,
                height = height,
                fileSizeBytes = fileSizeBytes,
                frameRate = frameRate,
                mimeType = mimeType,
                thumbnailBitmap = thumbnail,
            )
        } finally {
            retriever.release()
        }
    }

    private fun resolveMimeType(uri: Uri): String {
        context.contentResolver.getType(uri)?.let { return it }
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
            ?: "video/*"
    }

    private fun resolveFileSize(uri: Uri): Long {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
            if (descriptor.statSize >= 0) return descriptor.statSize
        }
        context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.SIZE), null, null, null)
            ?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (sizeIndex >= 0 && cursor.moveToFirst()) {
                    return cursor.getLong(sizeIndex)
                }
            }
        return 0L
    }

    fun isSupportedFormat(mimeType: String, uri: Uri): Boolean {
        val normalized = mimeType.lowercase()
        if (normalized in SUPPORTED_MIME_TYPES) return true
        val path = uri.lastPathSegment?.lowercase().orEmpty()
        return path.endsWith(".mp4") || path.endsWith(".mov")
    }

    companion object {
        private const val METADATA_TIMEOUT_MS = 3_000L
        val SUPPORTED_MIME_TYPES = setOf(
            "video/mp4",
            "video/quicktime",
        )
        const val MAX_FILE_SIZE_BYTES = 500L * 1024L * 1024L
    }
}
