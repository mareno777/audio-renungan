package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.google.gson.annotations.SerializedName

data class MusicDto(
    val album: String,
    val artist: String,
    @SerializedName("mediaId")
    val id: String,
    @SerializedName("imageUrl")
    val image: String,
    @SerializedName("songUrl")
    val source: String,
    val title: String,
    val description: String?,
    val synopsis: String?
)

fun MusicDto.toMusicDb(): MusicDbEntity {
    return MusicDbEntity(
        id = id,
        artist = artist,
        album = album,
        title = title,
        imageUri = image,
        mediaUri = source,
        description = description,
        synopsis = synopsis
    )
}