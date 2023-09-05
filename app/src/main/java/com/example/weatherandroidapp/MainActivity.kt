package com.example.weatherandroidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.data.models.WeatherDescriptionItemBindOneInRow
import com.example.weatherandroidapp.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val locationResultLiveData: MutableLiveData<Resource<Location>> = MutableLiveData(null)

    private val locationResultObserver = Observer<Resource<Location?>?> {
        it?.let {
            val intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("STATUS", it.status.toString())
            intent.putExtra(
                "LOCATION_RESULT", if (it.data == null) doubleArrayOf(0.0, 0.0) else doubleArrayOf(
                    it.data.latitude, it.data.longitude
                )
            )
            intent.putExtra("ERROR_MESSAGE", it.message)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("TEST_TAG", "onCreate: MainActivity")
        setContent {
            SetUi()
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        (applicationContext as App).appComponent.inject(this)

        locationResultLiveData.observe(this, locationResultObserver)
        getLastLocationWithPermissionCheck()

    }

    @Preview
    @Composable
    fun SetUi() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Image(
                modifier = Modifier
                    .width(200.dp)
                    .height(400.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_heavy_rain_icon),
                contentDescription = "background",
                colorFilter = ColorFilter.tint(Color(R.color.white))
            )
        }
    }

    fun getLastLocationWithPermissionCheck() {
        try {
            val location = getLastLocation()
            if (location == null) {
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
                null, "Не можем получить местоположение, разрешения не были даны"
            )
        } else {
            val location = getLastLocation()
            if (location == null) locationResultLiveData.value = Resource.error(
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
        val requestPermissionLauncher = this.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted: Map<String, Boolean> ->
            checkAskingResult(!isGranted.values.contains(false))
        }


        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLastLocation(): Task<Location>? {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return mFusedLocationClient.lastLocation
        }
        return null
    }

}