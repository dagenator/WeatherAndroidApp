package com.example.weatherandroidapp.core.di

import com.example.weatherandroidapp.MainActivity
import com.example.weatherandroidapp.WeatherActivity
import dagger.Component

@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(weatherActivity: WeatherActivity)
}