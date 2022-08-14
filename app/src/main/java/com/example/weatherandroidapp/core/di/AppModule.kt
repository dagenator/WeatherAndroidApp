package com.example.weatherandroidapp.core.di

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherandroidapp.data.models.ConfigForApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module()
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideWeatherConfig(): ConfigForApi {
        return ConfigForApi(
            "13e0b633f5a548ced04584af8d6c7213",
            "metric",
            "d55ba650a4f56631ccee55b698718ea5",
            "ru"
        )
    }

    @Provides
    fun provideSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    companion object {
        val PREFERENCES = "WEATHER_PREFERENCES"
    }
}
