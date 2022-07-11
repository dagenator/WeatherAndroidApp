package com.example.weatherandroidapp.data.models

import org.intellij.lang.annotations.Language

data class ConfigForApi(
    val weatherApiKey: String,
    val units: String,
    val uvApiKey: String,
    val language: String
)
