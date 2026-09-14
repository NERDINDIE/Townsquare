package com.example.data.model

data class TeletextLine(
    val text: String,
    val textColor: Long = 0xFFFFFFFF,
    val isDoubleHeight: Boolean = false,
    val isFlashing: Boolean = false,
    val isHeader: Boolean = false
)

data class TeletextPage(
    val pageNumber: Int,
    val title: String,
    val category: String,
    val fasttextRed: Int = 101,
    val fasttextGreen: Int = 102,
    val fasttextYellow: Int = 300,
    val fasttextCyan: Int = 200,
    val redLabel: String = "NEWS",
    val greenLabel: String = "WEATHER",
    val yellowLabel: String = "TRANSIT",
    val cyanLabel: String = "RADIO",
    val lines: List<TeletextLine>
)

object TeletextAirwaveRepository {

    const val White = 0xFFFFFFFFL
    const val Yellow = 0xFFFFFF00L
    const val Cyan = 0xFF00FFFFL
    const val Green = 0xFF00FF00L
    const val Magenta = 0xFFFF00FFL
    const val Red = 0xFFFF0000L
    const val Blue = 0xFF0000FFL
    const val DarkBg = 0xFF000000L

    val teletextColors = this

    val pages = mapOf(
        100 to TeletextPage(
            pageNumber = 100,
            title = "CEEFAX AIRWAVE INDEX",
            category = "INDEX",
            redLabel = "HEADLINES",
            greenLabel = "WEATHER",
            yellowLabel = "TRANSIT",
            cyanLabel = "AIRWAVES",
            lines = listOf(
                TeletextLine("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", teletextColors.Yellow, isHeader = true),
                TeletextLine("   TOWNSQUARE CEEFAX  •  AIRWAVE-VBI   ", teletextColors.Yellow, isDoubleHeight = true),
                TeletextLine("   SATELLITE & SUBCARRIER CIVIC DATA   ", teletextColors.Cyan),
                TeletextLine("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", teletextColors.Yellow),
                TeletextLine("  CIVIC AIRWAVE TRANSMISSION ACTIVE     ", teletextColors.Green, isFlashing = true),
                TeletextLine("  CARRIER: 142.85 MHz RDS • SAT: L-BAND ", teletextColors.White),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("  101  BREAKING CIVIC HEADLINES         ", teletextColors.White),
                TeletextLine("  102  WEATHER RADAR & CIVIL DEFENSE    ", teletextColors.Cyan),
                TeletextLine("  200  RADIO FREQUENCIES & LIVE WAVES   ", teletextColors.Yellow),
                TeletextLine("  250  HARBOR TIDES & MARITIME BEACON   ", teletextColors.Green),
                TeletextLine("  300  MUNICIPAL TRAM & FERRY TIMES     ", teletextColors.White),
                TeletextLine("  400  COMMODITY GRAIN & CLEAN VOLTAGE  ", teletextColors.Magenta),
                TeletextLine("  450  PARTNER PUBLICATIONS DIRECTORY   ", teletextColors.Cyan),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("----------------------------------------", teletextColors.Green),
                TeletextLine("  OFFLINE AUTONOMOUS AIRWAVE DECODER    ", teletextColors.Yellow),
                TeletextLine("  DATA PACKETS REFRESHED VIA SUBCARRIER ", teletextColors.White),
                TeletextLine("  DIAL 3-DIGIT NUMBER OR USE FASTTEXT   ", teletextColors.Cyan)
            )
        ),
        101 to TeletextPage(
            pageNumber = 101,
            title = "BREAKING CIVIC NEWS",
            category = "NEWS",
            lines = listOf(
                TeletextLine("P101  TOWNSQUARE NEWS  AIRWAVE-WIRE  1/2", teletextColors.Cyan, isHeader = true),
                TeletextLine("========================================", teletextColors.Cyan),
                TeletextLine("SOLAR TRAMWAY LAUNCHES WATERFRONT RUN", teletextColors.Yellow, isDoubleHeight = true),
                TeletextLine("08:12 CIVIC HALL -----------------------", teletextColors.Green),
                TeletextLine("Quiet catenary-free trams commenced live", teletextColors.White),
                TeletextLine("passenger operations this morning along ", teletextColors.White),
                TeletextLine("the 4.8-mile heritage wharf loop.       ", teletextColors.White),
                TeletextLine("Fare-free week sponsored by clean energy", teletextColors.White),
                TeletextLine("municipal infrastructure bonds.         ", teletextColors.White),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("FARM ALLIANCE EXPANDS GRAIN COOPERATIVE ", teletextColors.Yellow),
                TeletextLine("07:45 COMMERCE DESK --------------------", teletextColors.Green),
                TeletextLine("Over 60 independent regional farms sign ", teletextColors.White),
                TeletextLine("perpetual civic distribution compact.   ", teletextColors.White),
                TeletextLine("Grain silo reserves reported at +14%    ", teletextColors.White),
                TeletextLine("relative to five-year historical norm.  ", teletextColors.White),
                TeletextLine(" ", teletextColors.White),
                TeletextLine(">> NEXT NEWS: PRESS 102 WEATHER OR RED  ", teletextColors.Magenta)
            )
        ),
        102 to TeletextPage(
            pageNumber = 102,
            title = "WEATHER & CIVIL DEFENSE",
            category = "WEATHER",
            lines = listOf(
                TeletextLine("P102  CIVIC WEATHER & CIVIL DEFENSE WIRE", teletextColors.Green, isHeader = true),
                TeletextLine("========================================", teletextColors.Green),
                TeletextLine("CURRENT SATELLITE RADAR STATUS: NOMINAL ", teletextColors.Cyan),
                TeletextLine("BAROMETER: 30.12 INHG  •  TREND: RISING ", teletextColors.Yellow),
                TeletextLine("TEMPERATURE: 64°F (18°C) • HUMIDITY: 62%", teletextColors.White),
                TeletextLine("WIND: WEST-NORTHWEST 10 KNOTS (GALE 0)  ", teletextColors.White),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("CIVIL DEFENSE ADVISORY -----------------", teletextColors.Red, isDoubleHeight = true),
                TeletextLine("ALL AIRWAVE REPEATERS OPERATIONAL.      ", teletextColors.White),
                TeletextLine("WATER PURITY RATING: GRADE 1 (SUPERIOR) ", teletextColors.Green),
                TeletextLine("NO SEVERE STORM WATCHES IN 50-MILE ZONE ", teletextColors.Yellow),
                TeletextLine("AIR QUALITY INDEX: 16 (EXCELLENT)       ", teletextColors.Cyan),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("MARINE SUMMARY: CALM SWELLS 1-2 FEET.   ", teletextColors.White),
                TeletextLine("SUNRISE: 06:14 HRS  •  SUNSET: 19:42 HRS", teletextColors.Yellow),
                TeletextLine("========================================", teletextColors.Green)
            )
        ),
        200 to TeletextPage(
            pageNumber = 200,
            title = "AIRWAVE FREQUENCIES & RADIO",
            category = "AIRWAVES",
            lines = listOf(
                TeletextLine("P200  AIRWAVE BROADCAST DIRECTORY       ", teletextColors.Yellow, isHeader = true),
                TeletextLine("========================================", teletextColors.Yellow),
                TeletextLine("LOCAL FREQUENCIES & SUBCARRIER BEACONS  ", teletextColors.White),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("98.5 MHz FM   K-PULSE LO-FI & DOWNTOWN  ", teletextColors.Cyan),
                TeletextLine("              STATUS: LIVE ON-AIR 24/7  ", teletextColors.Green),
                TeletextLine("104.2 MHz FM  CIVIC TALK & CITY COUNCIL ", teletextColors.Cyan),
                TeletextLine("              STATUS: SATELLITE MONITORED", teletextColors.Green),
                TeletextLine("89.1 MHz FM   CLASSICAL & SYMPHONY HALL ", teletextColors.Cyan),
                TeletextLine("              STATUS: STEREO RDS PILOT  ", teletextColors.Green),
                TeletextLine("92.7 MHz FM   WEST END VINYL JAZZ NOOK  ", teletextColors.Cyan),
                TeletextLine("              STATUS: LIVE TRANSMISSION ", teletextColors.Green),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("EMERGENCY SHORTWAVE CHANNELS -----------", teletextColors.Red),
                TeletextLine("7.240 MHz AM  TOWNSQUARE CIVIL RELAY    ", teletextColors.Yellow),
                TeletextLine("142.85 MHz FM TOWNSQUARE CEEFAX SUBCARR ", teletextColors.Magenta)
            )
        ),
        300 to TeletextPage(
            pageNumber = 300,
            title = "MUNICIPAL TRANSIT & FERRY",
            category = "TRANSIT",
            lines = listOf(
                TeletextLine("P300  CIVIC TRANSIT & FERRY SCHEDULE    ", teletextColors.Cyan, isHeader = true),
                TeletextLine("========================================", teletextColors.Cyan),
                TeletextLine("WATERFRONT TRAM LOOP -------------------", teletextColors.Yellow),
                TeletextLine("CAR 04: FOUNDRY SQUARE      IN 2 MIN    ", teletextColors.White),
                TeletextLine("CAR 07: MARKET & BROAD ST   IN 6 MIN    ", teletextColors.White),
                TeletextLine("CAR 12: OLD HARBOR PIER     IN 11 MIN   ", teletextColors.White),
                TeletextLine("ALL TRAM RUNS ON TIME • 0 MIN DELAYS    ", teletextColors.Green),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("HARBOR FERRY DEPARTURES ----------------", teletextColors.Cyan),
                TeletextLine("08:30 ISLAND LIGHTHOUSE     BOARDING    ", teletextColors.Green),
                TeletextLine("09:00 NORTH HEADLANDS       ON SCHEDULE ", teletextColors.White),
                TeletextLine("09:30 EAST HARBOR LOCKS     ON SCHEDULE ", teletextColors.White),
                TeletextLine("VESSEL 'M.V. CIVIC HERALD' CERTIFIED OK ", teletextColors.Yellow),
                TeletextLine("========================================", teletextColors.Cyan)
            )
        ),
        400 to TeletextPage(
            pageNumber = 400,
            title = "COMMODITY & FREIGHT TICKER",
            category = "COMMODITY",
            lines = listOf(
                TeletextLine("P400  COMMODITY GRAIN & CLEAN VOLTAGE   ", teletextColors.Magenta, isHeader = true),
                TeletextLine("========================================", teletextColors.Magenta),
                TeletextLine("REGIONAL EXCHANGE TICKER (AIRWAVE SYNC) ", teletextColors.White),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("WINTER SPELT WHEAT  $6.80/BU   +0.12 ▲ ", teletextColors.Green),
                TeletextLine("ORGANIC OATS        $4.15/BU   +0.04 ▲ ", teletextColors.Green),
                TeletextLine("HEIRLOOM BARLEY     $5.40/BU   -0.02 ▼ ", teletextColors.Red),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("CIVIC CLEAN VOLTAGE --------------------", teletextColors.Yellow),
                TeletextLine("SOLAR SURPLUS RATE: 3.2¢/kWh (CIVIC CR) ", teletextColors.Cyan),
                TeletextLine("WIND BATTERY LEVEL: 98.4% (GRID STABLE) ", teletextColors.Green),
                TeletextLine("PORT DOCK CAPACITY: 82% BERTHS NOMINAL  ", teletextColors.White),
                TeletextLine("FREIGHT TONNAGE:    14,200 MT THIS WEEK ", teletextColors.White)
            )
        ),
        450 to TeletextPage(
            pageNumber = 450,
            title = "PARTNER PRESSES DIRECTORY",
            category = "PARTNERS",
            lines = listOf(
                TeletextLine("P450  TOWNSQUARE SYNDICATE PARTNERS     ", teletextColors.Cyan, isHeader = true),
                TeletextLine("========================================", teletextColors.Cyan),
                TeletextLine("INDEPENDENT EDITORIAL BROADCAST DESKS   ", teletextColors.Yellow),
                TeletextLine(" ", teletextColors.White),
                TeletextLine("1. THE HARBOR & MARITIME GAZETTE (EST 1894)", teletextColors.White),
                TeletextLine("   DAILY BROADSHEET • TIDE & CARGO WIRE ", teletextColors.Green),
                TeletextLine("2. METROPOLITAN CIVIC HERALD            ", teletextColors.White),
                TeletextLine("   INVESTIGATIVE COUNCIL DESK & AUDITS  ", teletextColors.Cyan),
                TeletextLine("3. THE CHRONICLE OF ECOLOGY & SCIENCE   ", teletextColors.White),
                TeletextLine("   RIVER WATERSHED & SOIL TELEMETRY     ", teletextColors.Green),
                TeletextLine("4. WEST END LITERARY REVIEW             ", teletextColors.White),
                TeletextLine("   RISOGRAPH ESSAYS & POETRY DISPATCHES ", teletextColors.Magenta),
                TeletextLine("5. CIVIC WIRE FM RADIO (98.5 MHz)       ", teletextColors.White),
                TeletextLine("   FREE-TO-AIR SUBCARRIER COOPERATIVE   ", teletextColors.Yellow),
                TeletextLine("========================================", teletextColors.Cyan)
            )
        )
    )
}
