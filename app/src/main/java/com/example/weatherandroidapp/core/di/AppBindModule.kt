package com.example.weatherandroidapp.core.di

import com.example.weatherandroidapp.useCases.*
import dagger.Binds
import dagger.Module

@Module
interface AppBindModule {

    @Binds
    fun bindGetCityFromMemoryUseCaseImpl_to_GetCityFromMemoryUseCase(impl: GetCityFromMemoryUseCaseImpl): GetCityFromMemoryUseCase

    @Binds
    fun getUVFromMemoryUseCaseImpl_to_GetUVFromMemoryUseCase(impl: GetUVFromMemoryUseCaseImpl): GetUVFromMemoryUseCase

    @Binds
    fun getWeatherFromMemoryUseCaseImpl_to_GetWeatherFromMemoryUseCase(impl: GetWeatherFromMemoryUseCaseImpl): GetWeatherFromMemoryUseCase

    @Binds
    fun updateAllWidgetsUseCaseImpl_to_UpdateAllWidgetsUseCase(impl: UpdateAllWidgetsUseCaseImpl): UpdateAllWidgetsUseCase

    @Binds
    fun updateCurrentWeatherDataUseCaseImpl_to_UpdateCurrentWeatherDataUseCase(impl: UpdateCurrentWeatherDataUseCaseImpl): UpdateCurrentWeatherDataUseCase

    @Binds
    fun updateUVDataUseCaseImpl_to_UpdateUVDataUseCase(impl: UpdateUVDataUseCaseImpl): UpdateUVDataUseCase


}