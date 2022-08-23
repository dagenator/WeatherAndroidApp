package com.example.weatherandroidapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.weatherandroidapp.MainActivity
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.core.di.AppModule
import com.example.weatherandroidapp.utils.SharedPreferencesUtils
import com.example.weatherandroidapp.utils.WeatherStateUtil


class WeatherWidgetProvider :
    AppWidgetProvider() {

    val LOG_TAG = "myLogs"

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d(LOG_TAG, intent?.action.toString())
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

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        context?.let {
            val sharedPreferencesUtils = SharedPreferencesUtils(
                context.getSharedPreferences(
                    AppModule.PREFERENCES,
                    Context.MODE_PRIVATE
                ), WeatherStateUtil()
            )

            appWidgetIds?.let {
                it.forEach { id ->
                    sharedPreferencesUtils.deleteWidgetId(id)
                }
            }
        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val sharedPreferencesUtils = SharedPreferencesUtils(
            context.getSharedPreferences(
                AppModule.PREFERENCES,
                Context.MODE_PRIVATE
            ), WeatherStateUtil()
        )

        sharedPreferencesUtils.saveNewWidgetId(appWidgetId)

        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.weather_widget
        )

        var error: String = " "
        val weatherError = sharedPreferencesUtils.getWeatherError()
        val UVError = sharedPreferencesUtils.getUVError()

        if (weatherError == null && UVError == null) {
            val weather = sharedPreferencesUtils.getWeatherInfo()
            val uv = sharedPreferencesUtils.getUVInfo()
            val city = sharedPreferencesUtils.getCity()

            views.setOnClickPendingIntent(
                R.id.widget_reload_button,
                MainActivity.getUpdateWeatherInfoPendingIntent(
                    context,
                    WEATHER_UPDATE_ACTION,
                    appWidgetId
                )
            )
            views.setTextViewText(
                R.id.widget_current_degree,
                String.format("%s°C", weather.currentDegree.toInt())
            )
            views.setTextViewText(
                R.id.widget_max_degree,
                String.format("%s°C", weather.maxDegree.toInt())
            )
            views.setTextViewText(
                R.id.widget_min_degree,
                String.format("%s°C", weather.minDegree.toInt())
            )
            views.setTextViewText(R.id.widget_wind, String.format("%.1f м/с", weather.wind))
            views.setTextViewText(R.id.widget_current_UV, String.format("%.1f", uv.currentUV))
            views.setTextViewText(R.id.widget_max_UV, String.format("%.1f", uv.maxUV))

            city.let {
                views.setTextViewText(R.id.city, it)
            }

            views.setImageViewResource(R.id.weather_widget_icon, weather.iconId)

        } else {
            weatherError?.let {
                error = it
            }
            UVError?.let {
                error = it
            }

            views.setViewVisibility(R.id.widget_max_UV, View.GONE)
            views.setViewVisibility(R.id.widget_max_degree, View.GONE)
            views.setViewVisibility(R.id.widget_min_degree, View.GONE)
            views.setViewVisibility(R.id.widget_wind, View.GONE)
            views.setViewVisibility(R.id.widget_current_UV, View.GONE)
            views.setTextViewText(R.id.widget_current_degree, error)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)

    }





    companion object {
        val WEATHER_UPDATE_ACTION = "WEATHER_UPDATE_ACTION"

        val WIDGET_UPDATE = "APPWIDGET_UPDATE"


        fun getUpdateWidgetIntent(
            context: Context,
            appWidgetId: Int
        ): Intent {
            val intent = Intent(context, WeatherWidgetProvider::class.java)
            intent.action = WIDGET_UPDATE
            intent.putExtra("id", appWidgetId)

            return intent
        }
    }


}