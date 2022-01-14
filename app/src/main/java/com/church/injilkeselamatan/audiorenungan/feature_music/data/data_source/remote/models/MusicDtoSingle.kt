package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicDtoSingle(
    val album: String,
    val artist: String,
    @SerialName("mediaId")
    val id: String,
    @SerialName("imageUrl")
    val image: String,
    @SerialName("songUrl")
    val source: String,
    val title: String,
    val description: String?,
    val synopsis: String?,
    val duration: Long,
    val uploadedAt: Long
)