package com.leon.common.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.leon.common.base.BaseViewModel

inline fun <reified T : BaseViewModel> Fragment.getViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

inline fun <reified T : BaseViewModel> Fragment.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
}

inline fun <reified T : AndroidViewModel> Fragment.getViewModelByActivity(): T {
    return ViewModelProviders.of(this.requireActivity()).get(T::class.java)
}

inline fun <reified T : AndroidViewModel> Fragment.getViewModelByActivity(viewModelFactory: ViewModelProvider.Factory?): T {
    return ViewModelProviders.of(this.requireActivity(), viewModelFactory).get(T::class.java)
}

fun <T : AndroidViewModel> Fragment.getViewModelByActivity(clazz: Class<T>,viewModelFactory: ViewModelProvider.Factory?): T {
    return ViewModelProviders.of(this.requireActivity(), viewModelFactory).get(clazz)
}