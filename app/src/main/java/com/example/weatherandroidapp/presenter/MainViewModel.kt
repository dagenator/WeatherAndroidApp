package com.example.weatherandroidapp.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherandroidapp.data.models.Weather
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

    val state: MutableLiveData<WeatherState> = MutableLiveData(WeatherState())

    fun updateState(error:String? = null){
        val newValue:WeatherState? = null
        if(error.isNullOrEmpty().not()){
            WeatherState(
                errorMessage =error
            )
        }else{
            WeatherState(
                weather = getWeatherFromPreferences(),
                uv = getUVFromPreferences(),
                city = getCity()
            )
        }
        newValue?.let {
            state.value = it
        }
    }


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
