package com.example.videoplayerassignment.data.model

import com.google.gson.annotations.SerializedName

data class SectionResponse(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result") val result: List<SectionItem>? = null
)
