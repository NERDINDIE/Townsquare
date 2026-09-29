package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ClockTvIdentWidget(modifier: Modifier = Modifier) {
    var timeFormatted by remember { mutableStateOf("") }
    var isAnalogue by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        while (isActive) {
            timeFormatted = LocalDateTime.now().format(formatter)
            delay(1000L)
        }
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Black,
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
        modifier = modifier.clickable { isAnalogue = !isAnalogue }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TCTV",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = NeonCyan
            )
            if (isAnalogue) {
                // Simplified Analog Clock Canvas
                Canvas(modifier = Modifier.size(30.dp)) {
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
                    drawCircle(color = Color.White, radius = size.width / 2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
                    
                    val now = LocalDateTime.now()
                    val hourAngle = (now.hour % 12 + now.minute / 60f) * 30f - 90f
                    val minuteAngle = now.minute * 6f - 90f
                    
                    // Hour hand
                    drawLine(
                        color = Color.White,
                        start = center,
                        end = center + androidx.compose.ui.geometry.Offset(
                            (size.width / 4 * kotlin.math.cos(Math.toRadians(hourAngle.toDouble()))).toFloat(),
                            (size.height / 4 * kotlin.math.sin(Math.toRadians(hourAngle.toDouble()))).toFloat()
                        ),
                        strokeWidth = 4f
                    )
                    // Minute hand
                    drawLine(
                        color = NeonCyan,
                        start = center,
                        end = center + androidx.compose.ui.geometry.Offset(
                            (size.width / 2.5 * kotlin.math.cos(Math.toRadians(minuteAngle.toDouble()))).toFloat(),
                            (size.height / 2.5 * kotlin.math.sin(Math.toRadians(minuteAngle.toDouble()))).toFloat()
                        ),
                        strokeWidth = 2f
                    )
                }
            } else {
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}
