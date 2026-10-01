package com.example.ui.plus.townsquares.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class TransitMode(val label: String, val emoji: String) {
    METRO("Subway / Metro", "🚇"),
    TRAM("Tram / Streetcar", "🚊"),
    COMMUTER_RAIL("Commuter Rail", "🚆"),
    FERRY("Water Bus / Ferry", "⛴️"),
    MONORAIL("Monorail / SkyTrain", "🚝"),
    BUS_RAPID("Bus Rapid Transit", "🚌")
}

data class WorldCity(
    val id: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val timezone: String,
    val localTime: String,
    val currentTemp: String,
    val weatherCondition: String,
    val weatherEmoji: String,
    val accentColorHex: Long,
    val skylineVibe: String,
    val popularDistricts: List<String>,
    val activePostsCount: Int
)

data class CityExperiencePost(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val authorHandle: String,
    val authorAvatarEmoji: String,
    val cityId: String,
    val cityName: String,
    val country: String,
    val district: String,
    val title: String,
    val storyText: String,
    val travelerTip: String,
    val bestTimeOfDay: String, // "Dawn", "Afternoon Gold", "Twilight", "Midnight"
    val categoryTags: List<String>,
    var likesCount: Int = 0,
    var commentsCount: Int = 0,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false,
    val timestamp: String = "2 hours ago",
    val highlightEmoji: String = "✨"
)

data class CityTransitRoute(
    val id: String,
    val cityId: String,
    val cityName: String,
    val lineName: String,
    val lineCode: String,
    val mode: TransitMode,
    val colorHex: Long,
    val startStation: String,
    val endStation: String,
    val keyStops: List<String>,
    val frequencyMinutes: String,
    val travelTimeMinutes: String,
    val scenicRating: String, // "⭐⭐⭐⭐⭐"
    val insiderTip: String,
    val transfersAvailable: List<String>
)

data class PostComment(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val authorHandle: String,
    val avatarEmoji: String,
    val text: String,
    val timestamp: String = "Just now"
)

object TownsquaresSeed {

