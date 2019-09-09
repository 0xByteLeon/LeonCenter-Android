package com.leon.center

import com.billy.android.loading.Gloading
import com.leon.common.base.BaseApp

class LeonApp: BaseApp() {
    override fun onCreate() {
        super.onCreate()
        Gloading.initDefault(GlobalAdapter())
        app = this
    }

    companion object{
        lateinit var app: LeonApp
    }
}