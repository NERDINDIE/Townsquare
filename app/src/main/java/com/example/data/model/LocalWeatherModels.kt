package com.example.data.model

data class WeatherCondition(
    val summary: String,
    val description: String,
    val iconEmoji: String,
    val iconResName: String = "",
    val precipitationType: String = "None"
)

data class HourlyForecast(
    val hourLabel: String,
    val temperatureF: Int,
    val temperatureC: Int,
    val conditionEmoji: String,
    val precipitationChance: Int,
    val isCurrentHour: Boolean = false
)

data class DailyForecast(
    val dayLabel: String,
    val dateLabel: String,
    val highTempF: Int,
    val highTempC: Int,
    val lowTempF: Int,
    val lowTempC: Int,
    val conditionEmoji: String,
    val conditionSummary: String,
    val precipitationChance: Int
)

data class WeatherAdvisory(
    val title: String,
    val severity: String, // Advisory, Watch, Warning
    val issuer: String,
    val details: String,
    val issuedTimeAgo: String
)

data class LocalWeatherData(
    val districtId: String,
    val districtName: String,
    val currentTempF: Int,
    val currentTempC: Int,
    val feelsLikeF: Int,
    val feelsLikeC: Int,
    val highTempF: Int,
    val highTempC: Int,
    val lowTempF: Int,
    val lowTempC: Int,
    val condition: WeatherCondition,
    val humidityPercent: Int,
    val windMph: Int,
    val windDirection: String,
    val uvIndex: Int,
    val uvDescription: String,
    val aqiValue: Int,
    val aqiDescription: String,
    val barometricPressureInHg: Double,
    val visibilityMiles: Int,
    val dewPointF: Int,
    val sunriseTime: String,
    val sunsetTime: String,
    val advisories: List<WeatherAdvisory>,
    val hourlyForecasts: List<HourlyForecast>,
    val dailyForecasts: List<DailyForecast>
)

object LocalWeatherRepository {

    val districts = listOf(
        "metro" to "Townsquare Metro & Downtown",
        "riverfront" to "Riverfront Parkway & Harbor",
        "westhills" to "West Hills & Upper Valley",
        "oldtown" to "Old Town Historic Quarter",
        "southpark" to "South Park Commons"
    )

    fun getWeatherDataForDistrict(districtId: String): LocalWeatherData {
        return when (districtId) {
            "riverfront" -> LocalWeatherData(
                districtId = "riverfront",
                districtName = "Riverfront Parkway & Harbor",
                currentTempF = 68,
                currentTempC = 20,
                feelsLikeF = 67,
                feelsLikeC = 19,
                highTempF = 73,
                highTempC = 23,
                lowTempF = 58,
                lowTempC = 14,
                condition = WeatherCondition(
                    summary = "Passing Coastal Fog",
                    description = "Cool maritime breeze with mist lifting by midday",
                    iconEmoji = "🌫️"
                ),
                humidityPercent = 76,
                windMph = 14,
                windDirection = "WNW",
                uvIndex = 3,
                uvDescription = "Moderate",
                aqiValue = 28,
                aqiDescription = "Good",
                barometricPressureInHg = 29.98,
                visibilityMiles = 8,
                dewPointF = 56,
                sunriseTime = "6:45 AM",
                sunsetTime = "7:26 PM",
                advisories = listOf(
                    WeatherAdvisory(
                        title = "Small Craft & Kayak Caution",
                        severity = "Advisory",
                        issuer = "Harbor Police & River Watch",
                        details = "Ebb currents running at 2.8 knots near Pier 4. Wear life preservers.",
                        issuedTimeAgo = "45m ago"
                    )
                ),
                hourlyForecasts = generateHourly(68, "🌫️"),
                dailyForecasts = sampleDailyForecasts
            )

            "westhills" -> LocalWeatherData(
                districtId = "westhills",
                districtName = "West Hills & Upper Valley",
                currentTempF = 75,
                currentTempC = 24,
                feelsLikeF = 74,
                feelsLikeC = 23,
                highTempF = 79,
                highTempC = 26,
                lowTempF = 56,
                lowTempC = 13,
                condition = WeatherCondition(
                    summary = "Crisp Autumn Sun",
                    description = "Bright clear skies with excellent mountain visibility",
                    iconEmoji = "☀️"
                ),
                humidityPercent = 42,
                windMph = 6,
                windDirection = "NE",
                uvIndex = 6,
                uvDescription = "High",
                aqiValue = 22,
                aqiDescription = "Excellent",
                barometricPressureInHg = 30.15,
                visibilityMiles = 15,
                dewPointF = 48,
                sunriseTime = "6:41 AM",
                sunsetTime = "7:29 PM",
                advisories = emptyList(),
                hourlyForecasts = generateHourly(75, "☀️"),
                dailyForecasts = sampleDailyForecasts
            )

            else -> LocalWeatherData(
                districtId = "metro",
                districtName = "Townsquare Metro & Downtown",
                currentTempF = 72,
                currentTempC = 22,
                feelsLikeF = 73,
                feelsLikeC = 23,
                highTempF = 77,
                highTempC = 25,
                lowTempF = 61,
                lowTempC = 16,
                condition = WeatherCondition(
                    summary = "Partly Cloudy",
                    description = "Gentle southwesterly breeze with intermittent sunshine",
                    iconEmoji = "⛅"
                ),
                humidityPercent = 54,
                windMph = 8,
                windDirection = "SW",
                uvIndex = 5,
                uvDescription = "Moderate",
                aqiValue = 35,
                aqiDescription = "Good",
                barometricPressureInHg = 30.08,
                visibilityMiles = 10,
                dewPointF = 54,
                sunriseTime = "6:42 AM",
                sunsetTime = "7:28 PM",
                advisories = listOf(
                    WeatherAdvisory(
                        title = "Air Quality Bulletin: Good",
                        severity = "Advisory",
                        issuer = "Metro Civic Environmental Station",
                        details = "Ozone and particulate indices well within clean civic thresholds.",
                        issuedTimeAgo = "1h ago"
                    )
                ),
                hourlyForecasts = generateHourly(72, "⛅"),
                dailyForecasts = sampleDailyForecasts
            )
        }
    }