    fun getInitialCities(): List<WorldCity> = listOf(
        WorldCity(
            id = "tokyo",
            name = "Tokyo",
            country = "Japan",
            flagEmoji = "🇯🇵",
            timezone = "JST (UTC+9)",
            localTime = "14:45",
            currentTemp = "21°C",
            weatherCondition = "Clear Autumn Sky",
            weatherEmoji = "☀️",
            accentColorHex = 0xFFFF0055,
            skylineVibe = "Neon alleyways, quiet cedar shrines & precision rail",
            popularDistricts = listOf("Shibuya", "Shinjuku", "Kichijoji", "Yanaka", "Shimokitazawa", "Akihabara"),
            activePostsCount = 842
        ),
        WorldCity(
            id = "paris",
            name = "Paris",
            country = "France",
            flagEmoji = "🇫🇷",
            timezone = "CET (UTC+1)",
            localTime = "07:45",
            currentTemp = "14°C",
            weatherCondition = "Gentle Morning Mist",
            weatherEmoji = "🌫️",
            accentColorHex = 0xFF3A86FF,
            skylineVibe = "Haussmannian boulevards, river quays & zinc rooftops",
            popularDistricts = listOf("Montmartre", "Le Marais", "Belleville", "Latin Quarter", "Canal Saint-Martin"),
            activePostsCount = 675
        ),
        WorldCity(
            id = "new_york",
            name = "New York City",
            country = "United States",
            flagEmoji = "🇺🇸",
            timezone = "EDT (UTC-4)",
            localTime = "01:45",
            currentTemp = "18°C",
            weatherCondition = "Starry Night",
            weatherEmoji = "🌙",
            accentColorHex = 0xFFFF9F1C,
            skylineVibe = "Art deco towers, steam vents & brownstone stoops",
            popularDistricts = listOf("Lower East Side", "Greenwich Village", "Williamsburg", "Harlem", "DUMBO"),
            activePostsCount = 920
        ),
        WorldCity(
            id = "london",
            name = "London",
            country = "United Kingdom",
            flagEmoji = "🇬🇧",
            timezone = "BST (UTC+1)",
            localTime = "06:45",
            currentTemp = "12°C",
            weatherCondition = "Light Drizzle",
            weatherEmoji = "🌧️",
            accentColorHex = 0xFFE71D36,
            skylineVibe = "Brick mews, Thames fog, historic pubs & royal parks",
            popularDistricts = listOf("Soho", "Shoreditch", "Notting Hill", "South Bank", "Camden", "Greenwich"),
            activePostsCount = 740
        ),
        WorldCity(
            id = "berlin",
            name = "Berlin",
            country = "Germany",
            flagEmoji = "🇩🇪",
            timezone = "CEST (UTC+2)",
            localTime = "07:45",
            currentTemp = "15°C",
            weatherCondition = "Breezy & Crisp",
            weatherEmoji = "🍃",
            accentColorHex = 0xFF70E000,
            skylineVibe = "Concrete brutalism, courtyards, club culture & open canals",
            popularDistricts = listOf("Kreuzberg", "Neukölln", "Prenzlauer Berg", "Friedrichshain", "Mitte"),
            activePostsCount = 510
        ),
        WorldCity(
            id = "kyoto",
            name = "Kyoto",
            country = "Japan",
            flagEmoji = "🇯🇵",
            timezone = "JST (UTC+9)",
            localTime = "14:45",
            currentTemp = "20°C",
            weatherCondition = "Autumn Maple Breeze",
            weatherEmoji = "🍁",
            accentColorHex = 0xFF9E0059,
            skylineVibe = "Bamboo groves, moss temple gardens & wooden machiya",
            popularDistricts = listOf("Gion", "Arashiyama", "Higashiyama", "Pontocho", "Kamigamo"),
            activePostsCount = 430
        ),
        WorldCity(
            id = "buenos_aires",
            name = "Buenos Aires",
            country = "Argentina",
            flagEmoji = "🇦🇷",
            timezone = "ART (UTC-3)",
            localTime = "02:45",
            currentTemp = "22°C",
            weatherCondition = "Mild Spring Breeze",
            weatherEmoji = "🌸",
            accentColorHex = 0xFF00D2FF,
            skylineVibe = "Tango courtyards, jacaranda blooms & historic cafe tertulias",
            popularDistricts = listOf("San Telmo", "Palermo Soho", "Recoleta", "La Boca", "Colegiales"),
            activePostsCount = 380
        )
    )

