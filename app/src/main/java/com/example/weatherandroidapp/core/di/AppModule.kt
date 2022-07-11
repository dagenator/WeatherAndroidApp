package com.example.weatherandroidapp.core.di

import android.content.Context
import com.example.weatherandroidapp.data.models.ConfigForApi
import dagger.Module
import dagger.Provides

@Module()
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideWeatherConfig():ConfigForApi {
        return ConfigForApi(
            "13e0b633f5a548ced04584af8d6c7213",
            "metric",
            "24009c857de07fa89174b98430fee065"
        )
    }

}
