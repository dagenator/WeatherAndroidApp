package com.example.weatherandroidapp.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.ImageStateSet
import com.example.weatherandroidapp.data.models.Result
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {

    val currentWeatherLiveData: MutableLiveData<Resource<CurrentWeather>> = MutableLiveData(null)
    val errorLiveData: MutableLiveData<String> = MutableLiveData(null)
    val UVInfoLiveData: MutableLiveData<Resource<UVInfo>> = MutableLiveData(null)

    fun getCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getCurrentWeather(lat = lat, lon = lon).collect {
                currentWeatherLiveData.postValue(it)
            }
        }
    }

    fun getUVinfo(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getUVInfo(lat = lat, lon = lon).collect {
                UVInfoLiveData.postValue(it)
            }
        }
    }

    fun UVDescription(UV :Int):String{
        when(UV){
            in 0..2-> return "Низкий"
            in 3..5-> return "Средний. Рекомендуется использовать солнцезащитный крем"
            in 6..7-> return "Высокий. Не забудьте про солнцезашитный крем"
            in 8..10-> return "Очень высокий. Для защиты кожи используйте солнцезащитный крем"
            else-> return "Экстремальный. Апокалипсис случился. Передвигаться можно только ночью"
        }
    }

    fun getImageStateSet(weatherId: Int): ImageStateSet {

        when (weatherId) {
            in 200..299 -> return ImageStateSet(
                weatherId,
                R.drawable.thunderstorm,
                R.drawable.ic_thunderstorm_icon
            )
            300, 301, 310, 311, 500, 501 -> return ImageStateSet(
                weatherId,
                R.drawable.rain,
                R.drawable.ic_rain_icon
            )
            in 300..399 -> return ImageStateSet(
                weatherId,
                R.drawable.heavy_rain,
                R.drawable.ic_heavy_rain_icon
            )
            511, in 611..616 -> return ImageStateSet(
                weatherId,
                R.drawable.freezing_rain,
                R.drawable.ic_frezing_rain_icon
            )
            in 520..531 -> return ImageStateSet(
                weatherId,
                R.drawable.heavy_rain,
                R.drawable.ic_heavy_rain_icon
            )
            in 600..601, 620 -> return ImageStateSet(
                weatherId,
                R.drawable.snow,
                R.drawable.ic_snow_icon
            )
            602, 621, 622 -> return ImageStateSet(
                weatherId,
                R.drawable.snow,
                R.drawable.ic_heavy_snow
            )
            in 700..799 -> return ImageStateSet(weatherId, R.drawable.mist, R.drawable.ic_mist_icon)
            800 -> return ImageStateSet(
                weatherId,
                R.drawable.clear_sky,
                R.drawable.ic_clear_sky_icon
            )
            801 -> return ImageStateSet(
                weatherId,
                R.drawable.few_clouds,
                R.drawable.ic_few_clouds_icon
            )
            802 -> return ImageStateSet(
                weatherId,
                R.drawable.scattered_clouds,
                R.drawable.ic_scattered_clouds_icon
            )
            803 -> return ImageStateSet(weatherId, R.drawable.clouds, R.drawable.ic_clouds_icon)
            804 -> return ImageStateSet(
                weatherId,
                R.drawable.heavy_clouds,
                R.drawable.ic_heavy_clouds_icon
            )
            else -> return ImageStateSet(weatherId, R.drawable.night_sky, R.drawable.ic_mist_icon)
        }

    }
}
