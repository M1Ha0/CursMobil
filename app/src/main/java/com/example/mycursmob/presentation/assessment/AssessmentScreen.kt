package com.example.mycursmob.presentation.assessment

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.R
import com.example.mycursmob.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ──────────────────────── Main Assessment Screen ────────────────────────
@Composable
fun AssessmentScreen(
    viewModel: AssessmentViewModel,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isWeightStep = state.currentStep == 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isWeightStep) BrandOrange else BackgroundWhite)
            .padding(top = 44.dp)
    ) {
        // ── Top Bar with dynamic badge (1 of 6, 2 of 6, 3 of 6, 4 of 6, 6 of 6) ──
        val stepBadgeLabel = when (state.currentStep) {
            1 -> "1 of 6"
            2 -> "2 of 6"
            3 -> "3 of 6"
            4 -> "4 of 6"
            else -> "6 of 6"
        }

        AssessmentTopBar(
            stepLabel = stepBadgeLabel,
            isOrangeTheme = isWeightStep,
            onBack = {
                if (state.currentStep > 1) viewModel.previousStep() else onBack()
            }
        )

        // ── Dynamic Step Content ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (state.currentStep) {
                1 -> AgeWheelStep(
                    age = state.age,
                    onAgeChange = { viewModel.onAgeChange(it) }
                )
                2 -> WeightStep(
                    weight = state.weightKg,
                    isKg = state.isKgSelected,
                    onWeightChange = { viewModel.onWeightChange(it) },
                    onUnitToggle = { viewModel.onUnitToggle(it) }
                )
                3 -> FitnessLevelArcStep(
                    level = state.fitnessLevel,
                    onLevelChange = { viewModel.onFitnessLevelChange(it) }
                )
                4 -> GenderStep(
                    selectedGender = state.gender,
                    onGenderSelect = { viewModel.onGenderSelect(it) }
                )
                5 -> GoalStep(
                    selectedGoals = state.fitnessGoals,
                    onGoalToggle = { viewModel.onGoalToggle(it) }
                )
            }
        }

        // ── Continue Button (Raised higher for comfortable thumb reach) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 56.dp, top = 8.dp)
        ) {
            Button(
                onClick = {
                    if (viewModel.isLastStep()) onComplete() else viewModel.nextStep()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isWeightStep) Color.White else BrandDark
                ),
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
                        color = if (isWeightStep) BrandDark else Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isWeightStep) BrandDark else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ──────────────────────── Top Bar ────────────────────────
@Composable
private fun AssessmentTopBar(
    stepLabel: String,
    isOrangeTheme: Boolean,
    onBack: () -> Unit
) {
    val contentColor = if (isOrangeTheme) Color.White else TextPrimary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = contentColor
            )
        }
        Text(
            text = "Assessment",
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isOrangeTheme) Color.White.copy(alpha = 0.25f)
                    else AssessmentStepBadgeBg
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = stepLabel,
                color = if (isOrangeTheme) Color.White else AssessmentStepBadgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ──────────────────────── Step 1: Age Wheel Picker (Figma Screen 1) ────────────────────────
