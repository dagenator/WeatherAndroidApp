package com.example.weatherandroidapp.core.app

import android.app.Application
import com.example.weatherandroidapp.core.di.AppComponent
import com.example.weatherandroidapp.core.di.AppModule
import com.example.weatherandroidapp.core.di.DaggerAppComponent
import com.github.terrakok.cicerone.Cicerone

class App : Application() {

    lateinit var appComponent: AppComponent

    private val cicerone = Cicerone.create()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this, cicerone))
            .build()
    }

    companion object {
        internal lateinit var INSTANCE: App
            private set
    }
}