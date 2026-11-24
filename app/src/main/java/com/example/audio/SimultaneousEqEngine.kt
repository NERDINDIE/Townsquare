package com.example.audio

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Core frequency categories supported by Townsquare's Simultaneous Equalizer.
 */
enum class EqContentCategory(
    val label: String,
    val emoji: String,
    val defaultTargetLufs: Float
) {
    MUSIC("Music", "🎵", -11.5f),
    RADIO("Radio", "📻", -16.0f),
    PODCAST("Podcast", "🎙️", -14.0f),
    AUDIOBOOK("Audiobook", "📚", -14.0f)
}

/**
 * Built-in hardware/gear acoustic coloration signatures so the Simultaneous EQ
 * can apply an inverse compensation curve regardless of how the user's gear is tuned.
 */
enum class OutputGearProfile(
    val title: String,
    val shortLabel: String,
    val emoji: String,
    val summary: String,
    /**
     * How the gear naturally sounds before correction across the 8 bands:
     * [32Hz, 64Hz, 125Hz, 250Hz, 1kHz, 4kHz, 8kHz, 16kHz] in dB.
     * Positive = gear boosts that band; Negative = gear rolls off / scoops that band.
     */
    val nativeColorationDb: List<Float>
) {
    FLAT_STUDIO(
        title = "Flat Studio Reference",
        shortLabel = "Flat Studio",
        emoji = "🎚️",
        summary = "Neutral monitors / linear DAC — pure content curve",
        nativeColorationDb = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    ),
    BASS_HEAVY_CANS(
        title = "Bass-Heavy Consumer Cans",
        shortLabel = "Bass Cans",
        emoji = "🎧",
        summary = "Boomy +7dB sub-bass & veiled mids — compensated & clarified",
        nativeColorationDb = listOf(6.5f, 7.0f, 5.0f, 2.0f, -2.0f, -1.5f, 0.5f, -1.0f)
    ),
    BRIGHT_V_IEM(
        title = "Bright / V-Shaped Earbuds",
        shortLabel = "Bright V-IEM",
        emoji = "⚡",
        summary = "Sharp 8kHz sibilance & scooped mids — smoothed & warmed",
        nativeColorationDb = listOf(3.0f, 2.5f, 0.0f, -2.5f, -3.5f, 2.5f, 6.0f, 5.0f)
    ),
    PHONE_SPEAKER(
        title = "Phone Built-In Micro Speaker",
        shortLabel = "Phone Spk",
        emoji = "📱",
        summary = "Rolled-off sub-bass & nasal 2kHz peak — psychoacoustic balance",
        nativeColorationDb = listOf(-9.0f, -8.0f, -5.0f, -1.5f, 3.5f, 4.5f, 1.0f, -4.0f)
    ),
    CAR_DASH_STEREO(
        title = "Car Cabin / Road-Noise Audio",
        shortLabel = "Car Audio",
        emoji = "🚗",
        summary = "Cabin boom @ 125Hz & road rumble — speech & transient focus",
        nativeColorationDb = listOf(3.5f, 4.5f, 5.5f, 2.0f, -1.5f, -2.0f, -3.0f, -4.0f)
    ),
    VINTAGE_WARM_SPEAKER(
        title = "Warm Vintage / Wood Cabinet",
        shortLabel = "Vintage Warm",
        emoji = "🪵",
        summary = "Thick low-mids & dark treble roll-off — airy high restoration",
        nativeColorationDb = listOf(-1.0f, 1.5f, 4.0f, 4.5f, 1.0f, -2.5f, -5.0f, -6.5f)
    ),
    CUSTOM_GEAR(
        title = "Custom Tuned Gear",
        shortLabel = "Custom Gear",
        emoji = "🎛️",
        summary = "Matched inversely to your custom gear coloration curve",
        nativeColorationDb = listOf(2.0f, 3.0f, 1.0f, -1.0f, -1.5f, 1.0f, 3.0f, 2.0f)
    )
}

