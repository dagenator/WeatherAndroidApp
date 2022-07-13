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
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.databinding.ActivityWeatherBinding
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.utils.Status
import javax.inject.Inject

class WeatherActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels<MainViewModel> { mainViewModelFactory }
    private lateinit var binding: ActivityWeatherBinding

    var weatherObserver = Observer<CurrentWeather> {
        it?.let {
            setUI(it)
        }
    }

    var errorObserver = Observer<String> {
        it?.let {
            setError(true, it)
        }
    }

    private var UVObserver = Observer<UVInfo> {
        it?.let {
            setUVInfo(it)
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
                location?.let {
                    viewModel.getCurrentWeather(it[0], it[1])
                    //viewModel.getUVinfo(it[0], it[1])
                }
            } else {
                message?.let { message ->
                    setError(visible = true, message)
                }
            }
        }

    }

    private fun setError(visible: Boolean, message: String?) {
        binding.error.text =message
        binding.error.visibility = if(visible) View.VISIBLE else View.GONE

    }

    private fun setUI(currentWeather: CurrentWeather) {
        binding.error.visibility = View.GONE
        val images =viewModel.getImageStateSet((currentWeather.weather.first().id).toInt())
        binding.city.text = currentWeather.name
        binding.background.background = AppCompatResources.getDrawable(this, images.background)
        binding.weatherIcon.setImageDrawable(AppCompatResources.getDrawable(this, images.icon))
        binding.temp.text = currentWeather.main.temp.toString() + "°C"
        binding.fellsTemp.text = currentWeather.main.feelsLike.toString() + "°C"
        binding.wind.text = currentWeather.wind.speed.toString() + "м/с"
        binding.cloudiness.text = currentWeather.clouds.all.toString() + "%"

        binding.comments.background =AppCompatResources.getDrawable(this, images.background)



    }

    private fun setUVInfo(uvInfo: UVInfo) {
        binding.UV.text =  "0.0"//uvInfo.uvMax.toString()
    }


}