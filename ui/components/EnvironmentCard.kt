package com.and.sensordua.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.and.sensordua.sensor.LightMode
import com.and.sensordua.ui.theme.LocalSensorStudioColors
import com.and.sensordua.ui.theme.StatusActive
import com.and.sensordua.ui.theme.StatusUnavailable
import kotlin.math.cos
import kotlin.math.sin

/**
 * Kartu UI untuk menampilkan metrik Ambient Light Sensor (Kategori Environment).
 * Secara langsung merefleksikan nilai lux dan mode adaptif tema.
 */
@Composable
fun EnvironmentCard(
    lux: Float?,
    mode: LightMode,
    isAvailable: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalSensorStudioColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.outline, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Icon + Title + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        LightModeIcon(mode = mode, tint = colors.accent)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ambient Light",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Sensor Lingkungan (TYPE_LIGHT)",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                // Sensor Status Chip
                StatusBadge(
                    isAvailable = isAvailable,
                    isActive = isActive
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lux Display & Mode Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "INTENSITAS CAHAYA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (lux != null) String.format("%.1f", lux) else "—",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "lux",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                }

                // Mode Badge
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "modeBadgeAnim"
                ) { currentMode ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentContainer)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = currentMode.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual meter bar lux (logarithmic / normalized visual bar)
            val meterProgress = if (lux != null) {
                (lux / 1000f).coerceIn(0f, 1f)
            } else 0.5f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(meterProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.accent)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Description Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant)
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kondisi: ${mode.badgeLabel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.accent
                        )
                        Text(
                            text = if (isAvailable) "Auto-Theme Aktif" else "Hardware Tidak Tersedia",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAvailable) {
                            mode.description
                        } else {
                            "Sensor cahaya tidak terdeteksi pada perangkat ini. Menggunakan tema Indoor default secara stabil."
                        },
                        fontSize = 13.sp,
                        color = colors.textPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * Chip penanda status sensor (Active / Unavailable).
 */
@Composable
fun StatusBadge(
    isAvailable: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusText) = when {
        !isAvailable -> Pair(StatusUnavailable, "Unavailable")
        isActive -> Pair(StatusActive, "Active")
        else -> Pair(Color.Gray, "Paused")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(statusColor.copy(alpha = 0.12f))
            .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(statusColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = statusText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = statusColor
        )
    }
}

/**
 * Ikon dinamis untuk setiap mode cahaya yang digambar presisi dengan Canvas.
 */
@Composable
private fun LightModeIcon(
    mode: LightMode,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 4f

        when (mode) {
            LightMode.NIGHT -> {
                // Crescent Moon
                drawCircle(
                    color = tint,
                    radius = radius * 1.1f,
                    center = center
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = radius * 0.95f,
                    center = Offset(center.x + radius * 0.55f, center.y - radius * 0.35f),
                    blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                )
            }
            LightMode.INDOOR -> {
                // Lamp / Lightbulb glyph
                drawCircle(
                    color = tint,
                    radius = radius * 0.85f,
                    center = Offset(center.x, center.y - 2.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawLine(
                    color = tint,
                    start = Offset(center.x - 3.dp.toPx(), center.y + radius * 0.8f),
                    end = Offset(center.x + 3.dp.toPx(), center.y + radius * 0.8f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            LightMode.BRIGHT -> {
                // Sun with rays
                drawCircle(
                    color = tint,
                    radius = radius * 0.65f,
                    center = center
                )
                for (i in 0 until 8) {
                    val angle = (i * Math.PI / 4).toFloat()
                    val startR = radius * 0.95f
                    val endR = radius * 1.35f
                    val startX = center.x + cos(angle) * startR
                    val startY = center.y + sin(angle) * startR
                    val endX = center.x + cos(angle) * endR
                    val endY = center.y + sin(angle) * endR
                    drawLine(
                        color = tint,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
