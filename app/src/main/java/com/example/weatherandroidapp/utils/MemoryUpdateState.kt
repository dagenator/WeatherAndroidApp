package com.example.weatherandroidapp.utils

data class MemoryUpdateState(val status: Status, val message: String?) {
    companion object {
        fun success(): MemoryUpdateState =
            MemoryUpdateState(status = Status.SUCCESS, message = null)

        fun error(message: String): MemoryUpdateState =
            MemoryUpdateState(status = Status.ERROR, message = message)

        fun loading(): MemoryUpdateState =
            MemoryUpdateState(status = Status.LOADING, message = null)
    }
}