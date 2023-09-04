package com.example.weatherandroidapp.presenter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.WeatherUIState
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.Response
import com.example.weatherandroidapp.utils.WeatherStateUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: MainRepository, val weather: WeatherStateUtil
) : ViewModel() {

    val currentWeatherLiveData: MutableLiveData<Response<WeatherUIState>?> = MutableLiveData(null)

    val UVInfoLiveData: MutableLiveData<Response<UVInfo>> = MutableLiveData(null)

    fun getCurrentWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getCurrentWeather(lat = lat, lon = lon).collect {
                when (it) {
                    is Response.loading -> {
                        currentWeatherLiveData.postValue(it)
                    }

                    is Response.error -> {
                        currentWeatherLiveData.postValue(it)
                    }

                    is Response.success -> {
                        val images = getImageStateSet(it.value.weather.first().id.toInt())
                        val updated = mapOf<String, WeatherDescriptionItem>(
                            "cityName" to WeatherDescriptionItem.UiDescription (it.value.name ?: ""),
                            "weatherIcon" to WeatherDescriptionItem.UiIcon(images.icon),
                            "temp" to WeatherDescriptionItem.UiDescription( it.value.main.temp.toString()),
                            "feelsLikeTitle" to WeatherDescriptionItem.UiTitle( R.string.feels_like_ru),
                            "feelsLikeTemp" to WeatherDescriptionItem.UiDescription(it.value.main.feelsLike.toString()),
                            "windIcon" to WeatherDescriptionItem.UiIcon(R.drawable.ic_wind_icon),
                            "wind" to WeatherDescriptionItem.UiDescription(it.value.wind.speed.toString()),
                            "cloudinessDesc" to WeatherDescriptionItem.UiTitle( R.string.cloudiness_ru),
                            "clouds" to WeatherDescriptionItem.UiDescription(it.value.clouds.all.toString())
                        )

                        val newInfo =
                            updateWeatherInfoList(currentWeatherLiveData.value?.data?.info, updated)
                        val newState = WeatherUIState(newInfo, images.background)

                        Log.i(
                            "TEST_TAG",
                            "CurrentWeather ${currentWeatherLiveData.value?.data?.info?.size}"
                        )
                        currentWeatherLiveData.postValue(
                            Response.success(
                                newState
                            ) as Response<WeatherUIState>
                        )
                    }
                }
            }
        }
    }

    fun getUVinfo(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getUVInfo(lat = lat, lon = lon).collect {
                when (it) {
                    is Response.loading -> {
                        currentWeatherLiveData.postValue(it)
                    }

                    is Response.error -> {
                        currentWeatherLiveData.postValue(it)
                    }

                    is Response.success -> {
                        val updated = mapOf<String, WeatherDescriptionItem>(
                            "uvValueDesc" to WeatherDescriptionItem.UiTitle(R.string.UV_index),
                            "uvValue" to WeatherDescriptionItem.UiDescription(it.value.result.uv.toString()),
                        )
                        val newInfo =
                            updateWeatherInfoList(currentWeatherLiveData.value?.data?.info, updated)
                        val newState = WeatherUIState(newInfo, currentWeatherLiveData.value?.data?.background)
                        Log.i(
                            "TEST_TAG",
                            "UVinfo ${currentWeatherLiveData.value?.data?.info?.size}"
                        )
                        currentWeatherLiveData.postValue(
                            Response.success(
                                newState
                            ) as Response<WeatherUIState>
                        )
                    }
                }

                UVInfoLiveData.postValue(it)
            }
        }
    }

    //fun UVDescription(UV: Int) = weather.UVDescription(UV)

    fun getImageStateSet(weatherId: Int) = weather.getImageStateSet(weatherId)

    private fun updateWeatherInfoList(
        previousState: Map<String, WeatherDescriptionItem>?,
        updated: Map<String, WeatherDescriptionItem>,
        addOnlyIfPreviousInfoListNotEmpty: Boolean = false
    ): Map<String, WeatherDescriptionItem> {
        val newState = previousState?.toMutableMap() ?: mutableMapOf()

        updated.forEach { entry ->
            if (addOnlyIfPreviousInfoListNotEmpty && (newState.containsKey(entry.key) || newState.size > updated.size)
                || addOnlyIfPreviousInfoListNotEmpty.not()) {
                newState[entry.key] = entry.value
            }
        }
        return newState
    }

    fun setError(msg: String) {
        currentWeatherLiveData.postValue(Response.error(msg))
    }

}
