package com.example.videoplayerassignment.data.api

import com.example.videoplayerassignment.data.model.SectionListRequest
import com.example.videoplayerassignment.data.model.SectionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("public/api/section_list")
    suspend fun getSectionList(
        @Body request: SectionListRequest = SectionListRequest()
    ): Response<SectionResponse>

    @FormUrlEncoded
    @POST("public/api/section_list")
    suspend fun getSectionListForm(
        @Field("is_home_screen") isHomeScreen: Int = 1,
        @Field("type_id") typeId: Int = 1
    ): Response<SectionResponse>
}
