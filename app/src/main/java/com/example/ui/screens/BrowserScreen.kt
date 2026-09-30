package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.plus.extensions.Android10SoundEffects
import com.example.ui.components.DEFAULT_BOOKMARKS
import com.example.ui.components.WebTabItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun BrowserTopBar(
    tabs: List<WebTabItem>,
    activeTabId: String,
    onTabSelected: (String) -> Unit,
    onNewTab: (String?) -> Unit,
    onCloseTab: (String) -> Unit,
    onOpenSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0B0F17),
        border = BorderStroke(0.dp, Color(0xFF1E293B)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Sidebar Drawer Trigger (T Logo)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NeonCyan,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onOpenSidebar() }
                    .testTag("browser_sidebar_trigger")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "T",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003544)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Scrollable Top Bar Browser Tabs Strip
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val isActive = tab.id == activeTabId
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isActive) Color(0xFF1E293B) else Color(0xFF0F172A),
                        border = BorderStroke(
                            1.dp,
                            if (isActive) NeonCyan else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .widthIn(min = 100.dp, max = 170.dp)
                            .clickable { onTabSelected(tab.id) }
                            .testTag("browser_topbar_tab_${tab.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (tab.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = NeonCyan
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                } else {
                                    Text(
                                        text = "🌐",
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                                Text(
                                    text = tab.title.ifEmpty { "Web Page" },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isActive) Color.White else Color(0xFF94A3B8),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (tabs.size > 1) {
                                IconButton(
                                    onClick = { onCloseTab(tab.id) },
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close tab",
                                        tint = if (isActive) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Add New Tab Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onNewTab(null) }
                        .testTag("browser_topbar_add_tab_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Tab",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    tabs: List<WebTabItem>,
    activeTabId: String,
    onTabSelected: (String) -> Unit,
    onNewTab: (String?) -> Unit,
    onCloseTab: (String) -> Unit,
    onUpdateTab: (WebTabItem) -> Unit,
    onOpenSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTab = tabs.find { it.id == activeTabId } ?: tabs.firstOrNull()
    var urlInput by remember(activeTabId, activeTab?.url) {
        mutableStateOf(activeTab?.url ?: "https://news.google.com")
    }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. TOP BAR WITH INTEGRATED BROWSER TABS STRIP
        BrowserTopBar(
            tabs = tabs,
            activeTabId = activeTabId,
            onTabSelected = onTabSelected,
            onNewTab = onNewTab,
            onCloseTab = onCloseTab,
            onOpenSidebar = onOpenSidebar
        )

        // 2. OMNIBOX & NAVIGATION BAR
        Surface(
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = activeTab?.canGoBack == true,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (activeTab?.canGoBack == true) NeonCyan else Color(0xFF475569)
                        )
                    }

                    // Forward button
                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = activeTab?.canGoForward == true,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_forward_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (activeTab?.canGoForward == true) NeonCyan else Color(0xFF475569)
                        )
                    }

                    // Reload / Stop button
                    IconButton(
                        onClick = {
                            if (activeTab?.isLoading == true) {
                                webViewInstance?.stopLoading()
                            } else {
                                webViewInstance?.reload()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_reload_button")
                    ) {
                        Icon(
                            imageVector = if (activeTab?.isLoading == true) Icons.Default.Close else Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = Color.White
                        )
                    }

                    // Home button
                    IconButton(
                        onClick = {
                            val homeUrl = "https://news.google.com"
                            urlInput = homeUrl
                            webViewInstance?.loadUrl(homeUrl)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_home_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = WarmAmber
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Omnibox URL Field
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("browser_omnibox_input"),
                        singleLine = true,
                        placeholder = { Text("Search or type web address", fontSize = 13.sp, color = Color(0xFF64748B)) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (urlInput.startsWith("https")) Icons.Default.Lock else Icons.Default.Public,
                                contentDescription = null,
                                tint = if (urlInput.startsWith("https")) Color(0xFF10B981) else WarmAmber,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (urlInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            var formattedUrl = urlInput.trim()
                                            if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://") && !formattedUrl.startsWith("bbs://")) {
                                                formattedUrl = if (formattedUrl.contains("bbs")) {
                                                    "bbs://townsquare.local"
                                                } else if (formattedUrl.contains(".") && !formattedUrl.contains(" ")) {
                                                    "https://$formattedUrl"
                                                } else {
                                                    "https://www.google.com/search?q=${java.net.URLEncoder.encode(formattedUrl, "UTF-8")}"
                                                }
                                            }
                                            urlInput = formattedUrl
                                            if (!formattedUrl.startsWith("bbs://")) {
                                                webViewInstance?.loadUrl(formattedUrl)
                                            }
                                            if (activeTab != null) {
                                                onUpdateTab(activeTab.copy(url = formattedUrl, title = if (formattedUrl.startsWith("bbs://")) "Townsquare BBS Terminal 📟" else "Loading..."))
                                            }
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("browser_go_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Go",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                var formattedUrl = urlInput.trim()
                                if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://") && !formattedUrl.startsWith("bbs://")) {
                                    formattedUrl = if (formattedUrl.contains("bbs")) {
                                        "bbs://townsquare.local"
                                    } else if (formattedUrl.contains(".") && !formattedUrl.contains(" ")) {
                                        "https://$formattedUrl"
                                    } else {
                                        "https://www.google.com/search?q=${java.net.URLEncoder.encode(formattedUrl, "UTF-8")}"
                                    }
                                }
                                urlInput = formattedUrl
                                if (!formattedUrl.startsWith("bbs://")) {
                                    webViewInstance?.loadUrl(formattedUrl)
                                }
                                if (activeTab != null) {
                                    onUpdateTab(activeTab.copy(url = formattedUrl, title = if (formattedUrl.startsWith("bbs://")) "Townsquare BBS Terminal 📟" else "Loading..."))
                                }
                                focusManager.clearFocus()
                            }
                        ),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF020617),
                            unfocusedContainerColor = Color(0xFF020617),
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Quick Bookmarks Toggle Button
                    IconButton(
                        onClick = { showBookmarksSheet = !showBookmarksSheet },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_bookmarks_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarks",
                            tint = if (showBookmarksSheet) WarmAmber else Color(0xFF94A3B8)
                        )
                    }
                }

                // Loading Bar
                if (activeTab?.isLoading == true) {
                    LinearProgressIndicator(
                        progress = { (activeTab.progress / 100f).coerceIn(0.05f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = NeonCyan,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }
        }

        // 3. BOOKMARKS FAST ACCESS BAR
        AnimatedVisibility(
            visible = showBookmarksSheet,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                color = Color(0xFF0B132B),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Press Bookmarks:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    DEFAULT_BOOKMARKS.forEach { bm ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .clickable {
                                    urlInput = bm.url
                                    if (!bm.url.startsWith("bbs://")) {
                                        webViewInstance?.loadUrl(bm.url)
                                    }
                                    if (activeTab != null) {
                                        onUpdateTab(activeTab.copy(url = bm.url, title = if (bm.url.startsWith("bbs://")) "Townsquare BBS Terminal 📟" else bm.title))
                                    }
                                    showBookmarksSheet = false
                                }
                                .testTag("bookmark_${bm.title}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = bm.icon, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = bm.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. ACTIVE TAB WEB VIEW CONTAINER OR BBS TERMINAL
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF020617))
        ) {
            if (activeTab != null) {
                if (activeTab.url.startsWith("bbs://")) {
                    TownsquareBbsTerminalView(
                        onNavigateUrl = { newUrl ->
                            urlInput = newUrl
                            onUpdateTab(activeTab.copy(url = newUrl, title = "Townsquare BBS Terminal 📟"))
                        }
                    )
                } else {
                    key(activeTab.id) {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        loadWithOverviewMode = true
                                        useWideViewPort = true
                                        builtInZoomControls = true
                                        displayZoomControls = false
                                        cacheMode = WebSettings.LOAD_DEFAULT
                                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                    }

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            url?.let {
                                                urlInput = it
                                                onUpdateTab(
                                                    activeTab.copy(
                                                        url = it,
                                                        isLoading = true,
                                                        canGoBack = view?.canGoBack() ?: false,
                                                        canGoForward = view?.canGoForward() ?: false
                                                    )
                                                )
                                            }
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            val pageTitle = view?.title ?: "Web Page"
                                            url?.let {
                                                onUpdateTab(
                                                    activeTab.copy(
                                                        url = it,
                                                        title = pageTitle,
                                                        isLoading = false,
                                                        canGoBack = view?.canGoBack() ?: false,
                                                        canGoForward = view?.canGoForward() ?: false
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    webChromeClient = object : WebChromeClient() {
                                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                            super.onProgressChanged(view, newProgress)
                                            onUpdateTab(
                                                activeTab.copy(
                                                    progress = newProgress,
                                                    isLoading = newProgress < 100
                                                )
                                            )
                                        }

                                        override fun onReceivedTitle(view: WebView?, title: String?) {
                                            super.onReceivedTitle(view, title)
                                            title?.let {
                                                onUpdateTab(activeTab.copy(title = it))
                                            }
                                        }
                                    }

                                    webViewInstance = this
                                    loadUrl(activeTab.url)
                                }
                            },
                            update = { webView ->
                                webViewInstance = webView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

data class BbsPost(
    val id: String,
    val author: String,
    val role: String,
    val timestamp: String,
    val content: String
)

data class BbsThread(
    val id: String,
    val title: String,
    val boardCategory: String,
    val author: String,
    val posts: List<BbsPost>
)

@Composable
fun TownsquareBbsTerminalView(
    onNavigateUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedThemeIndex by remember { mutableIntStateOf(0) } // 0: Green Phosphor, 1: Amber Phosphor, 2: Cyber Cyan
    val phosphorColors = listOf(
        Color(0xFF00FF66), // Green
        Color(0xFFFFB300), // Amber
        Color(0xFF00E5FF)  // Cyan
    )
    val mainColor = phosphorColors[selectedThemeIndex]

    var selectedBoard by remember { mutableStateOf("PUBLIC_SQUARE") }
    var activeThreadId by remember { mutableStateOf<String?>(null) }
    var postInputText by remember { mutableStateOf("") }

    val boards = listOf(
        Pair("PUBLIC_SQUARE", "1. [PUBLIC_SQUARE] Civics & Neighborhood Talk"),
        Pair("HAM_RADIO", "2. [HAM_RADIO] Packet Radio & DX Logs"),
        Pair("LETTERPRESS", "3. [LETTERPRESS] Antiquarian Printing & Zines"),
        Pair("SOUND_WAVES", "4. [SOUND_WAVES] Modular Synths & Solfeggio"),
        Pair("ANIME_SHINBUN", "5. [ANIME_SHINBUN] Otaku Lore & Kyoto Retrospectives"),
        Pair("BOOKWORM_CLUB", "6. [BOOKWORM_CLUB] Rare Editions & Reviews")
    )

    var threads by remember {
        mutableStateOf(
            listOf(
                BbsThread(
                    id = "t1",
                    title = "📜 Welcome to Node #04 Townsquare BBS Carrier System",
                    boardCategory = "PUBLIC_SQUARE",
                    author = "SysOp_Alex",
                    posts = listOf(
                        BbsPost("p1", "SysOp_Alex", "OFFICIAL", "10:14:02 UTC", "Welcome citizen! Townsquare BBS is now operational over packet radio & dial-up. Feel free to leave messages on all boards."),
                        BbsPost("p2", "Resident_Elena", "CITIZEN", "10:22:18 UTC", "Great to see ANSI color graphics in full 14.4k speed! The district newsletter looks crisp.")
                    )
                ),
                BbsThread(
                    id = "t2",
                    title = "📻 28.400 MHz Packet Node & Antenna Heights",
                    boardCategory = "HAM_RADIO",
                    author = "KF8VT_Radio",
                    posts = listOf(
                        BbsPost("p3", "KF8VT_Radio", "RESPONDER", "08:12:00 UTC", "Elevated the Yagi antenna over District 4. Packet nodes responding with sub-10ms latency.")
                    )
                ),
                BbsThread(
                    id = "t3",
                    title = "⛩️ Fall 2026 Otaku Dispatches & Kyoto Animation Retrospective",
                    boardCategory = "ANIME_SHINBUN",
                    author = "Kenji_Sato",
                    posts = listOf(
                        BbsPost("p4", "Kenji_Sato", "CREATOR", "09:30:00 UTC", "New Shinbun articles uploaded to the media server. Check out our retrospective on classic hand-drawn backgrounds!")
                    )
                ),
                BbsThread(
                    id = "t4",
                    title = "📚 Rare 19th Century Letterpress Typeface Digitization",
                    boardCategory = "BOOKWORM_CLUB",
                    author = "Julian_Vance",
                    posts = listOf(
                        BbsPost("p5", "Julian_Vance", "PRESS", "07:45:22 UTC", "Scanned 142 pages of foundry wood block fonts. Synchronized with the connected Bookworm tracker.")
                    )
                )
            )
        )
    }

    val activeThread = threads.find { it.id == activeThreadId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .padding(12.dp)
    ) {
        // BBS CRT Header Banner
        Surface(
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, mainColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📟 TOWNSQUARE BBS • ANSI CRT TERMINAL",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = mainColor
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                selectedThemeIndex = (selectedThemeIndex + 1) % phosphorColors.size
                            },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Text("🎨", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                Android10SoundEffects.playModemDialupSound()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = mainColor.copy(alpha = 0.2f), contentColor = mainColor),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("DIAL NEXT NODE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CONNECT 14400 V.32bis / ANSI 80x25 / Carrier: 100% ONLINE / Node #04",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = mainColor.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Board Switcher Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(boards) { board ->
                val isSel = selectedBoard == board.first && activeThreadId == null
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isSel) mainColor.copy(alpha = 0.25f) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, if (isSel) mainColor else Color(0xFF334155)),
                    modifier = Modifier.clickable {
                        selectedBoard = board.first
                        activeThreadId = null
                    }
                ) {
                    Text(
                        text = board.second,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = if (isSel) mainColor else Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Terminal Output Screen
        Surface(
            color = Color(0xFF030712),
            border = BorderStroke(1.dp, mainColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            if (activeThread != null) {
                // Thread Posts View
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "< BACK TO BOARD",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mainColor,
                            modifier = Modifier.clickable { activeThreadId = null }
                        )

                        Text(
                            text = "THREAD ID: ${activeThread.id}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "TITLE: ${activeThread.title}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeThread.posts) { post ->
                            Surface(
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, mainColor.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${post.author} [${post.role}]",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = mainColor
                                        )
                                        Text(
                                            text = post.timestamp,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = post.content,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Reply Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = postInputText,
                            onValueChange = { postInputText = it },
                            placeholder = { Text("> Type reply message...", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mainColor,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = mainColor,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                if (postInputText.isNotBlank()) {
                                    val newPost = BbsPost(
                                        id = "p_${System.currentTimeMillis()}",
                                        author = "Citizen_User",
                                        role = "CITIZEN",
                                        timestamp = "Just now",
                                        content = postInputText
                                    )
                                    threads = threads.map {
                                        if (it.id == activeThread.id) {
                                            it.copy(posts = it.posts + newPost)
                                        } else it
                                    }
                                    postInputText = ""
                                    Android10SoundEffects.playTrackballClick()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = mainColor, contentColor = Color.Black),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("POST", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Board Thread List
                val boardThreads = threads.filter { it.boardCategory == selectedBoard }
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Text(
                        text = "=== BOARD DIRECTORY: [$selectedBoard] ===",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = mainColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (boardThreads.isEmpty()) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f).fillMaxWidth()) {
                            Text(
                                text = "NO THREADS FOUND ON THIS BOARD.\nCREATE FIRST POST BELOW.",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(boardThreads) { thread ->
                                Surface(
                                    color = Color(0xFF0B132B),
                                    border = BorderStroke(1.dp, mainColor.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        activeThreadId = thread.id
                                        Android10SoundEffects.playTrackballClick()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = thread.title,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Posted by ${thread.author} • ${thread.posts.size} replies",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = mainColor.copy(alpha = 0.8f)
                                            )
                                        }
                                        Text(
                                            text = "[VIEW]",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = mainColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Start New Thread Field
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = postInputText,
                            onValueChange = { postInputText = it },
                            placeholder = { Text("> Start new thread subject...", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = mainColor,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = mainColor,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = {
                                if (postInputText.isNotBlank()) {
                                    val newTh = BbsThread(
                                        id = "t_${System.currentTimeMillis()}",
                                        title = postInputText,
                                        boardCategory = selectedBoard,
                                        author = "Citizen_User",
                                        posts = listOf(
                                            BbsPost(
                                                id = "p_1",
                                                author = "Citizen_User",
                                                role = "CITIZEN",
                                                timestamp = "Just now",
                                                content = "Thread started on $selectedBoard."
                                            )
                                        )
                                    )
                                    threads = listOf(newTh) + threads
                                    postInputText = ""
                                    activeThreadId = newTh.id
                                    Android10SoundEffects.playTrackballClick()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = mainColor, contentColor = Color.Black),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("+ NEW THREAD", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
