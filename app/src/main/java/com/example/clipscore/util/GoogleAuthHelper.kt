package com.example.clipscore.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthHelper(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserEmail(): String = auth.currentUser?.email ?: ""

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("171143139929-1acef8s23nrlag2dkei4rqcku12evql9.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("email address is already") == true ->
                    "Bu e-posta zaten kayıtlı. Giriş yapmayı deneyin."
                e.message?.contains("password is invalid") == true ||
                e.message?.contains("weak-password") == true ->
                    "Şifre en az 6 karakter olmalıdır."
                e.message?.contains("badly formatted") == true ->
                    "Geçerli bir e-posta adresi girin."
                else -> "Kayıt başarısız. Lütfen tekrar deneyin."
            }
            Result.failure(Exception(message))
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("password is invalid") == true ||
                e.message?.contains("no user record") == true ||
                e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                    "E-posta veya şifre hatalı."
                e.message?.contains("badly formatted") == true ->
                    "Geçerli bir e-posta adresi girin."
                e.message?.contains("too-many-requests") == true ||
                e.message?.contains("blocked") == true ->
                    "Çok fazla başarısız deneme. Lütfen birkaç dakika bekleyin."
                e.message?.contains("network") == true ->
                    "İnternet bağlantısı yok. Lütfen kontrol edin."
                else -> "Giriş başarısız. Lütfen tekrar deneyin."
            }
            Result.failure(Exception(message))
        }
    }

    fun logout() {
        auth.signOut()
        getGoogleSignInClient().signOut()
    }
}
