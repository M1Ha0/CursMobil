package com.example.mycursmob.presentation.assessment

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycursmob.R
import com.example.mycursmob.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
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
        // ── Top Bar with dynamic badge (1 of 6, 2 of 6, 3 of 6, etc.) ──
        val stepBadgeLabel = "${state.currentStep} of 6"

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
                6 -> AvatarSelectionStep(
                    selectedAvatar = state.avatarPath,
                    onAvatarChange = { viewModel.onAvatarChange(it) }
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * 5),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandOrange)
            )

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

        val displayWeight = if (isKg) weight else (weight * 2.205).toInt()
        val unit = if (isKg) "Kg" else "Lbs"
        Text(
            text = "$displayWeight $unit",
            color = Color.White,
            fontSize = 52.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        val weightRange = remember { (30..150).toList() }
        val initialIndex = remember { (weightRange.indexOf(weight)).coerceAtLeast(0) }
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
        val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        val coroutineScope = rememberCoroutineScope()

        // Real-time adaptive weight tracking as the user drags/scrolls the ruler
        LaunchedEffect(listState) {
            snapshotFlow {
                val layoutInfo = listState.layoutInfo
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                layoutInfo.visibleItemsInfo.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    kotlin.math.abs(itemCenter - viewportCenter)
                }?.index
            }.collect { centeredIndex ->
                if (centeredIndex != null && centeredIndex in weightRange.indices) {
                    val newWeight = weightRange[centeredIndex]
                    if (newWeight != weight) {
                        onWeightChange(newWeight)
                    }
                }
            }
        }

        // Programmatic scroll on external change (e.g. initial load or unit switch) without fighting user drag
        LaunchedEffect(weight) {
            if (!listState.isScrollInProgress) {
                val targetIndex = weightRange.indexOf(weight)
                if (targetIndex >= 0) {
                    val layoutInfo = listState.layoutInfo
                    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                    val currentIndex = layoutInfo.visibleItemsInfo.minByOrNull {
                        kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                    }?.index
                    if (currentIndex != targetIndex) {
                        listState.scrollToItem(targetIndex)
                    }
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
        ) {
            val halfWidth = maxWidth / 2

            // Center needle indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(3.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color.White)
            )

            LazyRow(
                state = listState,
                flingBehavior = snapFlingBehavior,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = halfWidth),
                verticalAlignment = Alignment.Top
            ) {
                items(weightRange) { w ->
                    val isMajor = w % 5 == 0
                    val isSelected = w == weight
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(18.dp)
                            .clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(weightRange.indexOf(w))
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 3.dp else if (isMajor) 2.dp else 1.dp)
                                .height(if (isSelected) 42.dp else if (isMajor) 32.dp else 18.dp)
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
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Drag to adjust",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fixed-layout container so slider position never shifts regardless of number/text size
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            val arcBoxSize = 275.dp
            val thumbSize = 58.dp
            val arcSizeRatio = 0.90f
            val startAngle = 130f
            val totalSweep = 175f
            val fraction = (level - 1) / 4f

            // 1. Slider Arc Box (Left-aligned with fixed position)
            Box(
                modifier = Modifier
                    .size(arcBoxSize)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val dx = change.position.x - cx
                            val dy = change.position.y - cy
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (angle < 0f) angle += 360f

                            val f = when {
                                angle < 130f && angle >= 60f -> 0f
                                angle > 305f || angle < 60f -> 1f
                                else -> ((angle - 130f) / 175f).coerceIn(0f, 1f)
                            }
                            val newLevel = (1 + f * 4).roundToInt().coerceIn(1, 5)
                            onLevelChange(newLevel)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val dx = offset.x - cx
                            val dy = offset.y - cy
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (angle < 0f) angle += 360f

                            val f = when {
                                angle < 130f && angle >= 60f -> 0f
                                angle > 305f || angle < 60f -> 1f
                                else -> ((angle - 130f) / 175f).coerceIn(0f, 1f)
                            }
                            val newLevel = (1 + f * 4).roundToInt().coerceIn(1, 5)
                            onLevelChange(newLevel)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 9.dp.toPx()
                    val arcPx = size.width * arcSizeRatio
                    val topLeft = Offset((size.width - arcPx) / 2f, (size.height - arcPx) / 2f)
                    val activeSweep = totalSweep * fraction
                    val radius = arcPx / 2f
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Inactive Arc (Black/Dark)
                    drawArc(
                        color = BrandDark,
                        startAngle = startAngle + activeSweep,
                        sweepAngle = totalSweep - activeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcPx, arcPx),
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
                            size = Size(arcPx, arcPx),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Draw tick marks across all 5 levels (1 to 5)
                    for (i in 0..4) {
                        val tickFraction = i / 4f
                        val tickAngleRad = Math.toRadians((startAngle + totalSweep * tickFraction).toDouble())
                        val tickStartX = cx + (radius - 12.dp.toPx()) * cos(tickAngleRad).toFloat()
                        val tickStartY = cy + (radius - 12.dp.toPx()) * sin(tickAngleRad).toFloat()
                        val tickEndX = cx + (radius + 12.dp.toPx()) * cos(tickAngleRad).toFloat()
                        val tickEndY = cy + (radius + 12.dp.toPx()) * sin(tickAngleRad).toFloat()
                        drawLine(
                            color = BrandDark,
                            start = Offset(tickStartX, tickStartY),
                            end = Offset(tickEndX, tickEndY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Orange Squircle Thumb (Enlarged)
                val currentAngleRad = Math.toRadians((startAngle + totalSweep * fraction).toDouble())
                val radiusDp = (275 * arcSizeRatio / 2f).dp
                val thumbOffsetXDp = radiusDp * cos(currentAngleRad).toFloat()
                val thumbOffsetYDp = radiusDp * sin(currentAngleRad).toFloat()

                Box(
                    modifier = Modifier
                        .offset(x = thumbOffsetXDp, y = thumbOffsetYDp)
                        .size(thumbSize)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BrandOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Sync,
                        contentDescription = "Thumb",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 2. Fixed Number and Description (Strictly fixed width & position so digit never moves)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(170.dp)
                    .padding(end = 4.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$level",
                    color = BrandDark,
                    fontSize = 112.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 110.sp,
                    modifier = Modifier.width(170.dp)
                )
                Text(
                    text = levelNames[level] ?: "",
                    color = BrandDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    modifier = Modifier.width(170.dp)
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
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What is your gender?",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))

        GenderCard(
            label = "Male",
            iconSymbol = "♂",
            imageRes = R.drawable.gender_male_bg,
            isSelected = selectedGender == "Male",
            onClick = { onGenderSelect("Male") }
        )

        Spacer(modifier = Modifier.height(18.dp))

        GenderCard(
            label = "Female",
            iconSymbol = "♀",
            imageRes = R.drawable.gender_female_bg,
            isSelected = selectedGender == "Female",
            onClick = { onGenderSelect("Female") }
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFFFF0E5))
                .clickable { onGenderSelect("") },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Prefer to skip, thanks!  ✕",
                color = BrandOrange,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
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
    val cardBg = if (isSelected) Color(0xFFFFF7F2) else Color(0xFFF6F7F9)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        border = if (isSelected) BorderStroke(2.dp, BrandOrange) else BorderStroke(1.dp, Color(0xFFEBECEF)),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo on the right side
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.56f)
                    .clip(RoundedCornerShape(topEnd = 22.dp, bottomEnd = 22.dp))
            )

            // Gradient overlay to smoothly fade the image left edge into card background
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.56f)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                cardBg,
                                cardBg.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Text info & Checkbox on the left
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Gender label & symbol
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = iconSymbol,
                        color = BrandDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = BrandDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Checkbox matching Figma (rounded square with inner marker when selected)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .border(
                            width = 2.dp,
                            color = if (isSelected) BrandDark else Color(0xFF8E8E93),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(BrandDark)
                        )
                    }
                }
            }
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

// ──────────────────────── Step 6: Select your Avatar (Figma Screen 7) ────────────────────────
@Composable
private fun AvatarSelectionStep(
    selectedAvatar: String,
    onAvatarChange: (String) -> Unit
) {
    val context = LocalContext.current

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
                onAvatarChange(file.absolutePath)
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Premade Avatars Carousel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssessmentPremadeAvatar(
                imageRes = R.drawable.avatar_premade_beanie,
                isSelected = selectedAvatar == "premade_beanie",
                size = 78.dp,
                onClick = { onAvatarChange("premade_beanie") }
            )

            Spacer(modifier = Modifier.width(12.dp))

            AssessmentPremadeAvatar(
                imageRes = R.drawable.avatar_premade_bunny,
                isSelected = selectedAvatar == "premade_bunny",
                size = 106.dp,
                isPrimary = true,
                onClick = { onAvatarChange("premade_bunny") }
            )

            Spacer(modifier = Modifier.width(12.dp))

            AssessmentPremadeAvatar(
                imageRes = R.drawable.avatar_premade_cat,
                isSelected = selectedAvatar == "premade_cat",
                size = 78.dp,
                onClick = { onAvatarChange("premade_cat") }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Select your Avatar",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "We have 23 custom premade avatars, or\nyou can upload profile locally",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dashed Circle Upload Container
        val customBitmap = remember(selectedAvatar) {
            if (selectedAvatar.isNotEmpty() && File(selectedAvatar).exists()) {
                try {
                    BitmapFactory.decodeFile(selectedAvatar)?.asImageBitmap()
                } catch (_: Exception) { null }
            } else null
        }

        Box(
            modifier = Modifier
                .size(116.dp)
                .clickable { photoPickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
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
                Image(
                    bitmap = customBitmap,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(102.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.FileUpload,
                    contentDescription = "Upload",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "or Upload from Local File",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { photoPickerLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = "Max 5mb, Format: jpg,png",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun AssessmentPremadeAvatar(
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
