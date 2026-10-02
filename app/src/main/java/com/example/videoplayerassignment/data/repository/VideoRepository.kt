package com.example.videoplayerassignment.data.repository

import com.example.videoplayerassignment.data.api.RetrofitClient
import com.example.videoplayerassignment.data.model.SectionItem
import com.example.videoplayerassignment.data.model.SectionListRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class VideoRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getSectionList(): Result<List<SectionItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSectionList(SectionListRequest(isHomeScreen = 1, typeId = 1))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == 200 && body.result != null) {
                    Result.Success(body.result)
                } else {
                    val formResponse = apiService.getSectionListForm(isHomeScreen = 1, typeId = 1)
                    val formBody = formResponse.body()
                    if (formResponse.isSuccessful && formBody != null && formBody.status == 200 && formBody.result != null) {
                        Result.Success(formBody.result)
                    } else {
                        Result.Error(body?.message ?: "Failed to fetch video sections")
                    }
                }
            } else {
                Result.Error("API call failed with status: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "An unexpected error occurred", e)
        }
    }
}
