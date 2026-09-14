package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.JournalEditionEntity
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
import java.util.Locale
import kotlin.math.sin

enum class NarrationStyle(
    val id: String,
    val title: String,
    val emoji: String,
    val badge: String,
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val introTemplate: (title: String, author: String) -> String
) {
    PODCAST(
        id = "podcast",
        title = "Deep Dive Podcast",
        emoji = "🎙️",
        badge = "Host Commentary",
        defaultPitch = 1.0f,
        defaultSpeed = 1.02f,
        introTemplate = { title, author ->
            "Welcome to the Townsquare Audio Dispatch. Today's deep dive feature is: $title, reported by $author. Let's explore the story."
        }
    ),
    BREAKING_NEWS(
        id = "breaking",
        title = "Breaking News Anchor",
        emoji = "⚡",
        badge = "Live Bulletin",
        defaultPitch = 1.12f,
        defaultSpeed = 1.18f,
        introTemplate = { title, _ ->
            "Townsquare News Alert. We are coming to you with an urgent editorial dispatch: $title. Here is the breaking coverage."
        }
    ),
    LITERARY_MAGAZINE(
        id = "literary",
        title = "Periodical Narrator",
        emoji = "📖",
        badge = "Longform Voiceover",
        defaultPitch = 0.94f,
        defaultSpeed = 0.92f,
        introTemplate = { title, author ->
            "From the pages of Townsquare Periodicals and Broadsheets. Reading: $title, by $author."
        }
    )
}

data class VoiceNarrationState(
    val isNarrating: Boolean = false,
    val isPaused: Boolean = false,
    val currentItemTitle: String = "",
    val currentAuthor: String = "",
    val currentMediaType: String = "",
    val style: NarrationStyle = NarrationStyle.PODCAST,
    val currentParagraphIndex: Int = 0,
    val totalParagraphs: Int = 1,
    val currentTextSnippet: String = "",
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val waveformHeights: List<Float> = listOf(0.3f, 0.5f, 0.8f, 0.4f, 0.7f, 0.6f, 0.9f)
)

class VoiceNarrationManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val _state = MutableStateFlow(VoiceNarrationState())
    val state: StateFlow<VoiceNarrationState> = _state.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var paragraphs: List<String> = emptyList()
    private var currentItem: MediaItemEntity? = null
    private var simulationJob: Job? = null
    private var waveformJob: Job? = null

    init {
        initTts()
    }

    private fun initTts() {
        try {
            textToSpeech = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = textToSpeech?.setLanguage(Locale.US)
                    isTtsInitialized = (result != TextToSpeech.LANG_MISSING_DATA &&
                            result != TextToSpeech.LANG_NOT_SUPPORTED)
                    setupUtteranceListener()
                } else {
                    isTtsInitialized = false
                }
            }
        } catch (_: Exception) {
            isTtsInitialized = false
        }
    }

    private fun setupUtteranceListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _state.update { it.copy(isPaused = false, isNarrating = true) }
            }

            override fun onDone(utteranceId: String?) {
                scope.launch {
                    val currentIdx = _state.value.currentParagraphIndex
                    if (currentIdx + 1 < paragraphs.size) {
                        speakParagraph(currentIdx + 1)
                    } else {
                        stop()
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                // Fall back to animated simulation ticker
                startFallbackProgress()
            }
        })
    }

    fun startNarration(
        item: MediaItemEntity,
        style: NarrationStyle = when (item.type) {
            MediaType.NEWSPAPER_MAGAZINE.name -> NarrationStyle.LITERARY_MAGAZINE
            MediaType.NEWSLETTER.name -> NarrationStyle.PODCAST
            else -> if (item.tags.contains("breaking")) NarrationStyle.BREAKING_NEWS else NarrationStyle.PODCAST
        }
    ) {
        stopInternal()
        currentItem = item

        // Build list of voiceable sections (intro + title + paragraphs)
        val intro = style.introTemplate(item.title, item.authorName)
        val rawParagraphs = item.bodyText
            .split("\n\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val list = mutableListOf<String>()
        list.add(intro)
        if (item.subtitle.isNotBlank()) {
            list.add(item.subtitle)
        }
        list.addAll(rawParagraphs)
        paragraphs = list

        _state.update {
            it.copy(
                isNarrating = true,
                isPaused = false,
                currentItemTitle = item.title,
                currentAuthor = item.authorName,
                currentMediaType = item.type,
                style = style,
                currentParagraphIndex = 0,
                totalParagraphs = list.size,
                currentTextSnippet = list.firstOrNull() ?: "",
                pitch = style.defaultPitch,
                speed = style.defaultSpeed
            )
        }

        startWaveformAnimation()
        speakParagraph(0)
    }

    fun startNarrationForJournal(
        journal: JournalEditionEntity,
        style: NarrationStyle = NarrationStyle.LITERARY_MAGAZINE
    ) {
        stopInternal()
        val intro = "Townsquare Journal Press special issue. Reading ${journal.newspaperTitle}, Issue Number ${journal.issueNumber}."
        val list = mutableListOf<String>()
        list.add(intro)
        list.add("Lead Headline: ${journal.leadHeadline}. Reported by ${journal.leadAuthor}.")
        list.add(journal.leadArticleBody)
        if (journal.secondaryHeadline.isNotBlank()) {
            list.add("Secondary Dispatch: ${journal.secondaryHeadline}.")
            list.add(journal.secondaryArticleBody)
        }
        paragraphs = list

        _state.update {
            it.copy(
                isNarrating = true,
                isPaused = false,
                currentItemTitle = journal.newspaperTitle,
                currentAuthor = journal.leadAuthor,
                currentMediaType = MediaType.NEWSPAPER_MAGAZINE.name,
                style = style,
                currentParagraphIndex = 0,
                totalParagraphs = list.size,
                currentTextSnippet = list.firstOrNull() ?: "",
                pitch = style.defaultPitch,
                speed = style.defaultSpeed
            )
        }

        startWaveformAnimation()
        speakParagraph(0)
    }

    fun startNarrationText(
        title: String,
        text: String,
        author: String = "Townsquare Press",
        style: NarrationStyle = NarrationStyle.BREAKING_NEWS
    ) {
        stopInternal()
        val intro = style.introTemplate(title, author)
        val rawParagraphs = text
            .split("\n\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val list = mutableListOf<String>()
        list.add(intro)
        list.addAll(rawParagraphs)
        paragraphs = list

        _state.update {
            it.copy(
                isNarrating = true,
                isPaused = false,
                currentItemTitle = title,
                currentAuthor = author,
                currentMediaType = MediaType.NEWSPAPER_MAGAZINE.name,
                style = style,
                currentParagraphIndex = 0,
                totalParagraphs = list.size,
                currentTextSnippet = list.firstOrNull() ?: "",
                pitch = style.defaultPitch,
                speed = style.defaultSpeed
            )
        }

        startWaveformAnimation()
        speakParagraph(0)
    }

    fun setStyle(newStyle: NarrationStyle) {
        val wasNarrating = _state.value.isNarrating
        _state.update {
            it.copy(
                style = newStyle,
                pitch = newStyle.defaultPitch,
                speed = newStyle.defaultSpeed
            )
        }
        if (wasNarrating) {
            val idx = _state.value.currentParagraphIndex
            speakParagraph(idx)
        }
    }

    fun setSpeed(speed: Float) {
        _state.update { it.copy(speed = speed) }
        textToSpeech?.setSpeechRate(speed)
    }

    fun setPitch(pitch: Float) {
        _state.update { it.copy(pitch = pitch) }
        textToSpeech?.setPitch(pitch)
    }

    fun togglePlayPause() {
        if (!_state.value.isNarrating) return
        if (_state.value.isPaused) {
            resume()
        } else {
            pause()
        }
    }

    fun pause() {
        _state.update { it.copy(isPaused = true) }
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
        simulationJob?.cancel()
    }

    fun resume() {
        _state.update { it.copy(isPaused = false) }
        speakParagraph(_state.value.currentParagraphIndex)
    }

    fun nextParagraph() {
        val next = _state.value.currentParagraphIndex + 1
        if (next < paragraphs.size) {
            speakParagraph(next)
        } else {
            stop()
        }
    }

    fun previousParagraph() {
        val prev = (_state.value.currentParagraphIndex - 1).coerceAtLeast(0)
        speakParagraph(prev)
    }

    fun stop() {
        stopInternal()
        _state.update {
            it.copy(
                isNarrating = false,
                isPaused = false,
                currentParagraphIndex = 0,
                currentTextSnippet = ""
            )
        }
    }

    private fun stopInternal() {
        simulationJob?.cancel()
        waveformJob?.cancel()
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
    }

    private fun speakParagraph(index: Int) {
        if (index !in paragraphs.indices) {
            stop()
            return
        }

        val text = paragraphs[index]
        _state.update {
            it.copy(
                currentParagraphIndex = index,
                currentTextSnippet = text,
                isPaused = false
            )
        }

        val style = _state.value.style
        val speed = _state.value.speed
        val pitch = _state.value.pitch

        if (isTtsInitialized && textToSpeech != null) {
            try {
                textToSpeech?.setPitch(pitch)
                textToSpeech?.setSpeechRate(speed)
                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "narrate_${System.currentTimeMillis()}")
                val result = textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "narrate_${index}")
                if (result == TextToSpeech.ERROR) {
                    startFallbackProgress()
                }
            } catch (_: Exception) {
                startFallbackProgress()
            }
        } else {
            startFallbackProgress()
        }
    }

    private fun startFallbackProgress() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            val text = _state.value.currentTextSnippet
            // Approximate duration based on word count
            val words = text.split(" ").size.coerceAtLeast(4)
            val durationMs = ((words / (2.5f * _state.value.speed)) * 1000L).toLong().coerceIn(3000L, 12000L)
            delay(durationMs)
            if (isActive && !_state.value.isPaused && _state.value.isNarrating) {
                val next = _state.value.currentParagraphIndex + 1
                if (next < paragraphs.size) {
                    speakParagraph(next)
                } else {
                    stop()
                }
            }
        }
    }

    private fun startWaveformAnimation() {
        waveformJob?.cancel()
        waveformJob = scope.launch {
            var step = 0
            while (isActive && _state.value.isNarrating) {
                delay(220L)
                if (!_state.value.isPaused) {
                    step++
                    val w1 = (0.2f + 0.7f * ((sin(step * 0.4) + 1) / 2)).toFloat()
                    val w2 = (0.3f + 0.6f * ((sin(step * 0.8 + 1) + 1) / 2)).toFloat()
                    val w3 = (0.25f + 0.75f * ((sin(step * 0.5 + 2) + 1) / 2)).toFloat()
                    val w4 = (0.4f + 0.5f * ((sin(step * 0.9 + 0.5) + 1) / 2)).toFloat()
                    val w5 = (0.2f + 0.8f * ((sin(step * 0.6 + 3) + 1) / 2)).toFloat()
                    val w6 = (0.35f + 0.55f * ((sin(step * 0.7 + 1.5) + 1) / 2)).toFloat()
                    val w7 = (0.15f + 0.8f * ((sin(step * 0.3 + 4) + 1) / 2)).toFloat()
                    _state.update {
                        it.copy(waveformHeights = listOf(w1, w2, w3, w4, w5, w6, w7))
                    }
                }
            }
        }
    }

    fun shutdown() {
        stopInternal()
        try {
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
        textToSpeech = null
    }
}
