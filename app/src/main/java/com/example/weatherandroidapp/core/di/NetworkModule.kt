package com.example.weatherandroidapp.core.di

import com.example.weatherandroidapp.core.retrofit.WeatherMapApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class NetworkModule {

    //@Singleton
    @Provides
    fun getRetrofit(): WeatherMapApi {
        val BASE_URL = "https://api.openweathermap.org/"
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherMapApi::class.java)
    }

}