package com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MusicApiDtoSingle
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.UpdateSongDto
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {

    var mediaMetadataCompats: List<MediaMetadataCompat>

    fun getSongs(fromDbSource: Boolean): Flow<Resource<List<Song>>>

    fun getFeaturedSong(): Flow<Resource<Song>>

    fun whenReady(performAction: (Boolean) -> Unit): Boolean

    fun onSearch(query: String, extras: Bundle): List<MediaMetadataCompat>

    suspend fun updateSong(song: MusicDbEntity): Resource<String>
    suspend fun updateDuration(
        mediaId: String,
        updateSongDto: UpdateSongDto
    ): Flow<Resource<MusicApiDtoSingle>>
}