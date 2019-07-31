package com.leon.center

import com.billy.android.loading.Gloading
import com.leon.common.base.BaseApp

class LeonApp: BaseApp() {
    override fun onCreate() {
        super.onCreate()
        Gloading.initDefault(GlobalAdapter())
    }
}