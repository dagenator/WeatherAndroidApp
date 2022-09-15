package com.example.weatherandroidapp.utils

import android.content.SharedPreferences
import com.example.weatherandroidapp.data.models.CurrentWeather
import com.example.weatherandroidapp.data.models.UVInfo
import com.example.weatherandroidapp.data.models.DisplayUVInfo
import com.example.weatherandroidapp.data.models.DisplayWeatherInfo
import java.time.LocalDateTime
import javax.inject.Inject

class SharedPreferencesUtils @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val weatherStateUtil: WeatherStateUtil
) {

    private val divider = ','

    fun saveNewWidgetId(id: Int) {
        val str = getListOfWidgetsIdInString()
        if (str.contains(id.toString())) {
            if (str.isEmpty()) {
                str.plus(id)
            } else {
                str.plus(divider + id)
            }
        } else
            return


        sharedPreferences.let {
            val myEdit: SharedPreferences.Editor = it.edit()
            myEdit.putString("widgetIdArray", str)
            myEdit.commit()
        }
    }

    private fun getListOfWidgetsIdInString(): String {
        sharedPreferences.let {
            return it.getString("widgetIdArray", "") ?: ""
        }
    }

    fun getListOfWidgetsId(): Array<Int> {
        val str = getListOfWidgetsIdInString()

        return if (str.isEmpty()) {
            arrayOf()
        } else {
            str.split(divider).map { x -> x.toInt() }.toTypedArray()
        }
    }

    fun deleteWidgetId(id: Int) {
        val idArr = getListOfWidgetsId().toMutableList()
        idArr.remove(id)

        val str = idArr.joinToString { x -> "${x}$divider" }

        sharedPreferences.let {
            val myEdit: SharedPreferences.Editor = it.edit()
            myEdit.putString("widgetIdArray", str)
            myEdit.commit()
        }

    }

    fun getCity(): String? {
        sharedPreferences.let {
            return it.getString("city", null)
        }
    }

    fun saveWeatherInfo(currentWeather: CurrentWeather) {
        sharedPreferences.let {
            val myEdit: SharedPreferences.Editor = it.edit()

            myEdit.putInt(
                "weatherId",
                currentWeather.weather[0].id.toInt()
            )
            myEdit.putFloat("currentDegree", currentWeather.main.temp.toFloat())
            myEdit.putFloat("maxDegree", currentWeather.main.tempMax.toFloat())
            myEdit.putFloat("minDegree", currentWeather.main.tempMin.toFloat())
            myEdit.putFloat("wind", currentWeather.wind.speed.toFloat())
            myEdit.putString("city", currentWeather.name)
            myEdit.putString("weatherError", null)
            myEdit.putFloat("feelDegree", currentWeather.main.feelsLike.toFloat())
            myEdit.putFloat("cloudiness",currentWeather.clouds.all.toFloat())
            myEdit.putString("description",currentWeather.weather[0].description.toString())
            myEdit.putString("callTime", LocalDateTime.now().toString())
            myEdit.commit()
        }
    }


    fun saveWeatherError(error: String) {
        sharedPreferences.let {
            val myEdit: SharedPreferences.Editor = it.edit()
            myEdit.putString("weatherError", error)
            myEdit.commit()
        }
    }

    fun getWeatherInfo(): DisplayWeatherInfo {
        sharedPreferences.let {
            return DisplayWeatherInfo(
                weatherId = it.getInt("weatherId", 0),
                currentDegree = it.getFloat("currentDegree", 0.0F),
                maxDegree = it.getFloat("maxDegree", 0.0F),
                minDegree = it.getFloat("minDegree", 0.0F),
                feelDegree = it.getFloat("feelDegree", 0.0F),
                wind = it.getFloat("wind", 0.0F),
                cloudiness = it.getFloat("cloudiness",0.0F),
                description = it.getString("description"," "),
                error = it.getString("weatherError", null),
                callTime =  it.getString("callTime", null)
            )
        }
    }

    fun getWeatherError(): String? {
        sharedPreferences.let {
            return it.getString("weatherError", null)
        }
    }

    fun saveUVInfo(uvInfo: UVInfo) {
        sharedPreferences.let {
            val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
            myEdit.putFloat("currentUV", uvInfo.result.uv.toFloat())
            myEdit.putFloat("maxUV", uvInfo.result.uvMax.toFloat())
            myEdit.putString("UVError", null)
            myEdit.commit()
        }
    }

    fun saveUVError(error: String) {
        sharedPreferences.let {
            val myEdit: SharedPreferences.Editor = it.edit()
            myEdit.putString("UVError", error)
            myEdit.commit()
        }
    }

    fun getUVError(): String? {
        sharedPreferences.let {
            return it.getString("UVError", null)
        }
    }

    fun getUVInfo(): DisplayUVInfo {
        sharedPreferences.let {
            return DisplayUVInfo(
                it.getFloat("currentUV", 0.0F),
                it.getFloat("maxUV", 0.0F),
                it.getString("UVError", null)
            )
        }
    }

}