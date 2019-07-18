package com.leon.common.datasource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.leon.common.BuildConfig
import com.leon.common.api.*
import kotlinx.coroutines.*

/**
 * @param ResponseType 网络请求原始数据
 * @param ResultType 处理后数据
 * 协程数据源，使用协程获取数据
 *
 * */

abstract class NetworkDataSource<ResponseType, ResultType> @MainThread constructor(private val coroutineScope: CoroutineScope = GlobalScope) :
    DataSource<ResultType>() {

    init {
        result.value = Resource.loading(null)
        coroutineScope.launch {
            val localData = withContext(Dispatchers.IO) {
                try {
                    loadFromLocal()
                } catch (e:Throwable){
                    null
                }
            }
            if (shouldFetch(localData)) {
                fetchFromNetwork()
            }
        }
    }

    private suspend fun fetchFromNetwork() {
        supervisorScope {
            withContext(Dispatchers.IO) {
                try {
                    val response = createCall()
                    val result = processResponse(response)
                    saveCallResult(response)
                    postValue(Resource.success(result))
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                        postValue(Resource.error(e.message ?: "获取数据失败", null))
                    } else {
                        postValue(Resource.error("获取数据失败", null))
                    }
                    supervisorScope {
                        onFetchFailed()
                    }
                }
            }
        }
    }

    /**请求失败时回调*/
    protected open fun onFetchFailed() {}

    @WorkerThread
    protected abstract fun saveCallResult(item: ResponseType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     * 从本地获取
     * */
    @WorkerThread
    protected abstract suspend fun loadFromLocal(): ResultType?

    /**
     * 获取数据后在后台线程处理任务
     **/
    @WorkerThread
    protected abstract fun processResponse(response: ResponseType): ResultType?


    @MainThread
    protected abstract suspend fun createCall(): ResponseType

}




