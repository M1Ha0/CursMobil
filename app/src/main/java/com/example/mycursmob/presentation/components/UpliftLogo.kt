package com.example.mycursmob.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mycursmob.R

/**
 * Uplift Logo from drawable image resources (R.drawable.ic_uplift_logo_white)
 */
@Composable
fun UpliftLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_uplift_logo_white),
        contentDescription = "Uplift.ai Logo",
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

/**
 * Uplift Orange Logo Badge from drawable image resources (R.drawable.ic_uplift_badge)
 */
@Composable
fun UpliftLogoBadge(
    modifier: Modifier = Modifier,
    badgeSize: Dp = 58.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_uplift_badge),
        contentDescription = "Uplift.ai Badge",
        modifier = modifier.size(badgeSize),
        contentScale = ContentScale.Fit
    )
}
