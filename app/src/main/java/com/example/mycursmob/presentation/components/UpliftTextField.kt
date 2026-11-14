package com.example.mycursmob.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.ui.theme.*

@Composable
fun UpliftTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor = when {
        isError -> InputBorderError
        isFocused -> InputBorderFocused
        else -> InputBorderDefault
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(InputBackground)
                .border(
                    width = if (isFocused || isError) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(14.dp)
                )
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isFocused) BrandOrange else TextMuted,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(BrandOrange),
                    visualTransformation = if (isPassword && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
            }

            if (isPassword) {
                IconButton(
                    onClick = { onVisibilityToggle?.invoke() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Error Alert Box matching Figma Screen 4:
 * ⚠️ ERROR: Password Don't Match! (red icon and text on pink container)
 */
@Composable
fun UpliftErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ErrorBackground)
            .border(1.dp, ErrorRed.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = ErrorRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = ErrorRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Success Alert Box:
 * ✓ Account created successfully! Please sign in.
 */
@Composable
fun UpliftSuccessBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = Color(0xFF2E7D32),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
