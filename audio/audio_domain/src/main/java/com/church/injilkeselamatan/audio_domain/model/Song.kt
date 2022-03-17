package com.church.injilkeselamatan.audio_domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val imageUri: String,
    val mediaUri: String,
    var isFavorite: Boolean = false,
    val description: String?,
    val synopsis: String?,
    val duration: Long,
    val uploadedAt: Long
)
