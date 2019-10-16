package com.leon.center.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.leon.center.repository.CommonRepository
import com.leon.common.base.BaseViewModel

class CommonViewModel(application: Application) : BaseViewModel(application) {
    private val _cityName = MutableLiveData<String>()
    private val repository = CommonRepository(viewModelScope)
    val cityWeatherForecastData = _cityName.switchMap {
        repository.getWeatherForecast(it)
    }

    fun getCityWeatherForecast(cityName: String) {
        _cityName.value = cityName
    }

}