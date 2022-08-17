package com.example.weatherandroidapp

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.getLastLocation
import com.example.weatherandroidapp.widget.WeatherWidgetProvider
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class UpdateWeatherService(
) : Service() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onCreate() {
        Log.i(TAG, "onCreate: Service created")
        (applicationContext as App).appComponent.inject(this)
    }

    override fun onStart(intent: Intent?, startid: Int) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show()

        val locationTask = getLastLocation(context, fusedLocationProviderClient)
        val id = intent?.let { intent -> intent.extras?.get("id") }.toString().toInt()

        locationTask?.let {
            it.addOnCompleteListener {
                CoroutineScope(Dispatchers.IO).launch {
                    mainRepository.getCurrentWeather(
                        lon = it.result.longitude,
                        lat = it.result.latitude
                    )
                    mainRepository.getUVInfo(lon = it.result.longitude, lat = it.result.latitude)

                }.invokeOnCompletion {
                    Log.i(TAG, "service done with no errors")
                    context.sendBroadcast(
                        WeatherWidgetProvider.getUpdateWidgetIntentWithId(
                            context,
                            id
                        )
                    )
                }
            }
        }

        if(locationTask == null){
            mainRepository.setCommonErrorInPreferences("Разрешения не были даны")
        }
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {

        fun getStartUpdateWeatherServiceIntent(
            context: Context,
            action: String,
            appWidgetId: Int
        ): PendingIntent {
            val intent = Intent(context, UpdateWeatherService::class.java)
            intent.action = action
            intent.putExtra("id", appWidgetId)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "service intent sdk > o")
                PendingIntent.getService(context, 0, intent, FLAG_MUTABLE)
            } else {
                Log.i(TAG, "service intent sdk < o")
                TODO("VERSION.SDK_INT < O")
            }
        }
    }
}