package com.example.weatherandroidapp.core.retrofit

import com.example.weatherandroidapp.data.models.UVInfo
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface UVApi {
    @GET("/api/v1/uv")
    suspend fun getCurrentUV(
        @Query("lat") lat: Double,
        @Query("lng") lon: Double,
        @Header("x-access-token") key:String
    ): UVInfo

}