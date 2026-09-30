package com.example.ui.components

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.MainActivity
import com.example.ui.theme.*
import com.example.widget.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareWidgetsDrawerDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Available Widgets, 1: Installation Guide
    var isClockAnalogue by remember { mutableStateOf(false) }
    var liveTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            liveTime = LocalDateTime.now()
            delay(1000L)
        }
    }

    fun pinWidgetToHomeScreen(providerClass: Class<*>, widgetName: String) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val myProvider = ComponentName(context, providerClass)
                val successCallback = PendingIntent.getActivity(
                    context,
                    999,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                Toast.makeText(context, "Request sent to add $widgetName to Home Screen!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        // Fallback for devices without direct pin support
        Toast.makeText(
            context,
            "To add $widgetName: Long-press your home screen, tap Widgets, and drag $widgetName onto your screen.",
            Toast.LENGTH_LONG
        ).show()
    }

    fun syncAllWidgetsNow() {
        TownsquareClockWidgetProvider.updateAllWidgets(context)
        TownsquareRadioWidgetProvider.updateAllWidgets(context, "Townsquare Live 88.5 FM", "88.5 MHz", true)
        TownsquareBriefWidgetProvider.updateAllWidgets(context, "Townsquare Morning Wire", "Clean Energy Transit Ordinance Passed", "Today • 3 Unread")
        TownsquareFacsimileWidgetProvider.updateAllWidgets(context)
        Toast.makeText(context, "All home screen widgets synchronized with live state!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("widgets_drawer_dialog"),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = NeonCyan.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Widgets,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Home Screen Widget Drawer",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Preview & pin live widgets directly to your device screen",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("widgets_drawer_close_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceVariant,
                    contentColor = NeonCyan
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("📱 Widget Catalog (4)", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("ℹ️ Setup Guide", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                if (selectedTab == 0) {
                    // Widgets List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Quick Sync Bar
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkBg,
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Sync, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sync all widgets with live data", fontSize = 12.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { syncAllWidgetsNow() },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 1. TCTV Station Clock Widget (Primary CUJ)
                        item {
                            WidgetPreviewCard(
                                title = "TCTV Station Clock Widget",
                                sizeBadge = "4x2 • Station Ident & Real-time Clock",
                                description = "Displays real-time synchronized station broadcast time, date, and live broadcast ident. Tap on the device screen opens the TV schedule directly.",
                                onPin = {
                                    pinWidgetToHomeScreen(TownsquareClockWidgetProvider::class.java, "TCTV Station Clock")
                                },
                                onSync = {
                                    TownsquareClockWidgetProvider.updateAllWidgets(context)
                                    Toast.makeText(context, "TCTV Station Clock updated!", Toast.LENGTH_SHORT).show()
                                },
                                content = {
                                    // Live preview matching widget_clock_tctv.xml
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFF0F172A),
                                        border = BorderStroke(1.dp, NeonCyan),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isClockAnalogue = !isClockAnalogue }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = NeonCyan
                                                    ) {
                                                        Text(
                                                            text = "TCTV",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color(0xFF003544),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "STATION CLOCK",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF88A0B8)
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (isClockAnalogue) WarmAmber.copy(alpha = 0.2f) else MintTeal.copy(alpha = 0.2f),
                                                    border = BorderStroke(1.dp, if (isClockAnalogue) WarmAmber else MintTeal)
                                                ) {
                                                    Text(
                                                        text = if (isClockAnalogue) "ANALOG VIEW" else "DIGITAL LIVE",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isClockAnalogue) WarmAmber else MintTeal,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            if (isClockAnalogue) {
                                                Canvas(modifier = Modifier.size(90.dp)) {
                                                    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                                                    drawCircle(
                                                        color = Color.White,
                                                        radius = size.width / 2,
                                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                                                    )
                                                    val hourAngle = (liveTime.hour % 12 + liveTime.minute / 60f) * 30f - 90f
                                                    val minuteAngle = liveTime.minute * 6f - 90f
                                                    val secondAngle = liveTime.second * 6f - 90f

                                                    // Hour hand
                                                    drawLine(
                                                        color = Color.White,
                                                        start = center,
                                                        end = center + androidx.compose.ui.geometry.Offset(
                                                            (size.width / 3.8 * kotlin.math.cos(Math.toRadians(hourAngle.toDouble()))).toFloat(),
                                                            (size.height / 3.8 * kotlin.math.sin(Math.toRadians(hourAngle.toDouble()))).toFloat()
                                                        ),
                                                        strokeWidth = 4.dp.toPx()
                                                    )
                                                    // Minute hand
                                                    drawLine(
                                                        color = NeonCyan,
                                                        start = center,
                                                        end = center + androidx.compose.ui.geometry.Offset(
                                                            (size.width / 2.5 * kotlin.math.cos(Math.toRadians(minuteAngle.toDouble()))).toFloat(),
                                                            (size.height / 2.5 * kotlin.math.sin(Math.toRadians(minuteAngle.toDouble()))).toFloat()
                                                        ),
                                                        strokeWidth = 2.5.dp.toPx()
                                                    )
                                                    // Second hand
                                                    drawLine(
                                                        color = CoralRed,
                                                        start = center,
                                                        end = center + androidx.compose.ui.geometry.Offset(
                                                            (size.width / 2.2 * kotlin.math.cos(Math.toRadians(secondAngle.toDouble()))).toFloat(),
                                                            (size.height / 2.2 * kotlin.math.sin(Math.toRadians(secondAngle.toDouble()))).toFloat()
                                                        ),
                                                        strokeWidth = 1.5.dp.toPx()
                                                    )
                                                    drawCircle(color = CoralRed, radius = 3.dp.toPx(), center = center)
                                                }
                                            } else {
                                                Text(
                                                    text = liveTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a")),
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = liveTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")),
                                                fontSize = 12.sp,
                                                color = NeonCyan
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Tap to toggle analog / digital view mode",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        // 2. Radio Tuner Widget
                        item {
                            WidgetPreviewCard(
                                title = "Townsquare Radio Tuner Widget",
                                sizeBadge = "4x1 • Live Stream & Audio Controls",
                                description = "Shows live broadcast station frequency, current track, and allows play/pause and station skipping right from your device home screen.",
                                onPin = {
                                    pinWidgetToHomeScreen(TownsquareRadioWidgetProvider::class.java, "Townsquare Radio Tuner")
                                },
                                onSync = {
                                    TownsquareRadioWidgetProvider.updateAllWidgets(context, "Townsquare Live 88.5 FM", "88.5 MHz", true)
                                    Toast.makeText(context, "Radio Widget synchronized!", Toast.LENGTH_SHORT).show()
                                },
                                content = {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFF1E1B4B),
                                        border = BorderStroke(1.dp, Color(0xFF818CF8)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFF6366F1).copy(alpha = 0.3f),
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.Radio, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text("88.5 MHz • FM Stereo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5B4FC))
                                                    Text("Townsquare Sonic Frequencies", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // 3. Morning Dispatch Daily Brief Widget
                        item {
                            WidgetPreviewCard(
                                title = "Morning Dispatch Daily Brief",
                                sizeBadge = "4x2 • Daily News & Weather",
                                description = "Delivers morning editorial headlines, weather summary, and direct jump to read full articles.",
                                onPin = {
                                    pinWidgetToHomeScreen(TownsquareBriefWidgetProvider::class.java, "Morning Dispatch Brief")
                                },
                                onSync = {
                                    TownsquareBriefWidgetProvider.updateAllWidgets(context, "Townsquare Morning Wire", "Historic Pedestrian Corridor Approved", "Today • 2 Unread")
                                    Toast.makeText(context, "Morning Dispatch Brief updated!", Toast.LENGTH_SHORT).show()
                                },
                                content = {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFF1B2A2F),
                                        border = BorderStroke(1.dp, MintTeal.copy(alpha = 0.6f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("📰 MORNING DISPATCH", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MintTeal)
                                                Text("72°F ⛅ Sunny", fontSize = 11.sp, color = Color.White)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                "Municipal Council votes 9-1 to approve 2030 Pedestrianization Corridor and Solar Retrofit.",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Updated 10m ago • 2 Unread Dispatches", fontSize = 10.sp, color = DarkTextSecondary)
                                        }
                                    }
                                }
                            )
                        }

                        // 4. Retro Facsimile Slip Widget
                        item {
                            WidgetPreviewCard(
                                title = "Retro Hourly Facsimile Slip",
                                sizeBadge = "3x2 • Thermal Broadsheet Receipt",
                                description = "Nostalgic dot-matrix receipt tape format displaying telegraphic wire dispatches with authentic thermal paper styling.",
                                onPin = {
                                    pinWidgetToHomeScreen(TownsquareFacsimileWidgetProvider::class.java, "Retro Hourly Facsimile")
                                },
                                onSync = {
                                    TownsquareFacsimileWidgetProvider.updateAllWidgets(context)
                                    Toast.makeText(context, "Facsimile Slip updated!", Toast.LENGTH_SHORT).show()
                                },
                                content = {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFBF8EE),
                                        border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                "=== TOWNSQUARE FACSIMILE WIRE ===",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2B2B2B)
                                            )
                                            Text(
                                                "TX: 2026-09-30 10:15 UTC // CHANNEL 01",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                color = Color(0xFF555555)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "URGENT: Harbor ferry retro electric conversion completes maiden voyage across the sound.",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1A1A1A)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // Setup Guide
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "How to add widgets on Android",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        val steps = listOf(
                            "1. Go to your device's Home Screen." to "Exit or minimize the app to view your Android desktop.",
                            "2. Long-press on an empty space." to "Touch and hold any open area on your wallpaper until the home screen menu appears.",
                            "3. Tap 'Widgets'." to "Browse through your installed apps to locate the 'Townsquare' section.",
                            "4. Drag & Drop your chosen widget." to "Touch and hold the TCTV Station Clock, Radio Tuner, Morning Brief, or Facsimile Slip and place it on your screen.",
                            "5. Resize & Enjoy Live Updates!" to "Drag the corner handles to resize the widget to your preference."
                        )

                        items(steps.size) { index ->
                            val (title, detail) = steps[index]
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF003544)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = detail, fontSize = 12.sp, color = DarkTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer
                Surface(
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetPreviewCard(
    title: String,
    sizeBadge: String,
    description: String,
    onPin: () -> Unit,
    onSync: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = sizeBadge, fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, fontSize = 12.sp, color = DarkTextSecondary)

            Spacer(modifier = Modifier.height(12.dp))
            // Widget Live Preview Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBg, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                content()
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSync,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync Data", fontSize = 11.sp, color = Color.White)
                }

                Button(
                    onClick = onPin,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pin to Home Screen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
