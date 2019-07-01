package com.example.hhtest.data

import com.example.hhtest.data.bean.Weather
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class WeatherRepository(private val weatherDataSource: WeatherDataSource) {

    suspend fun getWeatherAsync(): Deferred<Result<Weather>> = GlobalScope.async {
        return@async weatherDataSource.getWeather()
    }
}