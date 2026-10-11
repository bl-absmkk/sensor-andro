package com.and.sensordua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.and.sensordua.sensor.SensorDataManager
import com.and.sensordua.ui.SensorStudioScreen
import com.and.sensordua.ui.theme.SensorStudioTheme

/**
 * MainActivity untuk aplikasi Sensor Studio — Smart Environment.
 *
 * Mengelola lifecycle sensor secara eksplisit:
 * - onResume(): mendaftarkan SensorEventListener untuk sensor yang tersedia (Accelerometer & Light).
 * - onPause(): melepaskan seluruh listener untuk menghemat daya baterai dan mencegah memory leak.
 */
class MainActivity : ComponentActivity() {

    private lateinit var sensorDataManager: SensorDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Menginisialisasi SensorDataManager menggunakan applicationContext untuk keamanan memori
        sensorDataManager = SensorDataManager(applicationContext)

        setContent {
            // Tema UI beradaptasi secara dinamis mengikuti pembacaan sensor cahaya
            SensorStudioTheme(lightMode = sensorDataManager.currentLightMode) {
                SensorStudioScreen(sensorDataManager = sensorDataManager)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Mendaftarkan listener ketika Activity berada di foreground
        sensorDataManager.registerListeners()
    }

    override fun onPause() {
        // Melepaskan listener ketika Activity berada di background
        sensorDataManager.unregisterListeners()
        super.onPause()
    }
}