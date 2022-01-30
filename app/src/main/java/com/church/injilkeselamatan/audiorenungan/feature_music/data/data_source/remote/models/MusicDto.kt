package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusicDto(
    val album: String,
    val artist: String,
    @SerialName("mediaId")
    val id: String,
    @SerialName("imageUrl")
    val image: String,
    @SerialName("songUrl")
    val source: String,
    val title: String,
    val description: String?,
    val synopsis: String?,
    val duration: Long,
    val uploadedAt: Long
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
        synopsis = synopsis,
        duration = duration,
        uploadedAt = uploadedAt
    )
}

fun MusicDto.toSong(): Song {
    return Song(
        id = id,
        artist = artist,
        album = album,
        title = title,
        imageUri = image,
        mediaUri = source,
        description = description,
        synopsis = synopsis,
        duration = duration,
        uploadedAt = uploadedAt
    )
}