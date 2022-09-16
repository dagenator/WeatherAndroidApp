package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.repository.MainRepository
import javax.inject.Inject

class UpdateAllWidgetsUseCaseImpl @Inject constructor(val mainRepository: MainRepository) :
    UpdateAllWidgetsUseCase {
    override fun invoke() = mainRepository.updateWidgets()
}