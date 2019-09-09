package com.leon.center

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.postDelayed
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.leon.common.extensions.loading
import com.leon.module_router.BusEvent
import com.leon.module_router.RouterNavigationUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splash.loading()
        if (!LeonApp.app.isInit){
            LiveEventBus.get(BusEvent.APP_INIT_COMPLETED).observe(this, Observer {
                LeonApp.app.isInit = true
                splash.postDelayed(1000){
                    RouterNavigationUtils.goMainActivity()
                    finish()
                }
            })
        } else {
            splash.postDelayed(3000){
                RouterNavigationUtils.goMainActivity()
                finish()
            }
        }
    }
}
