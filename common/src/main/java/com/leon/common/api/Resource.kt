
package com.leon.common.api


/**
 * @time:2019/7/31 11:17
 * @author:Leon
 * @description:数据状态和数据类
 * @property status 数据加载状态
 * @property data 数据
 * @property message 错误信息等
*/
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}
