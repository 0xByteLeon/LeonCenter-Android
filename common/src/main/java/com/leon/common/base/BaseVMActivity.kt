package com.leon.common.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.leon.common.extensions.getViewModel

abstract class BaseVMActivity<VM : BaseViewModel>(private var modelClass: Class<VM>) : BaseActivity() {

    val viewModel: VM by lazy {
        getViewModel(modelClass, getViewModelFactory())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindModel()
    }

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }


    protected abstract fun bindModel()

}