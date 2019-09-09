package com.leon.center.api

import com.leon.center.di.AppModule
import com.leon.center.vo.WeatherForecast
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @time:2019/7/31 11:17
 * @author:Leon
 * @description:
 */
interface RollToolsApi {

    @GET("weather/forecast/{cityName}")
    suspend fun getWeatherForecast(@Path("cityName") cityName: String): com.leon.common.api.Result<WeatherForecast>

    companion object {
        fun getApi(baseUrl: String = "https://www.mxnzp.com/api/") =
            AppModule.provideRetrofit(baseUrl).create(RollToolsApi::class.java)

    }
}