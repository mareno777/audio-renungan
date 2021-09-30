package com.church.injilkeselamatan.audiorenungan.feature_music.domain.model

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val imageUri: String,
    val mediaUri: String,
    var isFavorite: Boolean = false
)

fun MusicDbEntity.toSong(): Song =
    Song(
        id, title, artist, album, imageUri, mediaUri
    )
