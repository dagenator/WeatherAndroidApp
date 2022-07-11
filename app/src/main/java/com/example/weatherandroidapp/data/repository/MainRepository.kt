package com.example.weatherandroidapp.data.repository

import com.example.weatherandroidapp.core.retrofit.WeatherMapApi
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.WeatherConfig
import com.example.weatherandroidapp.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    val weatherMapApi: WeatherMapApi,
    val weatherConfig: WeatherConfig
) {

    fun getCurrentWeather(lat: Double, lon: Double) = flow<Resource<CurrentWeather>> {
        emit(Resource.loading(null))
        try {
            val currentWeather = weatherMapApi.getCurrentWeather(
                lat = lat,
                lon = lon,
                key = weatherConfig.key,
                units = weatherConfig.units
            )
            emit(Resource.success(currentWeather))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

}