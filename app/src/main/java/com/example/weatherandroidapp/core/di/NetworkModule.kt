package com.example.weatherandroidapp.core.di

import com.example.weatherandroidapp.core.retrofit.UVApi
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
    fun provideWeatherRetrofit(): WeatherMapApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherMapApi::class.java)
    }

    @Provides
    fun provideUVRetrofit(): UVApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openuv.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UVApi::class.java)
    }

}