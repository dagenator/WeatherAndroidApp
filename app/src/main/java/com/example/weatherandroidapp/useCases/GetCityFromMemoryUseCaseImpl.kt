package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.repository.MainRepository
import javax.inject.Inject

class GetCityFromMemoryUseCaseImpl @Inject constructor(val mainRepository: MainRepository) :
    GetCityFromMemoryUseCase {
    override fun invoke(): String? = mainRepository.getCity()
}