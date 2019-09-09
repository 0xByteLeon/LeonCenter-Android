package com.leon.common.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.leon.common.utils.StatusBarUtil
import com.noober.background.BackgroundLibrary

abstract class BaseActivity : AppCompatActivity() {

    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        ARouter.getInstance().inject(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setStatusBar()
        initView(savedInstanceState)
        initData(savedInstanceState)
        bindModel()
    }

    protected open fun setStatusBar(){
        StatusBarUtil.setTransparent(this)
        StatusBarUtil.setLightMode(this)
    }

    protected abstract fun initData(savedInstanceState: Bundle?)

    protected abstract fun initView(savedInstanceState: Bundle?)

    inline fun < reified T : ViewModel> createVM(): T = ViewModelProviders.of(this, getViewModelFactory()).get(T::class.java)

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }

    protected abstract fun bindModel()
}