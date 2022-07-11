package com.example.weatherandroidapp.data.models

import com.google.gson.annotations.SerializedName

data class UVInfo (
    val uv: Long,

    @SerializedName("uv_time")
    val uvTime: String,

    @SerializedName("uv_max")
    val uvMax: Double,

    @SerializedName("uv_max_time")
    val uvMaxTime: String,

    val ozone: Double,

    @SerializedName("ozone_time")
    val ozoneTime: String,

    @SerializedName("safe_exposure_time")
    val safeExposureTime: SafeExposureTime,

    @SerializedName("sun_info")
    val sunInfo: SunInfo
)