    private fun generateHourly(baseTemp: Int, baseEmoji: String): List<HourlyForecast> {
        val hours = listOf(
            "Now", "10 AM", "11 AM", "12 PM", "1 PM", "2 PM", "3 PM", "4 PM",
            "5 PM", "6 PM", "7 PM", "8 PM", "9 PM", "10 PM", "11 PM", "12 AM"
        )
        val tempOffsets = listOf(0, 1, 3, 5, 5, 4, 3, 2, 0, -2, -4, -6, -8, -9, -10, -11)
        val rainChances = listOf(10, 10, 15, 20, 25, 20, 15, 10, 5, 5, 10, 20, 30, 25, 15, 10)
        val icons = listOf(
            baseEmoji, "⛅", "🌤️", "☀️", "☀️", "🌤️", "⛅", "⛅",
            "🌤️", "🌅", "🌆", "🌙", "☁️", "☁️", "🌧️", "🌧️"
        )

        return hours.mapIndexed { idx, hour ->
            val temp = baseTemp + (tempOffsets.getOrNull(idx) ?: 0)
            HourlyForecast(
                hourLabel = hour,
                temperatureF = temp,
                temperatureC = ((temp - 32) * 5 / 9),
                conditionEmoji = icons.getOrNull(idx) ?: "⛅",
                precipitationChance = rainChances.getOrNull(idx) ?: 10,
                isCurrentHour = idx == 0
            )
        }
    }

    private val sampleDailyForecasts = listOf(
        DailyForecast("Today", "Sep 12", 77, 25, 61, 16, "⛅", "Partly Cloudy", 15),
        DailyForecast("Sun", "Sep 13", 78, 26, 62, 17, "☀️", "Sunny & Warm", 5),
        DailyForecast("Mon", "Sep 14", 74, 23, 59, 15, "🌤️", "Mostly Clear", 10),
        DailyForecast("Tue", "Sep 15", 71, 22, 57, 14, "🌧️", "Afternoon Showers", 65),
        DailyForecast("Wed", "Sep 16", 69, 21, 55, 13, "🌦️", "Scattered Rain", 45),
        DailyForecast("Thu", "Sep 17", 73, 23, 58, 14, "⛅", "Partly Sunny", 20),
        DailyForecast("Fri", "Sep 18", 76, 24, 60, 16, "☀️", "Clear Sky", 0)
    )
}
