package com.kreggscode.tdscalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.kreggscode.tdscalculator.data.models.ThemeMode
import com.kreggscode.tdscalculator.ui.navigation.*
import com.kreggscode.tdscalculator.ui.screens.*
import com.kreggscode.tdscalculator.ui.theme.TDSAICalculatorTheme
import com.kreggscode.tdscalculator.ui.viewmodels.TDSViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val viewModel: TDSViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()
            
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }
            
            TDSAICalculatorTheme(darkTheme = darkTheme) {
                TDSApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TDSApp(viewModel: TDSViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    var currentDestination by remember { mutableStateOf(NavDestination.CALCULATOR) }
    
    if (showSplash) {
        SplashScreen(
            onSplashComplete = { showSplash = false }
        )
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                FloatingGlassmorphicNavBar(
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        currentDestination = destination
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(400)
                                ) + fadeOut(animationSpec = tween(400))
                    },
                    label = "screen_transition"
                ) { destination ->
                    when (destination) {
                        NavDestination.CALCULATOR -> CalculatorScreen(viewModel = viewModel)
                        NavDestination.LEARN -> LearningScreen(viewModel = viewModel)
                        NavDestination.CHAT -> ChatScreen(viewModel = viewModel)
                        NavDestination.ANALYSIS -> AnalysisScreen(viewModel = viewModel)
                        NavDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
