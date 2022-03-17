package com.church.injilkeselamatan.audio_data.data_source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicApiDto(
    @SerialName("data")
    val music: List<MusicDto>
)