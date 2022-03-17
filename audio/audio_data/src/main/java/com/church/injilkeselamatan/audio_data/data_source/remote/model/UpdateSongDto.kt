package com.church.injilkeselamatan.audio_data.data_source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSongDto(
    val title: String,
    val artist: String,
    val album: String,
    val songUrl: String,
    val imageUrl: String,
    val description: String?,
    val synopsis: String?,
    val duration: Long,
    val uploadedAt: Long
)

