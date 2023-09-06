package com.example.weatherandroidapp.presenter

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherandroidapp.R
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

    val descriptionWeatherLiveData: MutableLiveData<Response<WeatherUIState>?> =
        MutableLiveData(null)

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
                        setMainWeather(it.value)
                        setDescriptionWeather(it.value)
                    }
                }
            }
        }
    }

    fun getUVInfo(lat: Double, lon: Double) {
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
                        setMainWeather(it.value)
                        setDescriptionWeather(it.value)
                    }
                }
            }
        }
    }

    private fun setDescriptionWeather(weather: CurrentWeather) {
        val updated = mapOf<String, WeatherDescriptionItem>(
            "maxWeather" to WeatherDescriptionItem.RowDescription(
                icon = R.drawable.ic_temp_high_icon, description = weather.main.tempMax.toString()
            ),
            "minWeight" to WeatherDescriptionItem.RowDescription(
                icon = R.drawable.ic_temp_low_icon, description = weather.main.tempMin.toString()
            ),
            "WeatherDescription" to WeatherDescriptionItem.RowDescription(
                icon = R.drawable.ic_mist_icon, description = weather.weather.first().description
            ),
        )
        val newInfo = updateWeatherInfoList(currentWeatherLiveData.value?.data?.info, updated)
        val newState = WeatherUIState(newInfo, currentWeatherLiveData.value?.data?.background)

        descriptionWeatherLiveData.postValue(
            Response.success(
                newState
            ) as Response<WeatherUIState>
        )
    }

    private fun setDescriptionWeather(uv: UVInfo) {
        val updated = mapOf<String, WeatherDescriptionItem>(
            "maxUVIndex" to WeatherDescriptionItem.RowDescription(
                icon = R.drawable.ic_sun_uv_icon, description = uv.result.uvMax.toString()
            ),
            "UVDescription" to WeatherDescriptionItem.RowDescription(
                icon = R.drawable.ic_sun_protection_icon,
                description = UVDescription(uv.result.uvMax.toInt())
            )
        )
        val newInfo = updateWeatherInfoList(currentWeatherLiveData.value?.data?.info, updated)
        val newState = WeatherUIState(newInfo, currentWeatherLiveData.value?.data?.background)

        descriptionWeatherLiveData.postValue(
            Response.success(
                newState
            ) as Response<WeatherUIState>
        )
    }

    private fun setMainWeather(weather: CurrentWeather) {
        val images = getImageStateSet(weather.weather.first().id.toInt())
        val updated = mapOf<String, WeatherDescriptionItem>(
            "cityName" to WeatherDescriptionItem.UiDescription(weather.name ?: ""),
            "weatherIcon" to WeatherDescriptionItem.UiIcon(images.icon),
            "temp" to WeatherDescriptionItem.UiDescription(weather.main.temp.toString()),
            "feelsLikeTitle" to WeatherDescriptionItem.UiTitle(R.string.feels_like_ru),
            "feelsLikeTemp" to WeatherDescriptionItem.UiDescription(weather.main.feelsLike.toString()),
            "windIcon" to WeatherDescriptionItem.UiIcon(R.drawable.ic_wind_icon),
            "wind" to WeatherDescriptionItem.UiDescription(weather.wind.speed.toString()),
            "cloudinessDesc" to WeatherDescriptionItem.UiTitle(R.string.cloudiness_ru),
            "clouds" to WeatherDescriptionItem.UiDescription(weather.clouds.all.toString())
        )

        val newInfo = updateWeatherInfoList(currentWeatherLiveData.value?.data?.info, updated)
        val newState = WeatherUIState(newInfo, images.background)

        currentWeatherLiveData.postValue(
            Response.success(
                newState
            ) as Response<WeatherUIState>
        )
    }

    private fun setMainWeather(uv: UVInfo) {
        val updated = mapOf<String, WeatherDescriptionItem>(
            "uvValueDesc" to WeatherDescriptionItem.UiTitle(R.string.UV_index),
            "uvValue" to WeatherDescriptionItem.UiDescription(uv.result.uv.toString()),
        )
        val newInfo = updateWeatherInfoList(currentWeatherLiveData.value?.data?.info, updated)
        val newState = WeatherUIState(newInfo, currentWeatherLiveData.value?.data?.background)
        Log.i(
            "TEST_TAG", "UVinfo ${currentWeatherLiveData.value?.data?.info?.size}"
        )
        currentWeatherLiveData.postValue(
            Response.success(
                newState
            ) as Response<WeatherUIState>
        )
    }

    private fun getImageStateSet(weatherId: Int) = weather.getImageStateSet(weatherId)

    private fun updateWeatherInfoList(
        previousState: Map<String, WeatherDescriptionItem>?,
        updated: Map<String, WeatherDescriptionItem>,
        addOnlyIfPreviousInfoListNotEmpty: Boolean = false
    ): Map<String, WeatherDescriptionItem> {
        val newState = previousState?.toMutableMap() ?: mutableMapOf()

        updated.forEach { entry ->
            if (addOnlyIfPreviousInfoListNotEmpty && (newState.containsKey(entry.key) || newState.size > updated.size) || addOnlyIfPreviousInfoListNotEmpty.not()) {
                newState[entry.key] = entry.value
            }
        }
        return newState
    }

    fun setError(msg: String) {
        currentWeatherLiveData.postValue(Response.error(msg))
    }

    fun UVDescription(uv: Int) = weather.UVDescription(uv)
}
