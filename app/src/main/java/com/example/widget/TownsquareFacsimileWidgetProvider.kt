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
import com.example.data.model.FacsimileBroadsheetRepository
import java.util.Calendar

class TownsquareFacsimileWidgetProvider : AppWidgetProvider() {

    companion object {
        fun updateAllWidgets(
            context: Context,
            headline: String? = null,
            bulletin1: String? = null,
            bulletin2: String? = null,
            timestamp: String? = null
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, TownsquareFacsimileWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (widgetIds.isEmpty()) return

            for (widgetId in widgetIds) {
                val views = buildRemoteViews(context, headline, bulletin1, bulletin2, timestamp)
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }

        private fun buildRemoteViews(
            context: Context,
            headline: String?,
            bulletin1: String?,
            bulletin2: String?,
            timestamp: String?
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_facsimile_broadsheet)

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val edition = FacsimileBroadsheetRepository.getEditionForHour(currentHour)

            val displayHeadline = headline ?: edition.headline
            val displayTimestamp = timestamp ?: "EDITION #${String.format("%02d", currentHour)} • ${String.format("%02d:00 HRS", currentHour)}"
            val displayB1 = bulletin1 ?: edition.items.getOrNull(0)?.let { "• ${it.timeTag} ${it.content}" }
                ?: "• 08:08 Port harbor gates clear for deep-draft transit"
            val displayB2 = bulletin2 ?: edition.items.getOrNull(1)?.let { "• ${it.timeTag} ${it.content}" }
                ?: "• 08:24 City Hall tables protected bike network vote"

            views.setTextViewText(R.id.widget_fax_headline, displayHeadline)
            views.setTextViewText(R.id.widget_fax_timestamp, displayTimestamp)
            views.setTextViewText(R.id.widget_fax_bulletin_1, displayB1)
            views.setTextViewText(R.id.widget_fax_bulletin_2, displayB2)
            views.setTextViewText(R.id.widget_fax_carrier, edition.carrierSignalKhz)

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            // Click entire widget to launch MainActivity with facsimile broadsheet open
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("EXTRA_NAV_TAB", 0)
                putExtra("EXTRA_OPEN_FACSIMILE", true)
            }
            val openAppPendingIntent = PendingIntent.getActivity(context, 301, openAppIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_fax_root, openAppPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_fax_btn_read, openAppPendingIntent)

            return views
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val edition = FacsimileBroadsheetRepository.getEditionForHour(currentHour)
        for (appWidgetId in appWidgetIds) {
            val views = buildRemoteViews(
                context,
                edition.headline,
                edition.items.getOrNull(0)?.let { "• ${it.timeTag} ${it.content}" },
                edition.items.getOrNull(1)?.let { "• ${it.timeTag} ${it.content}" },
                "EDITION #${String.format("%02d", currentHour)} • ${String.format("%02d:00 HRS", currentHour)}"
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
