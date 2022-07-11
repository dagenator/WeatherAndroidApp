package com.example.weatherandroidapp.core.di

import android.content.Context
import com.example.weatherandroidapp.data.models.WeatherConfig
import dagger.Module
import dagger.Provides

@Module()
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideWeatherConfig():WeatherConfig {
        return WeatherConfig(
            "13e0b633f5a548ced04584af8d6c7213",
            "metric"
        )
    }

}
