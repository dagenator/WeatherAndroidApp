package com.example.weatherandroidapp

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.utils.Resource
import com.example.weatherandroidapp.utils.getLastLocation
import com.example.weatherandroidapp.widget.WeatherWidgetProvider
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
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
            startActivity(weatherIntent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App).appComponent.inject(this)

        locationResultLiveData.observe(this, locationResultObserver)
        getLastLocationWithPermissionCheck()

        setContentView(R.layout.activity_main)
    }

    fun getLastLocationWithPermissionCheck(askPermisson: Boolean = false) {
        try {
            val location = getLastLocation(this, mFusedLocationClient)
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
            val location = getLastLocation(this, mFusedLocationClient)
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
}