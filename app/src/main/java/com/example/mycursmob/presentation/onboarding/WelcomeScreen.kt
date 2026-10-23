package com.example.mycursmob.presentation.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.upliftai.ui.components.UpliftLogo
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
        // 1. Фоновая сцена спортзала (можно заменить на Image(painterResource(R.drawable.gym_bg)))
        GymBackgroundIllustration(modifier = Modifier.fillMaxSize())

        // 2. Темный градиент снизу для читаемости текста
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.92f)
                        ),
                        startY = 300f
                    )
                )
        )

        // 3. Основной контент в нижней части экрана
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
//            UpliftLogo(
//                modifier = Modifier.size(64.dp),
//                color = Color.White
//            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.welcomeTitle,
                color = Color.White,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.welcomeSubtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка "Get Started ->"
            Button(
                onClick = { viewModel.onGetStartedClicked() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .width(220.dp)
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
//                        modifier = Modifier.size(20.dp)
//                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Текст "Already have account? Sign In"
            val annotatedString = buildAnnotatedString {
                append("Already have account? ")
                pushStringAnnotation(tag = "SIGN_IN", annotation = "sign_in")
                withStyle(
                    style = SpanStyle(
                        color = BrandOrange,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Sign In")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                ),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "SIGN_IN",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        viewModel.onSignInClicked()
                    }
                }
            )
        }
    }
}

@Composable
private fun GymBackgroundIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Оранжевая стена справа
        drawRect(color = Color(0xFFE8832A), size = size)

        // Темно-серая панель стены слева
        drawRect(
            color = Color(0xFF2B2927),
            topLeft = Offset.Zero,
            size = Size(w * 0.15f, h)
        )

        // Стойка для штанги справа
        drawRect(
            color = Color(0xFF1A1918),
            topLeft = Offset(w * 0.9f, h * 0.25f),
            size = Size(w * 0.05f, h * 0.75f)
        )

        // Гриф штанги
        drawRect(
            color = Color(0xFF8C8C8C),
            topLeft = Offset(w * 0.35f, h * 0.495f),
            size = Size(w * 0.65f, h * 0.015f)
        )

        // Блин штанги (в центре)
        drawOval(
            color = Color(0xFF222222),
            topLeft = Offset(w * 0.56f, h * 0.40f),
            size = Size(w * 0.12f, h * 0.20f)
        )
        drawOval(
            color = Color(0xFF3A3A3A),
            topLeft = Offset(w * 0.575f, h * 0.42f),
            size = Size(w * 0.09f, h * 0.16f)
        )
    }
}