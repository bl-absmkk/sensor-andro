package com.and.sensordua.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.and.sensordua.sensor.SensorDataManager
import com.and.sensordua.ui.components.AppFooter
import com.and.sensordua.ui.components.EnvironmentCard
import com.and.sensordua.ui.components.MotionPlaygroundCard
import com.and.sensordua.ui.components.SensorInfoCard
import com.and.sensordua.ui.components.TopHeader
import com.and.sensordua.ui.theme.LocalSensorStudioColors

/**
 * Layar dashboard utama Sensor Studio — Smart Environment.
 * Terorganisasi secara responsif dan scrollable untuk berbagai ukuran layar smartphone.
 */
@Composable
fun SensorStudioScreen(
    sensorDataManager: SensorDataManager,
    modifier: Modifier = Modifier
) {
    val colors = LocalSensorStudioColors.current
    val scrollState = rememberScrollState()

    val isAnySensorActive = sensorDataManager.isAccelerometerActive || sensorDataManager.isLightSensorActive

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // A. Top Header
                TopHeader(
                    isAnySensorActive = isAnySensorActive
                )

                // B. Environment Card (Sensor Cahaya & Tema Adaptif)
                EnvironmentCard(
                    lux = sensorDataManager.lightLux,
                    mode = sensorDataManager.currentLightMode,
                    isAvailable = sensorDataManager.isLightSensorAvailable,
                    isActive = sensorDataManager.isLightSensorActive
                )

                // C. Motion Playground Card (Accelerometer & Bola Fisik)
                MotionPlaygroundCard(
                    accelData = sensorDataManager.accelerometerData,
                    isAvailable = sensorDataManager.isAccelerometerAvailable,
                    isActive = sensorDataManager.isAccelerometerActive
                )

                // D. Sensor Information (Daftar hardware & lifecycle overview)
                SensorInfoCard(
                    isAccelAvailable = sensorDataManager.isAccelerometerAvailable,
                    isAccelActive = sensorDataManager.isAccelerometerActive,
                    isLightAvailable = sensorDataManager.isLightSensorAvailable,
                    isLightActive = sensorDataManager.isLightSensorActive
                )

                // E. Footer
                AppFooter()

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
