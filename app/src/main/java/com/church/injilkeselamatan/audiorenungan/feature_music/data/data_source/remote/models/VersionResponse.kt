package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class VersionResponse(
    val version: Int,
    val releaseDate: Long
)
