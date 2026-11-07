package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Modern OS Interface Kit providing sleek glassmorphic containers,
 * quick action command center bars, and tactile haptic feedback widgets.
 */
@Composable
fun ModernOsCommandCenterBar(
    onOpenFlipbookEngine: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF141923).copy(alpha = 0.9f),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .testTag("modern_os_command_center")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Flipbook Engine Launcher
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenFlipbookEngine()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("command_center_flipbook_btn")
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Haptic Flip Engine", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }

            // Quick Inbox
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onOpenInbox()
                },
                modifier = Modifier.testTag("command_center_inbox_btn")
            ) {
                Surface(
                    shape = CircleShape,
                    color = WarmAmber.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp),
                    border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mail, contentDescription = "Inbox", tint = WarmAmber, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Quick Search / Multitask
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onOpenSearch()
                },
                modifier = Modifier.testTag("command_center_search_btn")
            ) {
                Surface(
                    shape = CircleShape,
                    color = ElectricIndigo.copy(alpha = 0.2f),
                    modifier = Modifier.size(38.dp),
                    border = BorderStroke(1.dp, ElectricIndigo.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = ElectricIndigo, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
