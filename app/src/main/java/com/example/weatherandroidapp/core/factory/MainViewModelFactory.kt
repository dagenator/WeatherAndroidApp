package com.example.weatherandroidapp.core.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.useCases.GetCityFromMemoryUseCase
import com.example.weatherandroidapp.useCases.GetUVFromMemoryUseCase
import com.example.weatherandroidapp.useCases.GetWeatherFromMemoryUseCase
import com.example.weatherandroidapp.useCases.UpdateAllWidgetsUseCase
import com.example.weatherandroidapp.utils.WeatherStateUtil
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory @Inject constructor(
    var weatherStateUtil: WeatherStateUtil,
    val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    val getWeatherFromMemoryUseCase: GetWeatherFromMemoryUseCase,
    val getUVFromMemoryUseCase: GetUVFromMemoryUseCase,
    val getCityFromMemoryUseCase: GetCityFromMemoryUseCase
) :
    ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(weatherStateUtil, updateAllWidgetsUseCase, getWeatherFromMemoryUseCase, getUVFromMemoryUseCase, getCityFromMemoryUseCase) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }


}