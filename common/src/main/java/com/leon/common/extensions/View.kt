package com.leon.common.extensions

import android.view.View
import com.billy.android.loading.Gloading
import com.leon.common.R

/**
 * @time:2019/7/30 11:08
 * @author:Leon
 * @description:View拓展属性及方法，主要是增加View的loading状态
 */

private val View.loadingHolder: Gloading.Holder
    get() {
        if (this.getTag(R.id.common_loading_holder) == null) {
            this.setTag(R.id.common_loading_holder, Gloading.getDefault().wrap(this))
        }
        return this.getTag(R.id.common_loading_holder) as Gloading.Holder
    }

/**
 * @param retryTask 失败重试
 */
fun View.onRetry(retryTask: Runnable) = apply {
    loadingHolder.withRetry(retryTask)
}

fun View.loading() = apply {
    loadingHolder.showLoading()
}

fun View.loadFailed(data: Any? = null) = apply {
    loadingHolder.withData(data).showLoadFailed()
}

fun View.loadSuccess() = apply {
    loadingHolder.showLoadSuccess()
}

fun View.empty(data: Any? = null) = apply {
    loadingHolder.withData(data).showEmpty()
}