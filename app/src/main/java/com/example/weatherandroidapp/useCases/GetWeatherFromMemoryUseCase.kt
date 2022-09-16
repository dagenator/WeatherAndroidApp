package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.models.DisplayWeatherInfo

interface GetWeatherFromMemoryUseCase {
    operator fun invoke(): DisplayWeatherInfo
}