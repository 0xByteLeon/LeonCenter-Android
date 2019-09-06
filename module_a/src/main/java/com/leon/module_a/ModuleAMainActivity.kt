package com.leon.module_a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.leon.common.base.BaseActivity
import com.leon.module_router.BusEvent
import com.leon.module_router.RouterUrls
import kotlinx.android.synthetic.main.module_a_activity_main.*

@Route(path = RouterUrls.MODULE_A_MAIN)
class ModuleAMainActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.module_a_activity_main

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun bindModel() {
        LiveEventBus.get(BusEvent.MAIN_COUNT).observe(this, Observer {
            timeCount.text = "$it"
        })
    }

}
