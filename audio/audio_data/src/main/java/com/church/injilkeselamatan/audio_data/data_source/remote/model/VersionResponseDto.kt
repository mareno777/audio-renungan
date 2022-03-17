package com.church.injilkeselamatan.audio_data.data_source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionResponseDto(
    val code: Int,
    @SerialName("data")
    val versionResponse: VersionResponse,
    val message: String
)
