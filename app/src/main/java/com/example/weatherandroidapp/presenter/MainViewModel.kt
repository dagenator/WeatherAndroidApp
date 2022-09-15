package com.example.weatherandroidapp.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.PreferencesUpdateState
import com.example.weatherandroidapp.utils.Resource
import com.example.weatherandroidapp.utils.WeatherStateUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: MainRepository,
    val weather: WeatherStateUtil
) : ViewModel() {

//    val currentWeatherLiveData: MutableLiveData<Resource<CurrentWeather>> = MutableLiveData(null)
//    val errorLiveData: MutableLiveData<String> = MutableLiveData(null)
//    val UVInfoLiveData: MutableLiveData<Resource<UVInfo>> = MutableLiveData(null)



    fun updateWidgets() {
        repository.updateWidgets()
    }

//    fun loadCurrentWeather(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            repository.updateCurrentWeather(lat = lat, lon = lon).collect {
//                currentWeatherLiveData.postValue(it)
//            }
//        }
//    }
//
//    fun loadUVInfo(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            repository.updateUVInfo(lat = lat, lon = lon).collect {
//                UVInfoLiveData.postValue(it)
//            }
//        }
//    }



    fun UVDescription(UV: Int) = weather.UVDescription(UV)

    fun getImageStateSet(weatherId: Int) = weather.getImageStateSet(weatherId)

    fun getWeatherFromPreferences() =
        repository.getWeatherFromPreferences()

    fun getUVFromPreferences() =
        repository.getUVFromPreferences()

    fun getCity()=repository.getCity()

    companion object{
        val weatherUpdateState = MutableLiveData(PreferencesUpdateState.loading())
    }
}
