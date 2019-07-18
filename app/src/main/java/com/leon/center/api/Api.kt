package com.leon.center.api

import com.leon.center.vo.LabelResponse
import com.leon.center.vo.Result
import retrofit2.http.*


interface Api {
    @POST("")
    suspend fun getLabel(): Result<List<LabelResponse>>
}
