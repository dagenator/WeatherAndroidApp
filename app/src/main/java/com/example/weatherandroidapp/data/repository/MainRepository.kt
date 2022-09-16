package com.example.weatherandroidapp.data.repository

import android.content.Context
import android.util.Log
import com.example.weatherandroidapp.core.retrofit.UVApi
import com.example.weatherandroidapp.core.retrofit.WeatherMapApi
import com.example.weatherandroidapp.data.models.ConfigForApi
import com.example.weatherandroidapp.data.models.DisplayUVInfo
import com.example.weatherandroidapp.data.models.DisplayWeatherInfo
import com.example.weatherandroidapp.utils.MemoryUpdateState
import com.example.weatherandroidapp.utils.SharedPreferencesUtils
import com.example.weatherandroidapp.widget.WeatherWidgetProvider
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    val weatherMapApi: WeatherMapApi,
    val config: ConfigForApi,
    val UVApi: UVApi,
    val sharedPreferencesUtils: SharedPreferencesUtils,
    val context: Context
) {

    fun updateCurrentWeather(lat: Double, lon: Double) = flow<MemoryUpdateState> {
        emit(MemoryUpdateState.loading())
        try {
            val currentWeather = weatherMapApi.getCurrentWeather(
                lat = lat,
                lon = lon,
                key = config.weatherApiKey,
                units = config.units,
                lang = config.language
            )
            sharedPreferencesUtils.saveWeatherInfo(currentWeather)
            emit(MemoryUpdateState.success())
        } catch (e: Exception) {
            sharedPreferencesUtils.saveWeatherError(e.message.toString())
            emit(MemoryUpdateState.error(e.message.toString()))
        }
    }

    fun updateUVInfo(lat: Double, lon: Double) = flow<MemoryUpdateState> {

        emit(MemoryUpdateState.loading())
        try {
            val uv = UVApi.getCurrentUV(
                lat = lat,
                lon = lon,
                key = config.uvApiKey
            )
            sharedPreferencesUtils.saveUVInfo(uv)
            emit(MemoryUpdateState.success())
        } catch (e: Exception) {
            sharedPreferencesUtils.saveUVError(e.message.toString())
            emit(MemoryUpdateState.error(e.message.toString()))
        }

    }

    fun updateWidgets() {
        Log.i("Main Repository", "updateWidgets: widgets update")
        val widgetsIds = sharedPreferencesUtils.getListOfWidgetsId()
        if (widgetsIds.isEmpty()) return

        widgetsIds.forEach {
            context.sendBroadcast(WeatherWidgetProvider.getUpdateWidgetIntentWithId(context, it))
        }
    }

    fun getWeatherFromPreferences(): DisplayWeatherInfo {
        val res = sharedPreferencesUtils.getWeatherInfo()
        Log.i("CALL_TIME__________", "getWeatherFromPreferences: ${res.callTime}")
        return res
    }

    fun getUVFromPreferences(): DisplayUVInfo {
        return sharedPreferencesUtils.getUVInfo()
    }

    fun getCity(): String? {
        return sharedPreferencesUtils.getCity()
    }
}