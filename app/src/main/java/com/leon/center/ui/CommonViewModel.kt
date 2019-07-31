package com.leon.center.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.leon.center.repository.CommonRepository
import com.leon.common.base.BaseViewModel
import org.jetbrains.anko.toast

class CommonViewModel(application: Application) : BaseViewModel(application) {
    private val _cityName = MutableLiveData<String>()
    private val repository = CommonRepository(viewModelScope)
    val cityWeatherForecastData = Transformations.switchMap(_cityName) {
        repository.getWeatherForecast(it)
    }
    fun getCityWeatherForecast(cityName: String) {
        _cityName.value = cityName
    }

}