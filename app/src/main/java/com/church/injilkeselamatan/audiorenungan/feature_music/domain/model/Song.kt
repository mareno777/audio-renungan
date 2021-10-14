package com.church.injilkeselamatan.audiorenungan.feature_music.domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val imageUri: String,
    val mediaUri: String,
    var isFavorite: Boolean = false
)
