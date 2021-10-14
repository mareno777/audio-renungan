package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.google.android.exoplayer2.C

@Entity
data class MusicDbEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val imageUri: String,
    val mediaUri: String,
    val position: Long = C.TIME_UNSET,
    var isFavorite: Boolean = false
)

fun MusicDbEntity.toSong(): Song {
    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        imageUri = imageUri,
        mediaUri = mediaUri,
        isFavorite = isFavorite
    )
}