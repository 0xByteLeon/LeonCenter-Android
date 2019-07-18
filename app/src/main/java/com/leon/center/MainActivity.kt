package com.leon.center

import android.os.Bundle
import androidx.lifecycle.Observer
import com.apkfuns.logutils.LogUtils
import com.leon.center.ui.CommonViewModel
import com.leon.common.base.BaseVMActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : BaseVMActivity<CommonViewModel>(CommonViewModel::class.java) {
    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun bindModel() {
        val lifecycleOwner = this@MainActivity
        viewModel.apply {
            labelTypeResponse.observe(lifecycleOwner, Observer {
                LogUtils.d(it)
            })
            test.observe(lifecycleOwner, Observer {
                LogUtils.d(it)
            })
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView.onClick {
            LogUtils.d("onClick")
            viewModel.getLabel()
        }
    }
}
