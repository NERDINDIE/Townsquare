package com.example.ui.plus

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TownsquareTopBar
import com.example.ui.plus.catalogs.TownsquareCatalogsApp
import com.example.ui.plus.extensions.TownsquareExtensionsApp
import com.example.ui.plus.finance.TownsquareFinanceApp
import com.example.ui.plus.mail.TownsquareMailboxApp
import com.example.ui.plus.maps.TownsquareMapsApp
import com.example.ui.plus.marketplace.TownsquareMarketplaceApp
import com.example.ui.plus.phone.TownsquarePhoneApp
import com.example.ui.plus.state.TownsquareStateApp
import com.example.ui.plus.arcade.TownsquareArcadeApp
import com.example.ui.plus.health.TownsquareHealthApp
import com.example.ui.plus.books.TownsquareBookwormApp
import com.example.ui.plus.lingo.TownsquareLingoApp
import com.example.ui.plus.planner.TownsquarePlannerApp
import com.example.ui.theme.*

enum class PlusModularApp {
    NONE, PHONE, MAILBOX, MAPS, MARKETPLACE, EXTENSIONS, FINANCE, CATALOGS, STATE, ARCADE, HEALTH, BOOKS, LINGO, PLANNER
}

@Composable
fun TownsquarePlusScreen(
    onOpenSidebar: () -> Unit,
    onBackToFeed: () -> Unit,
    initialSubApp: PlusModularApp = PlusModularApp.NONE,
    modifier: Modifier = Modifier
) {
    var activeSubApp by remember { mutableStateOf(initialSubApp) }

    // Intercept back button when inside a modular sub-app
    BackHandler {
        if (activeSubApp != PlusModularApp.NONE) {
            activeSubApp = PlusModularApp.NONE
        } else {
            onBackToFeed()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        when (activeSubApp) {
            PlusModularApp.PHONE -> {
                TownsquarePhoneApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.MAILBOX -> {
                TownsquareMailboxApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.MAPS -> {
                TownsquareMapsApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.MARKETPLACE -> {
                TownsquareMarketplaceApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.EXTENSIONS -> {
                TownsquareExtensionsApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.FINANCE -> {
                TownsquareFinanceApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.CATALOGS -> {
                TownsquareCatalogsApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.STATE -> {
                TownsquareStateApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.ARCADE -> {
                TownsquareArcadeApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.HEALTH -> {
                TownsquareHealthApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.BOOKS -> {
                TownsquareBookwormApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.LINGO -> {
                TownsquareLingoApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.PLANNER -> {
                TownsquarePlannerApp(onBack = { activeSubApp = PlusModularApp.NONE })
            }
            PlusModularApp.NONE -> {
                // Main Townsquare Plus Hub Dashboard
                Column(modifier = Modifier.fillMaxSize()) {
                    TownsquareTopBar(
                        title = "Townsquare Plus",
                        subtitle = "Experimental Concept Lab & Prototype Sandbox",
                        onOpenSidebar = onOpenSidebar
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Hero Banner
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag("townsquare_plus_hero")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF0F2B48),
                                                    Color(0xFF1B1B36),
                                                    Color(0xFF281C10)
                                                )
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(20.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = WarmAmber
                                            ) {
                                                Text(
                                                    text = "🧪 CONCEPT EXPERIMENTAL LAB",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                                                    color = Color(0xFF261800),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = NeonCyan.copy(alpha = 0.2f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = Color(0xFF30D158),
                                                        modifier = Modifier.size(6.dp)
                                                    ) {}
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("SANDBOX ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Text(
                                            text = "Temporary Feature & App Incubator",
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Townsquare Plus serves as an experimental sandbox hosting temporary prototype concepts, skin extension builders, custom applet overlays, and feature previews that may or may not be implemented into regular Townsquare over time.",
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                            color = DarkTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Concept Incubator Section Header
                        item {
                            Text(
                                text = "PROTOTYPE CONCEPTS & EXTENSIONS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                        }

                        // 1. Applet Extension & Skin Studio Card
                        item {
                            SuperappModuleCard(
                                title = "Extension & Skin Studio Builder",
                                subtitle = "Custom Welcome Skins • Retro Terminal • Parchment Theme",
                                description = "Interactive studio builder for creating applet skin overrides, welcome screen extensions, custom color palettes, and retro UI skins.",
                                icon = Icons.Default.Build,
                                iconColor = NeonCyan,
                                badgeText = "BUILDER",
                                onClick = { activeSubApp = PlusModularApp.EXTENSIONS },
                                testTag = "open_extensions_builder_card"
                            )
                        }

                        // 2. Catalogs & Storefront Prototype
                        item {
                            SuperappModuleCard(
                                title = "Storefront & Catalog Prototype",
                                subtitle = "Merchant Catalogs • Press Merch • Special Collections",
                                description = "Experimental storefront concept displaying merchant catalog items, press merchandise, and official subscriptions before marketplace integration.",
                                icon = Icons.Default.Category,
                                iconColor = WarmAmber,
                                badgeText = "PROTOTYPE",
                                onClick = { activeSubApp = PlusModularApp.CATALOGS },
                                testTag = "open_catalogs_app_card"
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuperappModuleCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = iconColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, iconColor.copy(alpha = 0.4f)),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = iconColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Open App",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickHighlightRow(
    icon: ImageVector,
    label: String,
    detail: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                Text(text = detail, fontSize = 11.sp, color = DarkTextSecondary)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(18.dp))
    }
}
