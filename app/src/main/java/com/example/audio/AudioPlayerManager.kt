package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class AudioState(
    val currentItem: MediaItemEntity? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1.0f,
    val waveformHeights: List<Float> = listOf(0.3f, 0.6f, 0.8f, 0.4f, 0.7f, 0.5f, 0.9f),
    val simultaneousEq: SimultaneousEqState = SimultaneousEqEngine.defaultState()
)

class AudioPlayerManager(private val context: Context) {

    companion object {
        @Volatile
        private var instance: AudioPlayerManager? = null

        fun getInstance(context: Context): AudioPlayerManager {
            return instance ?: synchronized(this) {
                instance ?: AudioPlayerManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val eqEngine = SimultaneousEqEngine(context)
    private val _audioState = MutableStateFlow(AudioState(simultaneousEq = initialCalibratedEqState()))
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var simulatedAudioJob: Job? = null
    private var lastCategoryHint: EqContentCategory? = null
    private var lastTitleHint: String? = null
    private var lastTagsHint: String = ""

    private fun initialCalibratedEqState(): SimultaneousEqState {
        val (gearProfile, deviceName) = eqEngine.detectConnectedOutputGear()
        val seed = SimultaneousEqState(
            selectedGearProfile = gearProfile,
            detectedDeviceName = deviceName,
            gearSignatureDb = gearProfile.nativeColorationDb
        )
        return SimultaneousEqEngine.computeState(seed)
    }

    private fun recomputeAndApplyEq(
        item: MediaItemEntity? = _audioState.value.currentItem,
        categoryHint: EqContentCategory? = lastCategoryHint,
        titleHint: String? = lastTitleHint,
        tagsHint: String = lastTagsHint,
        transform: (SimultaneousEqState) -> SimultaneousEqState = { it }
    ) {
        _audioState.update { state ->
            val updatedBase = transform(state.simultaneousEq)
            val computed = SimultaneousEqEngine.computeState(
                currentState = updatedBase,
                item = item,
                categoryHint = categoryHint,
                titleHint = titleHint,
                tagsHint = tagsHint
            )
            val sessionId = try {
                mediaPlayer?.audioSessionId ?: -1
            } catch (_: Throwable) {
                -1
            }
            val (attached, bandCount) = eqEngine.applyToAudioSession(sessionId, computed)
            state.copy(
                simultaneousEq = computed.copy(
                    hardwareEqAttached = attached,
                    hardwareBandCount = bandCount
                )
            )
        }
    }

    fun play(item: MediaItemEntity) {
        val sameItem = _audioState.value.currentItem?.id == item.id
        if (sameItem && _audioState.value.isPlaying) {
            pause()
            return
        }

        stopInternal()

        val isRadio = item.type == MediaType.RADIO_STATION.name
        val durationMs = if (isRadio) 0L else (item.durationSeconds.toLong() * 1000L).coerceAtLeast(180000L)

        lastCategoryHint = null
        lastTitleHint = item.title
        lastTagsHint = item.tags

        _audioState.update {
            it.copy(
                currentItem = item,
                isPlaying = true,
                isBuffering = false,
                positionMs = if (sameItem) it.positionMs else 0L,
                durationMs = durationMs
            )
        }

        recomputeAndApplyEq(
            item = item,
            categoryHint = null,
            titleHint = item.title,
            tagsHint = item.tags
        )

        notifyWidgetUpdate(item, true)
        startSimulatedAudioAndTicker(isRadio, durationMs)
    }

    fun togglePlayPause() {
        val current = _audioState.value.currentItem ?: return
        if (_audioState.value.isPlaying) {
            pause()
        } else {
            _audioState.update { it.copy(isPlaying = true) }
            recomputeAndApplyEq(item = current)
            notifyWidgetUpdate(current, true)
            val isRadio = current.type == MediaType.RADIO_STATION.name
            startSimulatedAudioAndTicker(isRadio, _audioState.value.durationMs)
        }
    }

    fun pause() {
        _audioState.update { it.copy(isPlaying = false) }
        notifyWidgetUpdate(_audioState.value.currentItem, false)
        progressJob?.cancel()
        simulatedAudioJob?.cancel()
        try {
            mediaPlayer?.pause()
        } catch (_: Exception) {}
    }

    fun seekTo(posMs: Long) {
        val bounded = posMs.coerceIn(0L, _audioState.value.durationMs)
        _audioState.update { it.copy(positionMs = bounded) }
        try {
            mediaPlayer?.seekTo(bounded.toInt())
        } catch (_: Exception) {}
    }

    fun skipForward15() {
        seekTo(_audioState.value.positionMs + 15000L)
    }

    fun skipBackward15() {
        seekTo((_audioState.value.positionMs - 15000L).coerceAtLeast(0L))
    }

    fun playRadioDirectly(station: MediaItemEntity) {
        val sameItem = _audioState.value.currentItem?.id == station.id
        if (sameItem && _audioState.value.isPlaying) {
            return
        }
        stopInternal()
        lastCategoryHint = EqContentCategory.RADIO
        lastTitleHint = station.title
        lastTagsHint = station.tags
        _audioState.update {
            it.copy(
                currentItem = station,
                isPlaying = true,
                isBuffering = false,
                positionMs = 0L,
                durationMs = 0L
            )
        }
        recomputeAndApplyEq(
            item = station,
            categoryHint = EqContentCategory.RADIO,
            titleHint = station.title,
            tagsHint = station.tags
        )
        notifyWidgetUpdate(station, true)
        startSimulatedAudioAndTicker(isRadio = true, totalDurationMs = 0L)
    }

    fun seekNextStation(stations: List<MediaItemEntity>) {
        if (stations.isEmpty()) return
        val currentId = _audioState.value.currentItem?.id
        val currentIndex = stations.indexOfFirst { it.id == currentId }
        val nextIndex = if (currentIndex in stations.indices) {
            (currentIndex + 1) % stations.size
        } else {
            0
        }
        playRadioDirectly(stations[nextIndex])
    }

    fun seekPreviousStation(stations: List<MediaItemEntity>) {
        if (stations.isEmpty()) return
        val currentId = _audioState.value.currentItem?.id
        val currentIndex = stations.indexOfFirst { it.id == currentId }
        val prevIndex = if (currentIndex in stations.indices) {
            if (currentIndex - 1 < 0) stations.size - 1 else currentIndex - 1
        } else {
            stations.size - 1
        }
        playRadioDirectly(stations[prevIndex])
    }

    fun setSpeed(speed: Float) {
        _audioState.update { it.copy(speed = speed) }
    }

    fun stop() {
        stopInternal()
        _audioState.update {
            it.copy(
                currentItem = null,
                isPlaying = false,
                positionMs = 0L
            )
        }
        notifyWidgetUpdate(null, false)
    }

    // =========================================================================
    // SIMULTANEOUS EQUALIZER CONTROLS
    // =========================================================================

    fun syncContentContext(
        categoryHint: EqContentCategory,
        titleHint: String? = null,
        tagsHint: String = ""
    ) {
        lastCategoryHint = categoryHint
        if (!titleHint.isNullOrBlank()) lastTitleHint = titleHint
        if (tagsHint.isNotBlank()) lastTagsHint = tagsHint
        recomputeAndApplyEq(
            item = _audioState.value.currentItem,
            categoryHint = categoryHint,
            titleHint = titleHint ?: lastTitleHint,
            tagsHint = tagsHint
        )
    }

    fun setSimultaneousEqEnabled(enabled: Boolean) {
        recomputeAndApplyEq { it.copy(isEnabled = enabled) }
    }

    fun toggleSimultaneousEq() {
        setSimultaneousEqEnabled(!_audioState.value.simultaneousEq.isEnabled)
    }

    fun toggleBypassCompare() {
        recomputeAndApplyEq { it.copy(isBypassCompare = !it.isBypassCompare) }
    }

    fun setAutoContentAdapt(enabled: Boolean) {
        recomputeAndApplyEq {
            it.copy(
                isAutoContentAdapt = enabled,
                categoryOverride = if (enabled) null else it.categoryOverride
            )
        }
    }

    fun setGearCompensationEnabled(enabled: Boolean) {
        recomputeAndApplyEq { it.copy(isGearCompensationEnabled = enabled) }
    }

    fun setAutoHeadroomEnabled(enabled: Boolean) {
        recomputeAndApplyEq { it.copy(autoHeadroomEnabled = enabled) }
    }

    fun setCompensationIntensity(intensity: Float) {
        recomputeAndApplyEq { it.copy(compensationIntensity = intensity.coerceIn(0f, 1.25f)) }
    }

    fun setCategoryOverride(category: EqContentCategory?) {
        recomputeAndApplyEq {
            it.copy(
                categoryOverride = category,
                isAutoContentAdapt = category == null
            )
        }
    }

    fun selectOutputGearProfile(profile: OutputGearProfile) {
        recomputeAndApplyEq {
            it.copy(
                selectedGearProfile = profile,
                gearSignatureDb = profile.nativeColorationDb,
                isGearCompensationEnabled = true
            )
        }
    }

    fun detectConnectedOutputGear() {
        val (gearProfile, deviceName) = eqEngine.detectConnectedOutputGear()
        recomputeAndApplyEq {
            it.copy(
                selectedGearProfile = gearProfile,
                detectedDeviceName = deviceName,
                gearSignatureDb = gearProfile.nativeColorationDb,
                isGearCompensationEnabled = true
            )
        }
    }

    fun updateGearColorationZone(
        bassBiasDb: Float? = null,
        midBiasDb: Float? = null,
        trebleBiasDb: Float? = null
    ) {
        recomputeAndApplyEq { state ->
            val updated = state.gearSignatureDb.toMutableList()
            bassBiasDb?.let { b ->
                val clamped = b.coerceIn(-10f, 10f)
                updated[0] = clamped
                updated[1] = clamped
                updated[2] = clamped * 0.7f
            }
            midBiasDb?.let { m ->
                val clamped = m.coerceIn(-10f, 10f)
                updated[3] = clamped * 0.8f
                updated[4] = clamped
            }
            trebleBiasDb?.let { t ->
                val clamped = t.coerceIn(-10f, 10f)
                updated[5] = clamped * 0.75f
                updated[6] = clamped
                updated[7] = clamped * 0.9f
            }
            state.copy(
                selectedGearProfile = OutputGearProfile.CUSTOM_GEAR,
                gearSignatureDb = updated,
                isGearCompensationEnabled = true
            )
        }
    }

    fun updateGearSignatureBand(bandIndex: Int, colorationDb: Float) {
        recomputeAndApplyEq { state ->
            if (bandIndex !in state.gearSignatureDb.indices) return@recomputeAndApplyEq state
            val updated = state.gearSignatureDb.toMutableList()
            updated[bandIndex] = colorationDb.coerceIn(-12f, 12f)
            state.copy(
                selectedGearProfile = OutputGearProfile.CUSTOM_GEAR,
                gearSignatureDb = updated,
                isGearCompensationEnabled = true
            )
        }
    }

    fun updateManualTrimBand(bandIndex: Int, trimDb: Float) {
        recomputeAndApplyEq { state ->
            if (bandIndex !in state.userFineTrimDb.indices) return@recomputeAndApplyEq state
            val updated = state.userFineTrimDb.toMutableList()
            updated[bandIndex] = trimDb.coerceIn(-6f, 6f)
            state.copy(userFineTrimDb = updated)
        }
    }

    fun resetManualTrim() {
        recomputeAndApplyEq { it.copy(userFineTrimDb = List(8) { 0f }) }
    }

    fun resetGearCompensation() {
        recomputeAndApplyEq {
            it.copy(
                selectedGearProfile = OutputGearProfile.FLAT_STUDIO,
                gearSignatureDb = OutputGearProfile.FLAT_STUDIO.nativeColorationDb,
                compensationIntensity = 1.0f
            )
        }
    }

    private fun notifyWidgetUpdate(item: MediaItemEntity?, isPlaying: Boolean) {
        try {
            com.example.widget.TownsquareRadioWidgetProvider.updateAllWidgets(
                context = context,
                stationTitle = item?.title,
                frequency = item?.stationFrequency,
                isPlaying = isPlaying
            )
        } catch (_: Exception) {}
    }

    private fun stopInternal() {
        progressJob?.cancel()
        simulatedAudioJob?.cancel()
        eqEngine.releaseHardwareEffects()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    private fun startSimulatedAudioAndTicker(isRadio: Boolean, totalDurationMs: Long) {
        progressJob?.cancel()
        progressJob = scope.launch {
            var step = 0
            while (isActive && _audioState.value.isPlaying) {
                delay(300L)
                step++
                val speed = _audioState.value.speed
                val delta = (300L * speed).toLong()
                val netEq = _audioState.value.simultaneousEq.netSimultaneousDb
                val eqActive = _audioState.value.simultaneousEq.isEnabled && !_audioState.value.simultaneousEq.isBypassCompare

                fun eqWeightedWave(bandIdx: Int, baseWave: Float): Float {
                    if (!eqActive) return baseWave
                    val eqGainFactor = 1f + (netEq.getOrElse(bandIdx) { 0f } / 28f)
                    return (baseWave * eqGainFactor).coerceIn(0.12f, 0.98f)
                }

                // Generate dynamic visualizer waveform bars modulated by active Simultaneous EQ curve
                val wave1 = eqWeightedWave(0, (0.2f + 0.6f * ((sin(step * 0.45) + 1) / 2)).toFloat())
                val wave2 = eqWeightedWave(1, (0.3f + 0.6f * ((sin(step * 0.7 + 1) + 1) / 2)).toFloat())
                val wave3 = eqWeightedWave(2, (0.15f + 0.75f * ((sin(step * 0.3 + 2) + 1) / 2)).toFloat())
                val wave4 = eqWeightedWave(4, (0.4f + 0.5f * ((sin(step * 0.9 + 0.5) + 1) / 2)).toFloat())
                val wave5 = eqWeightedWave(5, (0.25f + 0.65f * ((sin(step * 0.6 + 3) + 1) / 2)).toFloat())
                val wave6 = eqWeightedWave(6, (0.35f + 0.55f * ((sin(step * 0.8 + 1.5) + 1) / 2)).toFloat())
                val wave7 = eqWeightedWave(7, (0.2f + 0.7f * ((sin(step * 0.5 + 4) + 1) / 2)).toFloat())

                _audioState.update { st ->
                    val newPos = if (isRadio) {
                        st.positionMs + delta
                    } else {
                        val p = st.positionMs + delta
                        if (totalDurationMs > 0 && p >= totalDurationMs) {
                            0L // Loop or finish
                        } else {
                            p
                        }
                    }
                    st.copy(
                        positionMs = newPos,
                        waveformHeights = listOf(wave1, wave2, wave3, wave4, wave5, wave6, wave7)
                    )
                }
            }
        }
    }
}
