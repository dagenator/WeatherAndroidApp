package com.example.weatherandroidapp

import com.example.weatherandroidapp.data.models.WeatherDescriptionItem

data class WeatherUIState (val info: Map<String, WeatherDescriptionItem>, val background:Int?)