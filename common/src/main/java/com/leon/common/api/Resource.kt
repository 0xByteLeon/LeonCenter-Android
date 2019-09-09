package com.leon.common.api


/**
 * @time:2019/7/31 11:17
 * @author:Leon
 * @description:数据状态和数据类
 * @property status 数据加载状态
 * @property data 数据
 * @property message 错误信息等
 */
data class Resource<T>(val status: Status, val data: T?, val message: String?) {
    var exception: Exception? = null

    companion object {
        var defaultErrorMsg = "获取数据失败"

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(exception: Exception, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, exception.message ?: defaultErrorMsg).apply {
                this.exception = exception
            }
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    fun onSucceed(onSucceed: (T?) -> Unit): Resource<T> {
        onSucceed(data)
        return this
    }

    fun onError(onError: (T?) -> Unit): Resource<T> {
        onError(data)
        return this
    }

    fun onLoading(onLoading: (T?) -> Unit): Resource<T> {
        onLoading(data)
        return this
    }
}
