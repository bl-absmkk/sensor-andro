package com.and.sensordua.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.and.sensordua.sensor.AccelerometerData
import com.and.sensordua.ui.theme.LocalSensorStudioColors

/**
 * Kartu UI untuk Motion Playground (Sensor Accelerometer / Motion).
 * Mensimulasikan pergerakan bola fisik di dalam area permainan berbasis kemiringan ponsel.
 * Menggunakan graphicsLayer untuk rendering bola yang halus, responsif, dan hemat daya komputasi.
 */
@Composable
fun MotionPlaygroundCard(
    accelData: AccelerometerData,
    isAvailable: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalSensorStudioColors.current
    val density = LocalDensity.current

    // Menggunakan rememberUpdatedState agar loop coroutine selalu membaca data sensor terkini
    // dan tidak mengalami stale state closure capture
    val currentAccelData by rememberUpdatedState(accelData)

    // Ukuran bola
    val ballDiameterDp = 44.dp
    val ballDiameterPx = with(density) { ballDiameterDp.toPx() }
    val ballRadiusPx = ballDiameterPx / 2f

    // Physics State: Koordinat offset bola dari titik tengah arena (dalam piksel)
    var ballOffsetX by remember { mutableFloatStateOf(0f) }
    var ballOffsetY by remember { mutableFloatStateOf(0f) }
    var velX by remember { mutableFloatStateOf(0f) }
    var velY by remember { mutableFloatStateOf(0f) }

    // Dimensi aktual arena playground (diukur secara dinamis)
    var arenaWidthPx by remember { mutableFloatStateOf(0f) }
    var arenaHeightPx by remember { mutableFloatStateOf(0f) }

    // Offset kalibrasi saat tombol Reset ditekan
    var calibOffsetX by remember { mutableFloatStateOf(0f) }
    var calibOffsetY by remember { mutableFloatStateOf(0f) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        TiltIcon(tint = colors.accent)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Motion Playground",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Sensor Gerak (TYPE_ACCELEROMETER)",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                StatusBadge(
                    isAvailable = isAvailable,
                    isActive = isActive
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Miringkan ponsel ke segala arah untuk menggerakkan bola secara interaktif.",
                fontSize = 13.sp,
                color = colors.textSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Arena Bermain (Playground)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.playgroundBg)
                    .border(1.dp, colors.outline.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .onGloballyPositioned { coordinates ->
                        arenaWidthPx = coordinates.size.width.toFloat()
                        arenaHeightPx = coordinates.size.height.toFloat()
                    },
                contentAlignment = Alignment.Center
            ) {
                // Inisialisasi awal batas dari BoxWithConstraints
                val initialWidthPx = with(density) { maxWidth.toPx() }
                val initialHeightPx = with(density) { maxHeight.toPx() }

                val effectiveWidth = if (arenaWidthPx > 0f) arenaWidthPx else initialWidthPx
                val effectiveHeight = if (arenaHeightPx > 0f) arenaHeightPx else initialHeightPx

                val maxBoundX = ((effectiveWidth - ballDiameterPx) / 2f).coerceAtLeast(0f)
                val maxBoundY = ((effectiveHeight - ballDiameterPx) / 2f).coerceAtLeast(0f)

                // Loop simulasi fisika per-frame (60-120 FPS)
                LaunchedEffect(isActive, isAvailable) {
                    if (!isActive || !isAvailable) return@LaunchedEffect

                    var lastTimeNanos = 0L

                    while (true) {
                        withFrameNanos { frameTimeNanos ->
                            if (lastTimeNanos != 0L) {
                                val dt = ((frameTimeNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.005f, 0.05f)

                                // Membaca data accelerometer terbaru secara aman dari state Compose
                                val latest = currentAccelData
                                val relativeAx = latest.x - calibOffsetX
                                val relativeAy = latest.y - calibOffsetY

                                // Penentuan arah gerakan:
                                // Sumbu X layar: kemiringan ke kanan (relativeAx < 0) -> dorong bola ke kanan (+X)
                                val forceX = -relativeAx
                                // Sumbu Y layar: kemiringan ke bawah (relativeAy > 0) -> dorong bola ke bawah (+Y)
                                val forceY = relativeAy

                                if (maxBoundX > 0f && maxBoundY > 0f) {
                                    // Target perpindahan proporsional terhadap kemiringan
                                    // 7.5 m/s² setara dengan kemiringan ~50 derajat untuk jangkauan penuh
                                    val targetX = (forceX / 7.5f).coerceIn(-1f, 1f) * maxBoundX
                                    val targetY = (forceY / 7.5f).coerceIn(-1f, 1f) * maxBoundY

                                    // Dinamika spring-damper yang responsif dan tidak terkunci di batas
                                    val springK = 14f
                                    val damping = 0.82f

                                    velX = (velX + (targetX - ballOffsetX) * springK * dt) * damping
                                    velY = (velY + (targetY - ballOffsetY) * springK * dt) * damping

                                    ballOffsetX = (ballOffsetX + velX).coerceIn(-maxBoundX, maxBoundX)
                                    ballOffsetY = (ballOffsetY + velY).coerceIn(-maxBoundY, maxBoundY)
                                }
                            }
                            lastTimeNanos = frameTimeNanos
                        }
                    }
                }

                if (!isAvailable) {
                    // Tampilan saat hardware tidak tersedia
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Sensor Accelerometer Tidak Tersedia",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Perangkat keras accelerometer tidak terdeteksi pada perangkat ini.",
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    // 1. Grid target lingkaran tengah (Radar / Spirit-level)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f

                        drawCircle(
                            color = colors.outline.copy(alpha = 0.5f),
                            radius = 45.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = colors.outline.copy(alpha = 0.25f),
                            radius = 85.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Sumbu Crosshair tipis
                        drawLine(
                            color = colors.outline.copy(alpha = 0.4f),
                            start = Offset(centerX - 16.dp.toPx(), centerY),
                            end = Offset(centerX + 16.dp.toPx(), centerY),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = colors.outline.copy(alpha = 0.4f),
                            start = Offset(centerX, centerY - 16.dp.toPx()),
                            end = Offset(centerX, centerY + 16.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // 2. Bola Fisik dengan graphicsLayer (di-render terpisah untuk performa optimal)
                    Box(
                        modifier = Modifier
                            .size(ballDiameterDp)
                            .graphicsLayer {
                                translationX = ballOffsetX
                                translationY = ballOffsetY
                            }
                    ) {
                        // Bayangan lembut bola
                        Box(
                            modifier = Modifier
                                .size(ballDiameterDp)
                                .graphicsLayer {
                                    translationX = 3f
                                    translationY = 5f
                                    alpha = 0.22f
                                }
                                .clip(CircleShape)
                                .background(Color.Black)
                        )

                        // Gambar permukaan bola 3D dengan radial gradient & highlight
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val r = size.width / 2f
                            val center = Offset(r, r)

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        colors.ballStart,
                                        colors.ballEnd
                                    ),
                                    center = Offset(r * 0.65f, r * 0.65f),
                                    radius = r * 1.35f
                                ),
                                radius = r,
                                center = center
                            )

                            // Specular highlight refleksi cahaya
                            drawCircle(
                                color = Color.White.copy(alpha = 0.55f),
                                radius = r * 0.28f,
                                center = Offset(r * 0.62f, r * 0.62f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Telemetry: Nilai X, Y, Z
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AxisMetricBadge(
                    label = "X",
                    sublabel = "Kiri/Kanan",
                    value = accelData.x,
                    accentColor = colors.accent,
                    modifier = Modifier.weight(1f)
                )
                AxisMetricBadge(
                    label = "Y",
                    sublabel = "Atas/Bawah",
                    value = accelData.y,
                    accentColor = colors.accent,
                    modifier = Modifier.weight(1f)
                )
                AxisMetricBadge(
                    label = "Z",
                    sublabel = "Gravitasi",
                    value = accelData.z,
                    accentColor = colors.accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Reset Position: Mengembalikan bola ke tengah dan mengkalibrasi orientasi saat ini
            Button(
                onClick = {
                    calibOffsetX = currentAccelData.x
                    calibOffsetY = currentAccelData.y
                    ballOffsetX = 0f
                    ballOffsetY = 0f
                    velX = 0f
                    velY = 0f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = colors.onAccent
                )
            ) {
                ResetIcon(tint = colors.onAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Position",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Kartu metrik kecil untuk masing-masing sumbu percepatan (X, Y, Z).
 */
@Composable
private fun AxisMetricBadge(
    label: String,
    sublabel: String,
    value: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalSensorStudioColors.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sublabel,
                    fontSize = 10.sp,
                    color = colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format("%+.2f", value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.textPrimary
            )
            Text(
                text = "m/s²",
                fontSize = 10.sp,
                color = colors.textSecondary
            )
        }
    }
}

/**
 * Ikon orientasi kemiringan (Tilt Icon) dengan Canvas.
 */
@Composable
private fun TiltIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height

        // Gambar outline smartphone yang dimiringkan halus
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.25f, h * 0.15f),
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.7f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
        // Dot tombol home / sensor
        drawCircle(
            color = tint,
            radius = 1.5.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.76f)
        )
    }
}

/**
 * Ikon Reset melingkar dengan Canvas.
 */
@Composable
private fun ResetIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.width * 0.38f

        // Panah busur melingkar
        drawArc(
            color = tint,
            startAngle = 40f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(center.x - r, center.y - r),
            size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
