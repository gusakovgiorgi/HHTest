package com.example.hhtest.data.api

import com.example.hhtest.data.bean.Weather
import retrofit2.Call
import retrofit2.http.GET


interface WeatherApi {
    @GET("/data/2.5/find?q=London&units=metric&appid=5ef5f004674fb9aa0dc8202a83ffa7b3")
    fun getWeatherInLondon(): Call<Weather>
}