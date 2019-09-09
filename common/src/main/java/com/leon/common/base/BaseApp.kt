package com.leon.common.base

import androidx.multidex.MultiDexApplication
import com.leon.common.app.InitService

open class BaseApp: MultiDexApplication() {
    var isInit = false
    override fun onCreate() {
        super.onCreate()
        //初始化任务放在此服务中
        InitService.startInit(this)
    }
}