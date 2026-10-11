package com.and.sensordua.ui.theme

import androidx.compose.ui.graphics.Color

// Default template colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// --- Night Mode Color Palette (Lux < 50) ---
val NightBackground = Color(0xFF0B1120)
val NightSurface = Color(0xFF162032)
val NightSurfaceVariant = Color(0xFF1E293B)
val NightAccent = Color(0xFF38BDF8)          // Sky / Cyan Blue
val NightAccentContainer = Color(0xFF0C4A6E)
val NightOnAccent = Color(0xFF082F49)
val NightTextPrimary = Color(0xFFF8FAFC)
val NightTextSecondary = Color(0xFF94A3B8)
val NightOutline = Color(0xFF334155)
val NightPlaygroundBg = Color(0xFF0F172A)
val NightBallStart = Color(0xFF38BDF8)
val NightBallEnd = Color(0xFF0284C7)

// --- Indoor Mode Color Palette (50 <= Lux < 500) ---
val IndoorBackground = Color(0xFFF8FAFC)
val IndoorSurface = Color(0xFFFFFFFF)
val IndoorSurfaceVariant = Color(0xFFF1F5F9)
val IndoorAccent = Color(0xFF7C3AED)         // Rich Purple
val IndoorAccentContainer = Color(0xFFEDE9FE)
val IndoorOnAccent = Color(0xFFFFFFFF)
val IndoorTextPrimary = Color(0xFF0F172A)
val IndoorTextSecondary = Color(0xFF64748B)
val IndoorOutline = Color(0xFFE2E8F0)
val IndoorPlaygroundBg = Color(0xFFF1F5F9)
val IndoorBallStart = Color(0xFFA78BFA)
val IndoorBallEnd = Color(0xFF7C3AED)

// --- Bright Mode Color Palette (Lux >= 500) ---
val BrightBackground = Color(0xFFFFFDF5)
val BrightSurface = Color(0xFFFFFFFF)
val BrightSurfaceVariant = Color(0xFFFEF3C7)
val BrightAccent = Color(0xFFD97706)         // Amber Gold / Solar
val BrightAccentContainer = Color(0xFFFEF3C7)
val BrightOnAccent = Color(0xFFFFFFFF)
val BrightTextPrimary = Color(0xFF1C1917)
val BrightTextSecondary = Color(0xFF78716C)
val BrightOutline = Color(0xFFFDE68A)
val BrightPlaygroundBg = Color(0xFFFFFBEB)
val BrightBallStart = Color(0xFFFBBF24)
val BrightBallEnd = Color(0xFFD97706)

// Status colors
val StatusActive = Color(0xFF10B981)         // Emerald green
val StatusUnavailable = Color(0xFFEF4444)    // Red