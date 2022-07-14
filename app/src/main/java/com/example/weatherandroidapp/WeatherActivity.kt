package com.example.weatherandroidapp

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.databinding.ActivityWeatherBinding
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.presenter.WeatherDescriptionAdapter
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
                    viewModel.getUVinfo(it[0], it[1])
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
        binding.recyclerViewHolder.background =AppCompatResources.getDrawable(this, images.background)

        val description = arrayOf(
            WeatherDescriptionItem(R.drawable.ic_temp_high_icon , currentWeather.main.tempMax.toString()+"°C"),
            WeatherDescriptionItem(R.drawable.ic_temp_low_icon , currentWeather.main.tempMin.toString()+"°C"),
            WeatherDescriptionItem(images.icon, currentWeather.weather[0].description))

        setInfoRecyclerView(description)
    }

    private fun setInfoRecyclerView(description: Array<WeatherDescriptionItem> ){
        val recyclerView = findViewById<RecyclerView>(R.id.description_recycler)
        val weatherDescriptionAdapter = WeatherDescriptionAdapter(this, description)
        recyclerView.adapter = weatherDescriptionAdapter
    }

    private fun setUVInfo(uvInfo: UVInfo) {
        binding.UV.text =  uvInfo.uvMax.toString()
    }

    private fun setUIPermissionDeny(message: String){
        binding.mainWeatherWidget.visibility = View.GONE
        binding.recyclerViewHolder.visibility = View.GONE
        binding.background.background = AppCompatResources.getDrawable(this, R.drawable.night_sky)
        setError(true, message)

    }

}