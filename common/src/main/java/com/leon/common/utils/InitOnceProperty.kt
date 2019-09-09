package com.leon.common.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
/**
 * 属性代理类，只允许赋值一次的属性
 * */
class InitOnceProperty<T> : ReadWriteProperty<Any, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        check(value != EMPTY) { "Value isn't initialized" }
        return value as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        check(this.value == EMPTY) { "Value is initialized" }
        this.value = value
    }
}