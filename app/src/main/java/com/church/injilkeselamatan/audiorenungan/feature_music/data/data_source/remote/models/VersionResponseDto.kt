package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionResponseDto(
    val code: Int,
    @SerialName("data")
    val versionResponse: VersionResponse,
    val message: String
)
