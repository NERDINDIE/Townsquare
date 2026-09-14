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
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Townsquare Morning Dispatch Home Screen AppWidget.
 * Displays today's leading civic headlines and quick one-tap access to audio briefings.
 */
class TownsquareBriefWidgetProvider : AppWidgetProvider() {

    companion object {
        fun updateAllWidgets(
            context: Context,
            headline: String? = null,
            summary: String? = null,
            timeDate: String? = null
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, TownsquareBriefWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (widgetIds.isEmpty()) return

            for (widgetId in widgetIds) {
                val views = buildRemoteViews(context, headline, summary, timeDate)
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }

        private fun buildRemoteViews(
            context: Context,
            headline: String?,
            summary: String?,
            timeDate: String?
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_morning_brief)

            val displayHeadline = headline ?: "Autonomous Electric Tramways Open Connecting Historic Civic Loop"
            val displaySummary = summary ?: "Quiet, zero-emission transit links the Arts District with downtown markets starting this morning."
            val displayTime = timeDate ?: "TODAY • 7:00 AM"

            views.setTextViewText(R.id.widget_brief_headline, displayHeadline)
            views.setTextViewText(R.id.widget_brief_summary, displaySummary)
            views.setTextViewText(R.id.widget_brief_time, displayTime)

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Click entire widget to launch MainActivity to Feed
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("EXTRA_NAV_TAB", 0)
                putExtra("EXTRA_OPEN_BRIEF", true)
            }
            val openAppPendingIntent = PendingIntent.getActivity(context, 201, openAppIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_brief_root, openAppPendingIntent)

            // Read Dispatch Button Intent
            val readIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("EXTRA_NAV_TAB", 0)
                putExtra("EXTRA_OPEN_BRIEF", true)
            }
            val readPendingIntent = PendingIntent.getActivity(context, 202, readIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_btn_read, readPendingIntent)

            // Audio Brief Button Intent
            val audioIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("EXTRA_NAV_TAB", 0)
                putExtra("EXTRA_PLAY_BRIEF", true)
            }
            val audioPendingIntent = PendingIntent.getActivity(context, 203, audioIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_btn_listen, audioPendingIntent)

            return views
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(context)
            val latestJournal = db.mediaDao().getAllJournalEditions().firstOrNull()?.firstOrNull()
            val headline = latestJournal?.leadHeadline
                ?: "Clean Transit Tramway Loops Connect Historic Districts"
            val summary = latestJournal?.leadSubheadline?.takeIf { it.isNotBlank() }
                ?: latestJournal?.secondaryHeadline?.takeIf { it.isNotBlank() }
                ?: "Daily civic journalism, live broadcast telemetry, and community newsroom dispatches."

            for (appWidgetId in appWidgetIds) {
                val views = buildRemoteViews(context, headline, summary, "TODAY • 7:00 AM")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
