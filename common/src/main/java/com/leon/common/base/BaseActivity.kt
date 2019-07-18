package com.leon.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initView(savedInstanceState)
        initData(savedInstanceState)
    }


    protected abstract fun initData(savedInstanceState: Bundle?)

    protected abstract fun initView(savedInstanceState: Bundle?)
}