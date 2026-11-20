package com.example.mycursmob.presentation.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.ui.theme.BackgroundWhite
import com.example.mycursmob.ui.theme.BrandDark
import com.example.mycursmob.ui.theme.BrandOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val MacroBlack = Color(0xFF141416)
private val MacroOrange = BrandOrange
private val MacroBlue = Color(0xFF2F70F2)
private val MacroGreen = Color(0xFF76C816)
private val ColumnTrackBg = Color(0xFFE2E2E6)

@Composable
fun CalorieStatsScreen(
    viewModel: MealViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val calories = uiState.todayTotalCalories
    val proteinG = uiState.todayTotalProteinG
    val fatsG = uiState.todayTotalFatsG
    val carbsG = uiState.todayTotalCarbsG
    val totalMacroG = uiState.todayTotalMacroG

    val fatPct = if (totalMacroG > 0f) ((fatsG / totalMacroG) * 100).roundToInt() else 0
    val carbsPct = if (totalMacroG > 0f) ((carbsG / totalMacroG) * 100).roundToInt() else 0
    val proteinPct = if (totalMacroG > 0f) ((proteinG / totalMacroG) * 100).roundToInt() else 0
    val macroPct = if (calories > 0) ((calories / 2500f) * 100).coerceAtMost(100f).roundToInt() else 0

    val monthLabel = remember {
        SimpleDateFormat("MMM", Locale.ENGLISH).format(Date()).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(top = 46.dp)
    ) {
        // ───────────── Top Bar ─────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Calorie Stats",
                color = BrandDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ───────────── Total Calories & Month Header ─────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$calories",
                    color = BrandDark,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 44.sp
                )
                Text(
                    text = "kcal",
                    color = Color(0xFF8E8E93),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F1F4))
                    .padding(horizontal = 20.dp, vertical = 9.dp)
            ) {
                Text(
                    text = monthLabel,
                    color = BrandDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ───────────── 4 Vertical Rounded Columns ─────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Column 1: Fat (Black)
            StatVerticalColumn(
                percentage = fatPct,
                fillColor = MacroBlack,
                modifier = Modifier.weight(1f)
            )

            // Column 2: Carbs (Orange)
            StatVerticalColumn(
                percentage = carbsPct,
                fillColor = MacroOrange,
                modifier = Modifier.weight(1f)
            )

            // Column 3: Protein (Blue)
            StatVerticalColumn(
                percentage = proteinPct,
                fillColor = MacroBlue,
                modifier = Modifier.weight(1f)
            )

            // Column 4: Macro (Green)
            StatVerticalColumn(
                percentage = macroPct,
                fillColor = MacroGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ───────────── Macro Breakdown List ─────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Fat
            MacroListItem(
                label = "Fat",
                grams = fatsG.roundToInt(),
                color = MacroBlack
            )
            HorizontalDivider(
                color = Color(0xFFF0F0F2),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // Protein
            MacroListItem(
                label = "Protein",
                grams = proteinG.roundToInt(),
                color = MacroBlue
            )
            HorizontalDivider(
                color = Color(0xFFF0F0F2),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // Carbs
            MacroListItem(
                label = "Carbs",
                grams = carbsG.roundToInt(),
                color = MacroOrange
            )
            HorizontalDivider(
                color = Color(0xFFF0F0F2),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // Macro
            MacroListItem(
                label = "Macro",
                grams = totalMacroG.roundToInt(),
                color = MacroGreen
            )
        }
    }
}

@Composable
private fun StatVerticalColumn(
    percentage: Int,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    val visualFraction = if (percentage > 0) {
        (percentage / 100f).coerceIn(0.16f, 1f)
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(22.dp))
            .background(ColumnTrackBg),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (visualFraction > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(visualFraction)
                    .clip(RoundedCornerShape(22.dp))
                    .background(fillColor),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "$percentage%",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        } else {
            Text(
                text = "0%",
                color = Color(0xFF8E8E93),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun MacroListItem(
    label: String,
    grams: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = BrandDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${grams}g",
            color = Color(0xFF757575),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
