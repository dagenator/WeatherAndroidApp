package com.example.weatherandroidapp.presenter

import com.example.weatherandroidapp.data.models.DisplayUVInfo
import com.example.weatherandroidapp.data.models.DisplayWeatherInfo

data class WeatherState(
    val weather: DisplayWeatherInfo? = null,
    val uv: DisplayUVInfo? = null,
    val city: String? = null,
    var errorMessage: String? = null
)