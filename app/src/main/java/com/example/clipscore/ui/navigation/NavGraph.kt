package com.example.clipscore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clipscore.ui.screens.AuthScreen
import com.example.clipscore.ui.screens.HomeScreen
import com.example.clipscore.ui.screens.LoadingScreen
import com.example.clipscore.ui.screens.ResultScreen
import com.example.clipscore.ui.screens.SplashScreen
import com.example.clipscore.ui.screens.TitleInputScreen
import com.example.clipscore.ui.screens.VideoPreviewScreen
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel

object Routes {
    const val Splash = "splash"
    const val Auth = "auth"
    const val Home = "home"
    const val Preview = "preview"
    const val Input = "input"
    const val Loading = "loading"
    const val Result = "result"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash,
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Auth) {
            AuthScreen(
                onAuthed = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Home) {
            HomeScreen(
                onAnalyzeClick = { navController.navigate(Routes.Preview) },
                onOpenLastAnalysis = { navController.navigate(Routes.Result) },
            )
        }
        composable(Routes.Preview) {
            VideoPreviewScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.Input) },
            )
        }
        composable(Routes.Input) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Routes.Home)
            }
            val viewModel: AnalyzeViewModel = hiltViewModel(parentEntry)
            TitleInputScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCalculate = { navController.navigate(Routes.Loading) },
            )
        }
        composable(Routes.Loading) {
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Routes.Home)
            }
            val viewModel: AnalyzeViewModel = hiltViewModel(parentEntry)
            LoadingScreen(
                viewModel = viewModel,
                onCancel = { navController.popBackStack() },
                onFinished = {
                    navController.navigate(Routes.Result) {
                        popUpTo(Routes.Loading) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Result) {
            ResultScreen(
                onCloseToHome = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
