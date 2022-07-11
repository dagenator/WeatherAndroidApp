package com.example.weatherandroidapp.data.models

import com.google.gson.annotations.SerializedName

data class SunInfo (
    @SerializedName("sun_times")
    val sunTimes: SunTimes,

    @SerializedName("sun_position")
    val sunPosition: SunPosition
)