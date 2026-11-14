package com.example.mycursmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycursmob.presentation.assessment.AssessmentScreen
import com.example.mycursmob.presentation.assessment.AssessmentViewModel
import com.example.mycursmob.presentation.auth.AuthViewModel
import com.example.mycursmob.presentation.auth.LoginScreen
import com.example.mycursmob.presentation.auth.SignUpScreen
import com.example.mycursmob.presentation.home.HomeScreen
import com.example.mycursmob.presentation.onboarding.OnboardingViewModel
import com.example.mycursmob.presentation.onboarding.SplashScreen
import com.example.mycursmob.presentation.onboarding.WelcomeScreen
import com.example.mycursmob.ui.theme.MycursmobTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MycursmobTheme {
                FitnessAppNavigation()
            }
        }
    }
}

@Composable
fun FitnessAppNavigation() {
    val navController = rememberNavController()
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val assessmentViewModel: AssessmentViewModel = viewModel()

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
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToAssessment = {
                    navController.navigate("assessment") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("welcome") {
            WelcomeScreen(
                viewModel = onboardingViewModel,
                onGetStarted = {
                    navController.navigate("signup")
                },
                onSignIn = {
                    navController.navigate("login")
                }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onLoginSuccess = { isAssessmentCompleted ->
                    if (isAssessmentCompleted) {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        navController.navigate("assessment") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToSignIn = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("assessment") {
            AssessmentScreen(
                viewModel = assessmentViewModel,
                onComplete = {
                    val state = assessmentViewModel.uiState.value
                    authViewModel.saveAssessment(
                        age = state.age,
                        weightKg = state.weightKg,
                        isKgSelected = state.isKgSelected,
                        fitnessLevel = state.fitnessLevel,
                        fitnessLevelName = assessmentViewModel.getFitnessLevelName(),
                        gender = state.gender,
                        fitnessGoals = state.fitnessGoals,
                        onSuccess = {}
                    )
                    navController.navigate("home") {
                        popUpTo("assessment") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            HomeScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
