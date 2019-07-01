package com.example.hhtest.data.bean

class Weather(val list: List<WeatherData>)

class WeatherData(val main: WeatherMainParams)

class WeatherMainParams(val temp: Float, val preassure: Int)