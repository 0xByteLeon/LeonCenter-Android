package com.leon.common.app

import android.app.IntentService
import android.content.Intent
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.jeremyliao.liveeventbus.LiveEventBus

private const val ACTION_INIT = "com.leon.common.app.action.INIT"


/**
 * 所有第三方SDK在这里初始化，加快应用启动速度
 */
class InitService : IntentService("InitService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_INIT -> {
                handleActionInit()
            }
        }
    }

    /**
     * 第三方SDK初始化
     */
    private fun handleActionInit() {
        ARouter.openLog()
        ARouter.openDebug()
        ARouter.init(application)

        LiveEventBus
            .config()
            .supportBroadcast(this)
            .lifecycleObserverAlwaysActive(true)
            .autoClear(false)
    }

    companion object {

        @JvmStatic
        fun startInit(context: Context) {
            val intent = Intent(context, InitService::class.java).apply {
                action = ACTION_INIT
            }
            context.startService(intent)
        }
    }
}
