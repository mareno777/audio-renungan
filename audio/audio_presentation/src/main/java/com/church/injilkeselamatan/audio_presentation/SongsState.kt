package com.church.injilkeselamatan.audio_presentation

data class SongsState<T>(
    val songs: List<T> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
