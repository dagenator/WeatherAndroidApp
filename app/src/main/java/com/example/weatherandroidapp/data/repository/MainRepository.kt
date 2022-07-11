package com.example.weatherandroidapp.data.repository

import com.example.weatherandroidapp.core.retrofit.UVApi
import com.example.weatherandroidapp.core.retrofit.WeatherMapApi
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.models.ConfigForApi
import com.example.weatherandroidapp.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    val weatherMapApi: WeatherMapApi,
    val config: ConfigForApi,
    val UVApi :UVApi
) {

    fun getCurrentWeather(lat: Double, lon: Double) = flow<Resource<CurrentWeather>> {
        emit(Resource.loading(null))
        try {
            val currentWeather = weatherMapApi.getCurrentWeather(
                lat = lat,
                lon = lon,
                key = config.weatherApiKey,
                units = config.units
            )
            emit(Resource.success(currentWeather))
        } catch (e: Exception) {
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
            emit(Resource.success(uv))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

}