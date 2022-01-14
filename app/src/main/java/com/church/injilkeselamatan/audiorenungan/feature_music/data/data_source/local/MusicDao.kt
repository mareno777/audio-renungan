package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local

import androidx.room.*
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity

@Dao
interface MusicDao {

    @Query("SELECT * FROM MusicDbEntity")
    suspend fun getAllSongs(): List<MusicDbEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<MusicDbEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFavoriteSong(song: MusicDbEntity)

    @Query("DELETE FROM MusicDbEntity")
    suspend fun deleteAllSongs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecentSong(song: MusicDbEntity)
}