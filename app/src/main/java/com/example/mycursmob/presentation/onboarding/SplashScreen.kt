package com.example.mycursmob.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.presentation.components.UpliftLogo
import com.example.mycursmob.ui.theme.BrandOrange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: OnboardingViewModel,
    onNavigateToWelcome: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToAssessment: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToHome -> onNavigateToHome()
                is OnboardingNavigationEvent.NavigateToAssessment -> onNavigateToAssessment()
                is OnboardingNavigationEvent.NavigateToWelcome -> onNavigateToWelcome()
                else -> Unit
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandOrange),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Uplift Logo
            UpliftLogo(size = 110.dp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.appName,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.splashSubtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
