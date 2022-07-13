package com.example.weatherandroidapp.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.ImageStateSet
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {

    val currentWeatherLiveData: MutableLiveData<CurrentWeather> = MutableLiveData(null)
    val errorLiveData: MutableLiveData<String> = MutableLiveData(null)
    val UVInfoLiveData: MutableLiveData<UVInfo> = MutableLiveData(null)

    fun getCurrentWeather(lat:Double, lon:Double){
        viewModelScope.launch {
            repository.getCurrentWeather(lat = lat, lon = lon).collect{
                it.data?.let{
                        currentWeather ->  currentWeatherLiveData.value = currentWeather
                }
                it.message?.let {
                        message -> errorLiveData.postValue(message)
                }
            }
        }
    }

    fun getUVinfo(lat:Double, lon:Double){
        viewModelScope.launch {
            repository.getUVInfo(lat = lat, lon = lon).collect{
                it.data?.let{
                        UV ->  UVInfoLiveData.value = UV
                }
                it.message?.let {
                        message -> errorLiveData.postValue(message)
                }
            }
        }
    }

    fun getImageStateSet(weatherId: Int): ImageStateSet {

        when(weatherId){
            in 200..299 -> return ImageStateSet(weatherId, R.drawable.thunderstorm, R.drawable.ic_thunderstorm_icon)
            300, 301, 310, 311, 500, 501 -> return ImageStateSet(weatherId, R.drawable.rain, R.drawable.ic_rain_icon)
            in 300..399 -> return ImageStateSet(weatherId, R.drawable.heavy_rain, R.drawable.ic_heavy_rain_icon)
            511, in 611..616 -> return ImageStateSet(weatherId, R.drawable.freezing_rain, R.drawable.ic_frezing_rain_icon)
            in 520..531 -> return ImageStateSet(weatherId, R.drawable.heavy_rain, R.drawable.ic_heavy_rain_icon)
            in 600..601, 620 -> return ImageStateSet(weatherId, R.drawable.snow, R.drawable.ic_snow_icon)
            602, 621, 622 -> return ImageStateSet(weatherId, R.drawable.snow, R.drawable.ic_heavy_snow)
            in 700..799 -> return ImageStateSet(weatherId, R.drawable.mist, R.drawable.ic_mist_icon)
            800-> return ImageStateSet(weatherId, R.drawable.clear_sky, R.drawable.clear_sky)
            801-> return ImageStateSet(weatherId, R.drawable.few_clouds, R.drawable.ic_few_clouds_icon)
            802-> return ImageStateSet(weatherId, R.drawable.scattered_clouds, R.drawable.ic_scattered_clouds_icon)
            803-> return ImageStateSet(weatherId, R.drawable.clouds, R.drawable.ic_clouds_icon)
            804-> return ImageStateSet(weatherId, R.drawable.heavy_clouds, R.drawable.ic_heavy_clouds_icon)
            else -> return ImageStateSet(weatherId, R.drawable.night_sky, R.drawable.ic_mist_icon)
        }

    }
}