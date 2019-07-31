package com.leon.center.vo

/**
 * @time:2019/7/31 11:15
 * @author:Leon
 * @description:
 */
data class WeatherForecast(
    val address: String,
    val cityCode: String,
    val forecasts: List<Forecast>,
    val reportTime: String
)

data class Forecast(
    val date: String,
    val dayOfWeek: String,
    val dayTemp: String,
    val dayWeather: String,
    val dayWindDirection: String,
    val dayWindPower: String,
    val nightTemp: String,
    val nightWeather: String,
    val nightWindDirection: String,
    val nightWindPower: String
)