package com.example.clipscore.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clipscore.data.local.AnalysisDao
import com.example.clipscore.data.local.AnalysisEntity
import com.example.clipscore.data.model.AnalyzeRequest
import com.example.clipscore.data.model.Platform
import com.example.clipscore.data.model.VideoContext
import com.example.clipscore.util.VideoFormatUtils
import com.example.clipscore.data.model.AnalyzeResponse
import com.example.clipscore.data.repository.AnalyzeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnalyzeUiState {
    object Idle : AnalyzeUiState()
    object Loading : AnalyzeUiState()
    data class Success(val result: AnalyzeResponse) : AnalyzeUiState()
    data class Error(val message: String) : AnalyzeUiState()
}

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val repository: AnalyzeRepository,
    private val analysisDao: AnalysisDao,
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) : ViewModel() {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<AnalyzeUiState>(AnalyzeUiState.Idle)
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    private var videoContext: VideoContext? = null
    private var videoFrameBase64: String? = null

    fun setVideoContext(context: VideoContext?) {
        videoContext = context
    }

    fun setVideoFrame(base64: String?) {
        videoFrameBase64 = base64
    }

    fun hasVideoContext(): Boolean = videoContext != null

    fun analyze(title: String, description: String, platform: Platform, language: String) {
        viewModelScope.launch {
            _uiState.value = AnalyzeUiState.Loading
            try {
                val response = repository.analyze(
                    title = title,
                    description = description,
                    language = language,
                    platform = platform.displayName,
                    videoFrame = videoFrameBase64
                )
                _uiState.value = AnalyzeUiState.Success(response)
                
                // Save to Room
                saveToHistory(title, response, platform)
                
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("502") == true ||
                            e.message?.contains("503") == true ->
                        "Sunucu şu an meşgul, lütfen birkaç saniye bekleyip tekrar deneyin."
                    e.message?.contains("timeout") == true ||
                            e.message?.contains("timed out") == true ->
                        "Bağlantı zaman aşımına uğradı. Sunucu uyanıyor olabilir, tekrar deneyin."
                    e.message?.contains("Unable to resolve host") == true ->
                        "İnternet bağlantısı yok. Lütfen bağlantını kontrol et."
                    else -> "Bir hata oluştu: ${e.message}"
                }
                _uiState.value = AnalyzeUiState.Error(message)
            }
        }
    }

    private fun saveToHistory(title: String, response: AnalyzeResponse, platform: Platform) {
        viewModelScope.launch {
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val entity = AnalysisEntity(
                userEmail = currentUserEmail,
                platform = platform.displayName,
                title = title,
                vibeScore = response.vibeScore,
                hookScore = response.hookScore,
                keywordScore = response.keywordScore,
                emotionScore = response.emotionScore,
                ctaScore = response.ctaScore,
                hooks = response.hooks.joinToString("|||"),
                description = response.description,
                hashtags = response.hashtags.joinToString("|||")
            )
            analysisDao.insertAnalysis(entity)
            analysisDao.keepOnlyLast10(currentUserEmail)
        }
    }

    fun resetState() {
        _uiState.value = AnalyzeUiState.Idle
    }

    private fun buildEnrichedDescription(description: String, video: VideoContext?): String {
        if (video == null) return description
        return buildString {
            append(description.trim())
            append("\n\n[Video Metadata]\n")
            append("Süre: ${VideoFormatUtils.formatDuration(video.durationMs)}\n")
            append("Çözünürlük: ${VideoFormatUtils.formatResolution(video.width, video.height)}\n")
            append("Dosya boyutu: ${VideoFormatUtils.formatFileSize(video.fileSizeBytes)}\n")
            append("Kare hızı: ${VideoFormatUtils.formatFrameRate(video.frameRate)}\n")
            append("Format: ${VideoFormatUtils.formatMimeType(video.mimeType)}")
        }
    }

    companion object {
        const val PREFS_NAME = "clipscore"
        const val KEY_LAST_RESULT = "lastResult"
    }
}
