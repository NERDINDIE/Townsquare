package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.isActive

data class Headline(val title: String, val text: String, val source: String)

@Composable
fun NewsTicker(
    headlines: List<Headline>,
    onHeadlineClick: (Headline) -> Unit,
    modifier: Modifier = Modifier
) {
    if (headlines.isEmpty()) return

    val scrollState = remember { Animatable(0f) }
    
    // Simplistic infinite scroll: animate to a large negative number
    LaunchedEffect(Unit) {
        while(isActive) {
            scrollState.animateTo(
                targetValue = -2000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(20000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            scrollState.snapTo(1000f)
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
            Row(
                modifier = Modifier.offset { IntOffset(scrollState.value.toInt(), 0) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                headlines.forEach { headline ->
                    Text(
                        text = headline.title + " • ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = NeonCyan,
                        modifier = Modifier.clickable { onHeadlineClick(headline) }
                    )
                }
            }
        }
    }
}
