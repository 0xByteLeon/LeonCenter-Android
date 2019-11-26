package com.leon.center

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jeremyliao.liveeventbus.LiveEventBus
import com.leon.center.ui.CommonViewModel
import com.leon.center.vo.Forecast
import com.leon.common.api.Resource
import com.leon.common.api.Status
import com.leon.common.base.BaseActivity
import com.leon.common.extensions.*
import com.leon.common.widgets.CommonDialogFragment
import com.leon.module_router.BusEvent
import com.leon.module_router.RouterNavigationUtils
import com.leon.module_router.RouterUrls
import com.zyyoona7.itemdecoration.RecyclerViewDivider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

@Route(path = RouterUrls.MAIN_HOME)
class MainActivity : BaseActivity() {

    val viewModel by lazy {
        createVM<CommonViewModel>()
    }


    override val layoutId: Int = R.layout.activity_main

    val listAdapter = object : BaseQuickAdapter<Forecast, BaseViewHolder>(R.layout.item_main) {
        override fun convert(helper: BaseViewHolder, item: Forecast) {
            helper.setText(R.id.textView, item.toJson())
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        getWeather()
    }

    override fun initView(savedInstanceState: Bundle?) {
        listAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    RouterNavigationUtils.goModuleAMain()
                }
                1 -> {
                }
                2 -> {
                }
                3 -> {
                }
            }
        }

        recyclerView.addItemDecoration(RecyclerViewDivider.linear().color(Color.GRAY).dividerSize(8).build())
        recyclerView.adapter = listAdapter
        recyclerView.onRetry(Runnable {
            getWeather()
        })

        refresh.onClick {
            getWeather()
        }

        timeCount.onClick {
            RouterNavigationUtils.goModuleAMain()
        }

        cipherRSA.onClick {
            RouterNavigationUtils.goRSACipher()
        }
        lifecycle.coroutineScope.launch {
            delay(10000)
            repeat(1000) { count ->
                delay(1000)
                LiveEventBus.get(BusEvent.MAIN_COUNT).post(count)
                timeCount.text = "点击跳转ModuleA\n计数器：$count"
            }
        }

        dialogFragmentBtn.onClick {
            CommonDialogFragment.Builder(this@MainActivity).setLayoutId(R.layout.dialog_common)
                .addOnClickListener(R.id.cancelBtn,
                    object : CommonDialogFragment.OnClickListener {
                        override fun onClick(dialogFragment: CommonDialogFragment, view: View) {
                            toast("取消")
                            dialogFragment.dismiss()
                        }
                    })
                .build()
                .show(supportFragmentManager,"test")
        }
//        doAsync {
//            repeat(1000) { count ->
//                Thread.sleep(1000)
//                LiveEventBus.get(BusEvent.MAIN_COUNT).post(count)
//                uiThread {
//                    timeCount.text = "点击跳转ModuleA\n计数器：$count"
//                }
//            }
//        }
    }

    fun getWeather() {
        viewModel.getCityWeatherForecast("杭州市")
    }

    override fun bindModel() {
        val lifecycleOwner = this@MainActivity
        viewModel.apply {
            cityWeatherForecastData.observe(lifecycleOwner, Observer {
                updateViewByData(it, recyclerView) {
                    listAdapter.setNewData(it?.forecasts)
                }
            })
        }
    }

    fun <T> updateViewByData(resource: Resource<T>, view: View, onSucceed: (T?) -> Unit) {
        when (resource.status) {
            Status.SUCCESS -> {
                onSucceed(resource.data)
                view.loadSuccess()
            }
            Status.ERROR -> {
                view.loadFailed()
            }
            Status.LOADING -> {
                view.loading()
            }
        }
    }

}
