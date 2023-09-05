package com.example.weatherandroidapp.data.models

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherandroidapp.R


sealed class WeatherDescriptionItem(){
    data class UiDescription(val description: String):WeatherDescriptionItem()
    data class UiTitle(val title: Int):WeatherDescriptionItem()
    data class UiIcon(val icon: Int):WeatherDescriptionItem()
    data class UiBigTitleIcon(val icon: Int):WeatherDescriptionItem()
}

@Preview()
@Composable
fun WeatherDescriptionItemBindOneInRow(
    modifier: Modifier = Modifier, weather: WeatherDescriptionItem = WeatherDescriptionItem.UiIcon(R.drawable.ic_mist_icon)
) {
    Row(
        modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Center
    ) {

        when(weather){
            is WeatherDescriptionItem.UiDescription->{
                Text(
                    modifier = Modifier,
                    text = weather.description,
                    color = Color.White,
                    fontSize = 22.sp
                )
            }
            is WeatherDescriptionItem.UiIcon->{
                Image(
                    modifier = Modifier.height(22.dp),
                    painter = painterResource(id =  weather.icon ?: R.drawable.ic_mist_icon),
                    contentDescription = weather.icon.toString(),
                    colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.7f))
                )
            }
            is WeatherDescriptionItem.UiTitle->{
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = stringResource(weather.title),
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            is WeatherDescriptionItem.UiBigTitleIcon->{
                Image(
                    modifier = Modifier.height(60.dp),
                    painter = painterResource(id =  weather.icon ?: R.drawable.ic_mist_icon),
                    contentDescription = weather.icon.toString(),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}