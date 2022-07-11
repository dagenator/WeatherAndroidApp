package com.example.weatherandroidapp.core.app

import android.app.Application
import com.example.weatherandroidapp.core.di.AppComponent
import com.example.weatherandroidapp.core.di.AppModule
import com.example.weatherandroidapp.core.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this))
            .build()
    }
}