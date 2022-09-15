package com.example.weatherandroidapp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.UpdateWeatherService
import com.example.weatherandroidapp.core.di.AppModule
import com.example.weatherandroidapp.utils.SharedPreferencesUtils
import com.example.weatherandroidapp.utils.WeatherStateUtil


class WeatherWidgetProvider :
    AppWidgetProvider() {

    val LOG_TAG = "myLogs"
    val weatherStateUtil = WeatherStateUtil()

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d(LOG_TAG, intent?.action.toString())
        intent?.let {
            if(it.action.toString() == WIDGET_UPDATE){
                val id = it.extras?.getInt("id")
                id?.let {
                    val widgetManager = AppWidgetManager.getInstance(context)
                    context?.let {
                        updateAppWidget(context, widgetManager, id)
                    }
                }
            }

        }
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

        Log.i(LOG_TAG, "updateAppWidget: update start ")
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

        val weatherError = sharedPreferencesUtils.getWeatherError()
        val UVError = sharedPreferencesUtils.getUVError()

        if (weatherError == null && UVError == null) {
            val weather = sharedPreferencesUtils.getWeatherInfo()
            val uv = sharedPreferencesUtils.getUVInfo()
            val city = sharedPreferencesUtils.getCity()

            views.setOnClickPendingIntent(
                R.id.widget_reload_button,
                UpdateWeatherService.getStartUpdateWeatherServicePendingIntent(
                    context,
                    WEATHER_UPDATE_ACTION,
                    appWidgetId
                )
            )
            views.setViewVisibility(R.id.whole_content_layout, View.VISIBLE)
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
                views.setTextViewText(R.id.widget_city, it)
            }

            var images = weatherStateUtil.getImageStateSet(weather.weatherId)

            views.setImageViewResource(R.id.weather_widget_icon,  images.icon)
            Log.i(TAG, "updateAppWidget: ${ images.icon}")
            Log.i(TAG, "updateAppWidget: ${R.drawable.ic_clear_sky_icon}")

        } else {
            weatherError?.let {weatherError->
                views.setTextViewText(R.id.weather_error, weatherError)
                views.setViewVisibility(R.id.weather_error, View.VISIBLE)

                UVError?.let { uvError->
                    if(weatherError != uvError){
                        views.setTextViewText(R.id.uv_error, uvError)
                        views.setViewVisibility(R.id.uv_error, View.VISIBLE)
                    }
                }
            }

            views.setViewVisibility(R.id.whole_content_layout, View.GONE)
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        const val WEATHER_UPDATE_ACTION = "WEATHER_UPDATE_ACTION"
        const val WIDGET_UPDATE = "APPWIDGET_UPDATE"

        fun getUpdateWidgetIntentWithId(
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