package com.leon.common.datasource

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.leon.common.api.Resource


/**
 * @time:2019/7/31 11:17
 * @author:Leon
 * @description:数据源抽象，构造LiveData数据源，使用 MediatorLiveData 监听单个或多个数据源数据变动
 */
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