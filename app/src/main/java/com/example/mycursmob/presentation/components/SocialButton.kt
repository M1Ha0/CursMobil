package com.example.mycursmob.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.ui.theme.SocialCardBg
import com.example.mycursmob.ui.theme.SocialCardBorder
import com.example.mycursmob.ui.theme.TextPrimary

@Composable
fun SocialButtonCard(
    text: String,
    textColor: Color = TextPrimary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SocialCardBg)
            .border(1.dp, SocialCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SocialButtonsRow(
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    onAppleClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialButtonCard(
            text = "G",
            textColor = Color(0xFFEA4335),
            onClick = onGoogleClick
        )
        SocialButtonCard(
            text = "f",
            textColor = Color(0xFF1877F2),
            onClick = onFacebookClick
        )
        SocialButtonCard(
            text = "in",
            textColor = Color(0xFF0A66C2),
            onClick = onAppleClick
        )
    }
}
