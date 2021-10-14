package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity

data class MusicDto(
    val album: String,
    val artist: String,
    val id: String,
    val image: String,
    val source: String,
    val title: String
)

fun MusicDto.toMusicDb(): MusicDbEntity {
    return MusicDbEntity(
        id = id,
        artist = artist,
        album = album,
        title = title,
        imageUri = image,
        mediaUri = source
    )
}