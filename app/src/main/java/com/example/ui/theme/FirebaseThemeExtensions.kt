package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import kotlin.math.abs

/**
 * Brand color tokens from the CSS theme extension for the Firebase version of Townsquare.
 */
object FirebaseBrandColors {
    // Utility to convert HSL to Compose Color
    fun hsl(h: Float, sPercent: Float, lPercent: Float): Color {
        val s = sPercent / 100f
        val l = lPercent / 100f
        val c = (1f - abs(2f * l - 1f)) * s
        val x = c * (1f - abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f
        val (r1, g1, b1) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return Color(r1 + m, g1 + m, b1 + m, 1f)
    }

    // Media & Channel Brand Colors
    val Newsstand = hsl(220f, 13f, 69f)
    val BulletinBoard = hsl(22f, 95f, 55f)
    val Bookworm = hsl(30f, 70f, 50f)
    val TheCommunityPost = hsl(12f, 76f, 61f)
    val TheDowntownDish = hsl(340f, 82f, 52f)
    val TheUrbanist = hsl(210f, 89f, 53f)
    val TheBusinessBeat = hsl(195f, 85f, 41f)
    val CitySoundwaves = hsl(270f, 78f, 55f)
    val Marketplace = hsl(48f, 96f, 58f)
    val Takeouts = hsl(160f, 84f, 39f)
    val MapPin = hsl(190f, 70f, 50f)
    val Tyres = hsl(240f, 2f, 50f)
    val Dlc = hsl(260f, 85f, 60f)
    val Exchange = hsl(150f, 70f, 45f)
    val Charts = hsl(200f, 80f, 55f)
    val ArcadeSaloon = hsl(300f, 85f, 55f)
    val OnAir = hsl(0f, 84.2f, 60.2f)
    val Broadcast = hsl(345f, 85f, 55f)
    val Funnies = hsl(280f, 85f, 60f)
    val Weatherman = hsl(207f, 44f, 49f)
    val RemoteControl = hsl(220f, 75f, 55f)
    val Orientations = hsl(300f, 85f, 55f)
    val Retro = hsl(50f, 95f, 55f)
    val AnimeShinbun = hsl(350f, 95f, 55f)
    val EditorPro = hsl(240f, 80f, 60f)
    val FandomTimes = hsl(350f, 82f, 52f)
    val Tickets = hsl(265f, 82f, 60f)
    val Yeast = hsl(35f, 55f, 50f)
    val Matches = hsl(120f, 60f, 40f)
    val Beasts = hsl(30f, 40f, 30f)
    val Yesilcam = hsl(160f, 50f, 45f)
    val RestArea = hsl(195f, 53f, 49f)
    val Palapa = hsl(35f, 92f, 60f)
    val Hatay = hsl(0f, 65f, 50f)
    val Nicosia = hsl(40f, 85f, 55f)
    val Famagusta = hsl(200f, 50f, 45f)
    val Rendezvous = hsl(340f, 90f, 60f)
    val StarChart = hsl(250f, 70f, 55f)
    val SilverScreen = hsl(210f, 10f, 60f)
    val TechPulse = hsl(205f, 90f, 55f)
    val GameOn = hsl(275f, 80f, 50f)
    val Vitality = hsl(130f, 60f, 45f)
    val TheCurator = hsl(30f, 60f, 50f)
    val Aura = hsl(330f, 80f, 70f)
    val TheGrapevine = hsl(350f, 85f, 60f)
}

/**
 * Typography extensions matching the font classes in the CSS.
 */
object FirebaseTypographyExtensions {
    // Broadsheet & Tabloid: Serif headlines, classic editorial feel
    val BroadsheetTypography = Typography(
        headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp),
        headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
        headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
        titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
        titleMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
        titleSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp)
    )

    // Inter / Modern UI font
    val InterTypography = Typography(
        headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp),
        headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 26.sp),
        headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 22.sp),
        titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp)
    )

    // Teletext / VT323 / Monospace terminal font
    val TeletextTypography = Typography(
        headlineLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 30.sp, letterSpacing = 1.sp),
        headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 1.sp),
        titleLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 0.5.sp),
        titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 15.sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 13.sp),
        labelLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    )

    // Evening / Oswald Condensed font
    val CondensedTypography = Typography(
        headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 32.sp, letterSpacing = (-0.5).sp),
        headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 26.sp, letterSpacing = (-0.5).sp),
        titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 21.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp)
    )
}
