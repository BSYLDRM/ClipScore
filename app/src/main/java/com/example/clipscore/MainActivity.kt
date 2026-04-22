package com.example.clipscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.clipscore.ui.theme.ClipScoreTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClipScoreTheme {
                LaunchScreen()
            }
        }
    }
}

data class MessageResponse(
    val message: String
)

interface BackendApi {
    @GET("api/message")
    suspend fun getMessage(): MessageResponse
}

private val backendApi: BackendApi by lazy {
    Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BackendApi::class.java)
}

@Composable
fun LaunchScreen(modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("Yükleniyor...") }

    LaunchedEffect(Unit) {
        message = try {
            backendApi.getMessage().message
        } catch (_: Exception) {
            "Bağlantı hatası"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LaunchScreenPreview() {
    ClipScoreTheme {
        LaunchScreen()
    }
}