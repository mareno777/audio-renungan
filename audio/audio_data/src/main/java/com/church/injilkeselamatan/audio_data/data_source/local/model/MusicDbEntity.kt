package com.church.injilkeselamatan.audio_data.data_source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.church.injilkeselamatan.audio_domain.model.Song
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
    var isFavorite: Boolean = false,
    val description: String?,
    val synopsis: String?,
    val duration: Long,
    val uploadedAt: Long
)

fun MusicDbEntity.toSong(): Song {
    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        imageUri = imageUri,
        mediaUri = mediaUri,
        isFavorite = isFavorite,
        description = description,
        synopsis = synopsis,
        duration = duration,
        uploadedAt = uploadedAt
    )
}
fun Song.toDbEntity(): MusicDbEntity {
    return MusicDbEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        imageUri = imageUri,
        mediaUri = mediaUri,
        isFavorite = isFavorite,
        description = description,
        synopsis = synopsis,
        duration = duration,
        uploadedAt = uploadedAt
    )
}