/**
 * Full immutable snapshot of the Simultaneous Equalizer Engine's active state.
 */
data class SimultaneousEqState(
    val isEnabled: Boolean = true,
    val isAutoContentAdapt: Boolean = true,
    val isGearCompensationEnabled: Boolean = true,
    val isBypassCompare: Boolean = false,
    val autoHeadroomEnabled: Boolean = true,
    val compensationIntensity: Float = 1.0f,
    val detectedCategory: EqContentCategory = EqContentCategory.MUSIC,
    val categoryOverride: EqContentCategory? = null,
    val activeSubProfileLabel: String = "Dynamic Mastering Balance",
    val optimizationReason: String = "Auto-tuned optimal listening curve + inverse gear compensation",
    val selectedGearProfile: OutputGearProfile = OutputGearProfile.FLAT_STUDIO,
    val detectedDeviceName: String = "Standard Audio Output",
    val gearSignatureDb: List<Float> = OutputGearProfile.FLAT_STUDIO.nativeColorationDb,
    val contentCurveDb: List<Float> = listOf(2.0f, 1.5f, 0.5f, 0.0f, 0.5f, 2.0f, 2.5f, 2.0f),
    val gearCompensationDb: List<Float> = List(8) { 0f },
    val userFineTrimDb: List<Float> = List(8) { 0f },
    val netSimultaneousDb: List<Float> = listOf(2.0f, 1.5f, 0.5f, 0.0f, 0.5f, 2.0f, 2.5f, 2.0f),
    val preampGainDb: Float = -1.5f,
    val targetLufs: Float = -11.5f,
    val hardwareEqAttached: Boolean = false,
    val hardwareBandCount: Int = 8
) {
    val effectiveCategory: EqContentCategory
        get() = categoryOverride ?: detectedCategory
}

/**
 * Pure DSP math & Android AudioEffect coordinator for Townsquare's Simultaneous Equalizer.
 *
 * Layer 1 (Content Curve): Determines the ideal target frequency curve per Music, Radio, Podcast, or Audiobook.
 * Layer 2 (Gear Inverse Compensation): Inverts the connected gear's tonal coloration so the listener
 * experiences the true optimal sound regardless of whether their headphones/speakers are bass-heavy,
 * bright, tiny, or car-cabin skewed.
 */
class SimultaneousEqEngine(private val context: Context? = null) {

