package com.example.mycursmob.presentation.nutrition

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
fun MyMealsScreen(
    viewModel: MealViewModel,
    onBack: () -> Unit,
    onNavigateToAddMeal: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val daysWithToday = remember { CalendarHelper.generateDays(rangeDays = 15) }
    val daysList = daysWithToday.first
    val todayIndex = daysWithToday.second

    // Automatically scroll calendar strip so Today is visible and centered
    val calendarScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = (todayIndex - 2).coerceAtLeast(0)
    )

    val currentMeals = uiState.currentMeals

    Scaffold(
        containerColor = BackgroundWhite,
        topBar = {
            // ───────────── Header Bar (Figma Screen 3) ─────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp, bottom = 12.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "My Meals",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Plus (+) Button in Header -> navigates to swipeable NutritionDetailScreen
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, InputBorderDefault, CircleShape)
                        .background(Color.White)
                        .clickable { onNavigateToAddMeal() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Meal",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Share Button
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
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // ───────────── Horizontal Calendar Strip ─────────────
            LazyRow(
                state = calendarScrollState,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(daysList) { day ->
                    val isSelected = day.fullKey == uiState.selectedDateKey
                    CalendarDayItem(
                        day = day,
                        isSelected = isSelected,
                        onClick = { viewModel.selectDate(day.fullKey) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ───────────── Meal Categories Segmented Bar ─────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CategoryChipBg)
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    MealCategory.entries.forEach { category ->
                        val isSelected = category == uiState.selectedCategory
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { viewModel.selectCategory(category) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ───────────── Meals List or Empty State ─────────────
            if (currentMeals.isEmpty()) {
                // Empty state with prominent Plus button (matches user request)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.5.dp, InputBorderDefault, RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .padding(vertical = 40.dp, horizontal = 24.dp)
                    ) {
                        // Large Plus Squircle Button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(BrandOrange.copy(alpha = 0.12f))
                                .clickable { onNavigateToAddMeal() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Dish",
                                tint = BrandOrange,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "No meals logged yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tap + to choose a dish for ${uiState.selectedCategory.displayName.lowercase()}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onNavigateToAddMeal() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Dish",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Meal Cards List
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentMeals, key = { it.id }) { meal ->
                        MealCardItem(
                            meal = meal,
                            onDelete = { viewModel.deleteMeal(meal.id) }
                        )
                    }

                    item {
                        // "+ Add Another Dish" Button -> navigates to NutritionDetailScreen
                        OutlinedButton(
                            onClick = { onNavigateToAddMeal() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BrandOrange)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = BrandOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add another dish",
                                color = BrandOrange,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * Calendar Strip Item (Figma exact match):
 * Orange when selected, light gray when unselected.
 */
@Composable
private fun CalendarDayItem(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) BrandOrange else CategoryChipBg
    val monthColor = if (isSelected) Color.White.copy(alpha = 0.9f) else TextSecondary
    val dayColor = if (isSelected) Color.White else TextPrimary

    Box(
        modifier = Modifier
            .width(58.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.monthShort,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = monthColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = day.dayNumber,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = dayColor
            )
        }
    }
}

/**
 * Individual Meal Card matching Figma design:
 * Uses uploaded food stickers (egg, avocado, pancakes, pineapple),
 * title, calories, 3-dots menu, and 3 vertical macro bars (Protein, Fats, Carbs).
 */
@Composable
private fun MealCardItem(
    meal: MealItem,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F3ED))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Row: Food Sticker Icon + Title + Calories + Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Food Sticker Icon
                if (meal.iconRes != null) {
                    Image(
                        painter = painterResource(id = meal.iconRes),
                        contentDescription = meal.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CategoryChipBg)
                            .padding(4.dp)
                    )
                } else if (meal.imageRes != null) {
                    Image(
                        painter = painterResource(id = meal.imageRes),
                        contentDescription = meal.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CategoryChipBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = meal.emoji, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${meal.calories} kcal - ${meal.portion}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Three Dots Button with Delete Option
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Options",
                            tint = TextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = ErrorRed) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = ErrorRed
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bottom Row: Macros with vertical indicators (Protein, Fats, Carbs)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Protein (Green)
                MacroStatItem(
                    label = "Protein",
                    amount = "${meal.proteinG.toInt()}g",
                    indicatorColor = SuccessGreen
                )

                // Fats (Red/Orange)
                MacroStatItem(
                    label = "Fats",
                    amount = "${meal.fatsG.toInt()}g",
                    indicatorColor = Color(0xFFFF5C5C)
                )

                // Carbs (Yellow)
                MacroStatItem(
                    label = "Carbs",
                    amount = if (meal.carbsG < 10) "${meal.carbsG}g" else "${meal.carbsG.toInt()}g",
                    indicatorColor = Color(0xFFFBBF24)
                )
            }
        }
    }
}

/**
 * Vertical bar indicator + Amount & Label (Figma match)
 */
@Composable
private fun MacroStatItem(
    label: String,
    amount: String,
    indicatorColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(indicatorColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
