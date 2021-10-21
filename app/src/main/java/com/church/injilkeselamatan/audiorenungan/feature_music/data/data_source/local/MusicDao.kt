package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local

import androidx.room.*
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM MusicDbEntity")
    fun getAllSongs(): Flow<List<MusicDbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<MusicDbEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFavoriteSong(song: MusicDbEntity)

    @Query("DELETE FROM MusicDbEntity")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecentSong(song: MusicDbEntity)
}