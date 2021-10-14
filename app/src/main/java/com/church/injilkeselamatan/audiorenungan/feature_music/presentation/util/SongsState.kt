package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song

data class SongsState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
