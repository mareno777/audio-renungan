package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity

@Database(
    entities = [MusicDbEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao
}