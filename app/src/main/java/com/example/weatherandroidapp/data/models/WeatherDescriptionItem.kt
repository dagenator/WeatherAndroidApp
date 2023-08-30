package com.example.weatherandroidapp.data.models

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherandroidapp.R

data class WeatherDescriptionItem(
    val icon: Int? = null,
    val description: String = "",
    val value: Float? = null
)

@Preview()
@Composable
fun WeatherDescriptionItemBindTwoInRow(
    modifier: Modifier = Modifier, weather: WeatherDescriptionItem = WeatherDescriptionItem(
        R.drawable.ic_heavy_rain_icon, "SomeDescription"
    )
) {
    Row(
        modifier = Modifier

    ) {
        Image(
            modifier = Modifier
                .height(80.dp)
                .padding(20.dp),
            painter = painterResource(id = weather.icon ?: R.drawable.ic_mist_icon),
            contentDescription = weather.description
        )
        Text(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterVertically),
            text = weather.description,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Preview()
@Composable
fun WeatherDescriptionItemBindOneInRow(
    modifier: Modifier = Modifier, weather: WeatherDescriptionItem = WeatherDescriptionItem(
        R.drawable.ic_heavy_rain_icon, "SomeDescription"
    )
) {
    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        weather.icon?.let { icon ->
            Image(
                modifier = Modifier.height(80.dp),
                painter = painterResource(id = weather.icon ?: R.drawable.ic_mist_icon),
                contentDescription = weather.description
            )
        }

        weather.value?.let {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = weather.description,
                color = Color.White,
                fontSize = 18.sp
            )
        }

        if (weather.description.isNotEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = weather.description,
                color = Color.White,
                fontSize = 18.sp
            )
        }

    }
}