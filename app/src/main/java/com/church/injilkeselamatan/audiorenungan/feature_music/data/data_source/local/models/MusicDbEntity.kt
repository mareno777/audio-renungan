package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicDbEntity (
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val imageUri: String,
    val mediaUri: String,
    var isFavorite: Boolean = false
)