    companion object {
        val BAND_LABELS = listOf("32Hz", "64Hz", "125Hz", "250Hz", "1kHz", "4kHz", "8kHz", "16kHz")
        val BAND_FREQUENCIES_HZ = listOf(32, 64, 125, 250, 1000, 4000, 8000, 16000)
        const val MIN_DB = -12f
        const val MAX_DB = 12f

        fun defaultState(): SimultaneousEqState {
            return computeState(
                currentState = SimultaneousEqState(),
                item = null,
                categoryHint = EqContentCategory.MUSIC,
                titleHint = null,
                tagsHint = ""
            )
        }

        /**
         * Automatically infers the optimal EqContentCategory and sub-profile curve from media metadata.
         */
        fun detectContentProfile(
            item: MediaItemEntity?,
            categoryHint: EqContentCategory? = null,
            titleHint: String? = null,
            tagsHint: String = ""
        ): Triple<EqContentCategory, String, List<Float>> {
            val combinedText = buildString {
                append(item?.title.orEmpty()).append(' ')
                append(item?.subtitle.orEmpty()).append(' ')
                append(item?.tags.orEmpty()).append(' ')
                append(titleHint.orEmpty()).append(' ')
                append(tagsHint)
            }.lowercase()

            val inferredCategory = when {
                categoryHint != null && item == null -> categoryHint
                item?.type == MediaType.RADIO_STATION.name -> EqContentCategory.RADIO
                item?.type == MediaType.PODCAST_EPISODE.name -> {
                    if (combinedText.contains("audiobook") || combinedText.contains("chapter") || combinedText.contains("book")) {
                        EqContentCategory.AUDIOBOOK
                    } else {
                        EqContentCategory.PODCAST
                    }
                }
                item?.type == MediaType.SONG.name || item?.type == MediaType.PLAYLIST.name -> EqContentCategory.MUSIC
                categoryHint != null -> categoryHint
                combinedText.contains("audiobook") || combinedText.contains("chapter") || combinedText.contains("novel") -> EqContentCategory.AUDIOBOOK
                combinedText.contains("podcast") || combinedText.contains("episode") || combinedText.contains("q&a") || combinedText.contains("meeting") -> EqContentCategory.PODCAST
                combinedText.contains("fm") || combinedText.contains("radio") || combinedText.contains("broadcast") -> EqContentCategory.RADIO
                else -> EqContentCategory.MUSIC
            }

            return buildContentCurve(inferredCategory, combinedText)
        }

        fun buildContentCurve(
            category: EqContentCategory,
            metadataTextLower: String = ""
        ): Triple<EqContentCategory, String, List<Float>> {
            return when (category) {
                EqContentCategory.MUSIC -> {
                    when {
                        metadataTextLower.contains("jazz") || metadataTextLower.contains("acoustic") || metadataTextLower.contains("vinyl") || metadataTextLower.contains("coffee") ->
                            Triple(
                                EqContentCategory.MUSIC,
                                "Acoustic & Jazz Warmth",
                                // Warm upright bass, natural timber, silky cymbals
                                listOf(1.5f, 2.5f, 2.0f, 1.0f, 0.5f, 2.0f, 2.5f, 2.0f)
                            )
                        metadataTextLower.contains("synth") || metadataTextLower.contains("electronic") || metadataTextLower.contains("cyber") || metadataTextLower.contains("neon") ->
                            Triple(
                                EqContentCategory.MUSIC,
                                "Wide Harmonic Electronic",
                                // Tight sub-bass pulse, clean mids, crisp air
                                listOf(3.5f, 3.0f, 1.0f, -0.5f, 0.0f, 2.5f, 3.5f, 3.0f)
                            )
                        metadataTextLower.contains("classical") || metadataTextLower.contains("orchestra") || metadataTextLower.contains("symphony") ->
                            Triple(
                                EqContentCategory.MUSIC,
                                "Concert Hall Dynamic Flat+",
                                listOf(1.0f, 1.0f, 0.5f, 0.0f, 0.5f, 1.5f, 2.0f, 2.5f)
                            )
                        else ->
                            Triple(
                                EqContentCategory.MUSIC,
                                "Townsquare Studio Master",
                                // Balanced gentle Harman-inspired music curve
                                listOf(2.5f, 2.0f, 0.5f, 0.0f, 0.5f, 2.0f, 2.5f, 2.0f)
                            )
                    }
                }
                EqContentCategory.RADIO -> {
                    Triple(
                        EqContentCategory.RADIO,
                        "FM Broadcast Presence + De-Hiss",
                        // High-pass sub rumble cut, de-muddy 250Hz, lift announcer/stereo clarity 1k-4k, gentle 16k FM carrier hiss filter
                        listOf(-3.5f, -1.5f, 0.5f, -1.0f, 2.5f, 3.5f, 1.5f, -2.5f)
                    )
                }
                EqContentCategory.PODCAST -> {
                    Triple(
                        EqContentCategory.PODCAST,
                        "Vocal Intelligibility + Plosive Guard",
                        // Strong plosive/rumble cut @ 32-64Hz, remove chest mud @ 250Hz, vocal edge @ 1k-4kHz, de-ess @ 8k-16kHz
                        listOf(-6.0f, -4.0f, -1.0f, -2.0f, 2.5f, 4.5f, -1.5f, -3.5f)
                    )
                }
                EqContentCategory.AUDIOBOOK -> {
                    Triple(
                        EqContentCategory.AUDIOBOOK,
                        "Intimate Narrative Comfort (-14 LUFS)",
                        // Fatigue-free extended listening: cut sub rumble, gentle warmth @ 125-250Hz, crisp consonant @ 4kHz, soft breath de-harsh @ 8k-16k
                        listOf(-5.0f, -3.0f, 1.0f, 1.5f, 2.0f, 3.0f, -2.5f, -4.5f)
                    )
                }
            }
        }

        /**
         * Simultaneously combines the optimal content curve, inverse gear compensation curve,
         * and user fine trim, with dynamic headroom limiting so output never digitally clips.
         */
        fun computeState(
            currentState: SimultaneousEqState,
            item: MediaItemEntity? = null,
            categoryHint: EqContentCategory? = null,
            titleHint: String? = null,
            tagsHint: String = ""
        ): SimultaneousEqState {
            val combinedText = buildString {
                append(item?.title.orEmpty()).append(' ')
                append(item?.subtitle.orEmpty()).append(' ')
                append(item?.tags.orEmpty()).append(' ')
                append(titleHint.orEmpty()).append(' ')
                append(tagsHint)
            }.lowercase()

            val (detectedCat, detectedSubLabel, detectedCurve) = detectContentProfile(
                item = item,
                categoryHint = categoryHint,
                titleHint = titleHint,
                tagsHint = tagsHint
            )

            val effectiveCategory = currentState.categoryOverride ?: if (currentState.isAutoContentAdapt) {
                detectedCat
            } else {
                currentState.detectedCategory
            }

            val (_, activeSubLabel, contentCurve) = if (currentState.categoryOverride != null) {
                buildContentCurve(currentState.categoryOverride, combinedText)
            } else if (currentState.isAutoContentAdapt) {
                Triple(detectedCat, detectedSubLabel, detectedCurve)
            } else {
                buildContentCurve(effectiveCategory, "")
            }

            // Layer 2: Inverse Gear Compensation cancels the hardware's native coloration
            val intensity = currentState.compensationIntensity.coerceIn(0f, 1.25f)
            val gearComp = if (currentState.isGearCompensationEnabled) {
                currentState.gearSignatureDb.map { nativeBias ->
                    // Phone micro-speaker cannot safely take +9dB at 32Hz; cap sub-bass inverse boost softly below 64Hz
                    (-nativeBias * intensity).coerceIn(-10f, 9f)
                }
            } else {
                List(8) { 0f }
            }

            // Extra driver safety on Phone Speaker below 64Hz to prevent distortion
            val safeGearComp = if (currentState.selectedGearProfile == OutputGearProfile.PHONE_SPEAKER && currentState.isGearCompensationEnabled) {
                gearComp.mapIndexed { idx, gain ->
                    when (idx) {
                        0 -> (gain * 0.25f).coerceAtMost(2.5f) // protect 32Hz from blowing micro speaker
                        1 -> (gain * 0.45f).coerceAtMost(4.0f) // warm 64Hz harmonic overtone lift
                        else -> gain
                    }
                }
            } else {
                gearComp
            }

            val rawNet = if (!currentState.isEnabled || currentState.isBypassCompare) {
                List(8) { 0f }
            } else {
                List(8) { i ->
                    contentCurve[i] + safeGearComp[i] + currentState.userFineTrimDb.getOrElse(i) { 0f }
                }
            }

            // Headroom gain staging so stacked boosts never clip
            val maxPositivePeak = rawNet.maxOrNull()?.coerceAtLeast(0f) ?: 0f
            val preampGain = if (!currentState.isEnabled || currentState.isBypassCompare) {
                0f
            } else if (currentState.autoHeadroomEnabled && maxPositivePeak > 3.0f) {
                -(maxPositivePeak - 2.0f).coerceAtMost(8.0f)
            } else {
                0f
            }

            val clampedNet = rawNet.map { gain ->
                val scaled = if (currentState.autoHeadroomEnabled && abs(gain) > 10.5f) {
                    val sign = if (gain >= 0f) 1f else -1f
                    sign * (10.5f + (abs(gain) - 10.5f) * 0.35f)
                } else {
                    gain
                }
                ((scaled * 10f).roundToInt() / 10f).coerceIn(MIN_DB, MAX_DB)
            }

            val reason = buildString {
                if (currentState.isBypassCompare) {
                    append("A/B Bypass active — hearing raw uncompensated ")
                    append(currentState.selectedGearProfile.shortLabel)
                } else if (!currentState.isEnabled) {
                    append("Simultaneous EQ disabled")
                } else {
                    append(effectiveCategory.label)
                    append(" (").append(activeSubLabel).append(")")
                    if (currentState.isGearCompensationEnabled) {
                        append(" + Inverse ").append(currentState.selectedGearProfile.shortLabel)
                    }
                }
            }

            return currentState.copy(
                detectedCategory = detectedCat,
                activeSubProfileLabel = activeSubLabel,
                optimizationReason = reason,
                contentCurveDb = contentCurve,
                gearCompensationDb = safeGearComp,
                netSimultaneousDb = clampedNet,
                preampGainDb = (preampGain * 10f).roundToInt() / 10f,
                targetLufs = effectiveCategory.defaultTargetLufs
            )
        }
    }