    fun getInitialPosts(): List<CityExperiencePost> = listOf(
        CityExperiencePost(
            id = "post_tokyo_1",
            authorName = "Kenji Sato",
            authorHandle = "@kenji_walks",
            authorAvatarEmoji = "🏮",
            cityId = "tokyo",
            cityName = "Tokyo",
            country = "Japan",
            district = "Yanaka Ginza",
            title = "The Cat Alleyways & Slow Timber Cafes of Old Shitai",
            storyText = "Escaped the chaos of Yamanote rush hour by getting lost in Yanaka. Found a 70-year-old soba shop tucked behind a weeping cedar tree where the master still hand-rolls buckwheat noodles every morning at dawn. Cats slumber on stone lanterns and bicycles with wicker baskets glide past without a sound.",
            travelerTip = "Visit around 4:30 PM just as the lantern lamps begin to glow along the staircase known as Yuyake Dandan (Sunset Stairs).",
            bestTimeOfDay = "Twilight",
            categoryTags = listOf("#SlowTravel", "#OldTokyo", "#HiddenGem", "#SobaCraft"),
            likesCount = 284,
            commentsCount = 32,
            isLiked = true,
            timestamp = "1 hour ago",
            highlightEmoji = "🍜"
        ),
        CityExperiencePost(
            id = "post_paris_1",
            authorName = "Camille Laurent",
            authorHandle = "@camille_flaneur",
            authorAvatarEmoji = "🥐",
            cityId = "paris",
            cityName = "Paris",
            country = "France",
            district = "Canal Saint-Martin",
            title = "Early Baguette & Iron Footbridges in the Autumn Mist",
            storyText = "There is nothing quite like watching the canal locks swing open at 7:15 AM while holding a steaming espresso from Ten Belles. The iron arched footbridges reflect in the green water, and university students sit dangling their legs over the granite quay with sketchpads.",
            travelerTip = "Pick up pain au chocolat from Du Pain et des Idées on Rue Yves Toudic; their pistachio escargot is the best in all of Paris.",
            bestTimeOfDay = "Dawn",
            categoryTags = listOf("#CanalVibes", "#Boulangerie", "#Flâneur", "#MorningCoffee"),
            likesCount = 319,
            commentsCount = 41,
            isLiked = false,
            timestamp = "3 hours ago",
            highlightEmoji = "🥖"
        ),
        CityExperiencePost(
            id = "post_nyc_1",
            authorName = "Marcus Brody",
            authorHandle = "@brody_transit",
            authorAvatarEmoji = "🎷",
            cityId = "new_york",
            cityName = "New York City",
            country = "United States",
            district = "Greenwich Village",
            title = "Subterranean Jazz Chords & Rain on 7th Avenue",
            storyText = "Walked down the steep stairs into the Village Vanguard around 11 PM. No phones allowed, red velvet banquettes, and an upright bass solo that made the entire subterranean room hold its breath. When you step back out onto the damp street, the subway steam rises into the streetlight.",
            travelerTip = "Arrive 30 minutes before door time for the midnight set to grab the front corner table right next to the piano.",
            bestTimeOfDay = "Midnight",
            categoryTags = listOf("#LiveJazz", "#NYCSubway", "#Nightlife", "#Iconic"),
            likesCount = 452,
            commentsCount = 59,
            isLiked = true,
            timestamp = "4 hours ago",
            highlightEmoji = "🎺"
        ),
        CityExperiencePost(
            id = "post_london_1",
            authorName = "Eleanor Vance",
            authorHandle = "@eleanor_books",
            authorAvatarEmoji = "📚",
            cityId = "london",
            cityName = "London",
            country = "United Kingdom",
            district = "South Bank & Waterloo",
            title = "Browsing Wet Broadsheets under the Bridge Arches",
            storyText = "The second-hand book market under Waterloo Bridge is open rain or shine. You stand sheltered under the damp brick arch, thumbing through 1930s cloth-bound novels while red double-deckers rumble overhead and the Thames ripples below. Found a signed poetry anthology for £4.",
            travelerTip = "Walk across the pedestrian bridge to Embankment afterwards for roasted hot chestnuts from the wooden cart.",
            bestTimeOfDay = "Afternoon Gold",
            categoryTags = listOf("#SecondHandBooks", "#ThamesWalk", "#RainyLondon", "#Antiques"),
            likesCount = 276,
            commentsCount = 28,
            isLiked = false,
            timestamp = "5 hours ago",
            highlightEmoji = "📖"
        ),
        CityExperiencePost(
            id = "post_berlin_1",
            authorName = "Lukas Weber",
            authorHandle = "@weber_berlin",
            authorAvatarEmoji = "🚲",
            cityId = "berlin",
            cityName = "Germany",
            country = "Germany",
            district = "Kreuzberg & Landwehrkanal",
            title = "Flea Market Record Crates & Turkish Spices along the Canal",
            storyText = "Saturday morning bicycle ride along the Paul-Lincke-Ufer. The weeping willows brush the canal water, Turkish bakeries are putting out warm sesame simit bread, and crate diggers are flipping through vintage Krautrock vinyl on folding tables.",
            travelerTip = "Rent a three-speed city bike; Berlin's wide canal cobblestone paths and bike lanes make riding effortless.",
            bestTimeOfDay = "Morning",
            categoryTags = listOf("#BicycleCity", "#CanalLife", "#VinylDigging", "#Kreuzberg"),
            likesCount = 195,
            commentsCount = 19,
            isLiked = true,
            timestamp = "6 hours ago",
            highlightEmoji = "🚲"
        ),
        CityExperiencePost(
            id = "post_kyoto_1",
            authorName = "Mei Ling",
            authorHandle = "@meiling_travels",
            authorAvatarEmoji = "🍵",
            cityId = "kyoto",
            cityName = "Kyoto",
            country = "Japan",
            district = "Philosopher's Path",
            title = "Moss, Stone Water Basins & Whisked Uji Matcha",
            storyText = "Walked the stone canal path before the tour groups arrived. The only sound was the bamboo water scoop (shishi-odoshi) clacking against the mossy rock. A tiny tea pavilion served hand-whisked dark green matcha with a seasonal chestnut wagashi sweet.",
            travelerTip = "Start from Ginkaku-ji at 7:30 AM and take the side stairs up into the mountain ridge for an uncrowded view of the entire basin.",
            bestTimeOfDay = "Dawn",
            categoryTags = listOf("#Matcha", "#ZenGarden", "#PhilosophersPath", "#KyotoAutumn"),
            likesCount = 388,
            commentsCount = 47,
            isLiked = false,
            timestamp = "8 hours ago",
            highlightEmoji = "🍵"
        )
    )

