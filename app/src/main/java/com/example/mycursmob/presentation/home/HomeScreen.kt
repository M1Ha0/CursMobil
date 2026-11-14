package com.example.mycursmob.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.R
import com.example.mycursmob.presentation.auth.AuthViewModel
import com.example.mycursmob.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
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

    var calorieGoal by remember { mutableFloatStateOf(2500f) }

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
                    // Profile info with avatar placeholder
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BrandDarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(28.dp)
                            )
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

        var selectedCategory by remember { mutableStateOf("Vegetable") }
        val categories = listOf(
            Triple("💧", "Vegetable", CategoryVegetableBlue),
            Triple("🤍", "Meat", CategoryChipBg),
            Triple("🍐", "Fruit", CategoryChipBg),
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
                        .clickable { selectedCategory = name }
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

        // ───────────── Morning Routine Section ─────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Morning Routine",
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

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Runner's Diet (Green Card from Figma)
            item {
                Image(
                    painter = painterResource(id = R.drawable.card_runner_diet),
                    contentDescription = "Runner's Diet",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(168.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            // Card 2: Power Meal / Breakfast (Yellow Card from Figma)
            item {
                Image(
                    painter = painterResource(id = R.drawable.card_power_meal),
                    contentDescription = "Power Meal",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(136.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            // Card 3: MORE Card (Black vertical card matching Figma)
            item {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BrandDark),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Arrow Circle Button at Top
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "More",
                                tint = BrandDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Rotated Text "MORE"
                        Text(
                            text = "MORE",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .rotate(-90f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ───────────── Calorie Goal Section (Figma Exact Match) ─────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Calorie Goal",
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Big Orange Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = BrandOrange)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    // Number + Unit Row
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${calorieGoal.toInt()}",
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 44.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kilo calories",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Peach Slider Container
                    CalorieGoalSlider(
                        value = calorieGoal,
                        onValueChange = { calorieGoal = it }
                    )
                }
            }
        }
    }
}

/**
 * Custom Slider matching Figma Screen 2 Calorie Goal:
 * Peach background container, white track, orange squircle thumb with white border.
 */
@Composable
private fun CalorieGoalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    minVal: Float = 1000f,
    maxVal: Float = 4000f
) {
    val density = LocalDensity.current
    var sliderWidthPx by remember { mutableFloatStateOf(1f) }
    val thumbWidthDp = 48.dp
    val thumbWidthPx = with(density) { thumbWidthDp.toPx() }

    val fraction = ((value - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CalorieSliderPeach)
            .onGloballyPositioned { coordinates ->
                sliderWidthPx = coordinates.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val availableWidth = (sliderWidthPx - thumbWidthPx).coerceAtLeast(1f)
                    val newX = (change.position.x - thumbWidthPx / 2f).coerceIn(0f, availableWidth)
                    val newFraction = newX / availableWidth
                    val newVal = minVal + newFraction * (maxVal - minVal)
                    onValueChange(newVal)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // Track line
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )

        // Orange Squircle Thumb
        val availableWidthDp = with(density) {
            val aw = (sliderWidthPx - thumbWidthPx).coerceAtLeast(0f)
            aw.toDp()
        }
        val thumbOffset = availableWidthDp * fraction

        Box(
            modifier = Modifier
                .offset(x = thumbOffset + 4.dp)
                .size(thumbWidthDp)
                .clip(RoundedCornerShape(14.dp))
                .background(BrandOrange)
                .border(3.dp, Color.White, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Inner small dot/square
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
        }
    }
}
