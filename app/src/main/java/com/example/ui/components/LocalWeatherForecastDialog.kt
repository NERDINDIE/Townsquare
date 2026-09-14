package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyForecast
import com.example.data.model.HourlyForecast
import com.example.data.model.LocalWeatherData
import com.example.data.model.LocalWeatherRepository
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.util.ShareHelper
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalWeatherForecastDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    initialDistrictId: String = "metro",
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedDistrictId by remember { mutableStateOf(initialDistrictId) }
    var useCelsius by remember { mutableStateOf(false) }
    var selectedRadarLayer by remember { mutableStateOf("Precipitation") }

    val weather = remember(selectedDistrictId) {
        LocalWeatherRepository.getWeatherDataForDistrict(selectedDistrictId)
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .testTag("local_weather_forecast_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = WarmAmber.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = weather.condition.iconEmoji, fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Local Weather Desk",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Civic Meteorological Telemetry",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Temp Unit Toggle
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.clickable { useCelsius = !useCelsius }
                        ) {
                            Text(
                                text = if (useCelsius) "°C (Metric)" else "°F (Imperial)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Share Weather Snapshot
                        IconButton(
                            onClick = {
                                val temp = if (useCelsius) "${weather.currentTempC}°C" else "${weather.currentTempF}°F"
                                val high = if (useCelsius) "${weather.highTempC}°C" else "${weather.highTempF}°F"
                                val low = if (useCelsius) "${weather.lowTempC}°C" else "${weather.lowTempF}°F"
                                val shareText = "🌤️ Townsquare Local Weather Forecast\n" +
                                        "${weather.districtName}: $temp, ${weather.condition.summary}\n" +
                                        "High: $high • Low: $low\n" +
                                        "Humidity: ${weather.humidityPercent}% • Wind: ${weather.windMph} mph ${weather.windDirection}\n" +
                                        "Air Quality: ${weather.aqiValue} (${weather.aqiDescription})\n" +
                                        "— via Townsquare Civic Weather Desk"
                                ShareHelper.sharePlainText(context, "Local Weather Snapshot", shareText)
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share forecast", tint = Color.White)
                        }

                        // Close Dialog
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("close_weather_dialog_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // District Selector Chips
                val districtScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B111E))
                        .horizontalScroll(districtScroll)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocalWeatherRepository.districts.forEach { (id, name) ->
                        val isSelected = id == selectedDistrictId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDistrictId = id },
                            label = {
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) NeonCyan else Color.Gray
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                selectedLabelColor = NeonCyan,
                                containerColor = Color(0xFF161F30),
                                labelColor = Color.LightGray
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else Color(0xFF233047)
                            )
                        )
                    }
                }

                // Scrollable Weather Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    // 1. Current Weather Hero Card
                    item {
                        WeatherHeroCard(
                            weather = weather,
                            useCelsius = useCelsius
                        )
                    }

                    // 2. Active Weather Advisories (if any)
                    if (weather.advisories.isNotEmpty()) {
                        item {
                            weather.advisories.forEach { adv ->
                                WeatherAdvisoryCard(advisory = adv)
                            }
                        }
                    }

                    // 3. Hourly Forecast Section
                    item {
                        HourlyForecastSection(
                            hourlyList = weather.hourlyForecasts,
                            useCelsius = useCelsius
                        )
                    }

                    // 4. 7-Day Extended Outlook
                    item {
                        DailyForecastSection(
                            dailyList = weather.dailyForecasts,
                            useCelsius = useCelsius
                        )
                    }

                    // 5. Atmospheric Metrics Matrix Grid
                    item {
                        AtmosphericMetricsGrid(weather = weather)
                    }

                    // 6. Live Radar & Weather Satellite Simulator
                    item {
                        LiveRadarWidget(
                            districtName = weather.districtName,
                            selectedLayer = selectedRadarLayer,
                            onSelectLayer = { selectedRadarLayer = it }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun WeatherHeroCard(
    weather: LocalWeatherData,
    useCelsius: Boolean
) {
    val curTemp = if (useCelsius) "${weather.currentTempC}°" else "${weather.currentTempF}°"
    val feelsLike = if (useCelsius) "${weather.feelsLikeC}°" else "${weather.feelsLikeF}°"
    val high = if (useCelsius) "${weather.highTempC}°" else "${weather.highTempF}°"
    val low = if (useCelsius) "${weather.lowTempC}°" else "${weather.lowTempF}°"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weather_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101929)),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF10233B),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weather.districtName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = weather.condition.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE RADAR SYNC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = curTemp,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 64.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = weather.condition.summary,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                            Text(
                                text = "Feels like $feelsLike",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }

                    Text(
                        text = weather.condition.iconEmoji,
                        fontSize = 52.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High / Low, Wind & Rain row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF090E17), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "H: $high  •  L: $low",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Air, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${weather.windMph} mph ${weather.windDirection}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.LightGray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${weather.humidityPercent}% Hum",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherAdvisoryCard(advisory: com.example.data.model.WeatherAdvisory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261908)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Weather Advisory",
                tint = WarmAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = advisory.title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = advisory.issuedTimeAgo,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${advisory.details} (${advisory.issuer})",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2E8F0)
                )
            }
        }
    }
}

