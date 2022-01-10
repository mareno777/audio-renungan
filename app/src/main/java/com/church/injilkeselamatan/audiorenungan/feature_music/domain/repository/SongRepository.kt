package com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {

    fun getSongs(forceRefresh: Boolean): Flow<Resource<List<Song>>>

    suspend fun updateSong(song: MusicDbEntity): Resource<String>
}