package com.example.mycursmob.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.presentation.components.*
import com.example.mycursmob.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: (isAssessmentCompleted: Boolean) -> Unit,
    onForgotPassword: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Faint gym illustration at top
        GymHeaderWatermark(modifier = Modifier.align(Alignment.TopCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Orange Uplift Logo Badge
            UpliftLogoBadge(badgeSize = 58.dp)

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Sign In To Uplift",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Let's personalize your fitness with AI",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Email Field
            UpliftTextField(
                value = state.loginEmail,
                onValueChange = { viewModel.onLoginEmailChange(it) },
                label = "Email Address",
                placeholder = "elementary221b@gmail.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            UpliftTextField(
                value = state.loginPassword,
                onValueChange = { viewModel.onLoginPasswordChange(it) },
                label = "Password",
                placeholder = "••••••••••••",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                passwordVisible = state.isLoginPasswordVisible,
                onVisibilityToggle = { viewModel.toggleLoginPasswordVisibility() },
                keyboardType = KeyboardType.Password
            )

            // Success Banner after registration
            if (state.loginSuccessMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                UpliftSuccessBanner(message = state.loginSuccessMessage!!)
            }

            // Optional Error Banner
            if (state.loginError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                UpliftErrorBanner(message = state.loginError!!)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Black Sign In Button
            Button(
                onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                enabled = !state.isLoginLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandDark,
                    disabledContainerColor = BrandDark.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (state.isLoginLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sign In",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Social Buttons Row
            SocialButtonsRow()

            Spacer(modifier = Modifier.height(28.dp))

            // Don't have an account? Sign Up.
            val signUpAnnotated = buildAnnotatedString {
                append("Don't have an account? ")
                withStyle(
                    style = SpanStyle(
                        color = BrandOrange,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Sign Up.")
                }
            }
            Text(
                text = signUpAnnotated,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Forgot Password
            Text(
                text = "Forgot Password",
                color = BrandOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }
    }
}
