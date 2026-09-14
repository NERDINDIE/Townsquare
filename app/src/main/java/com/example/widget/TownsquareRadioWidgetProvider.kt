package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.audio.AudioPlayerManager
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Townsquare Radio Tuner Home Screen AppWidget.
 * Allows quick seeking and playback of local FM broadcast radio directly from the Android Home Screen.
 */
class TownsquareRadioWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.widget.ACTION_RADIO_PLAY_PAUSE"
        const val ACTION_SEEK_PREV = "com.example.widget.ACTION_RADIO_SEEK_PREV"
        const val ACTION_SEEK_NEXT = "com.example.widget.ACTION_RADIO_SEEK_NEXT"

        fun updateAllWidgets(
            context: Context,
            stationTitle: String? = null,
            frequency: String? = null,
            isPlaying: Boolean = false
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, TownsquareRadioWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (widgetIds.isEmpty()) return

            for (widgetId in widgetIds) {
                val views = buildRemoteViews(context, stationTitle, frequency, isPlaying)
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }

        private fun buildRemoteViews(
            context: Context,
            stationTitle: String?,
            frequency: String?,
            isPlaying: Boolean
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_radio_tuner)

            val displayTitle = stationTitle ?: "K-Pulse 98.5 FM"
            val displayFreq = frequency ?: "98.5 MHz FM"

            views.setTextViewText(R.id.widget_radio_title, displayTitle)
            views.setTextViewText(R.id.widget_radio_frequency, displayFreq)

            if (isPlaying) {
                views.setTextViewText(R.id.widget_radio_status, "▶ ON AIR • STREAMING")
                views.setImageViewResource(R.id.widget_btn_play_pause, R.drawable.ic_widget_pause)
            } else {
                views.setTextViewText(R.id.widget_radio_status, "● READY TO TUNE")
                views.setImageViewResource(R.id.widget_btn_play_pause, R.drawable.ic_widget_play)
            }

            // Flags for PendingIntents
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Click entire widget to launch MainActivity to Radio Hub
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("EXTRA_NAV_TAB", 3) // Radio / Audio Tab
            }
            val openAppPendingIntent = PendingIntent.getActivity(context, 101, openAppIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_radio_root, openAppPendingIntent)

            // Seek Prev Intent
            val prevIntent = Intent(context, TownsquareRadioWidgetProvider::class.java).apply {
                action = ACTION_SEEK_PREV
            }
            val prevPendingIntent = PendingIntent.getBroadcast(context, 102, prevIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_btn_prev, prevPendingIntent)

            // Play / Pause Intent
            val playPauseIntent = Intent(context, TownsquareRadioWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(context, 103, playPauseIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_btn_play_pause, playPausePendingIntent)

            // Seek Next Intent
            val nextIntent = Intent(context, TownsquareRadioWidgetProvider::class.java).apply {
                action = ACTION_SEEK_NEXT
            }
            val nextPendingIntent = PendingIntent.getBroadcast(context, 104, nextIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_btn_next, nextPendingIntent)

            return views
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val player = AudioPlayerManager.getInstance(context)
        val state = player.audioState.value
        val title = state.currentItem?.title ?: "K-Pulse 98.5 FM"
        val freq = state.currentItem?.stationFrequency.takeIf { !it.isNullOrBlank() } ?: "98.5 MHz FM"

        for (appWidgetId in appWidgetIds) {
            val views = buildRemoteViews(context, title, freq, state.isPlaying)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return

        val player = AudioPlayerManager.getInstance(context)

        when (action) {
            ACTION_PLAY_PAUSE -> {
                player.togglePlayPause()
                val state = player.audioState.value
                val title = state.currentItem?.title ?: "K-Pulse 98.5 FM"
                val freq = state.currentItem?.stationFrequency.takeIf { !it.isNullOrBlank() } ?: "98.5 MHz FM"
                updateAllWidgets(context, title, freq, state.isPlaying)
            }
            ACTION_SEEK_NEXT, ACTION_SEEK_PREV -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val stations = db.mediaDao().getMediaItemsListByType("RADIO_STATION")
                    if (stations.isNotEmpty()) {
                        if (action == ACTION_SEEK_NEXT) {
                            player.seekNextStation(stations)
                        } else {
                            player.seekPreviousStation(stations)
                        }
                        val state = player.audioState.value
                        val title = state.currentItem?.title
                        val freq = state.currentItem?.stationFrequency
                        updateAllWidgets(context, title, freq, state.isPlaying)
                    }
                }
            }
        }
    }
}
