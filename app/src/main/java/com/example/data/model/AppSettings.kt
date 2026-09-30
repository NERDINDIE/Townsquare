package com.example.data.model

enum class AudioStreamingQuality(val label: String, val bitrate: String) {
    HIGH_FIDELITY("High-Fidelity", "320 kbps AAC-LC"),
    BALANCED("Balanced", "192 kbps AAC"),
    DATA_SAVER("Data Saver", "96 kbps Opus")
}

enum class ReadingFontSize(val label: String, val scaleMultiplier: Float) {
    COMPACT("Compact", 0.9f),
    STANDARD("Standard", 1.0f),
    LARGE("Large", 1.15f),
    READABILITY("Dyslexic / Readability", 1.25f)
}

enum class VoiceNarrationPreset(val label: String, val description: String) {
    NEWS_ANCHOR("News Anchor", "Crisp, authoritative editorial delivery"),
    LITERARY_MAGAZINE("Literary Magazine", "Introspective, warm acoustic cadence"),
    INVESTIGATIVE("Investigative Host", "Grounded, analytical documentary tone"),
    COMMUNITY_DISPATCH("Community Dispatch", "Conversational, approachable neighborhood voice")
}

data class AppSettings(
    val audioStreamingQuality: AudioStreamingQuality = AudioStreamingQuality.BALANCED,
    val readingFontSize: ReadingFontSize = ReadingFontSize.STANDARD,
    val dialHapticFeedback: Boolean = true,
    val autoTuneLastStation: Boolean = true,
    val autoCacheMorningEdition: Boolean = true,
    val breakingNewsAlerts: Boolean = true,
    val printKioskArrivalAlerts: Boolean = true,
    val defaultVoiceNarrationPreset: VoiceNarrationPreset = VoiceNarrationPreset.NEWS_ANCHOR,
    val voiceNarrationSpeed: Float = 1.0f,
    val localCommunityRadiusMiles: Int = 15,
    val morningDispatchTime: String = "07:00 AM",
    val highContrastDisplay: Boolean = false,
    val cachedMediaSizeBytes: Long = 18_420_000L,
    val enableAiFeatures: Boolean = true,
    val enableAiFactChecking: Boolean = true,
    val enableAiVoiceNarration: Boolean = true,
    val enableAiSmartSummaries: Boolean = true,
    val activeAppSkinId: String? = null,
    val activeWelcomeSkinId: String = "BROADSHEET",
    val overrideBaseAppInterface: Boolean = false,
    val enableRetroTerminalMode: Boolean = false,
    val enableKeitai3GOverlay: Boolean = false,
    val enableManuscriptParchmentTheme: Boolean = false,
    val enableMetroTilesView: Boolean = false,
    val enableGeekLiveTickerHeader: Boolean = false
)
