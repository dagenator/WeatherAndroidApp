package com.example.weatherandroidapp.useCases

import com.example.weatherandroidapp.data.models.DisplayUVInfo

interface GetUVFromMemoryUseCase {
    operator fun invoke(): DisplayUVInfo
}