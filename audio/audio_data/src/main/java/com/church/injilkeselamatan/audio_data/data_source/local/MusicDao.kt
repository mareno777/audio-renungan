package com.church.injilkeselamatan.audio_data.data_source.local

import androidx.room.*
import com.church.injilkeselamatan.audio_data.data_source.local.model.MusicDbEntity

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
}