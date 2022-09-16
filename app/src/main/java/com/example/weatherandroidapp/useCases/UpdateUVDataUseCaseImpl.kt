package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.MemoryUpdateState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUVDataUseCaseImpl @Inject constructor(val mainRepository: MainRepository) :
    UpdateUVDataUseCase {
    override fun invoke(lat: Double, lon: Double): Flow<MemoryUpdateState> =
        mainRepository.updateUVInfo(lat, lon)
}