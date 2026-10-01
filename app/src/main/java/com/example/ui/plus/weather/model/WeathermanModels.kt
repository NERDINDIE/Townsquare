package com.example.ui.plus.weather.model

import java.util.UUID

enum class HostPersona(
    val title: String,
    val subtitle: String,
    val catchphrase: String,
    val avatarEmoji: String,
    val speechPitch: Float,
    val speechRate: Float,
    val badgeColorHex: Long
) {
    VINTAGE_BROADCASTER(
        title = "Alistair Finch",
        subtitle = "1940s BBC & Maritime Radio Barometer Desk",
        catchphrase = "Fair winds and steady glass to all listeners across the bays.",
        avatarEmoji = "🎙️",
        speechPitch = 0.90f,
        speechRate = 0.95f,
        badgeColorHex = 0xFFFF9F1C // Warm Amber
    ),
    MELANCHOLIC_BARISTA(
        title = "Sora Vance",
        subtitle = "Coffee Brewer, Rain Watcher & Poet",
        catchphrase = "Order a double cortado today; the clouds are lingering until sunset.",
        avatarEmoji = "☕",
        speechPitch = 1.05f,
        speechRate = 0.90f,
        badgeColorHex = 0xFF38BDF8 // Soft Sky Blue
    ),
    HIGH_ENERGY_MET(
        title = "Brock 'Radar' Sterling",
        subtitle = "Fast Commuter Hype & Storm Warning Live",
        catchphrase = "Stash the umbrella in your backpack and hustle, team!",
        avatarEmoji = "⚡",
        speechPitch = 1.15f,
        speechRate = 1.15f,
        badgeColorHex = 0xFFFF0055 // High Voltage Ruby
    ),
    ZEN_NATURALIST(
        title = "Elena Wood",
        subtitle = "Flora, Petrichor & Barometric Mindfulness",
        catchphrase = "Take a deep breath; notice the damp scent of wet sycamore bark.",
        avatarEmoji = "🌿",
        speechPitch = 0.95f,
        speechRate = 0.85f,
        badgeColorHex = 0xFF70E000 // Forest Moss Green
    ),
    CYBERPUNK_METEOROLOGIST(
        title = "Unit-9 / Nexus Stream",
        subtitle = "Acid Rain Index, Fog Telemetry & Cyberpunk Sky",
        catchphrase = "Atmospheric interference nominal. Neon refractivity at 88 percent.",
        avatarEmoji = "🛸",
        speechPitch = 0.80f,
        speechRate = 1.05f,
        badgeColorHex = 0xFF00D2FF // Cyber Cyan
    )
}

data class WeatherPodcastEpisode(
    val id: String = UUID.randomUUID().toString(),
    val episodeNumber: Int,
    val title: String,
    val location: String,
    val dateString: String,
    val host: HostPersona,
    val temperature: String,
    val condition: String,
    val conditionEmoji: String,
    val windSpeed: String,
    val humidity: String,
    val barometer: String,
    val precipitationChance: String,
    val airQualityIndex: String,
    val umbrellaRating: String, // "0/5 - Leave it home", "3/5 - Keep in bag", "5/5 - Heavy trench coat & umbrella required"
    val attireAdvice: String,
    val audioScript: String,
    val durationSeconds: Int = 145,
    val isFavorite: Boolean = false
)

object WeathermanSeed {

