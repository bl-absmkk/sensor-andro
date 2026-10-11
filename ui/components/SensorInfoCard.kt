package com.and.sensordua.ui.components

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.and.sensordua.ui.theme.LocalSensorStudioColors

/**
 * Kartu ringkasan spesifikasi kedua sensor yang diimplementasikan.
 */
@Composable
fun SensorInfoCard(
    isAccelAvailable: Boolean,
    isAccelActive: Boolean,
    isLightAvailable: Boolean,
    isLightActive: Boolean,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hardware Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Sensor API & Kategori Android",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accentContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "2 Sensor",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Baris Sensor 1: Accelerometer
            SensorItemRow(
                sensorName = "Accelerometer",
                category = "Motion",
                sensorType = "TYPE_ACCELEROMETER",
                function = "Percepatan sumbu X, Y, Z & kemiringan bola",
                isAvailable = isAccelAvailable,
                isActive = isAccelActive
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = colors.outline.copy(alpha = 0.6f)
            )

            // Baris Sensor 2: Light Sensor
            SensorItemRow(
                sensorName = "Light Sensor",
                category = "Environment",
                sensorType = "TYPE_LIGHT",
                function = "Intensitas cahaya (lux) & tema adaptif otomatis",
                isAvailable = isLightAvailable,
                isActive = isLightActive
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Penjelasan Lifecycle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lifecycle-Safe: Listener didaftarkan pada onResume() dan dilepas otomatis pada onPause() guna mencegah baterai boros & memory leak.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorItemRow(
    sensorName: String,
    category: String,
    sensorType: String,
    function: String,
    isAvailable: Boolean,
    isActive: Boolean
) {
    val colors = LocalSensorStudioColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sensorName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accent
                    )
                }
            }

            StatusBadge(isAvailable = isAvailable, isActive = isActive)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = sensorType,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            color = colors.accent
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = function,
            fontSize = 12.sp,
            color = colors.textSecondary
        )
    }
}
