package com.example.weatherandroidapp.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import androidx.fragment.app.viewModels
import com.example.weatherandroidapp.R
import com.example.weatherandroidapp.WeatherActivity
import com.example.weatherandroidapp.core.app.App
import com.example.weatherandroidapp.core.factory.MainViewModelFactory
import com.example.weatherandroidapp.data.models.WeatherDescriptionItem
import com.example.weatherandroidapp.data.models.WeatherDescriptionItemBindOneInRow
import com.example.weatherandroidapp.utils.Response
import com.github.terrakok.cicerone.Router
import javax.inject.Inject


class WeatherDetailsFragment : Fragment() {

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory

    @Inject
    lateinit var router: Router

    private val viewModel: MainViewModel by activityViewModels<MainViewModel> { mainViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComponent.inject(this)

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
    private fun MainComposeSet() {
        val state = viewModel.descriptionWeatherLiveData.observeAsState()

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
                        .fillMaxWidth()
                        .background(color = Color.Black.copy(alpha = 0.3f)),
                ) {
                    LazyColumn(
                        modifier = Modifier, horizontalAlignment = Alignment.Start
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
                .padding(10.dp)
                .align(Alignment.BottomStart)
                .background(color = Color.Black.copy(alpha = 0.3f)),
                onClick = { router.exit() }) {
                Text(text = "<-", fontSize = 26.sp)
            }
        }
    }

    companion object {
        fun getNewInstance(): WeatherDetailsFragment {
            return WeatherDetailsFragment()
        }
    }
}