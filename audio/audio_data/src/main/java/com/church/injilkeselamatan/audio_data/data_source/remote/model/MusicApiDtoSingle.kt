package com.church.injilkeselamatan.audio_data.data_source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicApiDtoSingle(
    val code: Int,
    @SerialName("data")
    val music: MusicDto,
    val message: String
)