package com.example.weatherandroidapp.presenter

import com.example.weatherandroidapp.data.models.WeatherDescriptionItem

data class WeatherUIState (val info: Map<String, WeatherDescriptionItem>, val background:Int?)