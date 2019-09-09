package com.leon.common.base

import androidx.multidex.MultiDexApplication
import com.leon.common.app.AppManager
import com.leon.common.app.InitService

open class BaseApp: MultiDexApplication() {
    var isInit = false
    override fun onCreate() {
        super.onCreate()
        AppManager.init(this)
        //初始化任务放在此服务中
        InitService.startInit(this)
    }
}