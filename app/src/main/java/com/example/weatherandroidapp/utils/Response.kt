package com.example.weatherandroidapp.utils

sealed class Response<out T> (val data: T?, val message: String?){
    data class success<out T>(val value: T): Response<T>(value, null)
    data class error(val msg: String): Response<Nothing>(null, msg )
    object loading: Response<Nothing>(null, null )
}