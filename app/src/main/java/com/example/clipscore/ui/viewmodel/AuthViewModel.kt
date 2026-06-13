package com.example.clipscore.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clipscore.util.GoogleAuthHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    val googleAuthHelper = GoogleAuthHelper(application)
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("E-posta ve şifre boş bırakılamaz.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            googleAuthHelper.loginWithEmail(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Hata oluştu.") }
        }
    }

    fun registerWithEmail(email: String, password: String, passwordConfirm: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("E-posta ve şifre boş bırakılamaz.")
            return
        }
        if (password != passwordConfirm) {
            _uiState.value = AuthUiState.Error("Şifreler eşleşmiyor.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Şifre en az 6 karakter olmalıdır.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            googleAuthHelper.registerWithEmail(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Hata oluştu.") }
        }
    }

    fun handleGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            googleAuthHelper.firebaseAuthWithGoogle(idToken)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error("Google girişi başarısız. Tekrar deneyin.") }
        }
    }

    fun logout() {
        googleAuthHelper.logout()
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