@Composable
private fun AgeWheelStep(
    age: Int,
    onAgeChange: (Int) -> Unit
) {
    val ageList = remember { (10..90).toList() }
    val itemHeightDp = 64.dp
    val coroutineScope = rememberCoroutineScope()

    val initialIndex = remember { (age - 10).coerceIn(0, ageList.size - 1) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Listen to wheel scroll and update selected age
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val selectedAge = ageList.getOrElse(index) { age }
                onAgeChange(selectedAge)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your Age?",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Spinning Wheel Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * 5),
            contentAlignment = Alignment.Center
        ) {
            // Orange rounded selection card in the exact vertical center
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandOrange)
            )

            // Scrollable LazyColumn with snapping
            LazyColumn(
                state = listState,
                flingBehavior = snapFlingBehavior,
                contentPadding = PaddingValues(vertical = itemHeightDp * 2),
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(ageList) { index, displayAge ->
                    val isSelected = displayAge == age
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp)
                            .clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$displayAge",
                            color = if (isSelected) Color.White else TextPrimary.copy(alpha = 0.35f),
                            fontSize = if (isSelected) 40.sp else 24.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────── Step 2: Weight Screen (Figma Screen 2) ────────────────────────
@Composable
private fun WeightStep(
    weight: Int,
    isKg: Boolean,
    onWeightChange: (Int) -> Unit,
    onUnitToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your current\nweight right now?",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        Spacer(modifier = Modifier.height(28.dp))

        // Kg / Lbs toggle buttons matching Figma
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(4.dp)
        ) {
            listOf(true to "Kg", false to "Lbs").forEach { (isKgOption, label) ->
                val selected = isKg == isKgOption
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) Color.White else Color.Transparent)
                        .clickable { onUnitToggle(isKgOption) }
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (selected) BrandOrange else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Huge weight number display
        val displayWeight = if (isKg) weight else (weight * 2.205).toInt()
        val unit = if (isKg) "Kg" else "Lbs"
        Text(
            text = "$displayWeight $unit",
            color = Color.White,
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Ruler scale with tick marks & numbers (50, 55, 60, 65, 70, 75, 80)
        val weightRange = remember { (30..150).toList() }
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(weight) {
            val index = weightRange.indexOf(weight)
            if (index >= 0) listState.animateScrollToItem(index)
        }

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 140.dp),
            verticalAlignment = Alignment.Top
        ) {
            items(weightRange) { w ->
                val isMajor = w % 5 == 0
                val isSelected = w == weight
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onWeightChange(w)
                        coroutineScope.launch {
                            listState.animateScrollToItem(weightRange.indexOf(w))
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (isSelected) 3.dp else if (isMajor) 2.dp else 1.dp)
                            .height(if (isSelected) 38.dp else if (isMajor) 32.dp else 18.dp)
                            .background(
                                if (isSelected) Color.White
                                else Color.White.copy(alpha = if (isMajor) 0.85f else 0.4f)
                            )
                    )
                    if (isMajor) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$w",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────── Step 3: Fitness Level Arc Gauge (Figma Screen 3) ────────────────────────
@Composable
private fun FitnessLevelArcStep(
    level: Int,
    onLevelChange: (Int) -> Unit
) {
    val levelNames = mapOf(
        1 to "Beginner",
        2 to "Lightly Active",
        3 to "Somewhat Athletic",
        4 to "Very Athletic",
        5 to "Professional"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How would you rate\nyour fitness level?",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Drag to adjust subtitle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Drag to adjust",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Custom Arc Gauge + Number Display matching Figma Screen 3 ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Arc Gauge Canvas
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            // Calculate angle or horizontal drag to adjust level
                            val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                            val newLevel = (1 + fraction * 4).toInt().coerceIn(1, 5)
                            onLevelChange(newLevel)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            val newLevel = (1 + fraction * 4).toInt().coerceIn(1, 5)
                            onLevelChange(newLevel)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 7.dp.toPx()
                    val arcSize = size.width * 0.85f
                    val topLeft = Offset((size.width - arcSize) / 2f, (size.height - arcSize) / 2f)

                    val startAngle = 145f
                    val totalSweep = 135f
                    val fraction = (level - 1) / 4f
                    val activeSweep = totalSweep * fraction

                    // Inactive Arc (Black/Dark)
                    drawArc(
                        color = BrandDark,
                        startAngle = startAngle + activeSweep,
                        sweepAngle = totalSweep - activeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active Arc (Orange)
                    if (activeSweep > 0f) {
                        drawArc(
                            color = BrandOrange,
                            startAngle = startAngle,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Draw end tick mark perpendicular to the arc end
                    val endAngleRad = Math.toRadians((startAngle + totalSweep).toDouble())
                    val radius = arcSize / 2f
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val tickStartX = cx + (radius - 8.dp.toPx()) * cos(endAngleRad).toFloat()
                    val tickStartY = cy + (radius - 8.dp.toPx()) * sin(endAngleRad).toFloat()
                    val tickEndX = cx + (radius + 8.dp.toPx()) * cos(endAngleRad).toFloat()
                    val tickEndY = cy + (radius + 8.dp.toPx()) * sin(endAngleRad).toFloat()
                    drawLine(
                        color = BrandDark,
                        start = Offset(tickStartX, tickStartY),
                        end = Offset(tickEndX, tickEndY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Orange Squircle Thumb with Circular Icon
                val fraction = (level - 1) / 4f
                val startAngle = 145f
                val totalSweep = 135f
                val currentAngleRad = Math.toRadians((startAngle + totalSweep * fraction).toDouble())
                val radiusDp = (190 * 0.85f / 2f).dp
                val thumbOffsetXDp = radiusDp * cos(currentAngleRad).toFloat()
                val thumbOffsetYDp = radiusDp * sin(currentAngleRad).toFloat()

                Box(
                    modifier = Modifier
                        .offset(x = thumbOffsetXDp, y = thumbOffsetYDp)
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sync,
                        contentDescription = "Thumb",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Number + Label on Right
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$level",
                    color = BrandDark,
                    fontSize = 92.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 94.sp
                )
                Text(
                    text = levelNames[level] ?: "",
                    color = BrandDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ──────────────────────── Step 4: Gender Screen (Figma Screen 4) ────────────────────────
@Composable
private fun GenderStep(
    selectedGender: String,
    onGenderSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What is your gender?",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Male Card
        GenderCard(
            label = "Male",
            iconSymbol = "♂",
            imageRes = R.drawable.runner_male,
            isSelected = selectedGender == "Male",
            onClick = { onGenderSelect("Male") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Female Card
        GenderCard(
            label = "Female",
            iconSymbol = "♀",
            imageRes = R.drawable.runner_female,
            isSelected = selectedGender == "Female",
            onClick = { onGenderSelect("Female") }
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Peach Skip Button matching Figma
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(PeachSkipButtonBg)
                .clickable { onGenderSelect("") }
                .padding(horizontal = 32.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Prefer to skip, thanks! ✕",
                color = BrandOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GenderCard(
    label: String,
    iconSymbol: String,
    imageRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        border = if (isSelected) BorderStroke(2.dp, BrandOrange) else BorderStroke(1.dp, InputBorderDefault),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BackgroundWhite else InputBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio circle
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (isSelected) BrandOrange else TextMuted,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BrandOrange)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Icon + Label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = iconSymbol,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Runner photo from Figma
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(82.dp)
                    .height(58.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

// ──────────────────────── Step 5: Goals (Figma Screen 6) ────────────────────────
@Composable
private fun GoalStep(
    selectedGoals: List<String>,
    onGoalToggle: (String) -> Unit
) {
    val goals = listOf(
        "⚖️  I wanna lose weight",
        "🤖  I wanna try AI Coach",
        "🏋️  I wanna get bulks",
        "⚡  I wanna gain endurance",
        "👍  Just trying out the app! 🤞"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's your fitness\ngoal/target?",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        Spacer(modifier = Modifier.height(28.dp))

        goals.forEach { goal ->
            val isSelected = selectedGoals.contains(goal)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onGoalToggle(goal) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) BrandOrange else InputBackground
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goal,
                        color = if (isSelected) Color.White else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    // Radio indicator
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                if (isSelected) Color.White else TextMuted,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}
