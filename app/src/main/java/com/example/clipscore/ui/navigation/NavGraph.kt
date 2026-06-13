package com.example.clipscore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clipscore.ui.screens.AnalysisDetailScreen
import com.example.clipscore.ui.screens.AuthScreen
import com.example.clipscore.ui.screens.HomeScreen
import com.example.clipscore.ui.screens.LoadingScreen
import com.example.clipscore.ui.screens.ResultScreen
import com.example.clipscore.ui.screens.SplashScreen
import com.example.clipscore.ui.screens.TitleInputScreen
import com.example.clipscore.ui.screens.VideoPickerScreen
import com.example.clipscore.ui.screens.VideoPreviewScreen
import com.example.clipscore.ui.viewmodel.AnalyzeViewModel
import com.example.clipscore.ui.viewmodel.AuthViewModel
import com.example.clipscore.ui.viewmodel.VideoPickerViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.clipscore.util.AuthPreferences
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.clipscore.ui.components.ClipScoreSnackbarHost
import com.example.clipscore.util.SnackbarManager
import com.example.clipscore.util.SnackbarType
import kotlinx.coroutines.launch

object Routes {
    const val Splash = "splash"
    const val Auth = "login"
    const val Home = "home"
    const val VideoPicker = "video_picker"
    const val Preview = "preview"
    const val Input = "title_input"
    const val Loading = "loading"
    const val Result = "result"
}

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isLoggedIn) Routes.Home else Routes.Auth

    LaunchedEffect(Unit) {
        SnackbarManager.messages.collect { snackbarMessage ->
            scope.launch {
                val prefix = when (snackbarMessage.type) {
                    SnackbarType.SUCCESS -> "SUCCESS: "
                    SnackbarType.ERROR -> "ERROR: "
                    SnackbarType.WARNING -> "WARNING: "
                    SnackbarType.INFO -> ""
                }
                snackbarHostState.showSnackbar(
                    message = "$prefix${snackbarMessage.message}",
                    actionLabel = snackbarMessage.actionLabel,
                    duration = when (snackbarMessage.type) {
                        SnackbarType.ERROR -> SnackbarDuration.Long
                        else -> SnackbarDuration.Short
                    }
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            ClipScoreSnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
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
                val authViewModel: AuthViewModel = hiltViewModel()
                AuthScreen(
                    viewModel = authViewModel,
                    navController = navController,
                    onAuthed = { email ->
                        authPreferences.saveSession(email)
                    },
                )
            }
            composable(Routes.Home) {
                HomeScreen(
                    onAnalyzeClick = { navController.navigate(Routes.VideoPicker) },
                    onLogout = {
                        navController.navigate(Routes.Auth) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }
            composable(Routes.VideoPicker) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Routes.Home)
                }
                val videoViewModel: VideoPickerViewModel = hiltViewModel(parentEntry)
                VideoPickerScreen(
                    viewModel = videoViewModel,
                    onBack = { navController.popBackStack() },
                    onVideoReady = {
                        navController.navigate(Routes.Preview)
                    },
                )
            }
            composable(Routes.Preview) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Routes.Home)
                }
                val videoViewModel: VideoPickerViewModel = hiltViewModel(parentEntry)
                val analyzeViewModel: AnalyzeViewModel = hiltViewModel(parentEntry)
                VideoPreviewScreen(
                    viewModel = videoViewModel,
                    onBack = { navController.popBackStack() },
                    onContinue = {
                        analyzeViewModel.setVideoContext(videoViewModel.getVideoContext())
                        analyzeViewModel.setVideoFrame(videoViewModel.videoFrameBase64.value)
                        navController.navigate(Routes.Input)
                    },
                    onPickDifferent = {
                        videoViewModel.clearSelection()
                        navController.navigate(Routes.VideoPicker) {
                            popUpTo(Routes.VideoPicker) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.Input) {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Routes.Home)
                }
                val viewModel: AnalyzeViewModel = hiltViewModel(parentEntry)
                TitleInputScreen(
                    viewModel = viewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.Loading) {
                val parentEntry = remember(it) {
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
                val parentEntry = remember(it) {
                    navController.getBackStackEntry(Routes.Home)
                }
                val viewModel: AnalyzeViewModel = hiltViewModel(parentEntry)
                ResultScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onCloseToHome = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Home) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(
                route = "analysis_detail/{analysisId}",
                arguments = listOf(navArgument("analysisId") { type = NavType.IntType })
            ) { backStackEntry ->
                val analysisId = backStackEntry.arguments?.getInt("analysisId") ?: -1
                if (analysisId == -1) {
                    navController.popBackStack()
                    return@composable
                }
                AnalysisDetailScreen(
                    navController = navController,
                    analysisId = analysisId
                )
            }
        }
    }
}
