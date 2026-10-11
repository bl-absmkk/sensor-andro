package com.and.sensordua.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Data model untuk pembacaan Accelerometer (Motion).
 * @property x Percepatan sumbu X dalam m/s² (horizontal perangkat).
 * @property y Percepatan sumbu Y dalam m/s² (vertikal perangkat).
 * @property z Percepatan sumbu Z dalam m/s² (tegak lurus layar).
 */
data class AccelerometerData(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
)

/**
 * Mode pencahayaan lingkungan berdasarkan nilai Lux dari Sensor Cahaya.
 */
enum class LightMode(
    val title: String,
    val badgeLabel: String,
    val description: String
) {
    NIGHT(
        title = "Night Mode",
        badgeLabel = "< 50 lux",
        description = "Kondisi cahaya minim / gelap. UI menggunakan tema gelap dengan aksen biru untuk kenyamanan mata."
    ),
    INDOOR(
        title = "Indoor Mode",
        badgeLabel = "50 - 499 lux",
        description = "Pencahayaan ruangan ideal. UI menggunakan latar belakang netral dengan aksen ungu yang elegan."
    ),
    BRIGHT(
        title = "Bright Mode",
        badgeLabel = "≥ 500 lux",
        description = "Pencahayaan terang / luar ruangan. UI menggunakan tema terang dengan aksen kuning berdaya kontras tinggi."
    )
}

/**
 * Pengelola sensor Android terpusat untuk Sensor Studio.
 * Menangani pendaftaran dan pelepasan listener secara aman sesuai siklus hidup Activity (onResume & onPause).
 */
class SensorDataManager(context: Context) {

    companion object {
        private const val TAG = "SensorStudio"
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    // Referensi hardware sensor
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

    // Status ketersediaan perangkat keras
    val isAccelerometerAvailable: Boolean = accelerometer != null
    val isLightSensorAvailable: Boolean = lightSensor != null

    // State Compose yang diamati oleh UI secara real-time
    var accelerometerData by mutableStateOf(AccelerometerData())
        private set

    var lightLux by mutableStateOf<Float?>(null)
        private set

    var currentLightMode by mutableStateOf(LightMode.INDOOR)
        private set

    var isAccelerometerActive by mutableStateOf(false)
        private set

    var isLightSensorActive by mutableStateOf(false)
        private set

    // Status apakah listener saat ini sedang terdaftar
    private var isRegistered = false

    // Low-pass filter smoothing untuk accelerometer data mentah
    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 0f
    private var isFirstAccelSample = true
    private val alpha = 0.25f // Koefisien smoothing low-pass filter

    // Timestamp throttling untuk logcat output
    private var lastAccelLogTime = 0L
    private var lastLightLogTime = 0L

    init {
        Log.d(TAG, "SensorDataManager init - Accelerometer: ${accelerometer?.name ?: "NULL"}, LightSensor: ${lightSensor?.name ?: "NULL"}")
    }

    /**
     * Listener untuk Accelerometer (Sensor Gerak).
     * Menggunakan low-pass filter agar pembacaan getaran halus dan stabil.
     */
    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

            val rawX = event.values.getOrNull(0) ?: 0f
            val rawY = event.values.getOrNull(1) ?: 0f
            val rawZ = event.values.getOrNull(2) ?: 0f

            // Logging sementara untuk verifikasi nilai aktual event.values (setiap 500ms)
            val now = System.currentTimeMillis()
            if (now - lastAccelLogTime > 500) {
                Log.d(TAG, "Accelerometer values: X=$rawX, Y=$rawY, Z=$rawZ")
                lastAccelLogTime = now
            }

            // Inisialisasi awal agar tidak ada lag dari (0,0,0)
            if (isFirstAccelSample) {
                filteredX = rawX
                filteredY = rawY
                filteredZ = rawZ
                isFirstAccelSample = false
            } else {
                // Low-pass filter sederhana: output = output + alpha * (input - output)
                filteredX += alpha * (rawX - filteredX)
                filteredY += alpha * (rawY - filteredY)
                filteredZ += alpha * (rawZ - filteredZ)
            }

            // Update state Compose secara atomic
            accelerometerData = AccelerometerData(
                x = filteredX,
                y = filteredY,
                z = filteredZ
            )
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d(TAG, "Accelerometer accuracy changed: $accuracy")
        }
    }

    /**
     * Listener untuk Sensor Cahaya Ambient (Sensor Lingkungan).
     */
    private val lightSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null || event.sensor.type != Sensor.TYPE_LIGHT) return

            val lux = event.values.getOrNull(0) ?: return
            lightLux = lux

            val now = System.currentTimeMillis()
            if (now - lastLightLogTime > 1000) {
                Log.d(TAG, "LightSensor lux: $lux")
                lastLightLogTime = now
            }

            // Logika pergantian tema berdasarkan ambang batas lux:
            // - Di bawah 50 lux: Night Mode
            // - 50 hingga kurang dari 500 lux: Indoor Mode
            // - 500 lux ke atas: Bright Mode
            currentLightMode = when {
                lux < 50f -> LightMode.NIGHT
                lux < 500f -> LightMode.INDOOR
                else -> LightMode.BRIGHT
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d(TAG, "LightSensor accuracy changed: $accuracy")
        }
    }

    /**
     * Daftarkan seluruh sensor yang tersedia.
     * Dipanggil pada onResume() Activity.
     */
    fun registerListeners() {
        if (isRegistered || sensorManager == null) return

        if (accelerometer != null) {
            val registered = sensorManager.registerListener(
                accelerometerListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            isAccelerometerActive = registered
            Log.d(TAG, "Accelerometer registered with SENSOR_DELAY_GAME: $registered")
        }

        if (lightSensor != null) {
            val registered = sensorManager.registerListener(
                lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            isLightSensorActive = registered
            Log.d(TAG, "LightSensor registered with SENSOR_DELAY_UI: $registered")
        }

        isRegistered = true
    }

    /**
     * Hentikan seluruh listener sensor untuk menghemat baterai dan mencegah memory leak.
     * Dipanggil pada onPause() Activity.
     */
    fun unregisterListeners() {
        if (!isRegistered || sensorManager == null) return

        try {
            sensorManager.unregisterListener(accelerometerListener)
            sensorManager.unregisterListener(lightSensorListener)
            Log.d(TAG, "All sensor listeners successfully unregistered.")
        } finally {
            isAccelerometerActive = false
            isLightSensorActive = false
            isRegistered = false
            isFirstAccelSample = true
        }
    }
}