@Composable
private fun HourlyForecastSection(
    hourlyList: List<HourlyForecast>,
    useCelsius: Boolean
) {
    Column {
        Text(
            text = "24-HOUR CIVIC FORECAST",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            hourlyList.forEach { hour ->
                val temp = if (useCelsius) "${hour.temperatureC}°" else "${hour.temperatureF}°"
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (hour.isCurrentHour) Color(0xFF1E293B) else Color(0xFF101726),
                    border = BorderStroke(
                        1.dp,
                        if (hour.isCurrentHour) NeonCyan.copy(alpha = 0.6f) else Color(0xFF1E293B)
                    ),
                    modifier = Modifier.width(68.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = hour.hourLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (hour.isCurrentHour) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (hour.isCurrentHour) NeonCyan else Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = hour.conditionEmoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = temp,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (hour.precipitationChance > 0) {
                            Text(
                                text = "${hour.precipitationChance}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF38BDF8)
                            )
                        } else {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyForecastSection(
    dailyList: List<DailyForecast>,
    useCelsius: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101726)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "7-DAY EXTENDED OUTLOOK",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            dailyList.forEachIndexed { index, day ->
                val high = if (useCelsius) "${day.highTempC}°" else "${day.highTempF}°"
                val low = if (useCelsius) "${day.lowTempC}°" else "${day.lowTempF}°"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.width(90.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = day.dateLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Text(text = day.conditionEmoji, fontSize = 18.sp)
                        Text(
                            text = day.conditionSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            maxLines = 1
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = low,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF38BDF8), WarmAmber)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = high,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                if (index < dailyList.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF1E293B))
                    )
                }
            }
        }
    }
}

@Composable
private fun AtmosphericMetricsGrid(weather: LocalWeatherData) {
    Column {
        Text(
            text = "ATMOSPHERIC & ENVIRONMENTAL SENSORS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Air Quality (AQI)
            AtmosphericMetricCard(
                title = "Air Quality (AQI)",
                value = "${weather.aqiValue}",
                subtitle = weather.aqiDescription,
                icon = Icons.Default.Air,
                accentColor = if (weather.aqiValue <= 50) Color(0xFF10B981) else WarmAmber,
                modifier = Modifier.weight(1f)
            )

            // UV Index
            AtmosphericMetricCard(
                title = "UV Radiation",
                value = "${weather.uvIndex} UV",
                subtitle = weather.uvDescription,
                icon = Icons.Default.WbSunny,
                accentColor = WarmAmber,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Barometer
            AtmosphericMetricCard(
                title = "Barometric Pressure",
                value = "${weather.barometricPressureInHg} inHg",
                subtitle = "Steady trend",
                icon = Icons.Default.Navigation,
                accentColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )

            // Sun Timings
            AtmosphericMetricCard(
                title = "Sun Horizon",
                value = weather.sunsetTime,
                subtitle = "Sunrise ${weather.sunriseTime}",
                icon = Icons.Default.WbSunny,
                accentColor = Color(0xFFF97316),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AtmosphericMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF101726),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accentColor
            )
        }
    }
}

@Composable
private fun LiveRadarWidget(
    districtName: String,
    selectedLayer: String,
    onSelectLayer: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B121E)),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CIVIC METEOROLOGICAL RADAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = NeonCyan
                    )
                    Text(
                        text = "Doppler Sweep • $districtName",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                // Layer selection chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Precipitation", "Cloud", "Wind").forEach { layer ->
                        val isSelected = selectedLayer == layer
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else Color.Transparent),
                            modifier = Modifier.clickable { onSelectLayer(layer) }
                        ) {
                            Text(
                                text = layer,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) NeonCyan else Color.LightGray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Animated Radar Sweep Canvas
            val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
            val sweepAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "radar_angle"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF030811))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = minOf(size.width, size.height) * 0.45f

                    // Concentric Range Rings
                    for (i in 1..4) {
                        val r = maxRadius * (i / 4f)
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Crosshairs
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Simulated Weather Echo Blips (Clouds / Rain)
                    val blip1 = Offset(center.x + maxRadius * 0.4f, center.y - maxRadius * 0.3f)
                    val blip2 = Offset(center.x - maxRadius * 0.5f, center.y + maxRadius * 0.2f)
                    val blip3 = Offset(center.x + maxRadius * 0.2f, center.y + maxRadius * 0.5f)

                    drawCircle(color = Color(0xFF10B981).copy(alpha = 0.45f), radius = 28.dp.toPx(), center = blip1)
                    drawCircle(color = Color(0xFF38BDF8).copy(alpha = 0.5f), radius = 20.dp.toPx(), center = blip2)
                    drawCircle(color = Color(0xFF06B6D4).copy(alpha = 0.4f), radius = 32.dp.toPx(), center = blip3)

                    // Rotating Radar Sweep line
                    val rad = Math.toRadians(sweepAngle.toDouble())
                    val sweepEnd = Offset(
                        (center.x + maxRadius * cos(rad)).toFloat(),
                        (center.y + maxRadius * sin(rad)).toFloat()
                    )
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.8f),
                        start = center,
                        end = sweepEnd,
                        strokeWidth = 2.dp.toPx()
                    )

                    // Center Radar Station dot
                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = center)
                }

                // Legend Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Text("Light Rain", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White)
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                    Text("Moderate", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White)
                }
            }
        }
    }
}
