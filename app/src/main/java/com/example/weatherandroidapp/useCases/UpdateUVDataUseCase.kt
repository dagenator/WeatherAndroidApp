package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.utils.MemoryUpdateState
import kotlinx.coroutines.flow.Flow

interface UpdateUVDataUseCase {
    operator fun invoke(lat: Double, lon: Double): Flow<MemoryUpdateState>
}