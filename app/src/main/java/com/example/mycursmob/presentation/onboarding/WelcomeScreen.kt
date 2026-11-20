package com.example.mycursmob.presentation.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.presentation.components.GymBackground
import com.example.mycursmob.presentation.components.UpliftLogo
import com.example.mycursmob.ui.theme.BrandOrange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WelcomeScreen(
    viewModel: OnboardingViewModel,
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToGetStarted -> onGetStarted()
                is OnboardingNavigationEvent.NavigateToSignIn -> onSignIn()
                else -> Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Gym equipment photo background with orange warm lighting & dark gradient
        GymBackground(modifier = Modifier.fillMaxSize())

        // 2. Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(bottom = 85.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Uplift Logo
            UpliftLogo(size = 56.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // Title: Welcome To Uplift.ai
            Text(
                text = uiState.welcomeTitle,
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle
            Text(
                text = uiState.welcomeSubtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Orange Button "Get Started ->"
            Button(
                onClick = { viewModel.onGetStartedClicked() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Get Started",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                        contentDescription = "Arrow Forward",
//                        tint = Color.White,
//                        modifier = Modifier.size(18.dp)
//                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Already have account? Sign In
            val annotatedString = buildAnnotatedString {
                append("Already have account? ")
                withStyle(
                    style = SpanStyle(
                        color = BrandOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Sign In")
                }
            }

            Text(
                text = annotatedString,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { viewModel.onSignInClicked() }
            )
        }
    }
}
