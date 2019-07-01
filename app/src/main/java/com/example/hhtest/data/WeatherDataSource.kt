package com.example.hhtest.data

import com.example.hhtest.data.api.WeatherApi
import com.example.hhtest.data.bean.Weather
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class WeatherDataSource {
    private val retrofit: Retrofit
    private val weatherApi: WeatherApi

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
    }

    fun getWeather(): Result<Weather> {
        try {
            val result = weatherApi.getWeatherInLondon().execute()
            if (result.isSuccessful) {
                return Result.Success(result.body()!!)
            } else {
                return Result.Error(IOException(result.errorBody().toString()))
            }
        } catch (e: IOException) {
            return Result.Error(e)
        }
    }
}