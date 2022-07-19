package com.example.weatherandroidapp.core.di

import com.example.weatherandroidapp.core.retrofit.UVApi
import com.example.weatherandroidapp.core.retrofit.WeatherMapApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
class NetworkModule {

    //@Singleton
    @Provides
    fun provideWeatherRetrofit(): WeatherMapApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherMapApi::class.java)
    }

    @Provides
    fun provideUVRetrofit(): UVApi {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openuv.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        return retrofit
            .create(UVApi::class.java)
    }

}