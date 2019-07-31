package com.leon.common.datasource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.apkfuns.logutils.LogUtils
import com.leon.common.BuildConfig
import com.leon.common.api.*
import kotlinx.coroutines.*

/**
 * @time:2019/7/31 11:17
 * @author:Leon
 * @description:此类实际承担了多个数据源获取策略的职责，使用协程获取数据
 * @param ResponseType 网络请求原始数据
 * @param ResultType 处理后数据
 * @property coroutineScope 启动携程请求失败等方法会在此协程调用
 */
abstract class NetworkDataSource<ResponseType, ResultType> @MainThread constructor(private val coroutineScope: CoroutineScope = GlobalScope) :
    DataSource<ResultType>() {

    /**
     * 数据源被创建时就开始执行数据加载流程
     * */
    init {
        result.value = Resource.loading(null)
        coroutineScope.launch {
            val localData = withContext(Dispatchers.IO) {
                try {
                    loadFromLocal()
                } catch (e:Throwable){
                    LogUtils.e(e)
                    null
                }
            }
            if (shouldFetch(localData)) {
                fetchFromNetwork()
            } else {
                postValue(Resource.success(localData))
            }
        }
    }

    /**
     * 发起网络请求
     * */
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

    /**
     * 请求失败时回调
     **/
    protected open fun onFetchFailed() {}

    @WorkerThread
    protected abstract fun saveCallResult(item: ResponseType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     * 本地数据源加载数据，在异步协程中执行,先于 @see <createCall()> 执行
     */
    @WorkerThread
    protected abstract suspend fun loadFromLocal(): ResultType?

    /**
     * 获取数据后在异步协程中处理数据
     * @param response Api请求得到的Response
     * @return ResultType 一般来说此处返回的是UI层直接使用的数据类型
     **/
    @WorkerThread
    protected abstract fun processResponse(response: ResponseType): ResultType?


    /**
     * 创建网络请求
     * @param ResponseType 网络请求返回结果类型
     * */
    @MainThread
    protected abstract suspend fun createCall(): ResponseType

}




