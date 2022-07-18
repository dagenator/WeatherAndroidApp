package com.example.weatherandroidapp

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.Result
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.databinding.ActivityWeatherBinding
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.presenter.WeatherDescriptionAdapter
import com.example.weatherandroidapp.utils.Resource
import com.example.weatherandroidapp.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels<MainViewModel> { mainViewModelFactory }
    private lateinit var binding: ActivityWeatherBinding

    var weatherObserver = Observer<Resource<CurrentWeather>> {
        it?.let {
            when (it.status) {
                Status.LOADING -> setLoader(true)
                Status.SUCCESS -> it.data?.let { setUI(it) }
                Status.ERROR -> it.message?.let { setError(true, it) }
            }
            setLoader(false)
        }
    }

    var errorObserver = Observer<String> {
        it?.let {
            setError(true, it)
        }
    }

    private var UVObserver = Observer<Resource<UVInfo>> {
        it?.let {
            when (it.status) {
                Status.LOADING -> {}
                Status.SUCCESS -> it.data?.let { setUVInfo(it.result) }
                Status.ERROR -> it.message?.let { setError(true, it) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        (applicationContext as App).appComponent.inject(this)


        val status = intent.getStringExtra("STATUS")
        val location = intent.getDoubleArrayExtra("LOCATION_RESULT")
        val message = intent.getStringExtra("ERROR_MESSAGE")

        viewModel.currentWeatherLiveData.observe(this, weatherObserver)
        viewModel.errorLiveData.observe(this, errorObserver)
        viewModel.UVInfoLiveData.observe(this, UVObserver)

        status?.let { it ->
            if (Status.valueOf(it) == Status.SUCCESS) {
                location?.let { weather ->
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getCurrentWeather(weather[0], weather[1])
                        //viewModel.getUVinfo(weather[0], weather[1])
                    }
                }
            } else {
                message?.let { message ->
                    setUIPermissionDeny(message)
                }
            }
        }

    }

    private fun setLoader(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
        binding.mainWeatherWidget.visibility = if (isLoad) View.GONE else View.VISIBLE
        binding.bottomSheet.bottomSheet.visibility = if (isLoad) View.GONE else View.VISIBLE

    }

    private fun setError(visible: Boolean, message: String?) {
        binding.error.text = message
        binding.error.visibility = if (visible) View.VISIBLE else View.GONE

    }

    private fun setUI(currentWeather: CurrentWeather) {
        binding.error.visibility = View.GONE
        val images = viewModel.getImageStateSet((currentWeather.weather.first().id).toInt())
        binding.city.text = currentWeather.name
        binding.mainBackground.background = AppCompatResources.getDrawable(this, images.background)
        binding.weatherIcon.setImageDrawable(AppCompatResources.getDrawable(this, images.icon))
        binding.temp.text = currentWeather.main.temp.toString() + "°C"
        binding.fellsTemp.text = currentWeather.main.feelsLike.toString() + "°C"
        binding.wind.text = currentWeather.wind.speed.toString() + "м/с"
        binding.cloudiness.text = currentWeather.clouds.all.toString() + "%"
        setInfoRecyclerView()

    }

    private fun setInfoRecyclerView() {
        val currentWeather = viewModel.currentWeatherLiveData.value?.data

        val description = mutableListOf<WeatherDescriptionItem>()
        currentWeather?.let {
            val images = viewModel.getImageStateSet((currentWeather.weather.first().id).toInt())
            description.addAll(
                arrayOf(
                    WeatherDescriptionItem(
                        R.drawable.ic_temp_high_icon,
                        currentWeather.main.tempMax.toString() + "°C"
                    ),
                    WeatherDescriptionItem(
                        R.drawable.ic_temp_low_icon,
                        currentWeather.main.tempMin.toString() + "°C"
                    ),
                    WeatherDescriptionItem(images.icon, currentWeather.weather[0].description)
                )
            )
        }

        val UV = viewModel.UVInfoLiveData.value?.data
        UV?.let{
            description.addAll(
                arrayOf(
                    WeatherDescriptionItem(
                        R.drawable.ic_sun_uv_icon,
                        "Макс УФ Индекс: " + it.result.uvMax
                    ),
                    WeatherDescriptionItem(
                        R.drawable.ic_sun_protection_icon,
                        viewModel.UVDescription(it.result.uvMax.toInt())
                    )
                )
            )
        }

        val weatherDescriptionAdapter = WeatherDescriptionAdapter(this, description.toTypedArray())
        binding.bottomSheet.descriptionRecycler.adapter = weatherDescriptionAdapter
    }

    private fun setUVInfo(uvInfo: Result) {
        binding.UV.text = uvInfo.uv.toString()
        setInfoRecyclerView()
    }

    private fun setUIPermissionDeny(message: String) {
        binding.mainWeatherWidget.visibility = View.GONE
        binding.bottomSheet.bottomSheet.visibility = View.GONE
        binding.mainBackground.background =
            AppCompatResources.getDrawable(this, R.drawable.night_sky)
        setError(true, message)
    }

}