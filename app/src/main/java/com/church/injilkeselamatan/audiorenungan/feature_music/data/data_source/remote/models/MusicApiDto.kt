package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import com.google.gson.annotations.SerializedName


data class MusicApiDto(
    @SerializedName("data")
    val music: List<MusicDto>
)