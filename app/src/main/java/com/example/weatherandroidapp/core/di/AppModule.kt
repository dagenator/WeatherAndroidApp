package com.example.weatherandroidapp.core.di

import android.content.Context
import com.example.weatherandroidapp.data.models.ConfigForApi
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides

@Module()
class AppModule(private val context: Context, private val ciceron: Cicerone<Router>) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideWeatherConfig():ConfigForApi {
        return ConfigForApi(
            "13e0b633f5a548ced04584af8d6c7213",
            "metric",
            "d55ba650a4f56631ccee55b698718ea5",
            "ru"
        )
    }

    @Provides
    fun provideRouter(): Router {
        return ciceron.router
    }

    @Provides
    fun provideNavigator(): NavigatorHolder {
        return ciceron.getNavigatorHolder()
    }

}
