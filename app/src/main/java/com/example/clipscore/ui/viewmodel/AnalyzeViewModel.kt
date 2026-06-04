package com.example.clipscore.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clipscore.data.model.AnalyzeRequest
import com.example.clipscore.data.model.AnalyzeResponse
import com.example.clipscore.data.repository.AnalyzeRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnalyzeUiState {
    data object Idle : AnalyzeUiState()
    data object Loading : AnalyzeUiState()
    data class Success(val response: AnalyzeResponse) : AnalyzeUiState()
    data class Error(val message: String) : AnalyzeUiState()
}

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val repository: AnalyzeRepository,
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) : ViewModel() {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<AnalyzeUiState>(AnalyzeUiState.Idle)
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    fun analyze(title: String, description: String, language: String) {
        viewModelScope.launch {
            _uiState.value = AnalyzeUiState.Loading
            repository.analyze(AnalyzeRequest(title, description, language))
                .onSuccess { response ->
                    prefs.edit().putString(KEY_LAST_RESULT, gson.toJson(response)).apply()
                    _uiState.value = AnalyzeUiState.Success(response)
                }
                .onFailure { e ->
                    _uiState.value = AnalyzeUiState.Error(
                        e.message?.takeIf { it.isNotBlank() } ?: "Analiz başarısız oldu",
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = AnalyzeUiState.Idle
    }

    companion object {
        const val PREFS_NAME = "clipscore"
        const val KEY_LAST_RESULT = "lastResult"
    }
}
