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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.DisplayUVInfo
import com.example.weatherandroidapp.data.models.DisplayWeatherInfo
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.databinding.ActivityWeatherBinding
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.presenter.WeatherDescriptionAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class WeatherActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels<MainViewModel> { mainViewModelFactory }
    private lateinit var binding: ActivityWeatherBinding

    private var mService: Messenger? = null
    private var bound: Boolean = false
    val mMessenger = Messenger(IncomingWeatherActivityHandler { loadUiFromPreferences() })

    internal class IncomingWeatherActivityHandler(var treatmentFun: () -> Unit ) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UpdateWeatherService.WEATHER_UPDATE_SUCCESS ->{
                    Log.i(TAG, "message gets in activity. success of work")
                    treatmentFun()
                }

                else ->{
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
                    null,
                    UpdateWeatherService.WEATHER_UPDATE
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

        setLoader(true, false)
        loadUiFromPreferences()

        setLoader(true, true)

        supportActionBar?.hide()
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

    private fun loadUiFromPreferences() {
        val weather = viewModel.getWeatherFromPreferences()
        Log.i(TAG, "loadUiFromPreferences: $weather ")
        val uv = viewModel.getUVFromPreferences()
        viewModel.getCity()?.let { setUI(weather, it, uv) }
        setLoader(false, true)
    }


    private fun setLoader(showLoader: Boolean, showWeatherInfo: Boolean) {
        binding.progressBar.visibility = if (showLoader) View.VISIBLE else View.GONE
        binding.mainWeatherWidget.visibility = if (showWeatherInfo) View.VISIBLE else View.GONE
        binding.recyclerViewHolder.visibility = if (showWeatherInfo) View.VISIBLE else View.GONE
    }

    private fun setError(visible: Boolean, message: String?) {
        binding.error.text = message
        binding.error.visibility = if (visible) View.VISIBLE else View.GONE
        setLoader(false, false)
    }

    private fun setUI(weather: DisplayWeatherInfo, city: String, UV: DisplayUVInfo) {
        binding.error.visibility = View.GONE
        val images = viewModel.getImageStateSet((weather.weatherId))
        binding.city.text = city
        binding.mainBackground.background = AppCompatResources.getDrawable(this, images.background)
        binding.weatherIcon.setImageDrawable(AppCompatResources.getDrawable(this, images.icon))
        binding.temp.text = weather.currentDegree.toString() + "°C"
        binding.fellsTemp.text = weather.feelDegree.toString() + "°C"
        binding.wind.text = weather.wind.toString() + "м/с"
        binding.cloudiness.text = weather.cloudiness.toString() + "%"
        binding.descriptionLabel.setImageDrawable(AppCompatResources.getDrawable(this, images.icon))

        setUVInfo(UV)
        setInfoRecyclerView(weather, UV)
    }

    private fun setInfoRecyclerView(weather: DisplayWeatherInfo, UV: DisplayUVInfo) {
        val description = mutableListOf<WeatherDescriptionItem>()
        weather.let {
            val images = viewModel.getImageStateSet((weather.weatherId))
            description.addAll(
                arrayOf(
                    WeatherDescriptionItem(
                        R.drawable.ic_temp_high_icon,
                        weather.maxDegree.toString() + "°C"
                    ),
                    WeatherDescriptionItem(
                        R.drawable.ic_temp_low_icon,
                        weather.minDegree.toString() + "°C"
                    ),
                    WeatherDescriptionItem(images.icon, weather.description ?: " ")
                )
            )
        }

        UV.let {
            description.addAll(
                arrayOf(
                    WeatherDescriptionItem(
                        R.drawable.ic_sun_uv_icon,
                        "Макс УФ Индекс: " + it.maxUV
                    ),
                    WeatherDescriptionItem(
                        R.drawable.ic_sun_protection_icon,
                        viewModel.UVDescription(it.currentUV.toInt())
                    )
                )
            )
        }

        val weatherDescriptionAdapter = WeatherDescriptionAdapter(this, description.toTypedArray())
        val lp = binding.recyclerViewHolder.layoutParams
        lp.height += 105 * description.size
        binding.recyclerViewHolder.layoutParams = lp
        binding.descriptionRecycler.layoutManager = LinearLayoutManager(this)
        binding.descriptionRecycler.adapter = weatherDescriptionAdapter
    }

    private fun setUVInfo(uvInfo: DisplayUVInfo) {
        binding.UV.text = uvInfo.currentUV.toString()
    }

    // TODO: check error logic
    private fun setUIPermissionDeny(message: String) {
        binding.mainWeatherWidget.visibility = View.GONE
        binding.recyclerViewHolder.visibility = View.GONE
        binding.mainBackground.background =
            AppCompatResources.getDrawable(this, R.drawable.night_sky)
        setError(true, message)
        setLoader(false, true)
    }

    companion object{
        fun getWeatherActivityIntent(context: Context,  error:String?):Intent{
            val weatherIntent = Intent(context, WeatherActivity::class.java)
            weatherIntent.putExtra("ERROR_MESSAGE", error)
            return weatherIntent
        }
    }
}