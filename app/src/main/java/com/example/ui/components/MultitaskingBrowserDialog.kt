package com.example.ui.components

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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import java.util.UUID

data class WebTabItem(
    val id: String = UUID.randomUUID().toString(),
    var url: String = "https://news.google.com",
    var title: String = "Townsquare Press Wire",
    var isLoading: Boolean = false,
    var progress: Int = 0,
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false
)

data class BookmarkItem(
    val title: String,
    val url: String,
    val icon: String
)

val DEFAULT_BOOKMARKS = listOf(
    BookmarkItem("Townsquare Wire", "https://news.google.com", "📰"),
    BookmarkItem("Townsquare BBS", "bbs://townsquare.local", "📟"),
    BookmarkItem("Civic Wikipedia", "https://wikipedia.org", "🌐"),
    BookmarkItem("Weather Pulse", "https://weather.com", "🌤️"),
    BookmarkItem("Public Archives", "https://archive.org", "📚"),
    BookmarkItem("Project Gutenberg", "https://gutenberg.org", "📖")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultitaskingBrowserDialog(
    tabs: List<WebTabItem>,
    activeTabId: String,
    onTabSelected: (String) -> Unit,
    onNewTab: (String?) -> Unit,
    onCloseTab: (String) -> Unit,
    onUpdateTab: (WebTabItem) -> Unit,
    onDismissRequest: () -> Unit
) {
    val activeTab = tabs.find { it.id == activeTabId } ?: tabs.firstOrNull()
    var urlInput by remember(activeTabId, activeTab?.url) {
        mutableStateOf(activeTab?.url ?: "https://news.google.com")
    }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("multitasking_browser_dialog"),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. TOP TAB BAR FOR MULTITASKING
                Surface(
                    color = Color(0xFF0B0F17),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Scrollable Tab List
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            tabs.forEach { tab ->
                                val isSelected = tab.id == activeTabId
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF090D14),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) WarmAmber else Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier
                                        .widthIn(min = 110.dp, max = 160.dp)
                                        .clickable { onTabSelected(tab.id) }
                                        .testTag("browser_tab_${tab.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (tab.isLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(12.dp),
                                                    strokeWidth = 1.5.dp,
                                                    color = WarmAmber
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Public,
                                                    contentDescription = null,
                                                    tint = if (isSelected) WarmAmber else Color.Gray,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (tab.title.isNotBlank()) tab.title else "New Tab",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        if (tabs.size > 1) {
                                            IconButton(
                                                onClick = { onCloseTab(tab.id) },
                                                modifier = Modifier.size(16.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close tab",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Add Tab Button
                            IconButton(
                                onClick = { onNewTab("https://news.google.com") },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("browser_add_tab_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Tab",
                                    tint = WarmAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Close Dialog Icon
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Browser",
                                tint = Color.White
                            )
                        }
                    }
                }

                // 2. OMNIBOX & NAVIGATION BAR
                Surface(
                    color = Color(0xFF131C2E),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Back
                            IconButton(
                                onClick = { webViewInstance?.goBack() },
                                enabled = activeTab?.canGoBack == true,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (activeTab?.canGoBack == true) Color.White else Color.DarkGray
                                )
                            }

                            // Forward
                            IconButton(
                                onClick = { webViewInstance?.goForward() },
                                enabled = activeTab?.canGoForward == true,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Forward",
                                    tint = if (activeTab?.canGoForward == true) Color.White else Color.DarkGray
                                )
                            }

                            // Reload / Stop
                            IconButton(
                                onClick = {
                                    if (activeTab?.isLoading == true) {
                                        webViewInstance?.stopLoading()
                                    } else {
                                        webViewInstance?.reload()
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (activeTab?.isLoading == true) Icons.Default.Close else Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                    tint = WarmAmber
                                )
                            }

                            // Address Bar Field
                            TextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("browser_address_bar"),
                                placeholder = {
                                    Text("Search or enter web address...", fontSize = 12.sp, color = Color.Gray)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Secure",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (urlInput.isNotEmpty()) {
                                        IconButton(onClick = { urlInput = "" }, modifier = Modifier.size(18.dp)) {
                                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        focusManager.clearFocus()
                                        val formatted = formatWebUrl(urlInput)
                                        urlInput = formatted
                                        activeTab?.let { tab ->
                                            onUpdateTab(tab.copy(url = formatted))
                                        }
                                        webViewInstance?.loadUrl(formatted)
                                    }
                                ),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = Color.White),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF0B0F17),
                                    unfocusedContainerColor = Color(0xFF0B0F17),
                                    focusedIndicatorColor = WarmAmber,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            // Quick Bookmarks Toggle
                            IconButton(
                                onClick = { showBookmarksSheet = !showBookmarksSheet },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmarks",
                                    tint = if (showBookmarksSheet) WarmAmber else Color.White
                                )
                            }
                        }

                        // Loading Progress Indicator
                        if (activeTab?.isLoading == true) {
                            LinearProgressIndicator(
                                progress = { (activeTab.progress / 100f).coerceIn(0.1f, 1.0f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp),
                                color = WarmAmber,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }

                // 3. BOOKMARKS QUICK BAR (COLLAPSIBLE)
                AnimatedVisibility(visible = showBookmarksSheet) {
                    Surface(
                        color = Color(0xFF090D14),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PRESS WIRE BOOKMARKS:",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = WarmAmber
                            )
                            DEFAULT_BOOKMARKS.forEach { bm ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(0.5.dp, Color(0xFF334155)),
                                    modifier = Modifier
                                        .clickable {
                                            urlInput = bm.url
                                            activeTab?.let { tab ->
                                                onUpdateTab(tab.copy(url = bm.url, title = bm.title))
                                            }
                                            webViewInstance?.loadUrl(bm.url)
                                            showBookmarksSheet = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = bm.icon, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = bm.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. MAIN WEBVIEW CONTAINER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White)
                ) {
                    if (activeTab != null) {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                                    @SuppressLint("SetJavaScriptEnabled")
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.loadWithOverviewMode = true
                                    settings.useWideViewPort = true
                                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            url?.let {
                                                urlInput = it
                                                onUpdateTab(
                                                    activeTab.copy(
                                                        url = it,
                                                        isLoading = true,
                                                        canGoBack = canGoBack(),
                                                        canGoForward = canGoForward()
                                                    )
                                                )
                                            }
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            val currentTitle = view?.title ?: "Web Page"
                                            onUpdateTab(
                                                activeTab.copy(
                                                    title = currentTitle,
                                                    isLoading = false,
                                                    canGoBack = canGoBack(),
                                                    canGoForward = canGoForward()
                                                )
                                            )
                                        }
                                    }

                                    webChromeClient = object : WebChromeClient() {
                                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                            super.onProgressChanged(view, newProgress)
                                            onUpdateTab(activeTab.copy(progress = newProgress))
                                        }

                                        override fun onReceivedTitle(view: WebView?, title: String?) {
                                            super.onReceivedTitle(view, title)
                                            title?.let {
                                                onUpdateTab(activeTab.copy(title = it))
                                            }
                                        }
                                    }

                                    loadUrl(activeTab.url)
                                    webViewInstance = this
                                }
                            },
                            update = { webView ->
                                webViewInstance = webView
                                if (webView.url != activeTab.url) {
                                    webView.loadUrl(activeTab.url)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // 5. FOOTER STATUS BAR
                Surface(
                    color = Color(0xFF0B0F17),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MULTITASKING TABS ACTIVE (${tabs.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                        Text(
                            text = activeTab?.title ?: "Townsquare Web Browser",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private fun formatWebUrl(input: String): String {
    val trimmed = input.trim()
    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
        else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
    }
}
