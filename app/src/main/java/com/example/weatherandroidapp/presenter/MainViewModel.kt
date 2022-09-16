package com.example.weatherandroidapp.presenter

import androidx.lifecycle.ViewModel
import com.example.weatherandroidapp.useCases.GetCityFromMemoryUseCase
import com.example.weatherandroidapp.useCases.GetUVFromMemoryUseCase
import com.example.weatherandroidapp.useCases.GetWeatherFromMemoryUseCase
import com.example.weatherandroidapp.useCases.UpdateAllWidgetsUseCase
import com.example.weatherandroidapp.utils.WeatherStateUtil
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val weather: WeatherStateUtil,
    val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    val getWeatherFromMemoryUseCase: GetWeatherFromMemoryUseCase,
    val getUVFromMemoryUseCase: GetUVFromMemoryUseCase,
    val getCityFromMemoryUseCase: GetCityFromMemoryUseCase
) : ViewModel() {

    fun updateWidgets() =
        updateAllWidgetsUseCase()

    fun UVDescription(UV: Int) = weather.UVDescription(UV)

    fun getImageStateSet(weatherId: Int) = weather.getImageStateSet(weatherId)

    fun getWeatherFromPreferences() =
        getWeatherFromMemoryUseCase()

    fun getUVFromPreferences() =
        getUVFromMemoryUseCase()

    fun getCity() = getCityFromMemoryUseCase()


}
