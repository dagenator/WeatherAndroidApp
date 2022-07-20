package com.example.weatherandroidapp.core.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.presenter.MainViewModel
import com.example.weatherandroidapp.utils.WeatherStateUtil
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory @Inject constructor(val repository: MainRepository, var weatherStateUtil: WeatherStateUtil) :
    ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository,weatherStateUtil) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }


}