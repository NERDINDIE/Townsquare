package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import com.example.data.model.AppSettings
import com.example.data.model.VoiceNarrationPreset
import com.example.data.model.ReadingFontSize
import com.example.data.model.AudioStreamingQuality
import com.example.ui.theme.ThemeConfig
import com.example.ui.components.settings.*
import com.example.data.model.CustomExtensionManifest
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.MediaType
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.Mic
import com.example.ui.components.MediaCardItem
import com.example.ui.components.GlobalSandwichMenu
import com.example.ui.components.SandwichMenuItem
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Menu
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun ProfileSpacesScreen(
    userSpaces: List<MediaSpaceEntity>,
    userCreatedItems: List<MediaItemEntity>,
    bookmarkedItems: List<MediaItemEntity>,
    audioState: AudioState,
    onOpenCreateSpace: () -> Unit,
    onOpenCreateContent: () -> Unit,
    onOpenReader: (MediaItemEntity) -> Unit,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onVoiceNarrate: ((MediaItemEntity) -> Unit)? = null,
    onShare: ((MediaItemEntity) -> Unit)? = null,
    onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,
    onOpenSettings: () -> Unit = {},
    onOpenVoiceBuilder: () -> Unit = {},
    onSelectSpace: (Long) -> Unit = {},
    tvWatchlist: List<com.example.data.model.TvWatchlistItem> = emptyList(),
    tvWatchedHistory: List<com.example.data.model.TvWatchedHistoryItem> = emptyList(),
    onRemoveFromTvWatchlist: (String) -> Unit = {},
    onMarkTvShowAsWatched: (com.example.data.model.TvWatchlistItem) -> Unit = {},
    onRemoveFromTvHistory: (String) -> Unit = {},
    onClearTvHistory: () -> Unit = {},
    onTuneTvChannel: (Int) -> Unit = {},
    onOpenTvGuide: () -> Unit = {},
    onOpenWebUrl: ((String) -> Unit)? = null,
    settings: AppSettings? = null,
    currentTheme: ThemeConfig? = null,
    isDynamicTheme: Boolean = false,
    onSelectTheme: (ThemeConfig) -> Unit = {},
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
    customExtensions: List<CustomExtensionManifest> = emptyList(),
    onUpdateActiveAppSkin: (String?) -> Unit = {},
    onUpdateActiveWelcomeSkin: (String) -> Unit = {},
    onUpdateOverrideBaseAppInterface: (Boolean) -> Unit = {},
    onUpdateRetroTerminalMode: (Boolean) -> Unit = {},
    onUpdateKeitai3GOverlay: (Boolean) -> Unit = {},
    onUpdateManuscriptParchmentTheme: (Boolean) -> Unit = {},
    onUpdateMetroTilesView: (Boolean) -> Unit = {},
    onUpdateGeekLiveTickerHeader: (Boolean) -> Unit = {},
    onOpenLegalNotice: () -> Unit = {},
    onOpenAdBlockVpn: () -> Unit = {},
    onOpenMeshNetwork: () -> Unit = {},
    onOpenDiagnostics: () -> Unit = {},
    onOpenServerConsole: () -> Unit = {},
    onOpenLoginSignup: () -> Unit = {},
    isOfflineMode: Boolean = false,
    onToggleOfflineMode: () -> Unit = {},
    initialModule: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Posts, 2: Newsletters, 3: Articles, 4: Audio, 5: Bookmarks
    var mainProfileModule by remember(initialModule) { mutableIntStateOf(initialModule) }

    val totalSubscribers = userSpaces.sumOf { it.subscriberCount }
    val totalPublished = userCreatedItems.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_spaces_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile 3D ID Card
            item {
                Profile3DIDCard(
                    onOpenSettings = {
                        mainProfileModule = 11
                        onOpenSettings()
                    },
                    subscriberCount = totalSubscribers,
                    publishedCount = totalPublished,
                    spacesCount = userSpaces.size
                )
            }
            
            // Train Voice Model Action
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NeonCyan.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVoiceBuilder() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Voice Builder",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                            Text(
                                text = "Train a custom voice model for narration",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Train Model",
                            tint = NeonCyan
                        )
                    }
                }
            }
            
            // Action buttons
            item {
                OutlinedButton(
                    onClick = onOpenCreateSpace,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth().testTag("profile_new_space_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Media Space")
                }
            }

            // "My Media Spaces" Showcase
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY MEDIA SPACES (${userSpaces.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "+ New Space",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan,
                            modifier = Modifier
                                .clickable { onOpenCreateSpace() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userSpaces, key = { it.id }) { space ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .testTag("user_space_card_${space.id}")
                                    .clickable { onSelectSpace(space.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(space.accentColorHex).copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(space.accentColorHex).copy(alpha = 0.2f),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Hub,
                                                    contentDescription = null,
                                                    tint = Color(space.accentColorHex),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(space.accentColorHex).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${space.subscriberCount} subs",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(space.accentColorHex),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = space.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = space.handle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = space.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Central Sandwich Menu for Profile Modules Navigation
            item {
                var isProfileMenuOpen by remember { mutableStateOf(false) }
                val profileModules = listOf(
                    Triple(0, "🖼️ Works", "Creative works & publications"),
                    Triple(11, "⚙️ Settings", "App, theme, audio, AI & display preferences"),
                    Triple(8, "🌐 Web Vault", "Browser bookmarks & saved web links"),
                    Triple(7, "📦 Media Vault", "Physical media & transformative studio"),
                    Triple(6, "📺 TV Watchlist", "Saved shows & EPG reminders"),
                    Triple(9, "🛡️ Moderation", "Content review & civic safety reports"),
                    Triple(10, "💰 Monetization", "Creator subscriptions & payouts"),
                    Triple(1, "💳 Finance", "Personal budgeting & accounts"),
                    Triple(2, "🫀 Health", "Health & well-being telemetry"),
                    Triple(3, "🏛️ State Pass", "Civic identity & credentials"),
                    Triple(4, "💼 Business ERP", "Commercial workspace & ledgers"),
                    Triple(5, "🏺 Collections", "Curated artifacts & archives")
                )
                val currentModule = profileModules.find { it.first == mainProfileModule } ?: profileModules[0]

                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF131D2E),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isProfileMenuOpen = !isProfileMenuOpen }
                            .testTag("profile_sandwich_menu_toggle")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentModule.second.take(2), fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentModule.second.drop(3),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentModule.third,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonCyan
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isProfileMenuOpen) Icons.Default.ExpandLess else Icons.Default.Menu,
                                contentDescription = "Toggle Modules Menu",
                                tint = NeonCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    GlobalSandwichMenu(
                        isOpen = isProfileMenuOpen,
                        title = "PROFILE SUBAPP MODULES",
                        items = profileModules.map { (id, label, desc) ->
                            SandwichMenuItem(
                                id = id.toString(),
                                title = label.drop(3),
                                description = desc,
                                iconEmoji = label.take(2),
                                accentColor = NeonCyan,
                                isSelected = mainProfileModule == id
                            )
                        },
                        onSelectItem = { menuItem ->
                            mainProfileModule = menuItem.id.toInt()
                            isProfileMenuOpen = false
                        }
                    )
                }
            }

            if (mainProfileModule == 11) {
                // Settings Section integrated directly into Profile
                item {
                    ProfileSettingsSection(
                        settings = settings,
                        currentTheme = currentTheme,
                        isDynamicTheme = isDynamicTheme,
                        onSelectTheme = onSelectTheme,
                        onSetDynamicThemeEnabled = onSetDynamicThemeEnabled,
                        onUpdateAudioQuality = onUpdateAudioQuality,
                        onUpdateFontSize = onUpdateFontSize,
                        onUpdateDialHaptic = onUpdateDialHaptic,
                        onUpdateAutoTune = onUpdateAutoTune,
                        onUpdateAutoCache = onUpdateAutoCache,
                        onUpdateBreakingNewsAlerts = onUpdateBreakingNewsAlerts,
                        onUpdatePrintKioskAlerts = onUpdatePrintKioskAlerts,
                        onUpdateVoicePreset = onUpdateVoicePreset,
                        onUpdateVoiceSpeed = onUpdateVoiceSpeed,
                        onUpdateCommunityRadius = onUpdateCommunityRadius,
                        onUpdateMorningDispatchTime = onUpdateMorningDispatchTime,
                        onUpdateHighContrast = onUpdateHighContrast,
                        onClearMediaCache = onClearMediaCache,
                        onResetToDefaults = onResetToDefaults,
                        onUpdateEnableAiFeatures = onUpdateEnableAiFeatures,
                        onUpdateEnableAiFactChecking = onUpdateEnableAiFactChecking,
                        onUpdateEnableAiVoiceNarration = onUpdateEnableAiVoiceNarration,
                        onUpdateEnableAiSmartSummaries = onUpdateEnableAiSmartSummaries,
                        customExtensions = customExtensions,
                        onUpdateActiveAppSkin = onUpdateActiveAppSkin,
                        onUpdateActiveWelcomeSkin = onUpdateActiveWelcomeSkin,
                        onUpdateOverrideBaseAppInterface = onUpdateOverrideBaseAppInterface,
                        onUpdateRetroTerminalMode = onUpdateRetroTerminalMode,
                        onUpdateKeitai3GOverlay = onUpdateKeitai3GOverlay,
                        onUpdateManuscriptParchmentTheme = onUpdateManuscriptParchmentTheme,
                        onUpdateMetroTilesView = onUpdateMetroTilesView,
                        onUpdateGeekLiveTickerHeader = onUpdateGeekLiveTickerHeader,
                        onOpenLegalNotice = onOpenLegalNotice,
                        onOpenAdBlockVpn = onOpenAdBlockVpn,
                        onOpenMeshNetwork = onOpenMeshNetwork,
                        onOpenDiagnostics = onOpenDiagnostics,
                        onOpenServerConsole = onOpenServerConsole,
                        onOpenLoginSignup = onOpenLoginSignup,
                        isOfflineMode = isOfflineMode,
                        onToggleOfflineMode = onToggleOfflineMode
                    )
                }
            } else if (mainProfileModule == 1) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                    ) {
                        com.example.ui.plus.finance.TownsquareFinanceApp(onBack = { mainProfileModule = 0 })
                    }
                }
            } else if (mainProfileModule == 2) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        com.example.ui.plus.health.TownsquareHealthApp(onBack = { mainProfileModule = 0 })
                    }
                }
            } else if (mainProfileModule == 3) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        com.example.ui.plus.state.TownsquareStateApp(onBack = { mainProfileModule = 0 })
                    }
                }
            } else if (mainProfileModule == 4) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(720.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            TownsquareBusinessErpSection(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            } else if (mainProfileModule == 5) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            com.example.ui.screens.profile.CollectionTrackerSection(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            } else if (mainProfileModule == 6) {
                item {
                    TvWatchlistProfileModule(
                        watchlist = tvWatchlist,
                        watchedHistory = tvWatchedHistory,
                        onRemoveFromWatchlist = onRemoveFromTvWatchlist,
                        onMarkAsWatched = onMarkTvShowAsWatched,
                        onRemoveFromHistory = onRemoveFromTvHistory,
                        onClearHistory = onClearTvHistory,
                        onTuneTvChannel = onTuneTvChannel,
                        onOpenTvGuide = onOpenTvGuide
                    )
                }
            } else if (mainProfileModule == 7) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        com.example.ui.components.media.PhysicalMediaVaultSection(
                            onPlayInPlayer = { vaultItem ->
                                val mediaEntity = MediaItemEntity(
                                    id = (1000..9999).random().toLong(),
                                    title = vaultItem.title,
                                    subtitle = vaultItem.description,
                                    authorName = vaultItem.creatorOrArtist,
                                    type = when (vaultItem.format) {
                                        com.example.data.model.PhysicalMediaFormat.VINYL, com.example.data.model.PhysicalMediaFormat.CASSETTE_TAPE, com.example.data.model.PhysicalMediaFormat.DISC_ALBUM -> MediaType.SONG.name
                                        com.example.data.model.PhysicalMediaFormat.DISC_MOVIE, com.example.data.model.PhysicalMediaFormat.VHS_TAPE, com.example.data.model.PhysicalMediaFormat.MOVIE_REEL, com.example.data.model.PhysicalMediaFormat.BOOK_PDF -> MediaType.PODCAST_EPISODE.name
                                    },
                                    channelId = "physical_vault",
                                    channelName = "Physical Vault",
                                    bodyText = vaultItem.description,
                                    mediaUrl = vaultItem.audioSampleUrl
                                )
                                onPlayAudio(mediaEntity)
                            }
                        )
                    }
                }
            } else if (mainProfileModule == 8) {
                // Personal Browser Features: Bookmarks & Saved Web Items
                item {
                    var customSavedLinks by remember {
                        mutableStateOf(
                            listOf(
                                com.example.ui.components.BookmarkItem("Townsquare Press Wire", "https://news.google.com", "📰"),
                                com.example.ui.components.BookmarkItem("Civic Wikipedia Reference", "https://wikipedia.org", "🌐"),
                                com.example.ui.components.BookmarkItem("Public Media & Audio Archives", "https://archive.org", "📚"),
                                com.example.ui.components.BookmarkItem("Weather Pulse Airwaves", "https://weather.com", "🌤️"),
                                com.example.ui.components.BookmarkItem("Townsquare Retro BBS Terminal", "bbs://townsquare.local", "📟"),
                                com.example.ui.components.BookmarkItem("Project Gutenberg Public Books", "https://gutenberg.org", "📖")
                            )
                        )
                    }
                    var newTitle by remember { mutableStateOf("") }
                    var newUrl by remember { mutableStateOf("") }
                    var isAddingBookmark by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_browser_vault_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = NeonCyan.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🌐", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Browser Bookmarks & Saved Items",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Personal web cache & quick-launch links (${customSavedLinks.size})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCyan
                                        )
                                    }
                                }

                                TextButton(onClick = { isAddingBookmark = !isAddingBookmark }) {
                                    Text(if (isAddingBookmark) "Cancel" else "+ Add", color = NeonCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (isAddingBookmark) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    androidx.compose.material3.OutlinedTextField(
                                        value = newTitle,
                                        onValueChange = { newTitle = it },
                                        label = { Text("Bookmark Label") },
                                        placeholder = { Text("e.g. Daily Tech Radar") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    androidx.compose.material3.OutlinedTextField(
                                        value = newUrl,
                                        onValueChange = { newUrl = it },
                                        label = { Text("Web URL") },
                                        placeholder = { Text("https://example.com") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Button(
                                        onClick = {
                                            if (newUrl.isNotBlank()) {
                                                val formatted = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://") && !newUrl.startsWith("bbs://")) "https://$newUrl" else newUrl
                                                customSavedLinks = customSavedLinks + com.example.ui.components.BookmarkItem(
                                                    title = if (newTitle.isNotBlank()) newTitle else formatted,
                                                    url = formatted,
                                                    icon = "🔖"
                                                )
                                                newTitle = ""
                                                newUrl = ""
                                                isAddingBookmark = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Save to Profile Web Vault", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Bookmarks List
                            customSavedLinks.forEach { bookmark ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF131D2E),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenWebUrl?.invoke(bookmark.url) }
                                        .testTag("profile_bookmark_${bookmark.title}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(bookmark.icon, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = bookmark.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = bookmark.url,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.LightGray
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    customSavedLinks = customSavedLinks.filterNot { it.url == bookmark.url }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Delete bookmark",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = "Open in browser tab",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (mainProfileModule == 9) {
                // Moderation Dashboard moved to Profile
                item {
                    var sampleReports by remember {
                        mutableStateOf(
                            listOf(
                                com.example.ui.screens.ReportedContent("rep_1", "Spam Link in Forum", "Anonymous_92", "usr_99", "Suspicious link redirecting to external tracker", System.currentTimeMillis() - 3600000, "SOCIAL_POST"),
                                com.example.ui.screens.ReportedContent("rep_2", "Low-effort AI Content Loop", "BotFarm_Air", "usr_104", "Machine-generated slop bypassing community guidelines", System.currentTimeMillis() - 7200000, "ARTICLE")
                            )
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_moderation_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = WarmAmber.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🛡️", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Moderation Dashboard",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Community safety & reported content review (${sampleReports.size} pending)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = WarmAmber
                                        )
                                    }
                                }
                            }

                            if (sampleReports.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF131D2E),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("✅", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("All Reports Cleared", fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("The civic community queue is currently clean.", fontSize = 11.sp, color = Color.LightGray)
                                    }
                                }
                            } else {
                                sampleReports.forEach { report ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF1E1E28),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333344)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(report.targetTitle, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = WarmAmber.copy(alpha = 0.2f)
                                                ) {
                                                    Text(report.itemType, color = WarmAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Reported by @${report.targetAuthor}: \"${report.reason}\"", fontSize = 11.sp, color = Color.LightGray)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = { sampleReports = sampleReports.filterNot { it.id == report.id } },
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(32.dp)
                                                ) {
                                                    Text("Dismiss", fontSize = 11.sp, color = Color.LightGray)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = { sampleReports = sampleReports.filterNot { it.id == report.id } },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252), contentColor = Color.White),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(32.dp)
                                                ) {
                                                    Text("Take Down", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (mainProfileModule == 10) {
                // Creator Monetization moved to Profile
                item {
                    var isAdsActive by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_monetization_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("💰", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Creator Monetization",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Subscriptions, direct tips & creator payout ledger",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF81C784)
                                        )
                                    }
                                }
                            }

                            // Revenue summary cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF102616),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Total Earnings", fontSize = 11.sp, color = Color.LightGray)
                                        Text("$3,450.75", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                                        Text("+$240 this week", fontSize = 9.sp, color = Color(0xFFA5D6A7))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF131D2E),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Paid Members", fontSize = 11.sp, color = Color.LightGray)
                                        Text("1,240", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                        Text("98% retention", fontSize = 9.sp, color = Color.LightGray)
                                    }
                                }
                            }

                            // Ad Revenue Sharing Switch
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF131D2E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Civic Sponsor Announcements", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text("Allow curated ethical business ads in your media spaces", fontSize = 11.sp, color = Color.LightGray)
                                    }
                                    androidx.compose.material3.Switch(
                                        checked = isAdsActive,
                                        onCheckedChange = { isAdsActive = it }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Default: Published Works Content Wall
                item {
                    val tabLabels = listOf(
                        "All My Works (${userCreatedItems.size})",
                        "💬 Posts",
                        "✉️ Newsletters",
                        "📰 Articles",
                        "📻 Audio",
                        "🔖 Saved (${bookmarkedItems.size})"
                    )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = NeonCyan,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = NeonCyan
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabLabels.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("profile_tab_$index")
                        )
                    }
                }
            }

            // Filtered Items List
            val displayedItems = when (selectedTab) {
                0 -> userCreatedItems
                1 -> userCreatedItems.filter { it.type == MediaType.SOCIAL_POST.name }
                2 -> userCreatedItems.filter { it.type == MediaType.NEWSLETTER.name }
                3 -> userCreatedItems.filter { it.type == MediaType.NEWSPAPER_MAGAZINE.name }
                4 -> userCreatedItems.filter { it.type == MediaType.RADIO_STATION.name || it.type == MediaType.PODCAST_EPISODE.name }
                5 -> bookmarkedItems
                else -> userCreatedItems
            }

            if (displayedItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "✍️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedTab == 5) "No Bookmarks Yet" else "No Content In This Section",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (selectedTab == 5) "Bookmark articles and newsletters to read later." else "Tap 'New Content' to publish your first piece!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(displayedItems, key = { it.id }) { item ->
                    MediaCardItem(
                        item = item,
                        audioState = audioState,
                        onOpenReader = onOpenReader,
                        onPlayAudio = onPlayAudio,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark,
                        onVoiceNarrate = onVoiceNarrate,
                        onShare = onShare,
                        onToggleSavedOffline = onToggleSavedOffline
                    )
                }
            }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun Profile3DIDCard(
    onOpenSettings: () -> Unit,
    subscriberCount: Int,
    publishedCount: Int,
    spacesCount: Int,
    modifier: Modifier = Modifier
) {
    var rotateX by androidx.compose.runtime.mutableFloatStateOf(0f)
    var rotateY by androidx.compose.runtime.mutableFloatStateOf(0f)
    
    val animatedRotateX by animateFloatAsState(targetValue = rotateX, animationSpec = tween(150), label = "rotX")
    val animatedRotateY by animateFloatAsState(targetValue = rotateY, animationSpec = tween(150), label = "rotY")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationX = animatedRotateX
                rotationY = animatedRotateY
                cameraDistance = 12f * density
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        rotateX = 0f
                        rotateY = 0f
                    },
                    onDragCancel = {
                        rotateX = 0f
                        rotateY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    rotateX = (rotateX - dragAmount.y * 0.3f).coerceIn(-20f, 20f)
                    rotateY = (rotateY + dragAmount.x * 0.3f).coerceIn(-20f, 20f)
                }
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "AC",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF003544)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Alex Chen",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@alexchen",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Independent curator",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                androidx.compose.material3.IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = spacesCount.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Spaces",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$subscriberCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    Text(
                        text = "Subscribers",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = publishedCount.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                    Text(
                        text = "Creations",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSettingsSection(
    settings: AppSettings?,
    currentTheme: ThemeConfig?,
    isDynamicTheme: Boolean,
    onSelectTheme: (ThemeConfig) -> Unit,
    onSetDynamicThemeEnabled: (Boolean) -> Unit,
    onUpdateAudioQuality: (AudioStreamingQuality) -> Unit,
    onUpdateFontSize: (ReadingFontSize) -> Unit,
    onUpdateDialHaptic: (Boolean) -> Unit,
    onUpdateAutoTune: (Boolean) -> Unit,
    onUpdateAutoCache: (Boolean) -> Unit,
    onUpdateBreakingNewsAlerts: (Boolean) -> Unit,
    onUpdatePrintKioskAlerts: (Boolean) -> Unit,
    onUpdateVoicePreset: (VoiceNarrationPreset) -> Unit,
    onUpdateVoiceSpeed: (Float) -> Unit,
    onUpdateCommunityRadius: (Int) -> Unit,
    onUpdateMorningDispatchTime: (String) -> Unit,
    onUpdateHighContrast: (Boolean) -> Unit,
    onClearMediaCache: () -> Unit,
    onResetToDefaults: () -> Unit,
    onUpdateEnableAiFeatures: (Boolean) -> Unit,
    onUpdateEnableAiFactChecking: (Boolean) -> Unit,
    onUpdateEnableAiVoiceNarration: (Boolean) -> Unit,
    onUpdateEnableAiSmartSummaries: (Boolean) -> Unit,
    customExtensions: List<CustomExtensionManifest>,
    onUpdateActiveAppSkin: (String?) -> Unit,
    onUpdateActiveWelcomeSkin: (String) -> Unit,
    onUpdateOverrideBaseAppInterface: (Boolean) -> Unit,
    onUpdateRetroTerminalMode: (Boolean) -> Unit,
    onUpdateKeitai3GOverlay: (Boolean) -> Unit,
    onUpdateManuscriptParchmentTheme: (Boolean) -> Unit,
    onUpdateMetroTilesView: (Boolean) -> Unit,
    onUpdateGeekLiveTickerHeader: (Boolean) -> Unit,
    onOpenLegalNotice: () -> Unit,
    onOpenAdBlockVpn: () -> Unit,
    onOpenMeshNetwork: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onOpenServerConsole: () -> Unit,
    onOpenLoginSignup: () -> Unit,
    isOfflineMode: Boolean,
    onToggleOfflineMode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_settings_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚙️ SETTINGS & PREFERENCES",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
            IconButton(onClick = onResetToDefaults) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset Defaults",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
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
            onOpenExtensionBuilderInPlus = {}
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

        // 3. Reading & Typography (Modular)
        ReadingSettings(
            selectedFontSize = settings?.readingFontSize ?: ReadingFontSize.STANDARD,
            onUpdateFontSize = onUpdateFontSize
        )

        // 4. Audio & Broadcast Tuning (Modular)
        AudioSettings(
            selectedQuality = settings?.audioStreamingQuality ?: AudioStreamingQuality.BALANCED,
            dialHapticEnabled = settings?.dialHapticFeedback ?: true,
            autoTuneEnabled = settings?.autoTuneLastStation ?: true,
            onUpdateAudioQuality = onUpdateAudioQuality,
            onUpdateDialHaptic = onUpdateDialHaptic,
            onUpdateAutoTune = onUpdateAutoTune
        )

        // 5. AI & Smart Features (Modular)
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

        // 6. Voice Narration TTS (Modular)
        VoiceSettings(
            selectedPreset = settings?.defaultVoiceNarrationPreset ?: VoiceNarrationPreset.NEWS_ANCHOR,
            speechSpeed = settings?.voiceNarrationSpeed ?: 1.0f,
            onUpdateVoicePreset = onUpdateVoicePreset,
            onUpdateVoiceSpeed = onUpdateVoiceSpeed
        )

        // 7. Identity & ID Management
        IdentitySettings()

        // 8. Activity History & Downloads Vault
        com.example.ui.components.settings.HistoryDownloadsSettings()

        // 9. Offline, Storage & Alerts (Modular)
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

        // 10. Connectivity, VPN & System Diagnostics
        com.example.ui.components.settings.ConnectivityAndDiagnosticsSettings(
            onOpenAdBlockVpn = onOpenAdBlockVpn,
            onOpenMeshNetwork = onOpenMeshNetwork,
            onOpenDiagnostics = onOpenDiagnostics
        )

        // 11. Account, SSO & Backend Server Infrastructure
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Account, SSO & Backend Infrastructure",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Account & SSO Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenLoginSignup)
                        .testTag("settings_account_sso_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Account & Passkey SSO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Passkeys, biometric identity & credentials",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Backend Server Console Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenServerConsole)
                        .testTag("settings_server_console_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🖧", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Backend Server Console",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Embedded HTTP daemon, ports & API routes",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
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

        // 12. Legal Notice & Editorial Charter
        OutlinedButton(
            onClick = onOpenLegalNotice,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("profile_legal_notice_btn")
        ) {
            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Legal Notice & Editorial Charter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
