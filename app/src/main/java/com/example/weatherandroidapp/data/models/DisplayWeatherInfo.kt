package com.example.weatherandroidapp.data.models

data class DisplayWeatherInfo(
    val weatherId: Int,
    val currentDegree: Float,
    val maxDegree: Float,
    val minDegree: Float,
    val feelDegree:Float,
    val wind: Float,
    val cloudiness: Float,
    val description:String?,
    val error:String?,
    var callTime:String?
)
