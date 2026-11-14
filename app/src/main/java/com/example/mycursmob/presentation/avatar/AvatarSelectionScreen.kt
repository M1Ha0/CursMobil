package com.example.mycursmob.presentation.avatar

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.R
import com.example.mycursmob.presentation.auth.AuthViewModel
import com.example.mycursmob.ui.theme.*
import java.io.File

@Composable
fun AvatarSelectionScreen(
    authViewModel: AuthViewModel,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val initialAvatar = authState.currentUser?.avatarPath?.ifEmpty { "premade_bunny" } ?: "premade_bunny"

    var selectedAvatar by remember { mutableStateOf(initialAvatar) }

    // Launcher for picking photo from phone's local storage/gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val file = File(context.filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                selectedAvatar = file.absolutePath
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(top = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InputBackground)
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
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Assessment",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── 3D Premade Avatars Carousel matching Figma ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Beanie 3D boy
            PremadeAvatarItem(
                imageRes = R.drawable.avatar_premade_beanie,
                isSelected = selectedAvatar == "premade_beanie",
                size = 84.dp,
                onClick = { selectedAvatar = "premade_beanie" }
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Center: Bunny 3D character (default / prominent)
            PremadeAvatarItem(
                imageRes = R.drawable.avatar_premade_bunny,
                isSelected = selectedAvatar == "premade_bunny",
                size = 114.dp,
                isPrimary = true,
                onClick = { selectedAvatar = "premade_bunny" }
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Right: Cat 3D character
            PremadeAvatarItem(
                imageRes = R.drawable.avatar_premade_cat,
                isSelected = selectedAvatar == "premade_cat",
                size = 84.dp,
                onClick = { selectedAvatar = "premade_cat" }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Title & Subtitle ──
        Text(
            text = "Select your Avatar",
            color = TextPrimary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We have 23 custom premade avatars, or\nyou can upload profile locally",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Dashed Circular Upload Container matching Figma ──
        val customBitmap = remember(selectedAvatar) {
            if (selectedAvatar.isNotEmpty() && File(selectedAvatar).exists()) {
                try {
                    BitmapFactory.decodeFile(selectedAvatar)?.asImageBitmap()
                } catch (_: Exception) { null }
            } else null
        }

        Box(
            modifier = Modifier
                .size(130.dp)
                .clickable { photoPickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            // Dashed outline circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
                )
                drawCircle(
                    color = if (customBitmap != null) BrandOrange else Color(0xFF777E90),
                    radius = size.minDimension / 2f - 2.dp.toPx(),
                    style = stroke
                )
            }

            if (customBitmap != null) {
                // Show selected custom photo
                Image(
                    bitmap = customBitmap,
                    contentDescription = "Custom Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                )
            } else {
                // Upload arrow icon from Figma
                Icon(
                    imageVector = Icons.Filled.FileUpload,
                    contentDescription = "Upload",
                    tint = TextPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "or Upload from Local File"
        Text(
            text = "or Upload from Local File",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { photoPickerLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Max 5mb, Format: jpg,png",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // ── Continue Button (Raised up for thumb reach) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 54.dp, top = 8.dp)
        ) {
            Button(
                onClick = {
                    authViewModel.updateAvatar(selectedAvatar)
                    onComplete()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continue",
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
    }
}

@Composable
private fun PremadeAvatarItem(
    imageRes: Int,
    isSelected: Boolean,
    size: androidx.compose.ui.unit.Dp,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isPrimary && isSelected) BrandOrange else InputBackground)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) BrandOrange else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
