package com.leon.module_a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.leon.module_router.BusEvent
import com.leon.module_router.RouterUrls
import kotlinx.android.synthetic.main.module_a_activity_main.*

@Route(path = RouterUrls.MODULE_A_MAIN)
class ModuleAMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.module_a_activity_main)
        LiveEventBus.get(BusEvent.MAIN_COUNT).observe(this, Observer {
            text.text = "Hello Module A!\n $it"
        })
    }
}
