package com.example.ui.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
fun AISmartSettings(
    enableAiFeatures: Boolean,
    enableFactChecking: Boolean,
    enableVoiceNarration: Boolean,
    enableSmartSummaries: Boolean,
    onUpdateEnableAiFeatures: (Boolean) -> Unit,
    onUpdateEnableFactChecking: (Boolean) -> Unit,
    onUpdateEnableVoiceNarration: (Boolean) -> Unit,
    onUpdateEnableSmartSummaries: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI & Smart Media Engine",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable AI Features",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = enableAiFeatures,
                    onCheckedChange = { onUpdateEnableAiFeatures(it) },
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
                    text = "Live AI Fact-Checking",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = enableFactChecking,
                    onCheckedChange = { onUpdateEnableFactChecking(it) },
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
                    text = "AI Voice Narration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = enableVoiceNarration,
                    onCheckedChange = { onUpdateEnableVoiceNarration(it) },
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
                    text = "AI Article Summaries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = enableSmartSummaries,
                    onCheckedChange = { onUpdateEnableSmartSummaries(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF003544),
                        checkedTrackColor = NeonCyan
                    )
                )
            }
        }
    }
}
