package com.example.ui.plus.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Isolated E-Ink Bedside Hub & Daily Paperwhite Bulletin Simulation View Component
 */
@Composable
fun IotEInkDisplayView(
    liveTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // E-Ink Frame
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF7F5EE),
            border = BorderStroke(3.dp, Color(0xFF4A463B)),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOWNSQUARE E-INK DAILY BULLETIN",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = liveTime.format(DateTimeFormatter.ofPattern("EEE MMM dd • HH:mm")),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF333333)
                    )
                }

                HorizontalDivider(color = Color(0xFF2B2B2B), thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "CIVIC CLEAN ENERGY ORDINANCE PASSES UNANIMOUSLY",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Commercial rooftops to begin solar conversions next quarter as transit corridors expand pedestrian lanes.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("🏡 INDOOR CLIMATE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Text("70°F • 45% Humidity", fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Column {
                        Text("⛅ OUTDOOR RADAR", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Text("72°F • Gentle Breeze", fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}
