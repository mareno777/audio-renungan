package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

data class FeaturedSongState<T>(
    val song: T? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
