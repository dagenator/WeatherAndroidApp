package com.example.weatherandroidapp.utils

import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.data.models.ImageStateSet
import javax.inject.Inject

class WeatherStateUtil @Inject constructor() {

    fun UVDescription(UV :Int):String{
        when(UV){
            in 0..2-> return "Дневной УФ Низкий"
            in 3..5-> return "Дневной УФ Средний. Рекомендуется использовать крем c spf"
            in 6..7-> return "Дневной УФ Высокий. Не забудьте про крем c spf"
            in 8..10-> return "Дневной УФ Очень высокий. Используйте крем c spf"
            else-> return "Дневной УФ Экстремальный. Апокалипсис случился. "
        }
    }

    fun getImageStateSet(weatherId: Int): ImageStateSet {

        when (weatherId) {
            in 200..299 -> return ImageStateSet(
                weatherId,
                R.drawable.thunderstorm,
                R.drawable.ic_thunderstorm_icon
            )
            300, 301, 310, 311, 500, 501 -> return ImageStateSet(
                weatherId,
                R.drawable.rain,
                R.drawable.ic_rain_icon
            )
            in 300..399 -> return ImageStateSet(
                weatherId,
                R.drawable.heavy_rain,
                R.drawable.ic_heavy_rain_icon
            )
            511, in 611..616 -> return ImageStateSet(
                weatherId,
                R.drawable.freezing_rain,
                R.drawable.ic_frezing_rain_icon
            )
            in 520..531 -> return ImageStateSet(
                weatherId,
                R.drawable.heavy_rain,
                R.drawable.ic_heavy_rain_icon
            )
            in 600..601, 620 -> return ImageStateSet(
                weatherId,
                R.drawable.snow,
                R.drawable.ic_snow_icon
            )
            602, 621, 622 -> return ImageStateSet(
                weatherId,
                R.drawable.snow,
                R.drawable.ic_heavy_snow
            )
            in 700..799 -> return ImageStateSet(weatherId, R.drawable.mist, R.drawable.ic_mist_icon)
            800 -> return ImageStateSet(
                weatherId,
                R.drawable.clear_sky,
                R.drawable.ic_clear_sky_icon
            )
            801 -> return ImageStateSet(
                weatherId,
                R.drawable.few_clouds,
                R.drawable.ic_few_clouds_icon
            )
            802 -> return ImageStateSet(
                weatherId,
                R.drawable.scattered_clouds,
                R.drawable.ic_scattered_clouds_icon
            )
            803 -> return ImageStateSet(weatherId, R.drawable.clouds, R.drawable.ic_clouds_icon)
            804 -> return ImageStateSet(
                weatherId,
                R.drawable.heavy_clouds,
                R.drawable.ic_heavy_clouds_icon
            )
            else -> return ImageStateSet(weatherId, R.drawable.night_sky, R.drawable.ic_mist_icon)
        }

    }
}