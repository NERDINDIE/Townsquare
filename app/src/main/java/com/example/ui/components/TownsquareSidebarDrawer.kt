package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tv
import com.example.ui.NavDestination
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Palette
import com.example.data.model.AppSettings
import com.example.data.model.AudioStreamingQuality
import com.example.data.model.ReadingFontSize
import com.example.data.model.VoiceNarrationPreset
import com.example.ui.components.settings.*

data class SidebarNavItem(
    val index: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String? = null,
    val isLive: Boolean = false,
    val testTag: String
)

@Composable
fun TownsquareSidebarDrawer(
    isOpen: Boolean,
    currentIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onClose: () -> Unit,
    onOpenSettings: () -> Unit,
    isOfflineMode: Boolean,
    onToggleOfflineMode: () -> Unit,
    onOpenLettersMailbox: () -> Unit = {},
    onOpenTeletext: () -> Unit = {},
    onOpenFacsimile: () -> Unit = {},
    onOpenBroadsheetCover: () -> Unit = {},
    onOpenMultitaskingBrowser: () -> Unit = {},
    onOpenEmergencyHub: () -> Unit = {},
    onOpenWeather: () -> Unit = {},
    onOpenInbox: () -> Unit = {},
    onOpenCreateContent: () -> Unit = {},
    onOpenModeration: () -> Unit = {},
    onOpenMonetization: () -> Unit = {},
    onOpenBroadcastSchedule: () -> Unit = {},
    onOpenFactCheckHub: () -> Unit = {},
    onOpenVoiceBuilder: () -> Unit = {},
    onOpenDrivingMode: () -> Unit = {},
    onOpenProfileSwitcher: () -> Unit = {},
    onOpenMarketplace: () -> Unit = {},
    onOpenMaps: () -> Unit = {},
    onOpenArcade: () -> Unit = {},
    onOpenLingo: () -> Unit = {},
    onOpenBookworm: () -> Unit = {},
    onOpenPhone: () -> Unit = {},
    onOpenIotCompanion: () -> Unit = {},
    onOpenTownsquares: () -> Unit = {},
    onOpenWeatherman: () -> Unit = {},
    onOpenArCamera: () -> Unit = {},
    onOpenMeshNetwork: () -> Unit = {},
    onOpenYellowPages: () -> Unit = {},
    onOpenFiles: () -> Unit = {},
    onOpenWatchfaceMaker: () -> Unit = {},
    onOpenPocket: () -> Unit = {},
    onOpenDiagnostics: () -> Unit = {},
    onOpenServerConsole: () -> Unit = {},
    onOpenLoginSignup: () -> Unit = {},
    onOpenFomoDigest: () -> Unit = {},
    onOpenAdBlockVpn: () -> Unit = {},
    onOpenWidgetsDrawer: () -> Unit = {},
    onRefreshApp: () -> Unit = {},
    unreadInboxCount: Int = 0,
    draftsCount: Int = 3,
    // Settings state & callbacks merged into Sidebar
    settings: AppSettings? = null,
    currentTheme: com.example.ui.theme.ThemeConfig? = null,
    isDynamicTheme: Boolean = false,
    onSelectTheme: (com.example.ui.theme.ThemeConfig) -> Unit = {},
    onSetDynamicThemeEnabled: (Boolean) -> Unit = {},
    onUpdateAudioQuality: (AudioStreamingQuality) -> Unit = {},
    onUpdateFontSize: (ReadingFontSize) -> Unit = {},
    onUpdateDialHaptic: (Boolean) -> Unit = {},
    onUpdateAutoTune: (Boolean) -> Unit = {},
    onUpdateAutoCache: (Boolean) -> Unit = {},
    onUpdateBreakingNewsAlerts: (Boolean) -> Unit = {},
    onUpdatePrintKioskAlerts: (Boolean) -> Unit = {},
    onUpdateVoicePreset: (VoiceNarrationPreset) -> Unit = {},
    onUpdateVoiceSpeed: (Float) -> Unit = {},
    onUpdateCommunityRadius: (Int) -> Unit = {},
    onUpdateMorningDispatchTime: (String) -> Unit = {},
    onUpdateHighContrast: (Boolean) -> Unit = {},
    onClearMediaCache: () -> Unit = {},
    onResetToDefaults: () -> Unit = {},
    onUpdateEnableAiFeatures: (Boolean) -> Unit = {},
    onUpdateEnableAiFactChecking: (Boolean) -> Unit = {},
    onUpdateEnableAiVoiceNarration: (Boolean) -> Unit = {},
    onUpdateEnableAiSmartSummaries: (Boolean) -> Unit = {},
    customExtensions: List<com.example.data.model.CustomExtensionManifest> = emptyList(),
    onUpdateActiveAppSkin: (String?) -> Unit = {},
    onUpdateActiveWelcomeSkin: (String) -> Unit = {},
    onUpdateOverrideBaseAppInterface: (Boolean) -> Unit = {},
    onUpdateRetroTerminalMode: (Boolean) -> Unit = {},
    onUpdateKeitai3GOverlay: (Boolean) -> Unit = {},
    onUpdateManuscriptParchmentTheme: (Boolean) -> Unit = {},
    onUpdateMetroTilesView: (Boolean) -> Unit = {},
    onUpdateGeekLiveTickerHeader: (Boolean) -> Unit = {},
    onOpenLegalNotice: () -> Unit = {},
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    // Live Clock & Date State
    var timeFormatted by remember { mutableStateOf("") }
    var dateFormatted by remember { mutableStateOf("") }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")
        val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")
        while (isActive) {
            val now = java.time.LocalDateTime.now()
            timeFormatted = now.format(timeFormatter)
            dateFormatted = now.format(dateFormatter)
            kotlinx.coroutines.delay(1000L)
        }
    }
    // Sidebar Tab State
    var selectedSidebarTab by remember(initialTab) { androidx.compose.runtime.mutableIntStateOf(initialTab) }
    val sidebarTabs = listOf("Navigation", "Tools", "Settings")

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(200)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
        ) {
            AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(250)),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .align(Alignment.CenterStart)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* consume clicks inside sidebar */ }
                        )
                        .testTag("sidebar_drawer_surface")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header: Townsquare Logo & Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = NeonCyan,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "T",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF003544)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Townsquare",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Civic & Editorial Media",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonCyan
                                    )
                                }
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.testTag("sidebar_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Sidebar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active Profile Indicator & Switcher Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131D2D),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenProfileSwitcher()
                                }
                                .testTag("sidebar_profile_switcher_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "T",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF261800)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Townsquare Profile",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Tap to switch to Playground",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCyan
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "SWITCH",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Date & Clock Card in Sidebar
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenBroadcastSchedule()
                                }
                                .testTag("sidebar_live_clock_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(Color(0xFF30D158), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "LIVE STATION CLOCK",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                            color = NeonCyan
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (timeFormatted.isNotEmpty()) timeFormatted else "12:00:00 PM",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (dateFormatted.isNotEmpty()) "$dateFormatted • Tap for Guide" else "Saturday, Sep 12, 2026 • Tap for Guide",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = NeonCyan.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "Station Schedule",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Townsquare Plus Superapp Suite Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E1609),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.WarmAmber.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectIndex(NavDestination.TOWNSQUARE_PLUS)
                                    onClose()
                                }
                                .testTag("sidebar_townsquare_plus_banner")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Stars,
                                                contentDescription = null,
                                                tint = Color(0xFF261800),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Townsquare Plus Lab",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = com.example.ui.theme.WarmAmber
                                            ) {
                                                Text(
                                                    text = "LAB",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFF261800),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Experimental Concepts & Extension Sandbox",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = com.example.ui.theme.WarmAmber.copy(alpha = 0.9f)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = com.example.ui.theme.WarmAmber.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.WarmAmber.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "EXPLORE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Townsquare Inbox Quick Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0F1E33),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenInbox()
                                    onClose()
                                }
                                .testTag("sidebar_inbox_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mail,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Townsquare Unified Inbox",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Alerts, Dispatches, Letters & Mailbox",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (unreadInboxCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NeonCyan
                                    ) {
                                        Text(
                                            text = "$unreadInboxCount NEW",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF003544),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Local Weather Quick Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0E2235),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D2FF).copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenWeather()
                                    onClose()
                                }
                                .testTag("sidebar_weather_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⛅", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Local Weather Forecast",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Radar, 24h & 7-Day Outlook",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF00D2FF).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D2FF).copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "72°F",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF00D2FF),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Letters to Editor Mailbox Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0F2E3A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenInbox()
                                    onClose()
                                }
                                .testTag("sidebar_letters_mailbox_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mail,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Letter to Editor Mailbox",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Direct messages to channel owners",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "MAIL",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Home Screen Widget Drawer Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF161E2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenWidgetsDrawer()
                                    onClose()
                                }
                                .testTag("sidebar_widgets_drawer_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Widgets,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Home Screen Widget Drawer",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Pin TCTV Clock, Radio & Brief to Screen",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "WIDGETS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Offline Mode & Refresh Section
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudOff,
                                            contentDescription = null,
                                            tint = if (isOfflineMode) com.example.ui.theme.CoralRed else NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Offline Reading Mode",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (isOfflineMode) "Active • Cached locally" else "Online • Live sync active",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isOfflineMode) com.example.ui.theme.CoralRed else Color.Gray
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = isOfflineMode,
                                        onCheckedChange = { onToggleOfflineMode() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = NeonCyan
                                        ),
                                        modifier = Modifier.testTag("sidebar_offline_switch")
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        onRefreshApp()
                                        onClose()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sidebar_refresh_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Refresh Feeds & App Data", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Standalone Apps Section
                        Text(
                            text = "STANDALONE CIVIC APPS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val standaloneApps = listOf(
                            Triple("🛍️ Marketplace", "Buy, sell & catalogs", onOpenMarketplace),
                            Triple("🗺️ Maps & Travel", "Cartography & guide", onOpenMaps),
                            Triple("🌍 Townsquares", "World city feeds & transit routes", onOpenTownsquares),
                            Triple("🎙️ Weatherman", "Personalized weather podcasts", onOpenWeatherman),
                            Triple("🎮 Arcade Games", "Retro games & arcade", onOpenArcade),
                            Triple("🗣️ Lingo Lab", "Language & phrasebook", onOpenLingo),
                            Triple("📚 Bookworm", "Library & archives", onOpenBookworm),
                            Triple("📞 Phone Link", "Satellite & voicemail", onOpenPhone),
                            Triple("⌚ IoT Companion", "Car dashboard, MP3, remote PC & watch", onOpenIotCompanion),
                            Triple("📷 AR Vision & HUD", "Civic Geo-AR, scanners & HUD camera", onOpenArCamera),
                            Triple("📶 Mesh Connectivity", "Off-grid P2P relay & SOS beacon", onOpenMeshNetwork),
                            Triple("📒 Yellow Pages", "Freelance gigs & skilled trades", onOpenYellowPages),
                            Triple("📁 File Explorer", "Storage volumes & media vault", onOpenFiles),
                            Triple("⌚ Watchface Maker", "Smartwatch dials & chronographs", onOpenWatchfaceMaker),
                            Triple("📖 Townsquare Pocket", "Clean RSS reader & offline vault", onOpenPocket),
                            Triple("🩺 System Diagnostics", "Glitch watchdog, freeze monitor & vitals", onOpenDiagnostics),
                            Triple("🖧 Backend Server", "Embedded HTTP/REST & remote control", onOpenServerConsole),
                            Triple("🔐 Account & SSO", "Townsquare ID, Passkeys & Google SSO", onOpenLoginSignup),
                            Triple("⚡ FOMO Digest", "While-you-were-away AI recap summary", onOpenFomoDigest),
                            Triple("🛡️ AdBlock & Mesh VPN", "Shield ads, trackers & encrypted tunnel", onOpenAdBlockVpn)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            standaloneApps.forEach { (title, subtitle, onClick) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onClose()
                                            onClick()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = subtitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // + New Content Button
                        androidx.compose.material3.Button(
                            onClick = {
                                onClose()
                                onOpenCreateContent()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color(0xFF003544)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("sidebar_create_content_button")
                        ) {
                            Icon(imageVector = Icons.Default.Create, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New Content / Dispatch", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Sidebar Tabs
                        TabRow(
                            selectedTabIndex = selectedSidebarTab,
                            containerColor = Color.Transparent,
                            contentColor = NeonCyan,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedSidebarTab]),
                                    color = NeonCyan
                                )
                            },
                            divider = { HorizontalDivider(color = DarkBorder) }
                        ) {
                            sidebarTabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedSidebarTab == index,
                                    onClick = { selectedSidebarTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 13.sp,
                                            fontWeight = if (selectedSidebarTab == index) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedSidebarTab) {
                            0 -> {
                                Text(
                                    text = "PRIMARY SECTIONS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                )

                                val navItems = listOf(
                            SidebarNavItem(
                                index = NavDestination.FEED,
                                title = "Civic Feed",
                                subtitle = "Editorial stream & breaking news",
                                icon = Icons.AutoMirrored.Filled.Feed,
                                testTag = "sidebar_nav_feed"
                            ),
                            SidebarNavItem(
                                index = NavDestination.NEWSBLOG,
                                title = "24/7 Live Newsblog",
                                subtitle = "Around-the-clock rolling wire",
                                icon = Icons.Default.Bolt,
                                badge = "LIVE",
                                isLive = true,
                                testTag = "sidebar_nav_newsblog"
                            ),
                            SidebarNavItem(
                                index = NavDestination.VISUAL_GALLERY,
                                title = "Visual & Gallery",
                                subtitle = "Image-heavy photojournalism",
                                icon = Icons.Default.Collections,
                                badge = "HD",
                                testTag = "sidebar_nav_visual"
                            ),
                            SidebarNavItem(
                                index = NavDestination.TV_STREAMING,
                                title = "TV & Streaming",
                                subtitle = "TCTV 6-Channel live broadcast",
                                icon = Icons.Default.Tv,
                                badge = "6 CH",
                                isLive = true,
                                testTag = "sidebar_nav_tv"
                            ),
                            SidebarNavItem(
                                index = NavDestination.NEWSSTAND,
                                title = "Newsstand & Kiosks",
                                subtitle = "Print flipbook & physical map",
                                icon = Icons.Default.MenuBook,
                                testTag = "sidebar_nav_newsstand"
                            ),
                            SidebarNavItem(
                                index = NavDestination.JOURNAL,
                                title = "Journal & Notepad",
                                subtitle = "Personal press & idea draft holder",
                                icon = Icons.Default.HistoryEdu,
                                badge = if (draftsCount > 0) "$draftsCount Drafts" else null,
                                testTag = "sidebar_nav_journal"
                            ),
                            SidebarNavItem(
                                index = NavDestination.AUDIO_HUB,
                                title = "Audio Hub & Podcasts",
                                subtitle = "Radio live & spatial voices",
                                icon = Icons.Default.Radio,
                                testTag = "sidebar_nav_audio"
                            ),
                            SidebarNavItem(
                                index = NavDestination.SPACES,
                                title = "Spaces & Guilds",
                                subtitle = "Discussion & community rooms",
                                icon = Icons.Default.Hub,
                                testTag = "sidebar_nav_spaces"
                            ),
                            SidebarNavItem(
                                index = NavDestination.COMMUNITY,
                                title = "Community Social",
                                subtitle = "Civic social feed & local discussions",
                                icon = Icons.Default.Forum,
                                badge = "NEW",
                                testTag = "sidebar_nav_community"
                            ),
                            SidebarNavItem(
                                index = NavDestination.FUNNIES,
                                title = "Sunday Funnies & Memes",
                                subtitle = "Syndicated cartoons & memes",
                                icon = Icons.Default.InsertEmoticon,
                                badge = "FUN",
                                testTag = "sidebar_nav_funnies"
                            ),
                            SidebarNavItem(
                                index = NavDestination.PARTNERS,
                                title = "Partner Syndicate",
                                subtitle = "Independent presses & broadcasters",
                                icon = Icons.Default.Newspaper,
                                badge = "PRESS",
                                testTag = "sidebar_nav_partners"
                            ),
                            SidebarNavItem(
                                index = NavDestination.DISCOVERY,
                                title = "Discovery Hub",
                                subtitle = "Find new channels & curators",
                                icon = Icons.Default.Explore,
                                testTag = "sidebar_nav_discovery"
                            ),
                            SidebarNavItem(
                                index = NavDestination.PLAYGROUND,
                                title = "Playground",
                                subtitle = "Child profile & school news",
                                icon = Icons.Default.ChildCare,
                                badge = "KIDS",
                                testTag = "sidebar_nav_playground"
                            ),
                            SidebarNavItem(
                                index = NavDestination.ENGAGEMENT_DASHBOARD,
                                title = "Engagement Dashboard",
                                subtitle = "Viewer engagement metrics",
                                icon = Icons.Default.Dashboard,
                                testTag = "sidebar_nav_dashboard"
                            ),
                            SidebarNavItem(
                                index = NavDestination.BROWSER,
                                title = "Multitasking Browser",
                                subtitle = "Open press wires & web tabs",
                                icon = Icons.Default.Public,
                                badge = "WEB",
                                testTag = "sidebar_nav_browser"
                            ),
                            SidebarNavItem(
                                index = NavDestination.TOWNSQUARE_PLUS,
                                title = "Townsquare Plus",
                                subtitle = "Prototypes: Townsquares, Weatherman & Lab",
                                icon = Icons.Default.Stars,
                                badge = "PLUS",
                                testTag = "sidebar_nav_plus"
                            )
                        )

                        navItems.forEach { item ->
                            val isSelected = currentIndex == item.index
                            SidebarNavButton(
                                item = item,
                                isSelected = isSelected,
                                onClick = {
                                    onSelectIndex(item.index)
                                    onClose()
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                            }
                            1 -> {
                                Text(
                                    text = "AIRWAVE & WIRE TOOLS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                )

                                // 1. Teletext Airwave Decoder Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF001F1F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenTeletext()
                                }
                                .testTag("sidebar_open_teletext_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Teletext Airwave Decoder",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Satellite & VBI airwave reception",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCyan.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "CEEFAX",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 2. Hourly Facsimile Broadsheet Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E1810),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.WarmAmber.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenFacsimile()
                                }
                                .testTag("sidebar_open_facsimile_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Print,
                                        contentDescription = null,
                                        tint = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Hourly Facsimile Wire",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Retro thermal broadsheet slip",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = com.example.ui.theme.WarmAmber.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = com.example.ui.theme.WarmAmber
                                ) {
                                    Text(
                                        text = "120 RPM",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color(0xFF261800),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Broadsheet Edition Cover Page
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.WarmAmber.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenBroadsheetCover()
                                }
                                .testTag("sidebar_open_broadsheet_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Newspaper,
                                        contentDescription = null,
                                        tint = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Today's Broadsheet Edition",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Front page masthead & lead story",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = com.example.ui.theme.WarmAmber.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = com.example.ui.theme.WarmAmber
                                ) {
                                    Text(
                                        text = "FRONT PAGE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 4. Emergency Alerts Hub
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2C0A0A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenEmergencyHub()
                                }
                                .testTag("sidebar_open_emergency_hub_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🚨", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Emergency Alert Hub",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Live severe weather & civic advisories",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFF8080)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFF3B30)
                                ) {
                                    Text(
                                        text = "ALERTS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 5. Weather Forecast & Radar
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F1B2B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenWeather()
                                }
                                .testTag("sidebar_open_weather_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⛅", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Weather Forecast & Radar",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Live hourly pulse & satellite radar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCyan.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "72°F",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 6. Moderation Dashboard
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2C1E0A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.WarmAmber.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenModeration()
                                }
                                .testTag("sidebar_open_moderation_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors, // Reusing icon or whatever looks good
                                        contentDescription = null,
                                        tint = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Moderation Dashboard",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Review reported content and users",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = com.example.ui.theme.WarmAmber.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 7. Creator Monetization
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0A2C1A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenMonetization()
                                }
                                .testTag("sidebar_open_monetization_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Radio, // Reusing icon
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Creator Monetization",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Manage subscriptions & earnings",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF4CAF50).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 8. Multitasking Web Browser Tabs
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F2236),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenMultitaskingBrowser()
                                }
                                .testTag("sidebar_open_multitasking_browser_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Multitasking Browser Tabs",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Open multiple press wires & web tabs",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCyan.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "TABS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 9. Broadcast Station Schedule & Timetable
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF001F2B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenBroadcastSchedule()
                                }
                                .testTag("sidebar_open_broadcast_schedule_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Station Master Schedule",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "24-Hour live TV & radio program guide",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCyan.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan
                                ) {
                                    Text(
                                        text = "GUIDE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 10. AI Fact-Checking Center
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF14241B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30D158).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenFactCheckHub()
                                }
                                .testTag("sidebar_open_fact_check_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FactCheck,
                                        contentDescription = null,
                                        tint = Color(0xFF30D158),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "AI Fact-Checking Center",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Claim verifier & editorial evidence audit",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF30D158).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF30D158)
                                ) {
                                    Text(
                                        text = "AI AUDIT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 11. AI Voice Builder Studio
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1B162C),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBF5AF2).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenVoiceBuilder()
                                }
                                .testTag("sidebar_open_voice_studio_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color(0xFFBF5AF2),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "AI Voice Builder Studio",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Custom narrator training & synthesis",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFBF5AF2).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFBF5AF2)
                                ) {
                                    Text(
                                        text = "VOICE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 12. Car & Driving Mode (Android Auto)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF261D0F),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.WarmAmber.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClose()
                                    onOpenDrivingMode()
                                }
                                .testTag("sidebar_open_driving_mode_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = com.example.ui.theme.WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Car & Driving Mode",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "High-contrast distraction-free radio tuner",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = com.example.ui.theme.WarmAmber.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = com.example.ui.theme.WarmAmber
                                ) {
                                    Text(
                                        text = "AUTO",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color(0xFF261800),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                            }
                            2 -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⚙️ SETTINGS & PREFERENCES",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                            color = NeonCyan
                                        )
                                        IconButton(onClick = onResetToDefaults) {
                                            Icon(
                                                imageVector = Icons.Default.RestartAlt,
                                                contentDescription = "Reset Defaults",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // 1. Skin Extensions & Overrides (Preinstalled & Custom)
                                    SkinExtensionsSettings(
                                        settings = settings,
                                        customExtensions = customExtensions,
                                        onUpdateActiveAppSkin = onUpdateActiveAppSkin,
                                        onUpdateActiveWelcomeSkin = onUpdateActiveWelcomeSkin,
                                        onUpdateOverrideBaseAppInterface = onUpdateOverrideBaseAppInterface,
                                        onUpdateRetroTerminalMode = onUpdateRetroTerminalMode,
                                        onUpdateKeitai3GOverlay = onUpdateKeitai3GOverlay,
                                        onUpdateManuscriptParchmentTheme = onUpdateManuscriptParchmentTheme,
                                        onUpdateMetroTilesView = onUpdateMetroTilesView,
                                        onUpdateGeekLiveTickerHeader = onUpdateGeekLiveTickerHeader,
                                        onOpenExtensionBuilderInPlus = {
                                            onSelectIndex(NavDestination.TOWNSQUARE_PLUS)
                                            onClose()
                                        }
                                    )

                                    // 2. Appearance & Themes (Modular)
                                    AppearanceSettings(
                                        currentTheme = currentTheme,
                                        isDynamicTheme = isDynamicTheme,
                                        highContrastEnabled = settings?.highContrastDisplay ?: false,
                                        onSelectTheme = onSelectTheme,
                                        onSetDynamicThemeEnabled = onSetDynamicThemeEnabled,
                                        onUpdateHighContrast = onUpdateHighContrast
                                    )

                                    // 2. Reading & Typography (Modular)
                                    ReadingSettings(
                                        selectedFontSize = settings?.readingFontSize ?: ReadingFontSize.STANDARD,
                                        onUpdateFontSize = onUpdateFontSize
                                    )

                                    // 3. Audio & Broadcast Tuning (Modular)
                                    AudioSettings(
                                        selectedQuality = settings?.audioStreamingQuality ?: AudioStreamingQuality.BALANCED,
                                        dialHapticEnabled = settings?.dialHapticFeedback ?: true,
                                        autoTuneEnabled = settings?.autoTuneLastStation ?: true,
                                        onUpdateAudioQuality = onUpdateAudioQuality,
                                        onUpdateDialHaptic = onUpdateDialHaptic,
                                        onUpdateAutoTune = onUpdateAutoTune
                                    )

                                    // 4. AI & Smart Features (Modular)
                                    AISmartSettings(
                                        enableAiFeatures = settings?.enableAiFeatures ?: true,
                                        enableFactChecking = settings?.enableAiFactChecking ?: true,
                                        enableVoiceNarration = settings?.enableAiVoiceNarration ?: true,
                                        enableSmartSummaries = settings?.enableAiSmartSummaries ?: true,
                                        onUpdateEnableAiFeatures = onUpdateEnableAiFeatures,
                                        onUpdateEnableFactChecking = onUpdateEnableAiFactChecking,
                                        onUpdateEnableVoiceNarration = onUpdateEnableAiVoiceNarration,
                                        onUpdateEnableSmartSummaries = onUpdateEnableAiSmartSummaries
                                    )

                                    // 5. Legal Notice & Guidelines
                                    OutlinedButton(
                                        onClick = {
                                            onClose()
                                            onOpenLegalNotice()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth().testTag("sidebar_legal_notice_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Legal Notice & Editorial Charter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    // 5. Voice Narration TTS (Modular)
                                    VoiceSettings(
                                        selectedPreset = settings?.defaultVoiceNarrationPreset ?: VoiceNarrationPreset.NEWS_ANCHOR,
                                        speechSpeed = settings?.voiceNarrationSpeed ?: 1.0f,
                                        onUpdateVoicePreset = onUpdateVoicePreset,
                                        onUpdateVoiceSpeed = onUpdateVoiceSpeed
                                    )

                                    // 6. Identity & ID Management (Modular & NEW!)
                                    IdentitySettings()

                                    // 7. Activity History & Downloads Vault (NEW!)
                                    com.example.ui.components.settings.HistoryDownloadsSettings()

                                    // 8. Offline, Storage & Alerts (Modular)
                                    StorageAlertsSettings(
                                        isOfflineMode = isOfflineMode,
                                        autoCacheEnabled = settings?.autoCacheMorningEdition ?: true,
                                        breakingNewsEnabled = settings?.breakingNewsAlerts ?: true,
                                        printKioskEnabled = settings?.printKioskArrivalAlerts ?: true,
                                        onToggleOfflineMode = onToggleOfflineMode,
                                        onUpdateAutoCache = onUpdateAutoCache,
                                        onUpdateBreakingNewsAlerts = onUpdateBreakingNewsAlerts,
                                        onUpdatePrintKioskAlerts = onUpdatePrintKioskAlerts,
                                        onClearMediaCache = onClearMediaCache
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarNavButton(
    item: SidebarNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) NeonCyan.copy(alpha = 0.14f) else Color.Transparent,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.45f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(item.testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) NeonCyan else DarkSurfaceElevated,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color(0xFF003544) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            item.badge?.let { badgeText ->
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isLive) Color(0xFFFF3B30).copy(alpha = 0.2f) else DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (item.isLive) Color(0xFFFF3B30) else DarkBorder
                    )
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (item.isLive) Color(0xFFFF3B30) else NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
