package com.leon.common.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.leon.common.extensions.getViewModelByActivity

abstract class BaseVMFragment<VM : BaseViewModel>(private var modelClass: Class<VM>) : BaseFragment() {

    val viewModel: VM by lazy {
        getViewModelByActivity(modelClass, getViewModelFactory())
    }

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindModel()
    }

    protected abstract fun bindModel()
}