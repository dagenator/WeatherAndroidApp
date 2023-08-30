package com.example.weatherandroidapp

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.DisplayUVInfo
import com.example.weatherandroidapp.data.models.DisplayWeatherInfo
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.data.models.WeatherDescriptionItemBindOneInRow
import com.example.weatherandroidapp.data.models.WeatherDescriptionItemBindTwoInRow
import com.example.weatherandroidapp.databinding.ActivityWeatherBinding
import com.example.weatherandroidapp.presenter.MainViewModel
import javax.inject.Inject


class WeatherActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels<MainViewModel> { mainViewModelFactory }

    private var mService: Messenger? = null
    private var bound: Boolean = false
    private lateinit var binding: ActivityWeatherBinding
    val mMessenger = Messenger(IncomingWeatherActivityHandler { viewModel.updateState() })


    //Для виджета нужно
    internal class IncomingWeatherActivityHandler(var treatmentFun: () -> Unit) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UpdateWeatherService.WEATHER_UPDATE_SUCCESS -> {
                    Log.i(TAG, "message gets in activity. success of work")
                    treatmentFun()
                }

                else -> {
                    Log.i(TAG, "message which in activity ${msg.what} ")
                    super.handleMessage(msg)
                }
            }
        }
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            bound = true

            try {
                val msg: Message = Message.obtain(
                    null, UpdateWeatherService.WEATHER_UPDATE
                )
                msg.replyTo = mMessenger
                mService!!.send(msg)

            } catch (e: RemoteException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            bound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        (applicationContext as App).appComponent.inject(this)
        setContentView(binding.root)

        setContent {
            SetComposeLoader(true)
            PrepareInfoFromStateForCompose()
        }

        viewModel.updateState()
        supportActionBar?.hide()
    }

    @Composable
    private fun SetComposeLoader(showLoader: Boolean) {
        if (showLoader) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
    }

    @Composable
    private fun SetComposeError(message: String?) {
        SetComposeLoader(false)

        Box(modifier = Modifier.fillMaxSize()) {
            message?.let {
                Text(modifier = Modifier.align(Alignment.Center), text = it, fontSize = 24.sp)
            }
        }

    }

    @Composable
    private fun PrepareInfoFromStateForCompose(
    ) {
        val state by viewModel.state.observeAsState()
        state.let {
            if (it?.errorMessage != null) {
                SetComposeError(message = it.errorMessage)
                SetComposeLoader(showLoader = true)

            } else {
                SetComposeLoader(showLoader = false)
                state?.weather?.weatherId?.let {
                    val images = viewModel.getImageStateSet((it))

                    val info = listOf<WeatherDescriptionItem>(
                        WeatherDescriptionItem(description = state?.city ?: ""),
                        WeatherDescriptionItem(
                            icon = images.icon, description = resources.getResourceName(images.icon)
                        ),
                        WeatherDescriptionItem(value = state?.weather?.currentDegree),
                        WeatherDescriptionItem(description = resources.getString(R.string.feels_like_ru)),
                        WeatherDescriptionItem(value = state?.weather?.feelDegree),
                        WeatherDescriptionItem(icon = R.drawable.ic_wind_icon),
                        WeatherDescriptionItem(value = state?.weather?.wind),
                        WeatherDescriptionItem(description = resources.getString(R.string.UV_index)),
                        WeatherDescriptionItem(value = state?.uv?.currentUV),
                        WeatherDescriptionItem(description = resources.getString(R.string.cloudiness_ru)),
                        WeatherDescriptionItem(value = state?.weather?.cloudiness)
                    )
                    SetUISuccessCompose(images.background, info)
                }
            }
        }
    }

    @Composable
    private fun SetUISuccessCompose(background: Int, info: List<WeatherDescriptionItem>) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = background),
                contentDescription = "background"
            )

            Column(
                modifier = Modifier
                    .background(Color.LightGray)
                    .alpha(0.15f)
                    .width(120.dp)
                    .height(300.dp)
                    .padding(30.dp)
                    .align(Alignment.CenterStart)
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(info) {
                        WeatherDescriptionItemBindOneInRow(modifier = Modifier, weather = it)
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service
        Intent(this, UpdateWeatherService::class.java).also { intent ->
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (bound) {
            unbindService(mConnection)
            bound = false
        }
    }


    private fun setInfoRecyclerView(weather: DisplayWeatherInfo, UV: DisplayUVInfo) {
        val description = mutableListOf<WeatherDescriptionItem>()
        weather.let {
            val images = viewModel.getImageStateSet((weather.weatherId))
            description.addAll(
                arrayOf(
                    WeatherDescriptionItem(
                        R.drawable.ic_temp_high_icon, weather.maxDegree.toString() + "°C"
                    ), WeatherDescriptionItem(
                        R.drawable.ic_temp_low_icon, weather.minDegree.toString() + "°C"
                    ), WeatherDescriptionItem(images.icon, weather.description ?: " ")
                )
            )
        }

        UV.let {
            description.addAll(
                arrayOf(
                    WeatherDescriptionItem(
                        R.drawable.ic_sun_uv_icon, "Макс УФ Индекс: " + it.maxUV
                    ), WeatherDescriptionItem(
                        R.drawable.ic_sun_protection_icon,
                        viewModel.UVDescription(it.currentUV.toInt())
                    )
                )
            )
        }

        //setWeatherDescriptionListCompose(binding.recyclerViewHolder,description)
    }

    companion object {
        fun getWeatherActivityIntent(context: Context, error: String?): Intent {
            val weatherIntent = Intent(context, WeatherActivity::class.java)
            weatherIntent.putExtra("ERROR_MESSAGE", error)
            return weatherIntent
        }
    }
}