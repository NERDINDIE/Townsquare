package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class ThemeConfig(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color
)

val PresetThemes = listOf(
    ThemeConfig("default", "Default Dark", true, NeonCyan, WarmAmber, DarkBg, DarkSurface),
    ThemeConfig("light", "Classic Light", false, Color(0xFF006782), Color(0xFF8B5000), LightBg, LightSurface),
    ThemeConfig("ocean", "Ocean Deep", true, Color(0xFF00D2FF), Color(0xFF005662), Color(0xFF001214), Color(0xFF002A30)),
    ThemeConfig("forest", "Forest Mist", true, Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFF0D1C0D), Color(0xFF1B301B)),
    
    // Clock-based editions requested by user
    ThemeConfig("late_night", "Late Night Edition (22:00-2:59)", true, Color(0xFF00F0FF), Color(0xFFFF0055), Color(0xFF020408), Color(0xFF0B0E14)),
    ThemeConfig("dawn", "Dawn Edition (3:00-6:59)", true, Color(0xFFFF758F), Color(0xFF70E000), Color(0xFF1A1A2E), Color(0xFF24243E)),
    ThemeConfig("morning", "Morning Edition (7:00-11:59)", false, Color(0xFFD84315), Color(0xFFF9A825), Color(0xFFFFFDE7), Color(0xFFFFF9C4)),
    ThemeConfig("noon", "Noon Edition (12:00-12:59)", false, Color(0xFFFFB300), Color(0xFF00897B), Color(0xFFFAFAFA), Color(0xFFFFFFFF)),
    ThemeConfig("afternoon", "Afternoon Edition (13:00-15:59)", false, Color(0xFFD32F2F), Color(0xFFF57C00), Color(0xFFFFF3E0), Color(0xFFFFE0B2)),
    ThemeConfig("evening", "Evening Edition (16:00-19:59)", true, Color(0xFFFF4081), Color(0xFF7C4DFF), Color(0xFF2C1B4D), Color(0xFF3F2B96)),
    ThemeConfig("night", "Night Edition (20:00-21:59)", true, Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFF0B132B), Color(0xFF1C2541))
)
