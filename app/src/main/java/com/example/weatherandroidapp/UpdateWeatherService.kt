package com.example.weatherandroidapp

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.data.repository.MainRepository
import com.example.weatherandroidapp.utils.LocationUtils
import com.example.weatherandroidapp.utils.PreferencesUpdateState
import com.example.weatherandroidapp.utils.Status
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class UpdateWeatherService(
) : Service() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var mainRepository: MainRepository

    @Inject
    lateinit var locationUtils: LocationUtils

    private var replyTo: Messenger? = null


    private lateinit var mMessenger: Messenger

    internal class IncomingServiceHandler(
        var treatmentFun: () -> Unit,
        var saveReplyTo: (Messenger) -> Unit
    ) :
        Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WEATHER_UPDATE -> {
                    saveReplyTo(msg.replyTo)
                    treatmentFun()
                }
                else -> super.handleMessage(msg)
            }
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        mMessenger = Messenger(
            IncomingServiceHandler({
                getLocationAndUpdateWeather()
            },
                { x: Messenger -> saveReplyTo(x) })
        )
        return mMessenger.binder
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate: Service created")
        (applicationContext as App).appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: OnStartCommand")
        getLocationAndUpdateWeather(sendResultMessage = false)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun getLocationAndUpdateWeather(sendResultMessage: Boolean = true) {
        val locationTask = locationUtils.getLastLocation(context, fusedLocationProviderClient)
        val currentLocation = locationUtils.getLocation(context, fusedLocationProviderClient)
        if (locationTask == null) sendResultMessage(listOf(PreferencesUpdateState.error("Разрешения не были даны")))

        locationTask?.let {
            it.addOnCompleteListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if (it.result != null) {
                        updateWeather(
                            lon = it.result.longitude,
                            lat = it.result.latitude,
                            sendResultMessage = sendResultMessage
                        )
                    } else {
                        currentLocation?.addOnCompleteListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                updateWeather(
                                    lon = it.result.longitude,
                                    lat = it.result.latitude,
                                    sendResultMessage = sendResultMessage
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateWeather(lon: Double, lat: Double, sendResultMessage: Boolean = true) {
        val list = mutableListOf<PreferencesUpdateState>()
        mainRepository.updateCurrentWeather(
            lon = lon,
            lat = lat
        ).collect {
            Log.i(TAG, "Weather: ${it.status}")
            list.add(it)
        }

        mainRepository.updateUVInfo(
            lon = lon,
            lat = lat
        ).collect {
            Log.i(TAG, "uv: ${it.status}")
            list.add(it)
        }
        if (sendResultMessage) {
            sendResultMessage(list)
        }
        mainRepository.updateWidgets()
    }

    fun saveReplyTo(to: Messenger) {
        replyTo = to
        Log.i(TAG, "saveReplyTo: messenger saved")
    }

    private fun sendResultMessage(listOfUpdateResult: List<PreferencesUpdateState>) {

        Log.i(TAG, "PreferenceStateList: $listOfUpdateResult")

        val msg: Message = listOfUpdateResult.let { list ->
            if (list.size == 1) {
                Message.obtain(
                    null,
                    WEATHER_UPDATE_ERROR,
                    this.hashCode(),
                    0
                )
            } else if (list[0].status == Status.ERROR) (
                    Message.obtain(
                        null,
                        WEATHER_UPDATE_ERROR,
                        this.hashCode(),
                        0
                    ))
            else if (list[1].status == Status.ERROR)
                Message.obtain(
                    null,
                    WEATHER_UPDATE_ERROR,
                    this.hashCode(),
                    0
                )
            else
                Message.obtain(
                    null,
                    WEATHER_UPDATE_SUCCESS,
                    this.hashCode(),
                    0
                )

        }

        replyTo?.send(msg)
        Log.i(TAG, "updateWeatherAndSendResultMessage: message send $msg")

    }

    companion object {

        const val WEATHER_UPDATE = 1
        const val WEATHER_UPDATE_SUCCESS = 2
        const val WEATHER_UPDATE_ERROR = 3

        fun getStartUpdateWeatherServicePendingIntent(
            context: Context,
            action: String,
            appWidgetId: Int
        ): PendingIntent {
            val intent = Intent(context, UpdateWeatherService::class.java)
            intent.action = action
            intent.putExtra("id", appWidgetId)

            return PendingIntent.getService(context, 0, intent, FLAG_MUTABLE)
        }
    }
}