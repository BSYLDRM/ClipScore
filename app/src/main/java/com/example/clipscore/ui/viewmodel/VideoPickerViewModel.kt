package com.example.clipscore.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clipscore.R
import com.example.clipscore.data.media.VideoMetadataExtractor
import com.example.clipscore.data.model.VideoContext
import com.example.clipscore.data.model.VideoMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPickerViewModel @Inject constructor(
    private val metadataExtractor: VideoMetadataExtractor,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _selectedVideoUri = MutableStateFlow<Uri?>(null)
    val selectedVideoUri: StateFlow<Uri?> = _selectedVideoUri.asStateFlow()

    private val _videoMetadata = MutableStateFlow<VideoMetadata?>(null)
    val videoMetadata: StateFlow<VideoMetadata?> = _videoMetadata.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showSettingsPrompt = MutableStateFlow(false)
    val showSettingsPrompt: StateFlow<Boolean> = _showSettingsPrompt.asStateFlow()

    val isFileTooLarge: Boolean
        get() = (_videoMetadata.value?.fileSizeBytes ?: 0L) > VideoMetadataExtractor.MAX_FILE_SIZE_BYTES

    fun pickVideo() {
        clearError()
    }

    fun onVideoSelected(uri: Uri) {
        clearError()
        _selectedVideoUri.value = uri
        extractMetadata(uri)
    }

    fun extractMetadata(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val mimeType = context.contentResolver.getType(uri).orEmpty()
            if (!metadataExtractor.isSupportedFormat(mimeType, uri)) {
                _isLoading.value = false
                _selectedVideoUri.value = null
                _videoMetadata.value = null
                _errorMessage.value = context.getString(R.string.error_unsupported_format)
                return@launch
            }

            metadataExtractor.extract(uri)
                .onSuccess { metadata ->
                    if (metadata.fileSizeBytes > VideoMetadataExtractor.MAX_FILE_SIZE_BYTES) {
                        _videoMetadata.value = metadata
                        _errorMessage.value = context.getString(R.string.error_file_too_large)
                    } else {
                        _videoMetadata.value = metadata
                        _errorMessage.value = null
                    }
                }
                .onFailure {
                    _selectedVideoUri.value = null
                    _videoMetadata.value = null
                    _errorMessage.value = context.getString(R.string.error_metadata_read_failed)
                }

            _isLoading.value = false
        }
    }

    fun onPermissionDenied() {
        _errorMessage.value = context.getString(R.string.error_permission_required)
        _showSettingsPrompt.value = true
    }

    fun clearError() {
        _errorMessage.value = null
        _showSettingsPrompt.value = false
    }

    fun clearSelection() {
        _selectedVideoUri.value = null
        _videoMetadata.value = null
        _errorMessage.value = null
        _showSettingsPrompt.value = false
        _isLoading.value = false
    }

    fun getVideoContext(): VideoContext? {
        val uri = _selectedVideoUri.value ?: return null
        val metadata = _videoMetadata.value ?: return null
        return VideoContext(
            uri = uri.toString(),
            durationMs = metadata.duration,
            width = metadata.width,
            height = metadata.height,
            fileSizeBytes = metadata.fileSizeBytes,
            frameRate = metadata.frameRate,
            mimeType = metadata.mimeType,
        )
    }
}
