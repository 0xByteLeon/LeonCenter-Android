package com.leon.center.vo

class Result<T>(
    var code: Int,
    var msg: String,
    var success: Boolean,
    var timestamp: Long,
    var data: T?
) {

    constructor() : this(0, "", false, 0, null)

    fun succeed(): Boolean {
        return code == 0
    }

    companion object {
        fun <T> toSucceed(): Result<T> {
            return Result<T>().apply {
                success = true
            }
        }
    }
}