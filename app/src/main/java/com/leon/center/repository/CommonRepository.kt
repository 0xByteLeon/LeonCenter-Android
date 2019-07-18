package com.leon.center.repository

import androidx.lifecycle.LiveData
import com.leon.center.api.Api
import com.leon.center.di.AppModule
import com.leon.center.vo.Result
import com.leon.common.datasource.NetworkDataSource
import com.leon.common.api.Resource
import com.leon.common.base.BaseRepository
import kotlinx.coroutines.CoroutineScope

class CommonRepository(val coroutineScope: CoroutineScope) : BaseRepository() {
    private val api: Api = AppModule.provideRetrofit().create(Api::class.java)

    fun getLabel(): LiveData<Resource<List<LabelTypeResponse>>> {
        return object : NetworkDataSource<Result<List<LabelTypeResponse>>, List<LabelTypeResponse>>(coroutineScope) {
            override fun shouldFetch(data: List<LabelTypeResponse>?): Boolean {
                return data == null
            }

            override suspend fun loadFromLocal(): List<LabelTypeResponse>? {
                return null
            }

            override fun saveCallResult(item: Result<List<LabelTypeResponse>>) {

            }

            override fun processResponse(response: Result<List<LabelTypeResponse>>): List<LabelTypeResponse>? {
                return response.data
            }

            override suspend fun createCall(): Result<List<LabelTypeResponse>> {
                return api.getLabel()
            }
        }.asLiveData()
    }

}