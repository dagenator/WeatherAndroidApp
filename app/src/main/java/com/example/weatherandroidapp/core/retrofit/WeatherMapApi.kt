package com.example.weatherandroidapp.core.retrofit

import com.example.weatherandroidapp.data.models.CurrentWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherMapApi {
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): CurrentWeather
}