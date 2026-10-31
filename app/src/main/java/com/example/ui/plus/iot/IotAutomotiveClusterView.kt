package com.example.ui.plus.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Isolated In-Car Automotive Heads-Up Cluster Simulation View Component
 */
@Composable
fun IotAutomotiveClusterView(
    liveTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF070C14),
            border = BorderStroke(2.dp, NeonCyan.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TOWNSQUARE AUTO CLUSTER", fontSize = 12.sp, style = MaterialTheme.typography.titleSmall, color = NeonCyan)
                    }
                    Text(liveTime.format(DateTimeFormatter.ofPattern("hh:mm a")), fontSize = 14.sp, style = MaterialTheme.typography.titleMedium, color = Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("45", fontSize = 48.sp, style = MaterialTheme.typography.displayMedium, color = Color.White)
                        Text("MPH SPEED", fontSize = 11.sp, color = DarkTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("88.5 FM", fontSize = 28.sp, style = MaterialTheme.typography.headlineSmall, color = WarmAmber)
                        Text("Sonic Frequencies", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F1A28),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = MintTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Next Stop: Azure Palapa Coastal Cove", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            Text("3.4 miles • Clear Traffic on Coastal Way", fontSize = 11.sp, color = DarkTextSecondary)
                        }
                    }
                }
            }
        }
    }
}
