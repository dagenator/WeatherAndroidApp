package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.models.DisplayUVInfo
import com.example.weatherandroidapp.data.repository.MainRepository
import javax.inject.Inject

class GetUVFromMemoryUseCaseImpl @Inject constructor(val mainRepository: MainRepository) :
    GetUVFromMemoryUseCase {
    override fun invoke(): DisplayUVInfo = mainRepository.getUVFromPreferences()
}