package com.leon.center.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.leon.center.repository.CommonRepository
import com.leon.common.base.BaseViewModel
import org.jetbrains.anko.toast

class CommonViewModel(application: Application) : BaseViewModel(application) {
    private val _getLabel = MutableLiveData<String>()
    private val repository = CommonRepository(viewModelScope)
    val labelTypeResponse = Transformations.switchMap(_getLabel) {
        repository.getLabel()
    }

    val test = Transformations.switchMap(_getLabel) {
        application.toast(it)
        liveData<String>()
    }


    fun getLabel() {
        _getLabel.value = "test trigger"
    }

}