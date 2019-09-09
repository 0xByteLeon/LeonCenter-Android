package com.leon.center.repository

import androidx.lifecycle.LiveData
import com.leon.common.api.Resource
import com.leon.common.base.BaseRepository
import com.leon.common.datasource.CoroutineDataResource
import com.leon.common.api.Result
import kotlinx.coroutines.CoroutineScope

/**
 * @time:2019/7/31 14:02
 * @author:Leon
 * @description:
 */
abstract class AbsRepository(val coroutineScope: CoroutineScope) : BaseRepository() {

    /**
     * 对于不需要本地缓存的数据直接调用此方法生成 CoroutineDataResource
     * */
    inline fun <reified T> generateNetSource(crossinline block: suspend CoroutineScope.() -> Result<T>): LiveData<Resource<T>> {
        return object : CoroutineDataResource<Result<T>, T>(coroutineScope) {
            override fun saveCallResult(item: Result<T>) {

            }

            override fun shouldFetch(data: T?): Boolean {
                return data == null
            }

            override suspend fun loadFromLocal(): T? {
                return null
            }

            override fun processResponse(response: Result<T>): T? {
                return response.data
            }

            override suspend fun createCall(): Result<T> {
                return block(coroutineScope)
            }

        }.asLiveData()
    }
}