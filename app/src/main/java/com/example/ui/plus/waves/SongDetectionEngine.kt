package com.example.ui.plus.waves

data class RecognizedSong(
    val id: String,
    val title: String,
    val artist: String,
    val timestampText: String, // e.g. "02:14" or "LIVE NOW"
    val albumArtEmoji: String,
    val genre: String,
    val bitrate: String = "320 kbps FLAC"
)

object SongDetectionEngine {
    fun getPodcastDetectedSongs(podcastId: String): List<RecognizedSong> {
        return listOf(
            RecognizedSong("s_1", "The Glow, Pt. 2", "The Microphones", "01:15", "🌲", "Indie Folk", "lossless"),
            RecognizedSong("s_2", "Midnight in a Perfect World", "DJ Shadow", "06:42", "🎧", "Trip Hop", "320 kbps"),
            RecognizedSong("s_3", "Avril 14th", "Aphex Twin", "14:20", "🎹", "Ambient", "FLAC"),
            RecognizedSong("s_4", "Trans-Europe Express", "Kraftwerk", "22:50", "🚆", "Electronic", "320 kbps")
        )
    }

    fun getRadioLiveDetectedSong(stationFrequency: String): RecognizedSong {
        return when (stationFrequency) {
            "88.5 MHz" -> RecognizedSong("r_1", "Blue Train", "John Coltrane Quintet", "LIVE ON AIR", "🎷", "Jazz & Blues", "FM Broadcast")
            "98.5 MHz" -> RecognizedSong("r_2", "Autobahn (Live Mix)", "Kraftwerk", "LIVE ON AIR", "⚡", "Electronic Wave", "FM Broadcast")
            "104.1 MHz" -> RecognizedSong("r_3", "Morning Coffee Serenade", "Harbor Philharmonic", "LIVE ON AIR", "🎻", "Classical & News", "FM Broadcast")
            else -> RecognizedSong("r_4", "Cityscape Nocturne #4", "The Metro Sound Guild", "LIVE ON AIR", "🌆", "Ambient Townscape", "FM Broadcast")
        }
    }
}
