package com.example.ui.components.player

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

/**
 * The 5 distinct operational modes for Townsquare's Global Media Player.
 */
enum class GlobalMediaMode(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val accentColor: Color
) {
    AUDIO(
        title = "Audio",
        iconEmoji = "🎵",
        description = "Music streaming, podcasts & device files",
        accentColor = NeonCyan
    ),
    VIDEO(
        title = "Video",
        iconEmoji = "🎬",
        description = "Device files & video platforms",
        accentColor = Color(0xFF8B5CF6) // Purple
    ),
    IMAGE(
        title = "Image",
        iconEmoji = "🖼️",
        description = "Image editing & viewing",
        accentColor = Color(0xFF10B981) // Emerald Green
    ),
    TV(
        title = "Live TV",
        iconEmoji = "📺",
        description = "Live TV & livestreams with chat",
        accentColor = Color(0xFFEF4444) // Bright Red
    ),
    RADIO(
        title = "Radio",
        iconEmoji = "📻",
        description = "Radio dials, saved stations, recording & subtitles",
        accentColor = WarmAmber
    ),
    WAVES(
        title = "Waves",
        iconEmoji = "🌊",
        description = "Townsquare Waves streaming & live song recognition",
        accentColor = Color(0xFF00D2FF)
    ),
    AUDIOBOOK(
        title = "Audiobook",
        iconEmoji = "📚",
        description = "Audiobooks with digital replica & translation",
        accentColor = Color(0xFFFFB300)
    )
}

/**
 * Data model for device audio files and custom music tracks.
 */
data class DeviceAudioTrack(
    val id: String,
    val fileName: String,
    val title: String,
    val artist: String,
    val album: String = "Device Internal Storage",
    val durationSeconds: Int = 210,
    val format: String = "MP3 • 320kbps",
    val fileSizeBytes: Long = 8400000L,
    val isDeviceStorage: Boolean = true
)

/**
 * Data model for Video mode items (device files & video platforms).
 */
data class VideoPlayerItem(
    val id: String,
    val title: String,
    val creatorName: String,
    val platformSource: VideoPlatformSource,
    val videoUrl: String,
    val durationSeconds: Int = 645,
    val resolutionBadge: String = "1080p FHD",
    val viewsCount: String = "18.4K views",
    val description: String = "",
    val aspectRatio: String = "16:9",
    val subtitleTracks: List<String> = listOf("English [CC]", "Spanish", "French", "None")
)

enum class VideoPlatformSource(val label: String, val badgeEmoji: String) {
    DEVICE_STORAGE("Device Storage", "💾"),
    TOWNSQUARE_TUBE("Townsquare Tube", "🌐"),
    YOUTUBE("YouTube Stream", "▶️"),
    VIMEO("Vimeo Video", "🎥"),
    CUSTOM_WEB("Web Stream URL", "🔗")
}

/**
 * Data model for Image viewing and editing mode.
 */
data class ImagePlayerItem(
    val id: String,
    val title: String,
    val photographer: String,
    val dateCaptured: String,
    val resolution: String = "3840 x 2160 (4K)",
    val location: String = "Townsquare Civic Plaza",
    val sourceName: String = "Townsquare Press Archive",
    val fileFormat: String = "JPEG • 6.2 MB",
    val cameraExif: String = "Sony A7 IV • 35mm f/1.8 • ISO 200 • 1/500s",
    val baseDrawableRes: Int? = null,
    val imageUrl: String = ""
)

/**
 * Creative image filters available in Image mode.
 */
enum class ImageFilterType(val label: String, val description: String) {
    ORIGINAL("Original", "Unfiltered natural photograph"),
    VINTAGE_FILM("Vintage 70s", "Warm nostalgic grain and sepia curves"),
    CYBERPUNK("Cyberpunk", "Neon cyan & magenta high-contrast saturation"),
    NOIR_BW("Noir B&W", "Deep shadows & silvery monochrome monochrome"),
    WARM_AMBER("Warm Glow", "Golden hour evening temperature"),
    VIVID_HDR("Vivid HDR", "Punchy clarity, enhanced dynamic range")
}

/**
 * Data model for Radio station presets and dial navigation.
 */
data class RadioPresetStation(
    val id: String,
    val callsign: String,
    val frequencyMhz: Float,
    val band: RadioBand = RadioBand.FM,
    val stationName: String,
    val genre: String,
    val city: String = "Townsquare Metro",
    val signalStrengthPercent: Int = 94,
    val isFavorite: Boolean = false
)

enum class RadioBand {
    FM, AM
}

/**
 * Data model for saved radio broadcast recordings.
 */
data class RadioRecordingSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val stationCallsign: String,
    val stationFrequency: String,
    val recordingTitle: String,
    val recordedEpochMs: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val fileSizeMb: Float,
    val audioFormat: String = "AAC Stereo • 192kbps"
)

/**
 * Real-time subtitle/transcription line for live radio broadcasts.
 */
data class RadioLiveSubtitle(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestampText: String,
    val speaker: String,
    val text: String
)
