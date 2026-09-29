package com.example.ui.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan

@Composable
fun StorageAlertsSettings(
    isOfflineMode: Boolean,
    autoCacheEnabled: Boolean,
    breakingNewsEnabled: Boolean,
    printKioskEnabled: Boolean,
    onToggleOfflineMode: () -> Unit,
    onUpdateAutoCache: (Boolean) -> Unit,
    onUpdateBreakingNewsAlerts: (Boolean) -> Unit,
    onUpdatePrintKioskAlerts: (Boolean) -> Unit,
    onClearMediaCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offline Mode",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isOfflineMode,
                    onCheckedChange = { onToggleOfflineMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF003544),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Auto-Cache Offline",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = autoCacheEnabled,
                    onCheckedChange = { onUpdateAutoCache(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF003544),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Breaking News Push Alerts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = breakingNewsEnabled,
                    onCheckedChange = { onUpdateBreakingNewsAlerts(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF003544),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Print Kiosk Stock Alerts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = printKioskEnabled,
                    onCheckedChange = { onUpdatePrintKioskAlerts(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF003544),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onClearMediaCache,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Clear Media Cache",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
