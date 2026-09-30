package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppSettings
import com.example.data.model.AudioStreamingQuality
import com.example.data.model.ReadingFontSize
import com.example.data.model.VoiceNarrationPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppSettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("townsquare_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    companion object {
        @Volatile
        private var instance: AppSettingsManager? = null

        fun getInstance(context: Context): AppSettingsManager {
            return instance ?: synchronized(this) {
                instance ?: AppSettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun loadSettings(): AppSettings {
        val audioQualityName = prefs.getString("audio_quality", AudioStreamingQuality.BALANCED.name)
            ?: AudioStreamingQuality.BALANCED.name
        val audioQuality = try {
            AudioStreamingQuality.valueOf(audioQualityName)
        } catch (_: Exception) {
            AudioStreamingQuality.BALANCED
        }

        val fontSizeName = prefs.getString("font_size", ReadingFontSize.STANDARD.name)
            ?: ReadingFontSize.STANDARD.name
        val fontSize = try {
            ReadingFontSize.valueOf(fontSizeName)
        } catch (_: Exception) {
            ReadingFontSize.STANDARD
        }

        val voicePresetName = prefs.getString("voice_preset", VoiceNarrationPreset.NEWS_ANCHOR.name)
            ?: VoiceNarrationPreset.NEWS_ANCHOR.name
        val voicePreset = try {
            VoiceNarrationPreset.valueOf(voicePresetName)
        } catch (_: Exception) {
            VoiceNarrationPreset.NEWS_ANCHOR
        }

        return AppSettings(
            audioStreamingQuality = audioQuality,
            readingFontSize = fontSize,
            dialHapticFeedback = prefs.getBoolean("dial_haptic", true),
            autoTuneLastStation = prefs.getBoolean("auto_tune_station", true),
            autoCacheMorningEdition = prefs.getBoolean("auto_cache_morning", true),
            breakingNewsAlerts = prefs.getBoolean("breaking_news_alerts", true),
            printKioskArrivalAlerts = prefs.getBoolean("print_kiosk_alerts", true),
            defaultVoiceNarrationPreset = voicePreset,
            voiceNarrationSpeed = prefs.getFloat("voice_speed", 1.0f),
            localCommunityRadiusMiles = prefs.getInt("community_radius", 15),
            morningDispatchTime = prefs.getString("morning_dispatch_time", "07:00 AM") ?: "07:00 AM",
            highContrastDisplay = prefs.getBoolean("high_contrast", false),
            cachedMediaSizeBytes = prefs.getLong("cache_size_bytes", 18_420_000L),
            enableAiFeatures = prefs.getBoolean("enable_ai_features", true),
            enableAiFactChecking = prefs.getBoolean("enable_ai_fact_checking", true),
            enableAiVoiceNarration = prefs.getBoolean("enable_ai_voice_narration", true),
            enableAiSmartSummaries = prefs.getBoolean("enable_ai_smart_summaries", true),
            activeAppSkinId = prefs.getString("active_app_skin_id", null),
            activeWelcomeSkinId = prefs.getString("active_welcome_skin_id", "BROADSHEET") ?: "BROADSHEET",
            overrideBaseAppInterface = prefs.getBoolean("override_base_app_interface", false),
            enableRetroTerminalMode = prefs.getBoolean("enable_retro_terminal_mode", false),
            enableKeitai3GOverlay = prefs.getBoolean("enable_keitai_3g_overlay", false),
            enableManuscriptParchmentTheme = prefs.getBoolean("enable_manuscript_parchment_theme", false),
            enableMetroTilesView = prefs.getBoolean("enable_metro_tiles_view", false),
            enableGeekLiveTickerHeader = prefs.getBoolean("enable_geek_live_ticker_header", false)
        )
    }

    fun setActiveAppSkinId(skinId: String?) {
        prefs.edit().putString("active_app_skin_id", skinId).apply()
        _settings.update { it.copy(activeAppSkinId = skinId) }
    }

    fun setActiveWelcomeSkinId(skinId: String) {
        prefs.edit().putString("active_welcome_skin_id", skinId).apply()
        _settings.update { it.copy(activeWelcomeSkinId = skinId) }
    }

    fun setOverrideBaseAppInterface(override: Boolean) {
        prefs.edit().putBoolean("override_base_app_interface", override).apply()
        _settings.update { it.copy(overrideBaseAppInterface = override) }
    }

    fun setEnableRetroTerminalMode(enabled: Boolean) {
        prefs.edit().putBoolean("enable_retro_terminal_mode", enabled).apply()
        _settings.update { it.copy(enableRetroTerminalMode = enabled) }
    }

    fun setEnableKeitai3GOverlay(enabled: Boolean) {
        prefs.edit().putBoolean("enable_keitai_3g_overlay", enabled).apply()
        _settings.update { it.copy(enableKeitai3GOverlay = enabled) }
    }

    fun setEnableManuscriptParchmentTheme(enabled: Boolean) {
        prefs.edit().putBoolean("enable_manuscript_parchment_theme", enabled).apply()
        _settings.update { it.copy(enableManuscriptParchmentTheme = enabled) }
    }

    fun setEnableMetroTilesView(enabled: Boolean) {
        prefs.edit().putBoolean("enable_metro_tiles_view", enabled).apply()
        _settings.update { it.copy(enableMetroTilesView = enabled) }
    }

    fun setEnableGeekLiveTickerHeader(enabled: Boolean) {
        prefs.edit().putBoolean("enable_geek_live_ticker_header", enabled).apply()
        _settings.update { it.copy(enableGeekLiveTickerHeader = enabled) }
    }

    fun setEnableAiFeatures(enabled: Boolean) {
        prefs.edit().putBoolean("enable_ai_features", enabled).apply()
        _settings.update { it.copy(enableAiFeatures = enabled) }
    }

    fun setEnableAiFactChecking(enabled: Boolean) {
        prefs.edit().putBoolean("enable_ai_fact_checking", enabled).apply()
        _settings.update { it.copy(enableAiFactChecking = enabled) }
    }

    fun setEnableAiVoiceNarration(enabled: Boolean) {
        prefs.edit().putBoolean("enable_ai_voice_narration", enabled).apply()
        _settings.update { it.copy(enableAiVoiceNarration = enabled) }
    }

    fun setEnableAiSmartSummaries(enabled: Boolean) {
        prefs.edit().putBoolean("enable_ai_smart_summaries", enabled).apply()
        _settings.update { it.copy(enableAiSmartSummaries = enabled) }
    }

    fun setAudioQuality(quality: AudioStreamingQuality) {
        prefs.edit().putString("audio_quality", quality.name).apply()
        _settings.update { it.copy(audioStreamingQuality = quality) }
    }

    fun setReadingFontSize(size: ReadingFontSize) {
        prefs.edit().putString("font_size", size.name).apply()
        _settings.update { it.copy(readingFontSize = size) }
    }

    fun setDialHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("dial_haptic", enabled).apply()
        _settings.update { it.copy(dialHapticFeedback = enabled) }
    }

    fun setAutoTuneLastStation(enabled: Boolean) {
        prefs.edit().putBoolean("auto_tune_station", enabled).apply()
        _settings.update { it.copy(autoTuneLastStation = enabled) }
    }

    fun setAutoCacheMorningEdition(enabled: Boolean) {
        prefs.edit().putBoolean("auto_cache_morning", enabled).apply()
        _settings.update { it.copy(autoCacheMorningEdition = enabled) }
    }

    fun setBreakingNewsAlerts(enabled: Boolean) {
        prefs.edit().putBoolean("breaking_news_alerts", enabled).apply()
        _settings.update { it.copy(breakingNewsAlerts = enabled) }
    }

    fun setPrintKioskAlerts(enabled: Boolean) {
        prefs.edit().putBoolean("print_kiosk_alerts", enabled).apply()
        _settings.update { it.copy(printKioskArrivalAlerts = enabled) }
    }

    fun setVoiceNarrationPreset(preset: VoiceNarrationPreset) {
        prefs.edit().putString("voice_preset", preset.name).apply()
        _settings.update { it.copy(defaultVoiceNarrationPreset = preset) }
    }

    fun setVoiceNarrationSpeed(speed: Float) {
        prefs.edit().putFloat("voice_speed", speed).apply()
        _settings.update { it.copy(voiceNarrationSpeed = speed) }
    }

    fun setCommunityRadius(radiusMiles: Int) {
        prefs.edit().putInt("community_radius", radiusMiles).apply()
        _settings.update { it.copy(localCommunityRadiusMiles = radiusMiles) }
    }

    fun setMorningDispatchTime(time: String) {
        prefs.edit().putString("morning_dispatch_time", time).apply()
        _settings.update { it.copy(morningDispatchTime = time) }
    }

    fun setHighContrastDisplay(enabled: Boolean) {
        prefs.edit().putBoolean("high_contrast", enabled).apply()
        _settings.update { it.copy(highContrastDisplay = enabled) }
    }

    fun clearMediaCache() {
        prefs.edit().putLong("cache_size_bytes", 0L).apply()
        _settings.update { it.copy(cachedMediaSizeBytes = 0L) }
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        val defaultSettings = AppSettings()
        _settings.value = defaultSettings
    }
}
