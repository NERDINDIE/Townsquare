package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItemEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import java.util.UUID

/**
 * Model representing a multitasking workspace browser tab.
 */
data class BrowserWorkspaceTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val sectionIndex: Int = 0,
    val activeArticle: MediaItemEntity? = null,
    val customSubtitle: String = ""
)

@Composable
fun BrowserMultitaskingBar(
    tabs: List<BrowserWorkspaceTab>,
    activeTabId: String,
    onSelectTab: (BrowserWorkspaceTab) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("browser_multitasking_tab_bar"),
        color = Color(0xFF0D121D),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Render each open browser tab
            tabs.forEach { tab ->
                val isActive = tab.id == activeTabId
                val icon = getIconForSection(tab.sectionIndex)

                BrowserTabItem(
                    tab = tab,
                    isActive = isActive,
                    icon = icon,
                    canClose = tabs.size > 1,
                    onClick = { onSelectTab(tab) },
                    onClose = { onCloseTab(tab.id) }
                )
            }

            // New Tab "+" Action Button
            IconButton(
                onClick = onNewTab,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .testTag("browser_new_tab_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Open New Multitasking Tab",
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun BrowserTabItem(
    tab: BrowserWorkspaceTab,
    isActive: Boolean,
    icon: ImageVector,
    canClose: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 6.dp, bottomEnd = 6.dp))
            .clickable { onClick() }
            .testTag("browser_tab_${tab.id}"),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
        color = if (isActive) Color(0xFF192233) else Color(0xFF101725),
        border = BorderStroke(
            1.dp,
            if (isActive) NeonCyan.copy(alpha = 0.5f) else Color(0xFF1F2A3D)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = if (canClose) 4.dp else 10.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Section or Content Favicon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) NeonCyan else Color.LightGray,
                modifier = Modifier.size(14.dp)
            )

            // Tab Title
            Text(
                text = tab.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                ),
                color = if (isActive) Color.White else Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 120.dp)
            )

            // Close Tab 'x' button (only if more than 1 tab open)
            if (canClose) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .testTag("browser_tab_close_${tab.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close tab",
                        tint = if (isActive) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

private fun getIconForSection(sectionIndex: Int): ImageVector {
    return when (sectionIndex) {
        0 -> Icons.AutoMirrored.Filled.Feed
        1 -> Icons.Default.DynamicFeed
        2 -> Icons.Default.Widgets
        3 -> Icons.Default.LiveTv
        4 -> Icons.Default.MenuBook
        5 -> Icons.Default.HistoryEdu
        6 -> Icons.Default.Radio
        7 -> Icons.Default.Hub
        8 -> Icons.Default.Tv
        9 -> Icons.Default.Newspaper
        else -> Icons.AutoMirrored.Filled.Feed
    }
}
