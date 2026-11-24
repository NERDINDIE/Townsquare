package com.example.ui.plus.waves

import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType

data class RecognizedSong(
    val id: String,
    val title: String,
    val artist: String,
    val timestampText: String = "LIVE NOW", // e.g. "02:14" or "LIVE NOW"
    val albumArtEmoji: String = "🎵",
    val genre: String = "Airwave Music",
    val bitrate: String = "320 kbps FLAC",
    val lyricsSnippet: String = "Instrumental airwave broadcast"
) {
    fun toMediaItemEntity(): MediaItemEntity {
        return MediaItemEntity(
            id = (id.hashCode().toLong() and 0x7FFFFFFF),
            type = MediaType.SONG.name,
            title = title,
            subtitle = "$artist • $genre ($bitrate)",
            authorName = artist,
            authorHandle = "@${artist.lowercase().replace(" ", "").replace(".", "")}",
            channelId = "channel_sonic",
            channelName = "📻 Sonic Waveform & Airwaves",
            bodyText = "$title by $artist. Genre: $genre. Audio fidelity: $bitrate. Lyrics/Notes: $lyricsSnippet",
            mediaUrl = "https://audio.townsquare.local/songs/$id.mp3",
            durationSeconds = 210,
            tags = "#song #$genre #airwaves #lossless #sonic"
        )
    }
}

object SongDetectionEngine {
    private val allCatalog = listOf(
        RecognizedSong("s_1", "The Glow, Pt. 2", "The Microphones", "01:15", "🌲", "Indie Folk", "24-bit Lossless", "I took the glow and blew it out / My blood was in the sound"),
        RecognizedSong("s_2", "Midnight in a Perfect World", "DJ Shadow", "06:42", "🎧", "Trip Hop", "320 kbps", "Lost in the instrumental haze between vinyl scratches and midnight echoes"),
        RecognizedSong("s_3", "Avril 14th", "Aphex Twin", "14:20", "🎹", "Ambient Piano", "FLAC Lossless", "Gentle acoustic prepared piano reverberating across quiet rooms"),
        RecognizedSong("s_4", "Trans-Europe Express", "Kraftwerk", "22:50", "🚆", "Electronic Synth", "320 kbps", "From station to station / Back to Dusseldorf city / Meet Iggy Pop and David Bowie"),
        RecognizedSong("r_1", "Blue Train", "John Coltrane Quintet", "LIVE ON AIR", "🎷", "Jazz & Blues", "24-bit FLAC", "Hard bop blues standard featuring Coltrane tenor saxophone solo"),
        RecognizedSong("r_2", "Autobahn (Live Mix)", "Kraftwerk", "LIVE ON AIR", "⚡", "Electronic Wave", "FM Broadcast", "Wir fahr'n fahr'n fahr'n auf der Autobahn / Vor uns liegt ein weites Tal"),
        RecognizedSong("r_3", "Morning Coffee Serenade", "Harbor Philharmonic", "LIVE ON AIR", "🎻", "Classical Symphony", "FM Broadcast", "Lyrical violin melody accompanied by soft French horns"),
        RecognizedSong("r_4", "Cityscape Nocturne #4", "The Metro Sound Guild", "LIVE ON AIR", "🌆", "Ambient Townscape", "FM Broadcast", "Modular synth arpeggios blended with actual nighttime district field recordings"),
        RecognizedSong("s_5", "Neon Horizon Beats", "DJ Sora", "03:15", "🌇", "Synthwave", "320 kbps", "Chasing the neon skyline through analog chorus and 80s drum machines"),
        RecognizedSong("s_6", "Autumn Wind Sonata", "Maestro Julian Frost", "04:00", "🍂", "Acoustic Classical", "FLAC", "Melancholic piano chords layered with acoustic cello lines"),
        RecognizedSong("s_7", "Solfeggio 528Hz Harmonic", "Waves Sound Lab", "05:28", "✨", "Healing Ambient", "Hi-Res FLAC", "Harmonic frequency modulation designed for mindfulness and restoration"),
        RecognizedSong("s_8", "Cassette Tape Nostalgia", "Analog Revivalists", "02:48", "📼", "Lo-Fi Beats", "Tape Saturated", "Warm hiss, flutter and tape saturation over soulful Rhodes chords")
    )

    fun getAllCatalogSongs(): List<RecognizedSong> = allCatalog

    fun searchSongs(query: String): List<RecognizedSong> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return allCatalog
        return allCatalog.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.genre.lowercase().contains(q) ||
            it.lyricsSnippet.lowercase().contains(q) ||
            it.bitrate.lowercase().contains(q)
        }
    }

    fun getPodcastDetectedSongs(podcastId: String): List<RecognizedSong> {
        return allCatalog.take(4)
    }

    fun getRadioLiveDetectedSong(stationFrequency: String): RecognizedSong {
        return when (stationFrequency) {
            "88.5 MHz" -> allCatalog.find { it.id == "r_1" } ?: allCatalog[0]
            "98.5 MHz" -> allCatalog.find { it.id == "r_2" } ?: allCatalog[1]
            "104.1 MHz" -> allCatalog.find { it.id == "r_3" } ?: allCatalog[2]
            else -> allCatalog.find { it.id == "r_4" } ?: allCatalog[3]
        }
    }
}
