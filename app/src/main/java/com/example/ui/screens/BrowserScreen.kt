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
import com.example.ui.components.ClockTvIdentWidget
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

            Spacer(modifier = Modifier.width(8.dp))

            ClockTvIdentWidget()
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
                                            if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                                                formattedUrl = if (formattedUrl.contains(".") && !formattedUrl.contains(" ")) {
                                                    "https://$formattedUrl"
                                                } else {
                                                    "https://www.google.com/search?q=${java.net.URLEncoder.encode(formattedUrl, "UTF-8")}"
                                                }
                                            }
                                            urlInput = formattedUrl
                                            webViewInstance?.loadUrl(formattedUrl)
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
                                if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                                    formattedUrl = if (formattedUrl.contains(".") && !formattedUrl.contains(" ")) {
                                        "https://$formattedUrl"
                                    } else {
                                        "https://www.google.com/search?q=${java.net.URLEncoder.encode(formattedUrl, "UTF-8")}"
                                    }
                                }
                                urlInput = formattedUrl
                                webViewInstance?.loadUrl(formattedUrl)
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
                                    webViewInstance?.loadUrl(bm.url)
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

        // 4. ACTIVE TAB WEB VIEW CONTAINER
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            if (activeTab != null) {
                key(activeTab.id) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
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
