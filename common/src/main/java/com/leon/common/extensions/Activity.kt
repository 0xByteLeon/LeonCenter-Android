package com.leon.common.extensions

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.leon.common.base.BaseViewModel
import kotlin.reflect.KClass

inline fun <reified T : BaseViewModel> FragmentActivity.getViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

inline fun <reified T : BaseViewModel> FragmentActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory?): T {
    return ViewModelProviders.of(this).get(T::class.java)
}

fun <T : AndroidViewModel> FragmentActivity.getViewModel(clazz: Class<T>, viewModelFactory: ViewModelProvider.Factory?): T {
    return androidx.lifecycle.ViewModelProviders.of(this, viewModelFactory).get(clazz)
}