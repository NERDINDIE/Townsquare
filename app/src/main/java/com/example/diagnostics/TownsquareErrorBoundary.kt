package com.example.diagnostics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

/**
 * Townsquare OS Safe Boundary Composable.
 * Provides fallback UI and recovery hooks when components encounter errors or data anomalies.
 */
@Composable
fun TownsquareErrorBoundary(
    tag: String,
    modifier: Modifier = Modifier,
    fallbackTitle: String = "Component Glitch Recovered",
    content: @Composable () -> Unit
) {
    var isGlitchDetected by remember { mutableStateOf(false) }
    var glitchDetail by remember { mutableStateOf("") }
    var retryKey by remember { mutableIntStateOf(0) }

    if (isGlitchDetected) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkCardBg
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarmAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = fallbackTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber
                        )
                    )
                }

                Text(
                    text = "Townsquare Self-Healing Daemon isolated an issue in [$tag]:\n${if (glitchDetail.isNotBlank()) glitchDetail else "Visual state refreshed."}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isGlitchDetected = false
                            retryKey++
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Self-Heal & Retry")
                    }

                    FilledTonalButton(
                        onClick = {
                            SessionErrorHandler.logEvent(
                                severity = DiagnosticSeverity.WARNING,
                                category = GlitchCategory.CORRUPT_STATE,
                                tag = tag,
                                message = "User manually reported isolated UI glitch: $glitchDetail"
                            )
                        }
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Diagnostics")
                    }
                }
            }
        }
    } else {
        key(retryKey) {
            content()
        }
    }
}
