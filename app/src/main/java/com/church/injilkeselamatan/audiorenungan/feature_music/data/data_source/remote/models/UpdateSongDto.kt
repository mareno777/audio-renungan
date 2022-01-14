package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

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

