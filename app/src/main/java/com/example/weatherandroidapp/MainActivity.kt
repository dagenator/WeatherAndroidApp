package com.example.weatherandroidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val isPermissionGet: MutableLiveData<Boolean> = MutableLiveData(false)

    private val locationResultLiveData: MutableLiveData<Resource<Location>> = MutableLiveData(null)

    private val locationResultObserver = Observer<Resource<Location>> {
        it?.let {
            val intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("STATUS", it.status.toString())
            intent.putExtra(
                "LOCATION_RESULT",
                if (it.data == null) doubleArrayOf(0.0, 0.0) else doubleArrayOf(it.data.latitude, it.data.longitude)
            )
            intent.putExtra("ERROR_MESSAGE", it.message)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        (applicationContext as App).appComponent.inject(this)

        locationResultLiveData.observe(this, locationResultObserver)
        getLastLocationWithPermissionCheck()

    }

    fun getLastLocationWithPermissionCheck() {
        //TODO refactoring

        var isGranted: Boolean = false
        val observer = Observer<Boolean> { isGranted = it }

        try {
            var location = getLastLocation()
            if (location == null) {
                isPermissionGet.observeForever { observer }
                askForLocalPermission()
                isPermissionGet.removeObserver { observer }

                if (!isGranted) {
                    locationResultLiveData.value = Resource.error(
                        null,
                        "Не можем получить местоположение, разрешения не были даны"
                    )
                } else {
                    location = getLastLocation()
                    if (location == null)
                        locationResultLiveData.value = Resource.error(
                            null,
                            "Произошла ошибка получения местоположения. Может вы не дали все необходимые разрешения?"
                        )
                    else {
                        location.addOnCompleteListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                locationResultLiveData.value = Resource.success(it.result)
                            }
                        }
                    }
                }
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

    private fun askForLocalPermission() {
        val requestPermissionLauncher =
            this.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted: Map<String, Boolean> ->
                isPermissionGet.value = isGranted.values.contains(false)
            }


        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLastLocation(): Task<Location>? {
        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this.applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            -> {
                return mFusedLocationClient.lastLocation
            }
        }
        return null
    }

}