package com.kreggscode.tdscalculator

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
        
        // Enable edge-to-edge with transparent system bars
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Make system bars fully transparent
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        
        // Set light/dark icons based on system theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }
        
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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Content
            Box(
                modifier = Modifier.fillMaxSize()
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
                        NavDestination.CHAT -> ChatScreen(
                            viewModel = viewModel,
                            onNavigate = { currentDestination = it }
                        )
                        NavDestination.ANALYSIS -> AnalysisScreen(viewModel = viewModel)
                        NavDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
            
            // Floating Navigation Bar (overlaid on top)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.BottomCenter
            ) {
                if (currentDestination != NavDestination.CHAT) {
                    FloatingGlassmorphicNavBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            currentDestination = destination
                        }
                    )
                }
            }
        }
    }
}
