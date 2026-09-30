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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * TCTV Station Clock Home Screen AppWidget.
 * Displays real-time synchronized station time, date, and live broadcast ident.
 */
class TownsquareClockWidgetProvider : AppWidgetProvider() {

    companion object {
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, TownsquareClockWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (widgetIds.isEmpty()) return

            for (widgetId in widgetIds) {
                val views = buildRemoteViews(context)
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }

        private fun buildRemoteViews(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_clock_tctv)

            val now = LocalDateTime.now()
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")

            views.setTextViewText(R.id.widget_clock_time, now.format(timeFormatter))
            views.setTextViewText(R.id.widget_clock_date, now.format(dateFormatter))

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Click entire widget to launch MainActivity directly to TV Streaming / Schedule
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("EXTRA_NAV_TAB", 3) // TV Streaming
            }
            val openAppPendingIntent = PendingIntent.getActivity(context, 401, openAppIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_clock_root, openAppPendingIntent)

            return views
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val views = buildRemoteViews(context)
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
