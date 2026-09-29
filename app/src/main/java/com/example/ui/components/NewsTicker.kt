package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun NewsTicker(
    headlines: List<String>,
    modifier: Modifier = Modifier
) {
    if (headlines.isEmpty()) return

    val tickerText = headlines.joinToString(" • ")
    var offset by remember { mutableFloatStateOf(0f) }
    
    // Simulate real-time updates
    var currentHeadlines by remember { mutableStateOf(headlines) }
    LaunchedEffect(Unit) {
        while(isActive) {
            delay(30000L)
            currentHeadlines = currentHeadlines.shuffled()
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth().height(36.dp),
        color = Color.Black,
        contentColor = NeonCyan
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = tickerText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = NeonCyan,
                modifier = Modifier.offset(x = offset.dp)
            )
        }
    }
}
