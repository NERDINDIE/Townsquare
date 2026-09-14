package com.example.data.model

data class FacsimileWireItem(
    val timeTag: String,
    val category: String,
    val content: String,
    val urgency: String = "ROUTINE"
)

data class FacsimileBroadsheet(
    val editionHour: Int,
    val editionCode: String,
    val timestampFormatted: String,
    val headline: String,
    val subheadline: String,
    val editorialNote: String,
    val barometer: String,
    val temperature: String,
    val carrierSignalKhz: String = "142.85 MHz / CW-FAX 120 RPM",
    val items: List<FacsimileWireItem>,
    val paperRollCondition: String = "ROLL #42 • 88% REMAINING • THERMAL 120 RPM",
    val isTornAndSaved: Boolean = false
)

object FacsimileBroadsheetRepository {

    fun getEditionForHour(hour: Int): FacsimileBroadsheet {
        val normalizedHour = hour.coerceIn(0, 23)
        return sampleHourlyEditions.getOrElse(normalizedHour % sampleHourlyEditions.size) {
            sampleHourlyEditions[0]
        }
    }

    val sampleHourlyEditions = listOf(
        FacsimileBroadsheet(
            editionHour = 8,
            editionCode = "FAX-0800-CIVIC-ALPHA",
            timestampFormatted = "08:00 HRS • SATURDAY MORNING WIRE",
            headline = "MUNICIPAL SOLAR TRAMWAY COMMENCES CIVIC RUNS ACROSS WATERFRONT DISTRICT",
            subheadline = "Zero-emission catenary-free cars connect arts corridor, municipal market & port docks on scheduled 6-minute loops.",
            editorialNote = "TRANSMITTED VIA HIGH-SPEED THERMAL WIRE TELEPRINTER • TOWNSQUARE CIVIC BUREAU",
            barometer = "30.12 INHG RISING",
            temperature = "62°F MILD BREEZE",
            items = listOf(
                FacsimileWireItem(
                    timeTag = "08:08",
                    category = "PORT DESK",
                    content = "Tide cresting at harbor gate; three deep-draft grain barges cleared for outer channel transit.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "08:24",
                    category = "CITY HALL",
                    content = "Public works committee tables final vote on protected bicycle express lanes for Old Town boulevard.",
                    urgency = "BULLETIN"
                ),
                FacsimileWireItem(
                    timeTag = "08:42",
                    category = "AIRWAVES",
                    content = "Shortwave relay station K-CIVIC testing AM carrier on 7.240 MHz; emergency packet broadcast verified nominal.",
                    urgency = "ROUTINE"
                )
            )
        ),
        FacsimileBroadsheet(
            editionHour = 9,
            editionCode = "FAX-0900-COMMERCE-BETA",
            timestampFormatted = "09:00 HRS • MID-MORNING DESK WIRE",
            headline = "REGIONAL FARMERS ALLIANCE COMMENCES COOPERATIVE GRAIN & PRODUCE AUCTION",
            subheadline = "Direct farm-to-neighborhood wholesale hub stabilizes seasonal market baskets and lowers distribution transport costs.",
            editorialNote = "PRINTHEAD CALIBRATION: OPTIMAL • DIRECT WIRE TRANSMISSION FREQ 142.85 MHZ",
            barometer = "30.14 INHG STEADY",
            temperature = "66°F CLEAR SUN",
            items = listOf(
                FacsimileWireItem(
                    timeTag = "09:15",
                    category = "MARKETS",
                    content = "Locally milled spelt & winter wheat trading briskly; regional reserves up 14% over prior season.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "09:31",
                    category = "CIVIC DEFENSE",
                    content = "Water treatment facility completes seasonal filtration valve upgrades ahead of weekend demand.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "09:50",
                    category = "LITERARY",
                    content = "Central Library opens historic maritime map archives for public digitization workshop at noon.",
                    urgency = "ROUTINE"
                )
            )
        ),
        FacsimileBroadsheet(
            editionHour = 10,
            editionCode = "FAX-1000-EDITION-GAMMA",
            timestampFormatted = "10:00 HRS • EDITORIAL BROADSHEET",
            headline = "COMMUNITY WIND MICRO-GRID SURPASSES 50 MEGAWATT PEAK GENERATION TARGET",
            subheadline = "Rooftop turbines and coastal headland arrays supply surplus clean voltage back into district hospital backup banks.",
            editorialNote = "OFFICIAL DISPATCH AUTHORIZED BY TOWNSQUARE CIVIC TELETYPE GUILD",
            barometer = "30.08 INHG STEADY",
            temperature = "69°F WESTERLY WIND 12KT",
            items = listOf(
                FacsimileWireItem(
                    timeTag = "10:12",
                    category = "ENERGY WIRE",
                    content = "Neighborhood battery banks reach 98% charge; smart meters automatically switch to civic storage mode.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "10:35",
                    category = "TRANSIT ALERT",
                    content = "Harbor Ferry Line confirms zero delays; additional boarding gates open at South Pier terminal.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "10:48",
                    category = "CIVIC ARTS",
                    content = "Foundry Square mural unveiling ceremony scheduled for 14:00 with live brass quartet broadcast.",
                    urgency = "ROUTINE"
                )
            )
        ),
        FacsimileBroadsheet(
            editionHour = 11,
            editionCode = "FAX-1100-PRESS-DELTA",
            timestampFormatted = "11:00 HRS • MIDDAY DESK DISPATCH",
            headline = "CIVIC HORTICULTURE GUILD RESTORES CENTURY-OLD GREENHOUSE BOTANICAL CORRIDOR",
            subheadline = "Community heirloom seed bank opens to urban gardeners; climate-adapted heritage varieties shared openly.",
            editorialNote = "TELEPRINTER SPEED: 100 WPM • PAPER TAPE CUT VERIFIED",
            barometer = "30.05 INHG STEADY",
            temperature = "72°F SUNNY",
            items = listOf(
                FacsimileWireItem(
                    timeTag = "11:10",
                    category = "CIVIC GREEN",
                    content = "Over 400 community plots seeded across three rooftop terraces overlooking the industrial canal.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "11:32",
                    category = "HEALTH DISPATCH",
                    content = "Air quality index certified at 18 (Exceptional) following complete phaseout of diesel fleet.",
                    urgency = "ROUTINE"
                ),
                FacsimileWireItem(
                    timeTag = "11:45",
                    category = "AIRWAVE RADAR",
                    content = "Marine VHF channel 16 broadcast testing scheduled at noon; all coastal radios advised to standby.",
                    urgency = "ROUTINE"
                )
            )
        )
    )
}
