package com.leon.common.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


val gson = Gson()

inline fun <reified T> String.toObject(): T {
    return gson.fromJson(this, object : TypeToken<T>() {}.type)
}

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)


fun <T> String.toObject(type: Type): T {
    return gson.fromJson(this, type)
}

fun Any.toJson(): String {
    return gson.toJson(this)
}