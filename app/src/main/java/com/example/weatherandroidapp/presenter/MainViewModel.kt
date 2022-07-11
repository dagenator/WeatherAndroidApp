package com.example.weatherandroidapp.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherandroidapp.data.models.CurrentWeather
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
}