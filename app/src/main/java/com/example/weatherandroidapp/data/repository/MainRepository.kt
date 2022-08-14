package com.example.weatherandroidapp.data.repository

import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.example.weatherandroidapp.core.retrofit.UVApi
import com.example.weatherandroidapp.core.retrofit.WeatherMapApi
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.Result
import com.example.weatherandroidapp.data.models.ConfigForApi
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.utils.Resource
import com.example.weatherandroidapp.utils.SharedPreferencesUtils
import com.example.weatherandroidapp.widget.WeatherWidgetProvider
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    val weatherMapApi: WeatherMapApi,
    val config: ConfigForApi,
    val UVApi :UVApi,
    val sharedPreferencesUtils: SharedPreferencesUtils,
    val context: Context
) {

    fun getCurrentWeather(lat: Double, lon: Double) = flow<Resource<CurrentWeather>> {
        emit(Resource.loading(null))
        try {
            val currentWeather = weatherMapApi.getCurrentWeather(
                lat = lat,
                lon = lon,
                key = config.weatherApiKey,
                units = config.units,
                lang = config.language
            )
            sharedPreferencesUtils.saveWeatherInfo(currentWeather)
            emit(Resource.success(currentWeather))
        } catch (e: Exception) {
            sharedPreferencesUtils.saveWeatherError(e.message.toString())
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun getUVInfo(lat:Double, lon:Double) = flow<Resource<UVInfo>> {
        emit(Resource.loading(null))
        try {
            val uv = UVApi.getCurrentUV(
                lat = lat,
                lon = lon,
                key = config.uvApiKey
            )
            sharedPreferencesUtils.saveUVInfo(uv)
            emit(Resource.success(uv))
        } catch (e: Exception) {
            sharedPreferencesUtils.saveUVError(e.message.toString())
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun updateWidgets(){
        val widgetsIds = sharedPreferencesUtils.getListOfWidgetsId()
        if(widgetsIds.isEmpty()) return

        widgetsIds.forEach {
            context.sendBroadcast(WeatherWidgetProvider.getUpdateWidgetIntent(context, it))
        }
    }
}