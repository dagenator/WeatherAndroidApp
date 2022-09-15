package com.example.weatherandroidapp.utils

data class PreferencesUpdateState(val status: Status, val message: String?) {
    companion object {
        fun success(): PreferencesUpdateState =
            PreferencesUpdateState(status = Status.SUCCESS, message = null)

        fun error(message: String): PreferencesUpdateState =
            PreferencesUpdateState(status = Status.ERROR, message = message)

        fun loading(): PreferencesUpdateState =
            PreferencesUpdateState(status = Status.LOADING, message = null)
    }
}