    fun getInitialTransitRoutes(): List<CityTransitRoute> = listOf(
        // Tokyo
        CityTransitRoute(
            id = "route_yamanote",
            cityId = "tokyo",
            cityName = "Tokyo",
            lineName = "JR Yamanote Loop Line",
            lineCode = "JY",
            mode = TransitMode.COMMUTER_RAIL,
            colorHex = 0xFF70E000, // Famous Yamanote Yellow-Green
            startStation = "Tokyo Station",
            endStation = "Loop (Circular 34.5 km)",
            keyStops = listOf("Tokyo", "Shimbashi", "Shibuya", "Shinjuku", "Ikebukuro", "Ueno", "Akihabara"),
            frequencyMinutes = "Every 2-3 mins",
            travelTimeMinutes = "60 mins full circle",
            scenicRating = "⭐⭐⭐⭐⭐",
            insiderTip = "Ride the front window car between Yurakucho and Shimbashi for stunning elevated track views through the Ginza brick viaducts.",
            transfersAvailable = listOf("Shinkansen", "Ginza Line", "Chuo Rapid", "Marunouchi Line")
        ),
        CityTransitRoute(
            id = "route_ginza",
            cityId = "tokyo",
            cityName = "Tokyo",
            lineName = "Tokyo Metro Ginza Line",
            lineCode = "G",
            mode = TransitMode.METRO,
            colorHex = 0xFFFF9F1C, // Bright Orange
            startStation = "Asakusa (Old Temple Gate)",
            endStation = "Shibuya (Scramble Crossing)",
            keyStops = listOf("Asakusa", "Ueno", "Nihombashi", "Ginza", "Omotesando", "Shibuya"),
            frequencyMinutes = "Every 3 mins",
            travelTimeMinutes = "31 mins",
            scenicRating = "⭐⭐⭐⭐",
            insiderTip = "Oldest subway line in Asia (1927). The retro yellow trains feature warm ambient interior teardrop lighting.",
            transfersAvailable = listOf("Yamanote Line", "Hanzomon Line", "Toei Asakusa Line")
        ),
        CityTransitRoute(
            id = "route_yurikamome",
            cityId = "tokyo",
            cityName = "Tokyo",
            lineName = "Yurikamome Automated Waterfront Line",
            lineCode = "U",
            mode = TransitMode.MONORAIL,
            colorHex = 0xFF00D2FF, // Azure Cyan
            startStation = "Shimbashi",
            endStation = "Toyosu (Fish Market)",
            keyStops = listOf("Shimbashi", "Shiodome", "Odaiba Seaside Park", "Daiba", "Toyosu"),
            frequencyMinutes = "Every 4 mins",
            travelTimeMinutes = "28 mins",
            scenicRating = "⭐⭐⭐⭐⭐",
            insiderTip = "Fully driverless automated monorail! Sit in the very front seat as the train climbs a 360-degree spiral loop over the Rainbow Bridge into Tokyo Bay.",
            transfersAvailable = listOf("Yamanote Line", "Yurakucho Line")
        ),

        // Paris
        CityTransitRoute(
            id = "route_metro6",
            cityId = "paris",
            cityName = "Paris",
            lineName = "Métro Line 6 (The Panoramic Viaduct)",
            lineCode = "M6",
            mode = TransitMode.METRO,
            colorHex = 0xFF2EC4B6, // Emerald Mint
            startStation = "Charles de Gaulle - Étoile",
            endStation = "Nation",
            keyStops = listOf("Étoile", "Passy", "Bir-Hakeim", "Montparnasse", "Place d'Italie", "Nation"),
            frequencyMinutes = "Every 3-4 mins",
            travelTimeMinutes = "32 mins",
            scenicRating = "⭐⭐⭐⭐⭐",
            insiderTip = "Crosses the Pont de Bir-Hakeim over the River Seine on an elevated steel viaduct. Unbeatable cinematic view of the Eiffel Tower!",
            transfersAvailable = listOf("Line 1", "RER C", "Line 4", "Line 14")
        ),
        CityTransitRoute(
            id = "route_metro1",
            cityId = "paris",
            cityName = "Paris",
            lineName = "Métro Line 1 (The Historical Axis)",
            lineCode = "M1",
            mode = TransitMode.METRO,
            colorHex = 0xFFFFD166, // Classic Warm Yellow
            startStation = "La Défense",
            endStation = "Château de Vincennes",
            keyStops = listOf("La Défense", "Champs-Élysées", "Concorde", "Louvre-Rivoli", "Bastille", "Nation"),
            frequencyMinutes = "Every 2 mins",
            travelTimeMinutes = "36 mins",
            scenicRating = "⭐⭐⭐⭐",
            insiderTip = "Completely automated rubber-tired transit that cuts straight through Paris from modern skyscrapers to medieval castle turrets.",
            transfersAvailable = listOf("Line 6", "Line 8", "RER A", "Line 14")
        ),

        // New York
        CityTransitRoute(
            id = "route_nyc7",
            cityId = "new_york",
            cityName = "New York City",
            lineName = "Subway 7 Train (The International Express)",
            lineCode = "(7)",
            mode = TransitMode.METRO,
            colorHex = 0xFF9D4EDD, // Vibrant Purple
            startStation = "34 St - Hudson Yards",
            endStation = "Flushing - Main St",
            keyStops = listOf("Hudson Yards", "Times Sq - 42 St", "Grand Central", "Queensboro Plaza", "Jackson Heights", "Flushing"),
            frequencyMinutes = "Every 3-5 mins",
            travelTimeMinutes = "38 mins",
            scenicRating = "⭐⭐⭐⭐⭐",
            insiderTip = "Exits the East River tube onto soaring elevated steel trestles across Queens. You pass through 30 immigrant culinary enclaves in 25 minutes.",
            transfersAvailable = listOf("Subway N/W", "Subway E/F/M/R", "Metro-North", "LIRR")
        ),
        CityTransitRoute(
            id = "route_nycl",
            cityId = "new_york",
            cityName = "New York City",
            lineName = "L Train (Crosstown & Brooklyn Express)",
            lineCode = "(L)",
            mode = TransitMode.METRO,
            colorHex = 0xFF94A3B8, // Sleek Platinum Grey
            startStation = "8 Av - 14 St (Manhattan)",
            endStation = "Canarsie - Rockaway Pkwy",
            keyStops = listOf("Union Sq - 14 St", "1 Av", "Bedford Av (Williamsburg)", "Lorimer St", "Myrtle-Wyckoff"),
            frequencyMinutes = "Every 4 mins",
            travelTimeMinutes = "30 mins",
            scenicRating = "⭐⭐⭐",
            insiderTip = "The heartbeat connecting Manhattan gallery districts to Brooklyn indie music venues. Clean CBTC communications-based train control.",
            transfersAvailable = listOf("Subway 4/5/6", "Subway N/Q/R/W", "Subway G")
        ),

        // London
        CityTransitRoute(
            id = "route_elizabeth",
            cityId = "london",
            cityName = "London",
            lineName = "Elizabeth Line (Crossrail)",
            lineCode = "EL",
            mode = TransitMode.COMMUTER_RAIL,
            colorHex = 0xFF7B2CBF, // Royal Purple
            startStation = "Paddington",
            endStation = "Abbey Wood / Shenfield",
            keyStops = listOf("Paddington", "Bond Street", "Tottenham Court Rd", "Farringdon", "Liverpool St", "Canary Wharf"),
            frequencyMinutes = "Every 2.5 mins",
            travelTimeMinutes = "22 mins across central",
            scenicRating = "⭐⭐⭐⭐",
            insiderTip = "Cathedral-scale stations with air-conditioned walk-through trains and high-speed silent running under central London.",
            transfersAvailable = listOf("Central Line", "Northern Line", "Thameslink", "DLR")
        ),

        // Berlin
        CityTransitRoute(
            id = "route_ringbahn",
            cityId = "berlin",
            cityName = "Berlin",
            lineName = "S-Bahn Ringbahn (S41 / S42)",
            lineCode = "S41",
            mode = TransitMode.COMMUTER_RAIL,
            colorHex = 0xFFFF5400, // S-Bahn Orange
            startStation = "Ostkreuz",
            endStation = "Circular Loop (37 km)",
            keyStops = listOf("Ostkreuz", "Südkreuz", "Westkreuz", "Gesundbrunnen", "Prenzlauer Allee"),
            frequencyMinutes = "Every 5 mins",
            travelTimeMinutes = "60 mins full orbit",
            scenicRating = "⭐⭐⭐⭐",
            insiderTip = "The circular heartbeat of Berlin. S41 runs clockwise, S42 counter-clockwise. Great for panoramic sunset views across industrial freight yards and brick canal bridges.",
            transfersAvailable = listOf("U-Bahn U8", "U-Bahn U2", "U-Bahn U7", "Regional Express")
        ),

        // Kyoto
        CityTransitRoute(
            id = "route_randen",
            cityId = "kyoto",
            cityName = "Kyoto",
            lineName = "Keifuku Randen Heritage Tram",
            lineCode = "B",
            mode = TransitMode.TRAM,
            colorHex = 0xFF0077B6, // Heritage Indigo
            startStation = "Shijo-Omiya",
            endStation = "Arashiyama (Bamboo Grove)",
            keyStops = listOf("Shijo-Omiya", "Nishi-Oji Sanjo", "Katabiranotsuji", "Ryoanji", "Arashiyama"),
            frequencyMinutes = "Every 10 mins",
            travelTimeMinutes = "24 mins",
            scenicRating = "⭐⭐⭐⭐⭐",
            insiderTip = "Kyoto's last remaining streetcar system dating back to 1910. The tracks pass directly through residential gardens and beneath weeping cherry blossom arches.",
            transfersAvailable = listOf("Hankyu Kyoto Line", "JR San-in Line")
        )
    )
}
