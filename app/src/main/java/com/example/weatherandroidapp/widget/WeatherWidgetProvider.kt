package com.example.weatherandroidapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager

import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.weatherandroidapp.R
import java.util.*


class WeatherWidgetProvider  : AppWidgetProvider() {

    private val WEATHER_UPDATE_ACTION = "WEATHER_UPDATE_ACTION"

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val intent = Intent(context, WeatherWidgetProvider::class.java)
        intent.action = WEATHER_UPDATE_ACTION
        intent.putExtra("id", appWidgetId)


        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Get the layout for the widget and attach an on-click listener
        // to the button.
        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.weather_widget
        ).apply {
            setOnClickPendingIntent(  R.id.weather_reload_button, pendingIntent)
        }
        views.setTextViewText(R.id.widgetText, Integer.toString(Info.counter))

        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


}