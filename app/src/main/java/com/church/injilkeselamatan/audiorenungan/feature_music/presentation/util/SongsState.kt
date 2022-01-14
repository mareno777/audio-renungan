package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

data class SongsState<T>(
    val songs: List<T> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
