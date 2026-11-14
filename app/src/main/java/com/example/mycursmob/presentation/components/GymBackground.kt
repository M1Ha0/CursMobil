package com.example.mycursmob.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mycursmob.R

/**
 * Real gym image background for Welcome Screen with dark gradient overlay
 */
@Composable
fun GymBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1. Gym photo
        Image(
            painter = painterResource(id = R.drawable.gym_bg),
            contentDescription = "Gym Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Cinematic dark gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF141416).copy(alpha = 0.35f),
                            Color(0xFF141416).copy(alpha = 0.85f)
                        ),
                        startY = 700f
                    )
                )
        )
    }
}

/**
 * Faint gym watermark image at the top of Sign In & Sign Up screens
 */
@Composable
fun GymHeaderWatermark(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.gym_header_bg),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
    )
}
