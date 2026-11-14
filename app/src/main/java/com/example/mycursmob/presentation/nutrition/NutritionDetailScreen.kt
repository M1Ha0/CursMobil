package com.example.mycursmob.presentation.nutrition

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.ui.theme.*

@Composable
fun NutritionDetailScreen(
    viewModel: MealViewModel,
    onBack: () -> Unit,
    onMealAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { carouselDishes.size })
    val currentDish = carouselDishes[pagerState.currentPage]

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            // Header Row: Back + "Nutrition" + More Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp, bottom = 12.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nutrition",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "For ${uiState.selectedCategory.displayName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandOrange
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, InputBorderDefault, CircleShape)
                        .background(Color.White)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "Options",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ───────────── Swipeable Food Carousel (Uploaded photos) ─────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val dish = carouselDishes[page]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(CategoryChipBg)
                    ) {
                        Image(
                            painter = painterResource(id = dish.imageRes),
                            contentDescription = dish.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Carousel Pager Dots Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(carouselDishes.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) BrandOrange else InputBorderDefault)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ───────────── Dish Information & Nutrition Details Card ─────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F3ED)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Title and Weight Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentDish.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = currentDish.portion,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Subtitle and Calories Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nutrition value",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${currentDish.calories} cal",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // ───────────── Macro Progress Bars ─────────────
                    // 1. Protein (Green)
                    MacroProgressBarItem(
                        iconEmoji = "🌱",
                        label = "Protein",
                        amountText = "${currentDish.proteinG.toInt()}g",
                        progress = (currentDish.proteinG / 40f).coerceIn(0.1f, 1f),
                        progressColor = SuccessGreen
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Carbs (Yellow/Amber)
                    MacroProgressBarItem(
                        iconEmoji = "🍞",
                        label = "Carbs",
                        amountText = if (currentDish.carbsG < 10) "${currentDish.carbsG}g" else "${currentDish.carbsG.toInt()}g",
                        progress = (currentDish.carbsG / 60f).coerceIn(0.1f, 1f),
                        progressColor = Color(0xFFFBBF24)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Fat (Orange/Red)
                    MacroProgressBarItem(
                        iconEmoji = "💧",
                        label = "Fat",
                        amountText = "${currentDish.fatsG.toInt()}g",
                        progress = (currentDish.fatsG / 40f).coerceIn(0.1f, 1f),
                        progressColor = BrandOrange
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // ───────────── Add Meal CTA Button ─────────────
                    Button(
                        onClick = {
                            viewModel.addMeal(
                                MealItem(
                                    name = currentDish.name,
                                    portion = currentDish.portion,
                                    calories = currentDish.calories,
                                    proteinG = currentDish.proteinG,
                                    fatsG = currentDish.fatsG,
                                    carbsG = currentDish.carbsG,
                                    emoji = currentDish.emoji,
                                    iconRes = currentDish.iconRes,
                                    imageRes = currentDish.imageRes
                                )
                            )
                            onMealAdded()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Add to ${uiState.selectedCategory.displayName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
    }
}

/**
 * Macro progress bar row matching Figma Screen 4:
 * Emoji/Icon in soft box + Label + Horizontal bar + Amount text on right
 */
@Composable
private fun MacroProgressBarItem(
    iconEmoji: String,
    label: String,
    amountText: String,
    progress: Float,
    progressColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CategoryChipBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconEmoji, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = progressColor,
                trackColor = InputBorderDefault
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = amountText,
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Normal
        )
    }
}
