package com.church.injilkeselamatan.audio_presentation.albums

data class FeaturedSongState<T>(
    val song: T? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
