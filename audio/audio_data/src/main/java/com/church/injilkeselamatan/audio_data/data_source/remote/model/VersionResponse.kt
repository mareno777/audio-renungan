package com.church.injilkeselamatan.audio_data.data_source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionResponse(
    val version: Int,
    val releaseDate: Long
)