    fun getInitialEpisodes(): List<WeatherPodcastEpisode> = listOf(
        WeatherPodcastEpisode(
            episodeNumber = 188,
            title = "The Maritime Morning Fog Lift & Autumn Golden Hour",
            location = "Townsquare Maritime Pier & Canal District",
            dateString = "Today • Morning Dispatch",
            host = HostPersona.VINTAGE_BROADCASTER,
            temperature = "14°C (57°F)",
            condition = "Overcast Clearing to Amber Sun",
            conditionEmoji = "🌤️",
            windSpeed = "9 knots NNE",
            humidity = "76%",
            barometer = "1016 hPa (Rising Steady)",
            precipitationChance = "15%",
            airQualityIndex = "AQI 22 • Pure Estuary Air",
            umbrellaRating = "1/5 - Leave at home",
            attireAdvice = "Light woolen overcoat, canvas tote, flat walking shoes for damp cobblestones.",
            audioScript = """Good morning, listeners of Townsquare and surrounding waterfront hamlets. This is Alistair Finch broadcasting from the Barometer Desk at Pier 14. 
            
At present, our mercury stands at fourteen degrees Celsius, under a veil of cool coastal fog rolling off the shipping canal. But take heart! The barometer has risen four hectopascals over the past three hours, and by eleven hundred hours, the cloud bank will break apart, giving way to glorious amber sunshine across the clocktower promenade. 

Winds are gentle from the north-northeast at nine knots, carrying the crisp scent of autumn pine from the upper ridges. Commuters cycling along the canal paths should expect dry asphalt by midday. Keep your spirits high, and fair winds to all listeners across the bays."""
        ),
        WeatherPodcastEpisode(
            episodeNumber = 187,
            title = "A Double Cortado Morning: Intermittent Drizzle & Gray Skies",
            location = "Old Quarter Canal Basin & Bookstores",
            dateString = "Yesterday • Afternoon Edition",
            host = HostPersona.MELANCHOLIC_BARISTA,
            temperature = "11°C (52°F)",
            condition = "Gentle Mist & Low Gray Ceiling",
            conditionEmoji = "🌧️",
            windSpeed = "6 km/h",
            humidity = "88%",
            barometer = "1008 hPa",
            precipitationChance = "65%",
            airQualityIndex = "AQI 18 • Crisp & Clean",
            umbrellaRating = "4/5 - Pack your compact umbrella",
            attireAdvice = "Cozy knit scarf, warm trench, insulated boots, and a favorite book.",
            audioScript = """Hey everyone, Sora here from the corner window at the Roastery. 

Outside, the rain is doing that gentle, misty tap against the leaded glass panes. The temperature is hovering right at eleven degrees, and honestly, the light today is pure poetry. You’re going to want a compact umbrella if you're walking between the print shops and the botanical glasshouse. 

My personal recommendation for today? Order a warm double cortado, find a velvet armchair by the radiator, and watch the yellow sycamore leaves float down the canal locks. Stay warm out there."""
        ),
        WeatherPodcastEpisode(
            episodeNumber = 186,
            title = "⚡ SQUALL LINE WARNING: Gusty Crosswinds & 20-Minute Downpour",
            location = "East Industrial Basin & Metro Terminal",
            dateString = "Two Days Ago",
            host = HostPersona.HIGH_ENERGY_MET,
            temperature = "17°C (63°F)",
            condition = "Approaching Cold Front Squall",
            conditionEmoji = "⛈️",
            windSpeed = "35 km/h with 50 km/h gusts",
            humidity = "82%",
            barometer = "998 hPa (Falling Fast)",
            precipitationChance = "90%",
            airQualityIndex = "AQI 15 • Washed by Rain",
            umbrellaRating = "5/5 - Heavy waterproof jacket recommended",
            attireAdvice = "Full waterproof windbreaker, secure backpack rain cover, avoid flimsy umbrellas.",
            audioScript = """Attention commuters and outdoor crews, this is Brock Sterling with your emergency radar flash! 

We've got a sharp cold front barreling across the western gap right now. Winds are clocking thirty-five kilometers per hour with gusts up to fifty. Expect a sudden fifteen-minute deluge between four-fifteen and four-thirty PM. Do NOT rely on cheap wire umbrellas; they will invert instantly in these crosswinds! Duck into the tram pavilions or grab shelter under the railway arches until the front passes. 

Once this cell blows through, temperatures will drop five degrees in ten minutes flat. Stay safe, stay dry, and hustle!"""
        ),
        WeatherPodcastEpisode(
            episodeNumber = 185,
            title = "Petrichor & Birdsong: The Forest Ridge Microclimate",
            location = "Botanical Reserve & Mountain Foothills",
            dateString = "Three Days Ago",
            host = HostPersona.ZEN_NATURALIST,
            temperature = "16°C (61°F)",
            condition = "Post-Rain Sunbeams & Wildflower Bloom",
            conditionEmoji = "🌿",
            windSpeed = "4 km/h Calm",
            humidity = "70%",
            barometer = "1014 hPa",
            precipitationChance = "10%",
            airQualityIndex = "AQI 10 • Mountain Pine Purity",
            umbrellaRating = "0/5 - Blue skies ahead",
            attireAdvice = "Breathable linen shirt, trail shoes, sunglasses.",
            audioScript = """Welcome, mindful wanderers. This is Elena Wood. 

Take a deep breath in through your nose right now. That rich, intoxicating aroma rising from the garden beds is geosmin and petrichor, the earth’s natural perfume following last night's gentle shower. 

At sixteen degrees under soft dappled sunbeams, the forest ridge trail is in peak condition today. The cedar trees are weeping clear droplets into the moss, and the migrating swallows are diving low over the water meadow. It is a perfect afternoon for a quiet walking meditation. Be present in this moment."""
        )
    )

