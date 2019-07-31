package com.leon.center

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.leon.center.ui.CommonViewModel
import com.leon.center.vo.Forecast
import com.leon.common.api.Resource
import com.leon.common.api.Status
import com.leon.common.base.BaseVMActivity
import com.leon.common.extensions.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : BaseVMActivity<CommonViewModel>(CommonViewModel::class.java) {

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
        recyclerView.adapter = listAdapter
        recyclerView.onRetry(Runnable {
            getWeather()
        })

        refresh.onClick {
            getWeather()
        }
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