    private var hardwareEqualizer: Equalizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var attachedSessionId: Int = -1

    /**
     * Scans Android's AudioManager output devices to detect connected Bluetooth/Wired/Speaker hardware
     * and map it to the closest OutputGearProfile compensation preset.
     */
    fun detectConnectedOutputGear(): Pair<OutputGearProfile, String> {
        val ctx = context ?: return OutputGearProfile.FLAT_STUDIO to "Standard Audio Output"
        return try {
            val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return OutputGearProfile.FLAT_STUDIO to "Standard Audio Output"
            val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val preferred = outputs.firstOrNull { device ->
                when (device.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                    AudioDeviceInfo.TYPE_BLE_HEADSET,
                    AudioDeviceInfo.TYPE_BLE_SPEAKER,
                    AudioDeviceInfo.TYPE_WIRED_HEADSET,
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_USB_HEADSET,
                    AudioDeviceInfo.TYPE_AUX_LINE -> true
                    else -> false
                }
            } ?: outputs.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }

            if (preferred == null) {
                OutputGearProfile.FLAT_STUDIO to "Standard Audio Output"
            } else {
                val rawName = preferred.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Audio Output"
                when (preferred.type) {
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER ->
                        OutputGearProfile.PHONE_SPEAKER to "$rawName (Built-in Speaker)"
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                    AudioDeviceInfo.TYPE_BLE_HEADSET -> {
                        val lower = rawName.lowercase()
                        when {
                            lower.contains("car") || lower.contains("auto") || lower.contains("uconnect") ->
                                OutputGearProfile.CAR_DASH_STEREO to "$rawName (Car Bluetooth)"
                            lower.contains("studio") || lower.contains("ref") || lower.contains("hd") ->
                                OutputGearProfile.FLAT_STUDIO to "$rawName (Reference BT)"
                            lower.contains("bud") || lower.contains("pod") || lower.contains("iem") ->
                                OutputGearProfile.BRIGHT_V_IEM to "$rawName (Earbuds BT)"
                            else ->
                                OutputGearProfile.BASS_HEAVY_CANS to "$rawName (Bluetooth)"
                        }
                    }
                    AudioDeviceInfo.TYPE_BLE_SPEAKER ->
                        OutputGearProfile.VINTAGE_WARM_SPEAKER to "$rawName (Portable Speaker)"
                    AudioDeviceInfo.TYPE_USB_HEADSET,
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                    AudioDeviceInfo.TYPE_WIRED_HEADSET ->
                        OutputGearProfile.FLAT_STUDIO to "$rawName (Wired DAC/Jack)"
                    AudioDeviceInfo.TYPE_AUX_LINE ->
                        OutputGearProfile.CAR_DASH_STEREO to "$rawName (AUX Line-Out)"
                    else ->
                        OutputGearProfile.FLAT_STUDIO to rawName
                }
            }
        } catch (_: Throwable) {
            OutputGearProfile.FLAT_STUDIO to "Standard Audio Output"
        }
    }

    /**
     * Binds and updates Android's native `android.media.audiofx.Equalizer` & `LoudnessEnhancer`
     * safely whenever a real audio session ID exists. Falls back cleanly on headless/simulated streams.
     */
    fun applyToAudioSession(audioSessionId: Int, eqState: SimultaneousEqState): Pair<Boolean, Int> {
        if (audioSessionId <= 0) {
            return false to BAND_LABELS.size
        }
        return try {
            if (attachedSessionId != audioSessionId || hardwareEqualizer == null) {
                releaseHardwareEffects()
                hardwareEqualizer = Equalizer(0, audioSessionId).apply {
                    enabled = eqState.isEnabled && !eqState.isBypassCompare
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                        enabled = eqState.isEnabled && !eqState.isBypassCompare
                    }
                }
                attachedSessionId = audioSessionId
            }

            val eq = hardwareEqualizer ?: return false to BAND_LABELS.size
            val active = eqState.isEnabled && !eqState.isBypassCompare
            eq.enabled = active

            val numBands = eq.numberOfBands.toInt().coerceAtLeast(1)
            val range = eq.bandLevelRange // [minMillibel, maxMillibel]
            val minMb = (range.getOrNull(0) ?: (-1500).toShort()).toInt()
            val maxMb = (range.getOrNull(1) ?: 1500.toShort()).toInt()

            for (bandIdx in 0 until numBands) {
                val centerHz = eq.getCenterFreq(bandIdx.toShort()) / 1000
                val interpolatedDb = interpolateNetDbForFrequency(centerHz, eqState.netSimultaneousDb)
                val targetMb = (interpolatedDb * 100f).roundToInt().coerceIn(minMb, maxMb).toShort()
                eq.setBandLevel(bandIdx.toShort(), targetMb)
            }

            loudnessEnhancer?.let { le ->
                le.enabled = active && eqState.autoHeadroomEnabled
                val boostMb = if (eqState.effectiveCategory == EqContentCategory.AUDIOBOOK || eqState.effectiveCategory == EqContentCategory.PODCAST) {
                    180 // subtle +1.8dB spoken clarity leveling
                } else {
                    0
                }
                le.setTargetGain(boostMb)
            }

            true to numBands
        } catch (_: Throwable) {
            false to BAND_LABELS.size
        }
    }

    fun releaseHardwareEffects() {
        try {
            hardwareEqualizer?.release()
        } catch (_: Throwable) {}
        try {
            loudnessEnhancer?.release()
        } catch (_: Throwable) {}
        hardwareEqualizer = null
        loudnessEnhancer = null
        attachedSessionId = -1
    }

    private fun interpolateNetDbForFrequency(hz: Int, curve8Band: List<Float>): Float {
        if (curve8Band.isEmpty()) return 0f
        if (hz <= BAND_FREQUENCIES_HZ.first()) return curve8Band.first()
        if (hz >= BAND_FREQUENCIES_HZ.last()) return curve8Band.last()

        for (i in 0 until BAND_FREQUENCIES_HZ.size - 1) {
            val fLow = BAND_FREQUENCIES_HZ[i]
            val fHigh = BAND_FREQUENCIES_HZ[i + 1]
            if (hz in fLow..fHigh) {
                val ratio = (hz - fLow).toFloat() / max(1, fHigh - fLow).toFloat()
                val vLow = curve8Band.getOrElse(i) { 0f }
                val vHigh = curve8Band.getOrElse(i + 1) { 0f }
                return min(MAX_DB, max(MIN_DB, vLow + (vHigh - vLow) * ratio))
            }
        }
        return curve8Band.last()
    }
}
