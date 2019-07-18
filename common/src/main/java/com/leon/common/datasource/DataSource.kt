package com.leon.common.datasource

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.leon.common.api.Resource

abstract class DataSource<ResultType> {
    protected val result: MediatorLiveData<Resource<ResultType>> = MediatorLiveData()
    @MainThread
    protected fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    protected fun postValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.postValue(newValue)
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

}