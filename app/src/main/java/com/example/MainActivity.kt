package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.main.MainContainerScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.TaqadomTheme
import com.example.viewmodel.TaqadomViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaqadomTheme {
                val viewModel: TaqadomViewModel = viewModel()
                TaqadomApp(viewModel)
            }
        }
    }
}

@Composable
fun TaqadomApp(viewModel: TaqadomViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            "splash" -> SplashScreen()
            "login" -> LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { viewModel.navigateTo("register") }
            )
            "register" -> RegisterScreen(
                viewModel = viewModel,
                onNavigateBack = { viewModel.navigateTo("login") }
            )
            "onboarding" -> OnboardingScreen(viewModel = viewModel)
            "main" -> MainContainerScreen(viewModel = viewModel)
            else -> MainContainerScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = GymGreenPrimary,
            modifier = Modifier.size(48.dp)
        )
    }
}
