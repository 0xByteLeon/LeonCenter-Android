package com.leon.center.repository

import androidx.lifecycle.LiveData
import com.leon.center.api.RollToolsApi
import com.leon.center.vo.Result
import com.leon.center.vo.WeatherForecast
import com.leon.common.datasource.CoroutineDataResource
import com.leon.common.api.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CommonRepository(coroutineScope: CoroutineScope) : AbsRepository(coroutineScope) {
    private val rollToolsApi = RollToolsApi.getApi()

    fun getWeatherForecast(cityName: String): LiveData<Resource<WeatherForecast>> {
        return object : CoroutineDataResource<Result<WeatherForecast>, WeatherForecast>(coroutineScope) {
            //需要保存网络请求结果至本地时可以在这里执行，此方法在异步协程中调用，createCall()
            override fun saveCallResult(item: Result<WeatherForecast>) {

            }
            //是否从远端Api获取数据 true获取远端数据 false仅从本地加载数据
            override fun shouldFetch(data: WeatherForecast?): Boolean {
                return data == null
            }

            //本地数据源加载数据，在异步协程中执行
            override suspend fun loadFromLocal(): WeatherForecast? {
                return null
            }

            //处理网络请求原始结果，并返回UI层数据
            override fun processResponse(response: Result<WeatherForecast>): WeatherForecast? {
                return response.data
            }

            //创建网络请求
            override suspend fun createCall(): Result<WeatherForecast> {
                return rollToolsApi.getWeatherForecast(cityName)
            }

        }.asLiveData()
    }


    fun getWeather(cityName: String): LiveData<Resource<WeatherForecast>> {
        return generateNetSource {
            rollToolsApi.getWeatherForecast(cityName)
        }
//
//        return object : CoroutineDataResource<Result<WeatherForecast>, WeatherForecast>(coroutineScope) {
//            override fun saveCallResult(item: Result<WeatherForecast>) {
//            }
//
//            override fun shouldFetch(data: WeatherForecast?): Boolean {
//                return data == null
//            }
//
//            override suspend fun loadFromLocal(): WeatherForecast? {
//                return null
//            }
//
//            override fun processResponse(response: Result<WeatherForecast>): WeatherForecast? {
//                return response.data
//            }
//
//            override suspend fun createCall(): Result<WeatherForecast> {
//                return rollToolsApi.getWeatherForecast(cityName)
//            }
//
//        }.asLiveData()
    }

}