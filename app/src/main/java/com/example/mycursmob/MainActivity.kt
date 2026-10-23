package com.example.mycursmob

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycursmob.presentation.onboarding.OnboardingViewModel
import com.example.mycursmob.presentation.onboarding.SplashScreen
import com.example.mycursmob.presentation.onboarding.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UpliftAppNavigation()
        }
    }
}

@Composable
fun UpliftAppNavigation() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                viewModel = onboardingViewModel,
                onNavigateToWelcome = {
                    navController.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("welcome") {
            WelcomeScreen(
                viewModel = onboardingViewModel,
                onGetStarted = {
                    Toast.makeText(context, "Переход к регистрации", Toast.LENGTH_SHORT).show()
                },
                onSignIn = {
                    Toast.makeText(context, "Переход ко входу", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}