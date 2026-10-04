package com.example.ui.plus.watchface

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.sin

enum class DialStyle(val label: String, val icon: String) {
    ANALOG_CHRONO("Analog Chrono", "⏱️"),
    DIGITAL_LED("Digital LED", "🔢"),
    CYBER_HUD("Cyberpunk HUD", "⚡"),
    RETRO_TELETEXT("Retro Teletext", "📺"),
    MINIMAL_CLASSIC("Minimalist Classic", "⌚")
}

enum class WatchHandStyle(val label: String) {
    NEON_NEEDLE("Neon Needle"),
    CLASSIC_BREGUET("Classic Breguet"),
    MINIMAL_STICK("Minimal Stick"),
    DIVER_ARROW("Diver Arrow")
}

data class WatchfaceTheme(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val dialRing: Color
)

val WatchfaceThemePresets = listOf(
    WatchfaceTheme("Neon Cyan", NeonCyan, WarmAmber, Color(0xFF070B14), Color(0xFF16253B)),
    WatchfaceTheme("Solar Amber", WarmAmber, CoralRed, Color(0xFF140D05), Color(0xFF38240D)),
    WatchfaceTheme("Emerald Matrix", Color(0xFF33FF33), Color(0xFF81C784), Color(0xFF051407), Color(0xFF0E3814)),
    WatchfaceTheme("Tokyo Crimson", Color(0xFFFF2A6D), Color(0xFF05D9E8), Color(0xFF140710), Color(0xFF381428)),
    WatchfaceTheme("Monochrome Luxury", Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF0F172A), Color(0xFF334155))
)

