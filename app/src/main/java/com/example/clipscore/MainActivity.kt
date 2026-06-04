package com.example.clipscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import com.example.clipscore.ui.theme.ClipScoreTheme
import com.example.clipscore.ui.theme.BrandBg
import com.example.clipscore.ui.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClipScoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BrandBg,
                ) {
                    NavGraph()
                }
            }
        }
    }
}