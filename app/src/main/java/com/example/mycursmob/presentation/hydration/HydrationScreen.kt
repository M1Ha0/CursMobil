package com.example.mycursmob.presentation.hydration

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.ui.theme.*

@Composable
fun HydrationScreen(
    viewModel: HydrationViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Smooth animated water fill level
    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progressFraction,
        animationSpec = tween(durationMillis = 600),
        label = "waterFill"
    )

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            // Header Row: Back + "Hydration"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp, bottom = 12.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, InputBorderDefault, CircleShape)
                        .background(Color.White)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Hydration",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                // Invisible box to balance back button
                Spacer(modifier = Modifier.size(44.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Current Water Amount Header
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${uiState.currentWaterMl}",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    lineHeight = 44.sp
                )
                Text(
                    text = "ml",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Remaining target text
            val subtitleText = if (uiState.remainingWaterMl > 0) {
                "You need ${uiState.remainingWaterMl}ml for today."
            } else {
                "Daily goal reached! Keep it up! 🎉"
            }

            Text(
                text = subtitleText,
                fontSize = 15.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ───────────── Water Beaker / Container (Figma exact match) ─────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp, bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(Color(0xFFF0F1F5))
            ) {
                // Goal Label at Top Left of Beaker
                Column(
                    modifier = Modifier
                        .padding(top = 28.dp, start = 28.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Goal",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.goalWaterMl}ml",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Dynamic Filling Blue Water Level
                val waterFraction = animatedProgress.coerceIn(0.18f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(waterFraction)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(Color(0xFF205BFF))
                ) {
                    // "Current 500ml" in top right of water
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 20.dp, end = 26.dp)
                    ) {
                        Text(
                            text = "Current",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.currentWaterMl}ml",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Interactive White Squircle Button (+250ml)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 36.dp)
                            .size(72.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(3.5.dp, Color(0x995CB0FF), RoundedCornerShape(24.dp))
                            .clickable { viewModel.addWater(250) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add 250ml",
                            tint = Color(0xFF205BFF),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
