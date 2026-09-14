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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
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

                        // Live Date & Clock Card in Sidebar
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
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
                                        text = if (dateFormatted.isNotEmpty()) dateFormatted else "Saturday, Sep 12, 2026",
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
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
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
                                            text = "Townsquare Inbox",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Briefs, Dispatches & Alerts",
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
                                    onOpenLettersMailbox()
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
                                index = 0,
                                title = "Civic Feed",
                                subtitle = "Editorial stream & breaking news",
                                icon = Icons.AutoMirrored.Filled.Feed,
                                testTag = "sidebar_nav_feed"
                            ),
                            SidebarNavItem(
                                index = 1,
                                title = "24/7 Live Newsblog",
                                subtitle = "Around-the-clock rolling wire",
                                icon = Icons.Default.Bolt,
                                badge = "LIVE",
                                isLive = true,
                                testTag = "sidebar_nav_newsblog"
                            ),
                            SidebarNavItem(
                                index = 2,
                                title = "Visual & Gallery",
                                subtitle = "Image-heavy photojournalism",
                                icon = Icons.Default.Collections,
                                badge = "HD",
                                testTag = "sidebar_nav_visual"
                            ),
                            SidebarNavItem(
                                index = 3,
                                title = "TV & Streaming",
                                subtitle = "TCTV 6-Channel live broadcast",
                                icon = Icons.Default.Tv,
                                badge = "6 CH",
                                isLive = true,
                                testTag = "sidebar_nav_tv"
                            ),
                            SidebarNavItem(
                                index = 4,
                                title = "Newsstand & Kiosks",
                                subtitle = "Print flipbook & physical map",
                                icon = Icons.Default.MenuBook,
                                testTag = "sidebar_nav_newsstand"
                            ),
                            SidebarNavItem(
                                index = 5,
                                title = "Journal & Notepad",
                                subtitle = "Personal press & idea draft holder",
                                icon = Icons.Default.HistoryEdu,
                                badge = if (draftsCount > 0) "$draftsCount Drafts" else null,
                                testTag = "sidebar_nav_journal"
                            ),
                            SidebarNavItem(
                                index = 6,
                                title = "Audio Hub & Podcasts",
                                subtitle = "Radio live & spatial voices",
                                icon = Icons.Default.Radio,
                                testTag = "sidebar_nav_audio"
                            ),
                            SidebarNavItem(
                                index = 7,
                                title = "Spaces & Guilds",
                                subtitle = "Discussion & community rooms",
                                icon = Icons.Default.Hub,
                                testTag = "sidebar_nav_spaces"
                            ),
                            SidebarNavItem(
                                index = 8,
                                title = "Sunday Funnies & Memes",
                                subtitle = "Syndicated cartoons & memes",
                                icon = Icons.Default.InsertEmoticon,
                                badge = "FUN",
                                testTag = "sidebar_nav_funnies"
                            ),
                            SidebarNavItem(
                                index = 9,
                                title = "Partner Syndicate",
                                subtitle = "Independent presses & broadcasters",
                                icon = Icons.Default.Newspaper,
                                badge = "PRESS",
                                testTag = "sidebar_nav_partners"
                            ),
                            SidebarNavItem(
                                index = 10,
                                title = "Discovery Hub",
                                subtitle = "Find new channels & curators",
                                icon = Icons.Default.Explore,
                                testTag = "sidebar_nav_discovery"
                            ),
                            SidebarNavItem(
                                index = 11,
                                title = "Playground",
                                subtitle = "Child profile & school news",
                                icon = Icons.Default.ChildCare,
                                badge = "KIDS",
                                testTag = "sidebar_nav_playground"
                            ),
                            SidebarNavItem(
                                index = 12,
                                title = "Engagement Dashboard",
                                subtitle = "Viewer engagement metrics",
                                icon = Icons.Default.Dashboard,
                                testTag = "sidebar_nav_dashboard"
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

                                    // 1. Appearance & Themes
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = DarkSurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Appearance & Theme", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            val themePresets = com.example.ui.theme.PresetThemes
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                themePresets.forEach { theme ->
                                                    val isSelected = currentTheme?.id == theme.id
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                                                        modifier = Modifier.fillMaxWidth().clickable { onSelectTheme(theme) }
                                                    ) {
                                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                            Box(modifier = Modifier.size(12.dp).background(theme.primary, CircleShape))
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(theme.name, style = MaterialTheme.typography.bodySmall, color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface)
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Dynamic Material You", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = isDynamicTheme,
                                                    onCheckedChange = { onSetDynamicThemeEnabled(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("High Contrast Mode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.highContrastDisplay ?: false,
                                                    onCheckedChange = { onUpdateHighContrast(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                        }
                                    }

                                    // 2. Reading & Typography
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = DarkSurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Reading Font Size", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                ReadingFontSize.entries.forEach { size ->
                                                    val isSelected = (settings?.readingFontSize ?: ReadingFontSize.STANDARD) == size
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) NeonCyan else Color.Transparent,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                                                        modifier = Modifier.weight(1f).clickable { onUpdateFontSize(size) }
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                                            Text(size.label, style = MaterialTheme.typography.labelSmall, color = if (isSelected) Color(0xFF003544) else MaterialTheme.colorScheme.onSurface)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 3. Audio & Broadcast Tuning
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = DarkSurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Audio Quality", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                AudioStreamingQuality.entries.forEach { quality ->
                                                    val isSelected = (settings?.audioStreamingQuality ?: AudioStreamingQuality.BALANCED) == quality
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                                                        modifier = Modifier.fillMaxWidth().clickable { onUpdateAudioQuality(quality) }
                                                    ) {
                                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                                            Text(quality.label, style = MaterialTheme.typography.bodySmall, color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface)
                                                            Text(quality.bitrate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Radio Dial Haptic", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.dialHapticFeedback ?: true,
                                                    onCheckedChange = { onUpdateDialHaptic(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Auto-Tune Next Station", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.autoTuneLastStation ?: true,
                                                    onCheckedChange = { onUpdateAutoTune(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                        }
                                    }

                                    // 4. AI & Smart Features
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = DarkSurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("AI & Smart Media Engine", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Enable AI Features", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.enableAiFeatures ?: true,
                                                    onCheckedChange = { onUpdateEnableAiFeatures(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Live AI Fact-Checking", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.enableAiFactChecking ?: true,
                                                    onCheckedChange = { onUpdateEnableAiFactChecking(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("AI Voice Narration", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.enableAiVoiceNarration ?: true,
                                                    onCheckedChange = { onUpdateEnableAiVoiceNarration(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("AI Article Summaries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.enableAiSmartSummaries ?: true,
                                                    onCheckedChange = { onUpdateEnableAiSmartSummaries(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                        }
                                    }

                                    // 5. Voice Narration TTS
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = DarkSurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Voice Narration Tuning", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Voice Persona", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                VoiceNarrationPreset.entries.forEach { preset ->
                                                    val isSelected = (settings?.defaultVoiceNarrationPreset ?: VoiceNarrationPreset.NEWS_ANCHOR) == preset
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent,
                                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                                                        modifier = Modifier.fillMaxWidth().clickable { onUpdateVoicePreset(preset) }
                                                    ) {
                                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                            Text(preset.label, style = MaterialTheme.typography.bodySmall, color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface)
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Speech Speed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(String.format("%.2fx", settings?.voiceNarrationSpeed ?: 1.0f), style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                                            }
                                            Slider(
                                                value = settings?.voiceNarrationSpeed ?: 1.0f,
                                                onValueChange = { onUpdateVoiceSpeed(it) },
                                                valueRange = 0.75f..1.5f,
                                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                                            )
                                        }
                                    }

                                    // 6. Offline, Storage & Alerts
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = DarkSurfaceElevated,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Offline Mode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = isOfflineMode,
                                                    onCheckedChange = { onToggleOfflineMode() },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Auto-Cache Offline", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.autoCacheMorningEdition ?: true,
                                                    onCheckedChange = { onUpdateAutoCache(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Breaking News Push Alerts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.breakingNewsAlerts ?: true,
                                                    onCheckedChange = { onUpdateBreakingNewsAlerts(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("Print Kiosk Stock Alerts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                                Switch(
                                                    checked = settings?.printKioskArrivalAlerts ?: true,
                                                    onCheckedChange = { onUpdatePrintKioskAlerts(it) },
                                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003544), checkedTrackColor = NeonCyan)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            OutlinedButton(
                                                onClick = onClearMediaCache,
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Clear Media Cache", fontSize = 13.sp)
                                            }
                                        }
                                    }
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
