package com.example.videoplayerassignment.data.model

import com.google.gson.annotations.SerializedName

data class SectionListRequest(
    @SerializedName("is_home_screen")
    val isHomeScreen: Int = 1,
    @SerializedName("type_id")
    val typeId: Int = 1
)
