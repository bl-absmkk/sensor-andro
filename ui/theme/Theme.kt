package com.and.sensordua.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.and.sensordua.sensor.LightMode

/**
 * Kumpulan warna kustom untuk Sensor Studio yang disesuaikan secara dinamis
 * berdasarkan intensitas sensor cahaya.
 */
data class SensorStudioColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val accent: Color,
    val accentContainer: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val outline: Color,
    val playgroundBg: Color,
    val ballStart: Color,
    val ballEnd: Color,
    val isDark: Boolean
)

val LocalSensorStudioColors = staticCompositionLocalOf {
    SensorStudioColors(
        background = IndoorBackground,
        surface = IndoorSurface,
        surfaceVariant = IndoorSurfaceVariant,
        accent = IndoorAccent,
        accentContainer = IndoorAccentContainer,
        onAccent = IndoorOnAccent,
        textPrimary = IndoorTextPrimary,
        textSecondary = IndoorTextSecondary,
        outline = IndoorOutline,
        playgroundBg = IndoorPlaygroundBg,
        ballStart = IndoorBallStart,
        ballEnd = IndoorBallEnd,
        isDark = false
    )
}

@Composable
fun SensorStudioTheme(
    lightMode: LightMode = LightMode.INDOOR,
    content: @Composable () -> Unit
) {
    val animDuration = 600

    // Animasi perubahan warna tema secara halus saat nilai sensor cahaya melintasi threshold
    val targetBackground = when (lightMode) {
        LightMode.NIGHT -> NightBackground
        LightMode.INDOOR -> IndoorBackground
        LightMode.BRIGHT -> BrightBackground
    }
    val targetSurface = when (lightMode) {
        LightMode.NIGHT -> NightSurface
        LightMode.INDOOR -> IndoorSurface
        LightMode.BRIGHT -> BrightSurface
    }
    val targetSurfaceVariant = when (lightMode) {
        LightMode.NIGHT -> NightSurfaceVariant
        LightMode.INDOOR -> IndoorSurfaceVariant
        LightMode.BRIGHT -> BrightSurfaceVariant
    }
    val targetAccent = when (lightMode) {
        LightMode.NIGHT -> NightAccent
        LightMode.INDOOR -> IndoorAccent
        LightMode.BRIGHT -> BrightAccent
    }
    val targetAccentContainer = when (lightMode) {
        LightMode.NIGHT -> NightAccentContainer
        LightMode.INDOOR -> IndoorAccentContainer
        LightMode.BRIGHT -> BrightAccentContainer
    }
    val targetOnAccent = when (lightMode) {
        LightMode.NIGHT -> NightOnAccent
        LightMode.INDOOR -> IndoorOnAccent
        LightMode.BRIGHT -> BrightOnAccent
    }
    val targetTextPrimary = when (lightMode) {
        LightMode.NIGHT -> NightTextPrimary
        LightMode.INDOOR -> IndoorTextPrimary
        LightMode.BRIGHT -> BrightTextPrimary
    }
    val targetTextSecondary = when (lightMode) {
        LightMode.NIGHT -> NightTextSecondary
        LightMode.INDOOR -> IndoorTextSecondary
        LightMode.BRIGHT -> BrightTextSecondary
    }
    val targetOutline = when (lightMode) {
        LightMode.NIGHT -> NightOutline
        LightMode.INDOOR -> IndoorOutline
        LightMode.BRIGHT -> BrightOutline
    }
    val targetPlaygroundBg = when (lightMode) {
        LightMode.NIGHT -> NightPlaygroundBg
        LightMode.INDOOR -> IndoorPlaygroundBg
        LightMode.BRIGHT -> BrightPlaygroundBg
    }
    val targetBallStart = when (lightMode) {
        LightMode.NIGHT -> NightBallStart
        LightMode.INDOOR -> IndoorBallStart
        LightMode.BRIGHT -> BrightBallStart
    }
    val targetBallEnd = when (lightMode) {
        LightMode.NIGHT -> NightBallEnd
        LightMode.INDOOR -> IndoorBallEnd
        LightMode.BRIGHT -> BrightBallEnd
    }

    val animatedColors = SensorStudioColors(
        background = animateColorAsState(targetBackground, tween(animDuration), label = "bg").value,
        surface = animateColorAsState(targetSurface, tween(animDuration), label = "surface").value,
        surfaceVariant = animateColorAsState(targetSurfaceVariant, tween(animDuration), label = "surfaceVar").value,
        accent = animateColorAsState(targetAccent, tween(animDuration), label = "accent").value,
        accentContainer = animateColorAsState(targetAccentContainer, tween(animDuration), label = "accentCont").value,
        onAccent = animateColorAsState(targetOnAccent, tween(animDuration), label = "onAccent").value,
        textPrimary = animateColorAsState(targetTextPrimary, tween(animDuration), label = "textPrim").value,
        textSecondary = animateColorAsState(targetTextSecondary, tween(animDuration), label = "textSec").value,
        outline = animateColorAsState(targetOutline, tween(animDuration), label = "outline").value,
        playgroundBg = animateColorAsState(targetPlaygroundBg, tween(animDuration), label = "pgBg").value,
        ballStart = animateColorAsState(targetBallStart, tween(animDuration), label = "ballStart").value,
        ballEnd = animateColorAsState(targetBallEnd, tween(animDuration), label = "ballEnd").value,
        isDark = lightMode == LightMode.NIGHT
    )

    val materialColorScheme = if (animatedColors.isDark) {
        darkColorScheme(
            primary = animatedColors.accent,
            onPrimary = animatedColors.onAccent,
            primaryContainer = animatedColors.accentContainer,
            background = animatedColors.background,
            surface = animatedColors.surface,
            surfaceVariant = animatedColors.surfaceVariant,
            outline = animatedColors.outline
        )
    } else {
        lightColorScheme(
            primary = animatedColors.accent,
            onPrimary = animatedColors.onAccent,
            primaryContainer = animatedColors.accentContainer,
            background = animatedColors.background,
            surface = animatedColors.surface,
            surfaceVariant = animatedColors.surfaceVariant,
            outline = animatedColors.outline
        )
    }

    CompositionLocalProvider(LocalSensorStudioColors provides animatedColors) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography,
            content = content
        )
    }
}