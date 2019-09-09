package com.leon.center.api

import retrofit2.http.*
import com.leon.common.api.Result


interface Api {
    @GET("abc/dags")
    suspend fun getLabel(): Result<List<String>>
}
