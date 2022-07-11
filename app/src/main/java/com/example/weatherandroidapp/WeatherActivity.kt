package com.example.weatherandroidapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.utils.Status
import javax.inject.Inject

class WeatherActivity: AppCompatActivity() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels<MainViewModel> { mainViewModelFactory }

    var weatherObserver = Observer<CurrentWeather>{
        it?.let {
            setUI(it)
        }
    }

    var errorObserver = Observer<String>{
        it?.let {
            setError(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        (applicationContext as App).appComponent.inject(this)

        val status = intent.getStringExtra("STATUS")
        val location = intent.getDoubleArrayExtra("LOCATION_RESULT")
        val message = intent.getStringExtra("ERROR_MESSAGE")

        viewModel.currentWeatherLiveData.observe(this, weatherObserver)
        viewModel.errorLiveData.observe(this, errorObserver)

        status?.let { it ->
            if(Status.valueOf(it) == Status.SUCCESS){
                location?.let {
                    viewModel.getCurrentWeather(it[0], it[1])
                }
            }else{
                message?.let {message->
                    setError(message)
                }
            }
        }

    }

    fun setError(err: String){
        findViewById<TextView>(R.id.text).text = err
    }

    fun setUI(currentWeather: CurrentWeather){
        findViewById<TextView>(R.id.text).text = currentWeather.main.toString()

    }
}