data class WatchfaceConfig(
    val id: String,
    val name: String,
    val dialStyle: DialStyle = DialStyle.ANALOG_CHRONO,
    val handStyle: WatchHandStyle = WatchHandStyle.NEON_NEEDLE,
    val theme: WatchfaceTheme = WatchfaceThemePresets.first(),
    val showBatteryComplication: Boolean = true,
    val showHeartRateComplication: Boolean = true,
    val showStepsComplication: Boolean = true,
    val showWeatherComplication: Boolean = true,
    val showDateComplication: Boolean = true,
    val showSecondTicks: Boolean = true,
    val isSmoothSecondSweep: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareWatchfaceMaker(
    onBack: () -> Unit,
    onApplyToWatch: (WatchfaceConfig) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var config by remember {
        mutableStateOf(
            WatchfaceConfig(
                id = "wf_${System.currentTimeMillis()}",
                name = "Townsquare Chrono Elite"
            )
        )
    }

    var isAmbientAodPreview by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    var savedSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Live clock ticker
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(100)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("watchface_maker_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("⌚ Watchface Studio", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                        Text("Interactive Smartwatch Dial Designer & Complication Lab", fontSize = 11.sp, color = NeonCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onApplyToWatch(config)
                            savedSuccessMessage = "Watchface '${config.name}' flashed to Smartwatch!"
                        }
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Apply to Watch", fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurfaceElevated)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success alert banner
            if (savedSuccessMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1B3D2A),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✅", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(savedSuccessMessage!!, color = Color(0xFFA5D6A7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { savedSuccessMessage = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // 1. WATCH HARDWARE CHASSIS PREVIEW
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Physical Watch Bezel
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF333E4D), Color(0xFF1A212B), Color(0xFF090D14))
                                )
                            )
                            .padding(12.dp)
                            .clip(CircleShape)
                            .background(if (isAmbientAodPreview) Color.Black else config.theme.background)
                            .testTag("watchface_canvas_preview"),
                        contentAlignment = Alignment.Center
                    ) {
                        WatchfaceInteractiveCanvas(
                            config = config,
                            time = currentTime,
                            isAod = isAmbientAodPreview
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode Toggles (AOD vs Normal, Sweep vs Tick)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = isAmbientAodPreview,
                            onClick = { isAmbientAodPreview = !isAmbientAodPreview },
                            label = { Text("Always-On (AOD) Mode", fontSize = 11.sp) },
                            leadingIcon = { Text(if (isAmbientAodPreview) "🌙" else "☀️", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarmAmber.copy(alpha = 0.25f),
                                selectedLabelColor = WarmAmber
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            selected = config.isSmoothSecondSweep,
                            onClick = { config = config.copy(isSmoothSecondSweep = !config.isSmoothSecondSweep) },
                            label = { Text(if (config.isSmoothSecondSweep) "Smooth Sweep" else "Classic Tick", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.25f),
                                selectedLabelColor = NeonCyan
                            )
                        )
                    }
                }
            }

            // 2. DIAL STYLE PICKER
            item {
                Text("DIAL ARCHITECTURE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NeonCyan, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(DialStyle.entries) { style ->
                        val isSelected = config.dialStyle == style
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                            modifier = Modifier.clickable { config = config.copy(dialStyle = style) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(style.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = style.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NeonCyan else Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 3. COLOR PALETTE PRESETS
            item {
                Text("COLOR HARMONY PRESETS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = WarmAmber, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(WatchfaceThemePresets) { theme ->
                        val isSelected = config.theme.name == theme.name
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) theme.primary.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) theme.primary else DarkBorder),
                            modifier = Modifier.clickable { config = config.copy(theme = theme) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(theme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = theme.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 4. WATCH HANDS CUSTOMIZER
            item {
                Text("HAND DESIGN (FOR ANALOG MODES)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(WatchHandStyle.entries) { hand ->
                        val isSelected = config.handStyle == hand
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF1E293B) else DarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                            modifier = Modifier.clickable { config = config.copy(handStyle = hand) }
                        ) {
                            Text(
                                text = hand.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else Color.LightGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 5. COMPLICATION SLOTS TOGGLES
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ACTIVE COMPLICATIONS & SENSORS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(8.dp))

                        ComplicationToggleRow(
                            icon = "🔋",
                            title = "Battery Gauge Complication",
                            checked = config.showBatteryComplication,
                            onCheckedChange = { config = config.copy(showBatteryComplication = it) }
                        )
                        ComplicationToggleRow(
                            icon = "❤️",
                            title = "Heart Rate Monitor (BPM)",
                            checked = config.showHeartRateComplication,
                            onCheckedChange = { config = config.copy(showHeartRateComplication = it) }
                        )
                        ComplicationToggleRow(
                            icon = "👟",
                            title = "Step Counter Pedometer",
                            checked = config.showStepsComplication,
                            onCheckedChange = { config = config.copy(showStepsComplication = it) }
                        )
                        ComplicationToggleRow(
                            icon = "🌤️",
                            title = "Civic Weather & Temp",
                            checked = config.showWeatherComplication,
                            onCheckedChange = { config = config.copy(showWeatherComplication = it) }
                        )
                        ComplicationToggleRow(
                            icon = "📅",
                            title = "Calendar Date & Day",
                            checked = config.showDateComplication,
                            onCheckedChange = { config = config.copy(showDateComplication = it) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun WatchfaceInteractiveCanvas(
    config: WatchfaceConfig,
    time: LocalDateTime,
    isAod: Boolean
) {
    val primaryColor = if (isAod) Color.White.copy(alpha = 0.8f) else config.theme.primary
    val secondaryColor = if (isAod) Color.Gray else config.theme.secondary

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Draw Analog Hands & Dial Markers
        if (config.dialStyle != DialStyle.DIGITAL_LED && config.dialStyle != DialStyle.RETRO_TELETEXT) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f - 12f

                // Dial Outer Ring
                drawCircle(
                    color = if (isAod) Color(0xFF1E293B) else config.theme.dialRing,
                    radius = radius,
                    style = Stroke(width = 2.dp.toPx())
                )

                // 12-Hour Markers
                for (hour in 0 until 12) {
                    val angle = Math.toRadians((hour * 30.0) - 90.0)
                    val isMajor = hour % 3 == 0
                    val tickLen = if (isMajor) 14f else 8f
                    val strokeW = if (isMajor) 3.dp.toPx() else 1.5.dp.toPx()

                    val startX = (center.x + (radius - tickLen) * cos(angle)).toFloat()
                    val startY = (center.y + (radius - tickLen) * sin(angle)).toFloat()
                    val endX = (center.x + radius * cos(angle)).toFloat()
                    val endY = (center.y + radius * sin(angle)).toFloat()

                    drawLine(
                        color = if (isMajor) primaryColor else secondaryColor.copy(alpha = 0.6f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }

                // Hour Hand
                val hourAngle = Math.toRadians(((time.hour % 12 + time.minute / 60.0) * 30.0) - 90.0)
                val hourLength = radius * 0.5f
                drawLine(
                    color = primaryColor,
                    start = center,
                    end = Offset(
                        (center.x + hourLength * cos(hourAngle)).toFloat(),
                        (center.y + hourLength * sin(hourAngle)).toFloat()
                    ),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Minute Hand
                val minAngle = Math.toRadians((time.minute * 6.0) - 90.0)
                val minLength = radius * 0.72f
                drawLine(
                    color = primaryColor,
                    start = center,
                    end = Offset(
                        (center.x + minLength * cos(minAngle)).toFloat(),
                        (center.y + minLength * sin(minAngle)).toFloat()
                    ),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Second Hand (hidden in AOD mode)
                if (!isAod) {
                    val secFraction = if (config.isSmoothSecondSweep) (time.second + time.nano / 1_000_000_000.0) else time.second.toDouble()
                    val secAngle = Math.toRadians((secFraction * 6.0) - 90.0)
                    val secLength = radius * 0.85f

                    drawLine(
                        color = secondaryColor,
                        start = center,
                        end = Offset(
                            (center.x + secLength * cos(secAngle)).toFloat(),
                            (center.y + secLength * sin(secAngle)).toFloat()
                        ),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Center Hub Pin
                    drawCircle(color = secondaryColor, radius = 4.dp.toPx())
                }
            }
        }

        // Complications & Digital Displays Overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            when (config.dialStyle) {
                DialStyle.DIGITAL_LED -> {
                    Text(
                        text = String.format("%02d:%02d", time.hour, time.minute),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp,
                        color = primaryColor,
                        letterSpacing = 2.sp
                    )
                    if (!isAod) {
                        Text(
                            text = String.format(":%02d SEC", time.second),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = secondaryColor
                        )
                    }
                }
                DialStyle.RETRO_TELETEXT -> {
                    Text("TOWNSQUARE P.888", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = WarmAmber)
                    Text(
                        text = String.format("%02d:%02d:%02d", time.hour, time.minute, time.second),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = primaryColor
                    )
                }
                DialStyle.CYBER_HUD -> {
                    Text("SYS.TIME // 2026", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = NeonCyan)
                    Text(
                        text = String.format("%02d:%02d", time.hour, time.minute),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = primaryColor
                    )
                }
                else -> {
                    // For Analog, show digital time sub-dial at 6 o'clock
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = String.format("%02d:%02d", time.hour, time.minute),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Complications Row
            if (!isAod) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (config.showBatteryComplication) {
                        Text("🔋 94%", fontSize = 9.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                    }
                    if (config.showHeartRateComplication) {
                        Text("❤️ 72", fontSize = 9.sp, color = CoralRed, fontWeight = FontWeight.Bold)
                    }
                    if (config.showStepsComplication) {
                        Text("👟 6.4k", fontSize = 9.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                }
                if (config.showWeatherComplication) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("☀️ 21°C • Clear", fontSize = 9.sp, color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun ComplicationToggleRow(
    icon: String,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 12.sp, color = Color.White)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.3f)
            )
        )
    }
}
