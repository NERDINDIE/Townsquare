package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class ThemeConfig(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val typographyStyle: String = "default"
)

val PresetThemes = listOf(
    ThemeConfig("default", "Default Dark", true, NeonCyan, WarmAmber, DarkBg, DarkSurface),
    ThemeConfig("light", "Classic Light", false, Color(0xFF006782), Color(0xFF8B5000), LightBg, LightSurface),
    ThemeConfig("ocean", "Ocean Deep", true, Color(0xFF00D2FF), Color(0xFF005662), Color(0xFF001214), Color(0xFF002A30)),
    ThemeConfig("forest", "Forest Mist", true, Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFF0D1C0D), Color(0xFF1B301B)),
    
    // CSS Theme Extensions (from Firebase version)
    ThemeConfig(
        id = "css_morning",
        name = "Firebase: Morning (Cream & Pink)",
        isDark = false,
        primary = FirebaseBrandColors.hsl(340f, 82f, 52f),
        secondary = FirebaseBrandColors.hsl(45f, 50f, 88f),
        background = FirebaseBrandColors.hsl(48f, 83f, 94f),
        surface = FirebaseBrandColors.hsl(48f, 83f, 98f),
        typographyStyle = "broadsheet"
    ),
    ThemeConfig(
        id = "css_afternoon",
        name = "Firebase: Afternoon (Crisp & Azure)",
        isDark = false,
        primary = FirebaseBrandColors.hsl(210f, 89f, 53f),
        secondary = FirebaseBrandColors.hsl(210f, 40f, 94f),
        background = FirebaseBrandColors.hsl(210f, 40f, 98f),
        surface = Color.White,
        typographyStyle = "inter"
    ),
    ThemeConfig(
        id = "css_evening",
        name = "Firebase: Evening (Dark Navy & Orange)",
        isDark = true,
        primary = FirebaseBrandColors.hsl(22f, 95f, 55f),
        secondary = FirebaseBrandColors.hsl(217f, 33f, 18f),
        background = FirebaseBrandColors.hsl(222f, 47f, 11f),
        surface = FirebaseBrandColors.hsl(222f, 47f, 13f),
        typographyStyle = "condensed"
    ),
    ThemeConfig(
        id = "css_late_night",
        name = "Firebase: Late Night (Carbon & Emerald)",
        isDark = true,
        primary = FirebaseBrandColors.hsl(150f, 70f, 45f),
        secondary = FirebaseBrandColors.hsl(240f, 4f, 16f),
        background = FirebaseBrandColors.hsl(240f, 6f, 10f),
        surface = FirebaseBrandColors.hsl(240f, 4f, 14f),
        typographyStyle = "monospace"
    ),
    ThemeConfig(
        id = "css_monochrome",
        name = "Firebase: Monochrome (Phosphor Red)",
        isDark = true,
        primary = FirebaseBrandColors.hsl(0f, 100f, 50f),
        secondary = FirebaseBrandColors.hsl(0f, 100f, 50f),
        background = FirebaseBrandColors.hsl(0f, 0f, 7.8f),
        surface = FirebaseBrandColors.hsl(0f, 0f, 11.8f),
        typographyStyle = "teletext"
    ),
    ThemeConfig(
        id = "css_aura",
        name = "Firebase: Cyber Aura (Lilac & Hot Pink)",
        isDark = true,
        primary = FirebaseBrandColors.Aura,
        secondary = FirebaseBrandColors.GameOn,
        background = Color(0xFF140D26),
        surface = Color(0xFF22173B),
        typographyStyle = "default"
    ),

    // Clock-based editions requested by user
    ThemeConfig("late_night", "Late Night Edition (22:00-2:59)", true, Color(0xFF00F0FF), Color(0xFFFF0055), Color(0xFF020408), Color(0xFF0B0E14)),
    ThemeConfig("dawn", "Dawn Edition (3:00-6:59)", true, Color(0xFFFF758F), Color(0xFF70E000), Color(0xFF1A1A2E), Color(0xFF24243E)),
    ThemeConfig("morning", "Morning Edition (7:00-11:59)", false, Color(0xFFD84315), Color(0xFFF9A825), Color(0xFFFFFDE7), Color(0xFFFFF9C4)),
    ThemeConfig("noon", "Noon Edition (12:00-12:59)", false, Color(0xFFFFB300), Color(0xFF00897B), Color(0xFFFAFAFA), Color(0xFFFFFFFF)),
    ThemeConfig("afternoon", "Afternoon Edition (13:00-15:59)", false, Color(0xFFD32F2F), Color(0xFFF57C00), Color(0xFFFFF3E0), Color(0xFFFFE0B2)),
    ThemeConfig("evening", "Evening Edition (16:00-19:59)", true, Color(0xFFFF4081), Color(0xFF7C4DFF), Color(0xFF2C1B4D), Color(0xFF3F2B96)),
    ThemeConfig("night", "Night Edition (20:00-21:59)", true, Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFF0B132B), Color(0xFF1C2541)),
    
    // Custom CLI/DOS Extension requested by user
    ThemeConfig("cli_dos", "CLI / MS-DOS Terminal", true, Color(0xFF33FF33), Color(0xFF00AA00), Color(0xFF000000), Color(0xFF0A140A), typographyStyle = "monospace")
)
