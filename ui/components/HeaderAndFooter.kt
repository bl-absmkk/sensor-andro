package com.and.sensordua.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.and.sensordua.ui.theme.LocalSensorStudioColors
import com.and.sensordua.ui.theme.StatusActive

/**
 * Top Header: Menampilkan identitas Sensor Studio dan indikator pemantauan aktif.
 */
@Composable
fun TopHeader(
    isAnySensorActive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalSensorStudioColors.current

    // Animasi denyut (pulse) indikator live
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Sensor Studio",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Explore your phone sensors",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary
            )
        }

        // Live Monitoring Status Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(if (isAnySensorActive) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isAnySensorActive) StatusActive else colors.textSecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAnySensorActive) "LIVE" else "IDLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAnySensorActive) StatusActive else colors.textSecondary
                )
            }
        }
    }
}

/**
 * Footer: Menampilkan informasi platform dan teknologi.
 */
@Composable
fun AppFooter(
    modifier: Modifier = Modifier
) {
    val colors = LocalSensorStudioColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Built with Kotlin & Jetpack Compose",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Real-time Android Sensor Demo • Material 3",
            fontSize = 11.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