    fun generatePersonalizedEpisode(
        city: String,
        persona: HostPersona,
        commuteMode: String,
        outdoorActivity: String
    ): WeatherPodcastEpisode {
        val tempC = (12..22).random()
        val tempF = (tempC * 9 / 5) + 32
        val wind = (5..25).random()
        val humidity = (55..85).random()
        val aqi = (12..35).random()

        val script = when (persona) {
            HostPersona.VINTAGE_BROADCASTER -> """Good day to our valued listeners in $city. This is ${persona.title} with your bespoke Weatherman dispatch. 

Barometric readings across $city indicate an ambient temperature of $tempC degrees Celsius, fifty-seven percent relative humidity, and steady surface breezes at $wind kilometers per hour. 

For those of you preparing for your daily journey via $commuteMode, the atmosphere will remain remarkably cooperative. If your itinerary includes $outdoorActivity, our meteorological charts advise a light overcoat and steady footwear. ${persona.catchphrase}"""

            HostPersona.MELANCHOLIC_BARISTA -> """Hey from the coffee counter in $city. This is ${persona.title}. 

The sky today has that gentle pastel hue, hovering at $tempC degrees Celsius. It's the kind of day that asks you to slow down. If you're heading out by $commuteMode, take the quiet scenic backstreets. 

And if you're planning on $outdoorActivity today, make sure to bring along a warm thermos of tea or black coffee. The breeze is blowing at $wind kilometers per hour, just enough to flutter the leaves on the pavement. ${persona.catchphrase}"""

            HostPersona.HIGH_ENERGY_MET -> """WHAT'S UP $city! This is ${persona.title} with your custom high-speed weather power briefing! 

We are looking at $tempC degrees Celsius right now across the district! Winds are pushing at $wind kph, keep your hat pinned down! 

If you're commuting by $commuteMode today, the transit lines are green and rolling smooth! Planning on $outdoorActivity? Grab your gear, get your energy up, and seize the window! ${persona.catchphrase}"""

            HostPersona.ZEN_NATURALIST -> """Hello, gentle travelers in $city. ${persona.title} here, bringing you today's atmospheric breath. 

Feel the cool, clean air at $tempC degrees Celsius. Humidity is resting peacefully at $humidity percent, and our air quality index is a pristine $aqi. 

As you navigate the world via $commuteMode, observe how the light catches the greenery around you. For your plans of $outdoorActivity, the rhythm of nature welcomes you with open arms. ${persona.catchphrase}"""

            HostPersona.CYBERPUNK_METEOROLOGIST -> """System online. Nexus broadcast channel 104 transmitting to citizen terminal in $city. 

Ambient thermal scan: $tempC degrees Celsius. Wind vector steady at $wind kph. Atmospheric particulates minimal with air quality reading at $aqi. 

Commute protocol: $commuteMode authorized with zero weather delays detected. Projected window for $outdoorActivity remains optimal through cycle nineteen hundred hours. ${persona.catchphrase}"""
        }

        return WeatherPodcastEpisode(
            episodeNumber = (200..999).random(),
            title = "Personalized Dispatch for $city",
            location = "$city District Hub",
            dateString = "Generated Just Now",
            host = persona,
            temperature = "$tempC°C (${tempF}°F)",
            condition = "Personalized Forecast",
            conditionEmoji = "🌤️",
            windSpeed = "$wind km/h",
            humidity = "$humidity%",
            barometer = "1015 hPa",
            precipitationChance = "20%",
            airQualityIndex = "AQI $aqi • Crisp & Clean",
            umbrellaRating = "2/5 - Good to keep handy",
            attireAdvice = "Comfortable layers suited for $commuteMode and $outdoorActivity.",
            audioScript = script,
            durationSeconds = 120
        )
    }
}
