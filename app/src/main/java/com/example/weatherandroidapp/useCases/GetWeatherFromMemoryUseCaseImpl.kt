package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.models.DisplayWeatherInfo
import com.example.weatherandroidapp.data.repository.MainRepository
import javax.inject.Inject

class GetWeatherFromMemoryUseCaseImpl @Inject constructor(val mainRepository: MainRepository) :
    GetWeatherFromMemoryUseCase {
    override fun invoke(): DisplayWeatherInfo = mainRepository.getWeatherFromPreferences()
}