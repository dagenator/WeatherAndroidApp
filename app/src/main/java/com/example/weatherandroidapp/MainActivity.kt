package com.example.weatherandroidapp

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.utils.Resource
import com.example.weatherandroidapp.widget.WeatherWidgetProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val locationResultLiveData: MutableLiveData<Resource<Location>> = MutableLiveData(null)

    private val locationResultObserver = Observer<Resource<Location>> {
        it?.let {
            val weatherIntent = Intent(this, WeatherActivity::class.java)
            weatherIntent.putExtra("STATUS", it.status.toString())
            weatherIntent.putExtra(
                "LOCATION_RESULT",
                if (it.data == null) doubleArrayOf(0.0, 0.0) else doubleArrayOf(
                    it.data.latitude,
                    it.data.longitude
                )
            )
            weatherIntent.putExtra("ERROR_MESSAGE", it.message)

            if (intent.action == WeatherWidgetProvider.WEATHER_UPDATE_ACTION) {
                weatherIntent.putExtra(
                    "IS_WITHOUT_UI", true
                )
            }
            startActivity(weatherIntent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        (applicationContext as App).appComponent.inject(this)

        locationResultLiveData.observe(this, locationResultObserver)

        getLastLocationWithPermissionCheck()

        if (intent.action == WeatherWidgetProvider.WEATHER_UPDATE_ACTION) {
            getLastLocationWithPermissionCheck(true)
            finish()
        }

        setContentView(R.layout.activity_main)
    }

    fun getLastLocationWithPermissionCheck(askPermisson: Boolean = false) {
        try {
            var location = getLastLocation()
            if (location == null) {
                if (askPermisson)
                    throw Exception("Разрешения не были даны")
                askForLocalPermission()
            } else {
                location.addOnCompleteListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        locationResultLiveData.postValue(Resource.success(it.result))
                    }
                }
            }
        } catch (e: Exception) {
            locationResultLiveData.value = Resource.error(null, e.message.toString())
        }
    }

    private fun checkAskingResult(isGranted: Boolean) {
        if (!isGranted) {
            locationResultLiveData.value = Resource.error(
                null,
                "Не можем получить местоположение, разрешения не были даны"
            )
        } else {
            val location = getLastLocation()
            if (location == null)
                locationResultLiveData.value = Resource.error(
                    null,
                    "Произошла ошибка получения местоположения. Может вы не дали все необходимые разрешения?"
                )
            else {
                location.addOnCompleteListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        locationResultLiveData.postValue(Resource.success(it.result))
                    }
                }
            }
        }
    }

    private fun askForLocalPermission() {
        val requestPermissionLauncher =
            this.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted: Map<String, Boolean> ->
                checkAskingResult(!isGranted.values.contains(false))
            }


        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLastLocation(): Task<Location>? {
        if (
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return mFusedLocationClient.lastLocation
        }
        return null
    }

    companion object {

        fun getUpdateWeatherInfoPendingIntent(
            context: Context,
            action: String,
            appWidgetId: Int
        ): PendingIntent {
            val intent = Intent(context, WeatherWidgetProvider::class.java)
            intent.action = action
            intent.putExtra("id", appWidgetId)

            return PendingIntent.getBroadcast(
                context, appWidgetId, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

}