package com.example.weatherandroidapp.presenter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.WeatherActivity
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.data.models.WeatherDescriptionItemBindOneInRow
import com.example.weatherandroidapp.utils.Response
import com.example.weatherandroidapp.utils.Status
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherFragment : Fragment() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    @Inject
    lateinit var router: Router

    private val viewModel: MainViewModel by activityViewModels<MainViewModel> { mainViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComponent.inject(this)

        val status = activity?.intent?.getStringExtra("STATUS")
        val location = activity?.intent?.getDoubleArrayExtra("LOCATION_RESULT")
        val message = activity?.intent?.getStringExtra("ERROR_MESSAGE")

        status?.let { it ->
            if (Status.valueOf(it) == Status.SUCCESS) {
                location?.let { weather ->
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getCurrentWeather(weather[0], weather[1])
                        viewModel.getUVInfo(weather[0], weather[1])
                    }
                }
            } else {
                message?.let { message ->
                    viewModel.setError(message)
                }
            }
        }
    }

    @Composable
    private fun MainComposeSet() {
        val state = viewModel.currentWeatherLiveData.observeAsState()

        state.value?.let {
            when (it) {
                is Response.error -> {
                    SetComposeError(message = it.msg)
                }

                is Response.loading -> {
                    SetComposeLoader()
                }

                is Response.success -> {
                    it.value.let {
                        SetUISuccessCompose(
                            it.background ?: R.drawable.clouds, it.info.values.toList()
                        )
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MainComposeSet()
            }
        }
    }

    @Composable
    private fun SetComposeLoader() {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    private fun SetComposeError(message: String?) {
        Box(modifier = Modifier.fillMaxSize()) {
            message?.let {
                Text(modifier = Modifier.align(Alignment.Center), text = it, fontSize = 24.sp)
            }
        }
    }

    @Composable
    private fun SetUISuccessCompose(background: Int, info: List<WeatherDescriptionItem>) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = background),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .padding(20.dp, 0.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .width(160.dp)
                        .background(color = Color.Black.copy(alpha = 0.3f)),
                ) {
                    LazyColumn(
                        modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(info) {
                            WeatherDescriptionItemBindOneInRow(
                                modifier = Modifier, weather = it
                            )
                        }
                    }
                }
            }

            TextButton(modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(color = Color.Black.copy(alpha = 0.3f)),
                onClick = { router.navigateTo(WeatherActivity.Screens.WeatherDetailsFragment()) }) {
                Text(text = "->", fontSize = 26.sp)
            }
        }
    }

    companion object {

        fun getNewInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }

}