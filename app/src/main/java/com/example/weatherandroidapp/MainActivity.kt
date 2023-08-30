package com.example.weatherandroidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.utils.LocationUtils
import com.example.weatherandroidapp.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var locationUtils: LocationUtils

    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val locationResultLiveData: MutableLiveData<Resource<Location>> = MutableLiveData(null)

    private val locationResultObserver = Observer<Resource<Location?>?> {
        it?.let {
            val weatherIntent = WeatherActivity.getWeatherActivityIntent(
                this, it.message
            )
            weatherIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(weatherIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as App).appComponent.inject(this)

        locationResultLiveData.observe(this, locationResultObserver)
        getLastLocationWithPermissionCheck()

        setContent {
            SetView()
        }

    }

    @Preview
    @Composable
    fun SetView() {
        MaterialTheme {
            Box(modifier = with(Modifier) {
                fillMaxSize().paint(
                    // Replace with your image id
                    painterResource(id = R.drawable.night_sky),
                    contentScale = ContentScale.FillBounds
                )

            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_heavy_rain_icon),
                    contentDescription = "heavy rain icon",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(150.dp)
                )

                Text(
                    text = resources.getString(R.string.api_sites),
                    modifier = Modifier
                        .height(150.dp)
                        .wrapContentHeight(Alignment.Bottom)
                        .align(Alignment.BottomStart)

                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    locationUtils.getLastLocation(applicationContext, mFusedLocationClient)
                        ?.let { task ->
                            task.addOnCompleteListener {
                                locationResultLiveData.value = Resource.success(it.result)
                            }
                        }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Sorry app can't work without permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun getLastLocationWithPermissionCheck() {
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                locationUtils.getLastLocation(applicationContext, mFusedLocationClient)
                    ?.let { task ->
                        task.addOnCompleteListener {
                            locationResultLiveData.value = Resource.success(it.result)
                        }
                    }

            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            locationResultLiveData.value = Resource.error(null, e.message.toString())
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}