package com.example.clipscore.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

enum class SnackbarType {
    SUCCESS, ERROR, WARNING, INFO
}

data class SnackbarMessage(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

object SnackbarManager {
    val messages = MutableSharedFlow<SnackbarMessage>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun showError(message: String) {
        messages.tryEmit(SnackbarMessage(message, SnackbarType.ERROR))
    }

    fun showSuccess(message: String) {
        messages.tryEmit(SnackbarMessage(message, SnackbarType.SUCCESS))
    }

    fun showWarning(message: String) {
        messages.tryEmit(SnackbarMessage(message, SnackbarType.WARNING))
    }

    fun showInfo(message: String) {
        messages.tryEmit(SnackbarMessage(message, SnackbarType.INFO))
    }
}
