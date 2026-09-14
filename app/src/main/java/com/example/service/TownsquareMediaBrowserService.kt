package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import android.service.media.MediaBrowserService
import com.example.MainActivity
import com.example.R
import com.example.audio.AudioPlayerManager
import com.example.data.local.AppDatabase
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * TownsquareMediaBrowserService provides standard Android Auto & in-vehicle head unit
 * media browsing, radio station seeking, and audio playback controls.
 */
class TownsquareMediaBrowserService : MediaBrowserService() {

    companion object {
        const val ROOT_ID = "townsquare_media_root"
        const val CATEGORY_RADIO = "cat_live_radio"
        const val CATEGORY_PODCASTS = "cat_podcasts"
        const val CATEGORY_BRIEF = "cat_morning_brief"

        private const val CHANNEL_ID = "townsquare_auto_channel"
        private const val NOTIFICATION_ID = 2001
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var mediaSession: MediaSession
    private lateinit var audioPlayer: AudioPlayerManager
    private lateinit var repository: MediaRepository
    private var cachedStations = listOf<MediaItemEntity>()

    override fun onCreate() {
        super.onCreate()

        val db = AppDatabase.getInstance(applicationContext)
        repository = MediaRepository(db.mediaDao())
        audioPlayer = AudioPlayerManager.getInstance(applicationContext)

        // Create MediaSession for Android Auto transport controls
        mediaSession = MediaSession(this, "TownsquareAutoMediaSession").apply {
            setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(sessionCallback)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        createNotificationChannel()

        // Observe audio player state to keep Android Auto session & notification in sync
        serviceScope.launch {
            audioPlayer.audioState.collect { state ->
                updatePlaybackState(state.isPlaying, state.positionMs, state.durationMs)
                state.currentItem?.let { item ->
                    updateMetadata(item)
                    if (state.isPlaying) {
                        startForeground(NOTIFICATION_ID, buildNotification(item, state.isPlaying))
                    } else {
                        stopForeground(STOP_FOREGROUND_DETACH)
                        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        manager.notify(NOTIFICATION_ID, buildNotification(item, isPlaying = false))
                    }
                } ?: run {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }
            }
        }

        // Cache stations for fast steering wheel next/prev seeking
        serviceScope.launch(Dispatchers.IO) {
            repository.allMediaItems.collect { all ->
                cachedStations = all.filter { it.type == MediaType.RADIO_STATION.name }
            }
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // Return root for Android Auto head unit browsing
        return BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        result.detach()

        serviceScope.launch(Dispatchers.IO) {
            val mediaItems = mutableListOf<MediaBrowser.MediaItem>()

            when (parentId) {
                ROOT_ID -> {
                    // Top-level categories visible on Android Auto screen
                    mediaItems.add(
                        buildCategoryItem(
                            id = CATEGORY_RADIO,
                            title = "📻 Live Local Radio Stations",
                            subtitle = "Analog FM & Digital civic stations"
                        )
                    )
                    mediaItems.add(
                        buildCategoryItem(
                            id = CATEGORY_PODCASTS,
                            title = "🎙️ Podcasts & Audio Dispatches",
                            subtitle = "On-demand journalism and deep dives"
                        )
                    )
                    mediaItems.add(
                        buildCategoryItem(
                            id = CATEGORY_BRIEF,
                            title = "🌅 Morning Civic Brief (Audio)",
                            subtitle = "3-minute daily commute recap",
                            isPlayable = true
                        )
                    )
                }

                CATEGORY_RADIO -> {
                    val all = repository.allMediaItems.firstOrNull() ?: emptyList()
                    val stations = all.filter { it.type == MediaType.RADIO_STATION.name }
                    cachedStations = stations
                    stations.forEach { station ->
                        val desc = MediaDescription.Builder()
                            .setMediaId("radio_${station.id}")
                            .setTitle(station.title)
                            .setSubtitle("${station.stationFrequency} • ${station.subtitle}")
                            .setDescription(station.bodyText)
                            .build()
                        mediaItems.add(
                            MediaBrowser.MediaItem(desc, MediaBrowser.MediaItem.FLAG_PLAYABLE)
                        )
                    }
                }

                CATEGORY_PODCASTS -> {
                    val all = repository.allMediaItems.firstOrNull() ?: emptyList()
                    val podcasts = all.filter { it.type == MediaType.PODCAST_EPISODE.name }
                    podcasts.forEach { pod ->
                        val desc = MediaDescription.Builder()
                            .setMediaId("podcast_${pod.id}")
                            .setTitle(pod.title)
                            .setSubtitle("${pod.authorName} • ${pod.channelName}")
                            .setDescription(pod.subtitle)
                            .build()
                        mediaItems.add(
                            MediaBrowser.MediaItem(desc, MediaBrowser.MediaItem.FLAG_PLAYABLE)
                        )
                    }
                }

                else -> {
                    // Empty for unhandled categories
                }
            }

            result.sendResult(mediaItems)
        }
    }

    private fun buildCategoryItem(
        id: String,
        title: String,
        subtitle: String,
        isPlayable: Boolean = false
    ): MediaBrowser.MediaItem {
        val desc = MediaDescription.Builder()
            .setMediaId(id)
            .setTitle(title)
            .setSubtitle(subtitle)
            .build()
        val flag = if (isPlayable) MediaBrowser.MediaItem.FLAG_PLAYABLE else MediaBrowser.MediaItem.FLAG_BROWSABLE
        return MediaBrowser.MediaItem(desc, flag)
    }

    private val sessionCallback = object : MediaSession.Callback() {
        override fun onPlay() {
            audioPlayer.togglePlayPause()
        }

        override fun onPause() {
            audioPlayer.pause()
        }

        override fun onSkipToNext() {
            // Seek to next radio station (ideal for steering wheel controls)
            if (cachedStations.isNotEmpty()) {
                audioPlayer.seekNextStation(cachedStations)
            }
        }

        override fun onSkipToPrevious() {
            // Seek to previous radio station
            if (cachedStations.isNotEmpty()) {
                audioPlayer.seekPreviousStation(cachedStations)
            }
        }

        override fun onSeekTo(pos: Long) {
            audioPlayer.seekTo(pos)
        }

        override fun onStop() {
            audioPlayer.stop()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            if (mediaId == null) return

            serviceScope.launch(Dispatchers.IO) {
                val all = repository.allMediaItems.firstOrNull() ?: emptyList()

                when {
                    mediaId.startsWith("radio_") -> {
                        val id = mediaId.removePrefix("radio_").toLongOrNull()
                        val station = all.firstOrNull { it.id == id }
                        station?.let { audioPlayer.playRadioDirectly(it) }
                    }
                    mediaId.startsWith("podcast_") -> {
                        val id = mediaId.removePrefix("podcast_").toLongOrNull()
                        val pod = all.firstOrNull { it.id == id }
                        pod?.let { audioPlayer.play(it) }
                    }
                    mediaId == CATEGORY_BRIEF -> {
                        val brief = all.firstOrNull { it.tags.contains("morningbrief", ignoreCase = true) }
                            ?: all.firstOrNull { it.type == MediaType.PODCAST_EPISODE.name }
                        brief?.let { audioPlayer.play(it) }
                    }
                }
            }
        }
    }

    private fun updatePlaybackState(isPlaying: Boolean, positionMs: Long, durationMs: Long) {
        val state = if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED
        val actions = PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackState.ACTION_SEEK_TO or
                PlaybackState.ACTION_STOP

        val playbackState = PlaybackState.Builder()
            .setActions(actions)
            .setState(state, positionMs, 1.0f)
            .build()

        mediaSession.setPlaybackState(playbackState)
    }

    private fun updateMetadata(item: MediaItemEntity) {
        val isRadio = item.type == MediaType.RADIO_STATION.name
        val metadata = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, item.id.toString())
            .putString(MediaMetadata.METADATA_KEY_TITLE, item.title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, if (isRadio) item.stationFrequency else item.authorName)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, item.channelName)
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, item.subtitle)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, if (isRadio) 0L else item.durationSeconds * 1000L)
            .build()

        mediaSession.setMetadata(metadata)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Townsquare In-Car Audio & Radio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Android Auto and system playback controls for Townsquare Radio"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(item: MediaItemEntity, isPlaying: Boolean): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isRadio = item.type == MediaType.RADIO_STATION.name
        val titleText = if (isRadio) "${item.stationFrequency} • ${item.title}" else item.title
        val subText = if (isRadio) item.subtitle else "With ${item.authorName} • ${item.channelName}"

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(titleText)
            .setContentText(subText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setStyle(
                Notification.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setOngoing(isPlaying)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaSession.release()
    }
}
