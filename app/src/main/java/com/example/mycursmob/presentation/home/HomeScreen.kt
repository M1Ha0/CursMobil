package com.example.mycursmob.presentation.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.R
import com.example.mycursmob.presentation.auth.AuthViewModel
import com.example.mycursmob.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAvatarSelection: () -> Unit = {},
    onNavigateToNutrition: () -> Unit = {},
    onNavigateToHydration: () -> Unit = {}
) {
    val authState by authViewModel.uiState.collectAsState()
    val user = authState.currentUser
    val displayName = user?.fullName?.ifEmpty { null }
        ?: user?.userName?.ifEmpty { null }
        ?: user?.email?.substringBefore("@")
        ?: "Eren"

    val today = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 36.dp)
    ) {
        // ───────────── Top Dark Header Card (Figma Screen 2) ─────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(BrandDark)
                .padding(top = 46.dp, bottom = 22.dp)
                .padding(horizontal = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Profile info with avatar (Clickable to change avatar)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val avatarPath = user?.avatarPath ?: ""
                        val customBitmap = remember(avatarPath) {
                            if (avatarPath.isNotEmpty() && File(avatarPath).exists()) {
                                try {
                                    BitmapFactory.decodeFile(avatarPath)?.asImageBitmap()
                                } catch (_: Exception) { null }
                            } else null
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BrandDarkGray)
                                .clickable { onNavigateToAvatarSelection() },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                customBitmap != null -> {
                                    Image(
                                        bitmap = customBitmap,
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                avatarPath == "premade_bunny" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar_premade_bunny),
                                        contentDescription = "Avatar Bunny",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                avatarPath == "premade_beanie" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar_premade_beanie),
                                        contentDescription = "Avatar Beanie",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                avatarPath == "premade_cat" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar_premade_cat),
                                        contentDescription = "Avatar Cat",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Profile",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = today,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hello, $displayName",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "251kcal     Hungry",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    // Notification Squircle Button with Badge + Logout
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BrandDarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            // Red Badge with "8+"
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "8+",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Logout button
                        IconButton(
                            onClick = {
                                authViewModel.logout()
                                onLogout()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar inside Dark Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandDarkGray)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search our food database...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ───────────── Browse Category Section ─────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Browse Category",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See All",
                    color = BrandOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        var selectedCategory by remember { mutableStateOf("Hydration") }
        val categories = listOf(
            Triple("💧", "Hydration", CategoryVegetableBlue),
            Triple("🖤", "Score", CategoryChipBg),
            Triple("🍐", "Calorie", CategoryChipBg),
            Triple("🥩", "Meat", CategoryChipBg),
            Triple("🥛", "Dairy", CategoryChipBg),
            Triple("🍞", "Grains", CategoryChipBg)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { (icon, name, defaultBg) ->
                val isSelected = name == selectedCategory
                val bg = if (isSelected) CategoryVegetableBlue else CategoryChipBg
                val contentColor = if (isSelected) Color.White else TextPrimary
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bg)
                        .clickable {
                            selectedCategory = name
                            if (name == "Hydration") {
                                onNavigateToHydration()
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = icon, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = name,
                            color = contentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ───────────── Workouts Section (Figma Exact Match) ─────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workouts",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See All",
                    color = BrandOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(BrandDark)
            ) {
                // Background image: Man lifting dumbbells
                Image(
                    painter = painterResource(id = R.drawable.workout_upper_strength),
                    contentDescription = "Upper Strength 2",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle dark gradient scrim for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                // Top Left Badges: 25min and 412kcal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 18.dp, end = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "25min",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "412kcal",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Bottom Content: Upper Strength 2, 8 Series Workout & Orange Squircle Play Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(start = 18.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Upper Strength 2",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "8 Series Workout",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // Orange Squircle Play Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BrandOrange)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start Workout",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ───────────── Diet & Nutrition Section (Figma Exact Match) ─────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diet & Nutrition",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToNutrition() }
                )
                Text(
                    text = "See All",
                    color = BrandOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToNutrition() }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(152.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(CategoryChipBg)
                    .clickable { onNavigateToNutrition() }
            ) {
                // Salad plate entering from right
                Image(
                    painter = painterResource(id = R.drawable.diet_salad_egg),
                    contentDescription = "Salad & Egg",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(185.dp)
                        .align(Alignment.CenterEnd)
                )

                // Left stats & title
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 18.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Pill 1: 25g Protein
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "25g",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Protein",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Pill 2: 16g Fats
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "16g",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Fats",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "Salad & Egg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "548kcal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "20min",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ───────────── Activities Section (Figma Exact Match) ─────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activities",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See All",
                    color = BrandOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(CategoryChipBg),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.activity_blocks),
                        contentDescription = "Activities",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(126.dp)
                    )

                    // 7h Red Badge positioned over bottom-left corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 12.dp, y = (-4).dp)
                            .rotate(-22f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF74343))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "7h",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
