package com.church.injilkeselamatan.audio_data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.church.injilkeselamatan.audio_data.data_source.local.model.MusicDbEntity

@Database(
    entities = [MusicDbEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao
}